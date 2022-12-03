package org.odk.collect.android.pkl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.ItemsetDbAdapter;
import org.odk.collect.android.listeners.DownloadPcl;
import org.odk.collect.android.pkl.adapter.KortimAdapter;
import org.odk.collect.android.pkl.material_design.views.ButtonRectangle;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.task.DownloadIsian2;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author AJI PRADANA
 */

public class PclListActivity extends Activity implements DownloadPcl {
    ListView listV;
    MaterialButton Btngetdata, BtnSendAgain;
    int id = 0;
    String nimKortim, nim, idJabatan;
    LinkedList<Notifikasi> monitors;
    KortimAdapter adapter;
    private static final String TAG_LIST = "list";
    private static final String TAG_NIM_PCL = "nim_pcl";
    private static final String TAG_BLOK_SENSUS = "blok_sensus";
    private static final String TAG_STATUS = "status";
    private static Object bb = new Object();
    private AlertDialog mAlertDialog;
    private ProgressDialog pDialog;
    private TextView loadingState;

    private String actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Mengunduh Isian Kuesioner... ");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplication(), R.color.colorPrimary)));
        actionBarTitle = getString(R.string.get_notifikasi);
        setTitle(actionBarTitle);

        // must be at the beginning of any activity that can be called from an external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Gagal Membuat Direktori", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        monitors = new LinkedList<>();
        CapiPreference pref = CapiPreference.getInstance();

        if (!pref.getBoolean(CapiKey.IS_LOGIN, false)) {
            Intent b = new Intent(PclListActivity.this,
                    LoginActivity.class);
            startActivity(b);
            finish();
        }

        nimKortim = (String) pref.get(CapiKey.KEY_NIM_KORTIM);
        nim = (String) pref.get(CapiKey.KEY_NIM);
        idJabatan = (String) pref.get(CapiKey.KEY_ID_JABATAN);
        setContentView(R.layout.pkl_activity_kortim_list);
        loadingState = (TextView) findViewById(R.id.loading_state);
        downloadAllLastNotif();

        listV = (ListView) findViewById(R.id.list);
        adapter = new KortimAdapter(this, monitors);

        listV.setAdapter(adapter);
        getallnotif();
        listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                option(monitors.get(position));
                return false;
            }
        });
        Btngetdata = findViewById(R.id.getdata);
        BtnSendAgain = findViewById(R.id.getdataError);
        checkButton(Btngetdata, BtnSendAgain);
    }

    public void checkButton(final MaterialButton btn, final MaterialButton btnerror) {
        ItemsetDbAdapter itemsetDbAdapter = new ItemsetDbAdapter();
        itemsetDbAdapter.open();

        final ArrayList<Notifikasi> notifGagal = itemsetDbAdapter.getFailedNotifikasi();
        itemsetDbAdapter.close();
        if (notifGagal.size() > 0) {
            btnerror.setVisibility(View.VISIBLE);
            btnerror.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    for (int i = 0; i < notifGagal.size(); i++) {
                        ubahstatus(notifGagal.get(i), btn, btnerror);
                    }
                }
            });
        } else {
            btnerror.setVisibility(View.GONE);
        }
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                downloadAllLastNotif();
            }
        });
    }

    private void ubahStatusDialog(final Notifikasi ds, final String finalFinalPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Isian " + ds.getFilename() + " Sudah Ada, Tetap Download?")
                .setCancelable(false)
                .setNegativeButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(ds);
//                        startDownload(ds, finalFinalPath);
                        new DownloadForm(PclListActivity.this, ds, finalFinalPath).execute();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();

    }

    private void ubahstatus(final Notifikasi ad, final MaterialButton btn, final MaterialButton btnerror) {
        String urlpostnotif = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PreferencesActivity.KEY_HOST,
                        Collect.host) + "/post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlpostnotif, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("UploadErrorDanIns", "" + response.toString());
//                ad.setStatus(baru);
//                onreturnedcompleted(ad);
//                adapter.notifyDataSetChanged();
                onSuccessSend(ad, btn, btnerror);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("UploadErrorDanIns Err ", "" + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uuid", ad.getUnique_id_instance());
                params.put("nim", ad.getNim());
                params.put("nama", ad.getNama());
                params.put("kortim", ad.getKortim());
                params.put("statusisian", ad.getStatus_isian());
                params.put("status", ad.getStatus());
                params.put("formid", ad.getForm_id());
                params.put("fileName", ad.getFilename());
                return params;
            }

        };
        Collect.getInstance2().addToRequestQueue(strReq);
    }

    private void onSuccessSend(Notifikasi ad, MaterialButton btn, MaterialButton btnerror) {
        ItemsetDbAdapter dbas = new ItemsetDbAdapter();
        dbas.open();
        dbas.deleteNotifikasiGagal(ad);
        dbas.close();
        checkButton(btn, btnerror);
        downloadAllLastNotif();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void getallnotif() {
//        monitors=new ArrayList<Notifikasi>();
        ItemsetDbAdapter dbas = new ItemsetDbAdapter();
        dbas.open();
        monitors = dbas.getAllNotifikasi();
        adapter = new KortimAdapter(this, monitors) {
            @Override
            public View getView(int position, View v, ViewGroup parent) {
                v = super.getView(position, v, parent);
                LinearLayout container = (LinearLayout) v.findViewById(R.id.container);
                TextView title = (TextView) v.findViewById(R.id.blok_sensus);
                TextView statusIsian = (TextView) v.findViewById(R.id.statusisian);
                TextView status = (TextView) v.findViewById(R.id.status);

                if (!monitors.get(position).getStatus().equalsIgnoreCase(InstanceProviderAPI.STATUS_RETURNED)) {
//                    container.setBackgroundColor(ContextCompat.getColor(KortimListActivity.this, R.color.grey));
                    title.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                    statusIsian.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
                    status.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray));
//                    listV.setSelector(android.R.color.transparent);
                }

                return v;
            }
        };
        listV.setAdapter(adapter);
        Log.d("saNotifikasi moni", "" + monitors.size());
        adapter.notifyDataSetChanged();
        dbas.close();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy kk:mm:ss");
        loadingState.setText("Terakhir dimuat : " + sdf.format(new Date()));
    }

    private void option(final Notifikasi ds) {
        final String urlgetisian = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PreferencesActivity.KEY_HOST,
                        Collect.host) + "/block/" + ds.getFilename() + ".xml";

        //delete(ds);

        Cursor results = null;
        String[] proj = {
                FormsProviderAPI.FormsColumns.FORM_FILE_PATH
        };
        String[] whereargs = {
                ds.getForm_id()
        };
        String FilePath = null;
        try {
            results = getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                    proj, FormsProviderAPI.FormsColumns.JR_FORM_ID + " =? ", whereargs, null);
            results.moveToPosition(-1);
            while (results.moveToNext()) {
                FilePath = results.getString(results.getColumnIndex(FormsProviderAPI.FormsColumns.FORM_FILE_PATH));
            }
        } catch (Exception ex) {
            Log.d("download", "" + ex);
        } finally {
            if (results != null) {
                results.close();
            }
        }
        results = null;
        String[] proj2 = {
                InstanceProviderAPI.InstanceColumns.DISPLAY_NAME
        };
        String[] whereargs2 = {
                ds.getFilename()
        };
        String instanceName = null;
        try {
            results = getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                    proj2, InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " = ?", whereargs2, null);
            results.moveToPosition(-1);
            while (results.moveToNext()) {
                instanceName = results.getString(results.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
            }
        } catch (Exception ex) {
            Log.d("download", "" + ex);
        } finally {
            if (results != null) {
                results.close();
            }
        }
        final String finalFinalPath = FilePath;
        if (ds.getStatus().equals(InstanceProviderAPI.STATUS_RETURNED) && !ds.getNim().equals(ds.getKortim())) {
            if (finalFinalPath != null && !finalFinalPath.equals("")) {
                //if(instanceName==null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            mDownloadIsian = new DownloadIsian();
//            mDownloadIsian.setContext(getApplicationContext(), this);
                builder.setMessage("Apa yang akan dilakukan ?")
                        .setCancelable(false)
                        .setPositiveButton("Download Isian", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                if (ds.getDow() == 1) {
//                                    onpostdownload(true);
                                        //if ((ds.getStatus().equals(InstanceProviderAPI.STATUS_SUBMITTED)) && (ds.getDow() == 0)) {
                                        if ((ds.getStatus().equals(InstanceProviderAPI.STATUS_RETURNED))) {
                                            Cursor results = null;
                                            String[] proj = {
                                                    InstanceProviderAPI.InstanceColumns.DISPLAY_NAME
                                            };
                                            String where = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " =? AND "
                                                    + InstanceProviderAPI.InstanceColumns.STATUS + " !=? ";
                                            String[] whereargs = {
                                                    ds.getFilename(), InstanceProviderAPI.STATUS_SUBMITTED
                                            };
                                            String FilePath = null;
                                            try {
                                                results = getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                                                        proj, where, whereargs, null);
                                                results.moveToPosition(-1);
                                                while (results.moveToNext()) {
                                                    FilePath = results.getString(results.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
                                                }
                                            } catch (Exception ex) {
                                                Log.d("download", "" + ex);
                                            } finally {
                                                if (results != null) {
                                                    results.close();
                                                }
                                            }
                                            if (FilePath == null) {
//                                                startDownload(ds, finalFinalPath);
                                                new DownloadForm(PclListActivity.this, ds, finalFinalPath).execute();
                                            } else {
                                                ubahStatusDialog(ds, finalFinalPath);
                                            }
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        }).show();
//                }else{
//                    Toast.makeText(this,"Isian "+ds.getFilename()+" Sudah didownload \n atau masih ada versi sebelumnya  ", Toast.LENGTH_SHORT)
//                            .show();
//                }
            } else {
                Toast.makeText(this, "Kamu Tidak Punya Kuesioner dengan id " + ds.getForm_id() + "  ", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this, "Hanya Bekerja Untuk Status " + InstanceProviderAPI.STATUS_RETURNED + " ", Toast.LENGTH_SHORT)
                    .show();
        }

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Apa yang akan dilakukan ?")
//                    .setCancelable(false)
//                    .setPositiveButton("Download Isian", new
//                            DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    if (ds.getStatus().equals(InstanceProviderAPI.STATUS_RETURNED)) {
//
//
//                                    }
//                                }
//                            })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
//                        }
//                    }).show();

    }

    private String getUpUrl(String url) {
        String arrayUrl[] = url.split("/");
        int N = arrayUrl.length;
        String newURL = "";
        for (int i = 0; i < N - 1; i++) {
            newURL += arrayUrl[i] + "/";
        }
        return newURL;
    }

    public void startDownload(Notifikasi ds, String finalFinalPath) {
//        synchronized (bb) {
        DownloadIsian2 downloadIsian2 = new DownloadIsian2(ds, finalFinalPath, PclListActivity.this, this);
        downloadIsian2.exscute();
//        }
    }

    private class DownloadForm extends AsyncTask<Void, Void, Void> {

        private Notifikasi ds;
        private String finalPath;
        private Context context;
        private ProgressDialog pDialog;

        public DownloadForm(Context context, Notifikasi ds, String finalPath) {
            this.context = context;
            this.ds = ds;
            this.finalPath = finalPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Mengunduh Isian Kuesioner... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            startDownload(ds, finalPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }

    public void onpostdownload(boolean mboolean, Notifikasi mnotif) {
        pDialog.dismiss();
        if (mboolean) {
//            Toast.makeText(this, "File Download Completed", Toast.LENGTH_SHORT)
//                    .show();
            mAlertDialog = new AlertDialog.Builder(this).create();
            mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
            mAlertDialog.setTitle("Hasil Unduhan Kuesioner");
            mAlertDialog.setMessage("Berhasil Mengunduh Kuesioner");
            DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            mAlertDialog.setCancelable(false);
            mAlertDialog.setButton(getString(R.string.ok), errorListener);
            mAlertDialog.show();
//            ItemsetDbAdapter dbas = new ItemsetDbAdapter();
//            dbas.open();
//            dbas.updatedown(mnotif);
//            dbas.close();

        } else {
//            Toast.makeText(this, "File Download not Completed", Toast.LENGTH_SHORT)
//                    .show();
            mAlertDialog = new AlertDialog.Builder(this).create();
            mAlertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            mAlertDialog.setTitle("Hasil Unduhan Kuesioner");
            mAlertDialog.setMessage("Gagal Mengunduh Kuesioner");
            DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            mAlertDialog.setCancelable(false);
            mAlertDialog.setButton(getString(R.string.ok), errorListener);
            mAlertDialog.show();
        }
    }

    private String encode(String text) {
        String ada = text;
        return ada.replace("'", "");
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private void delete(Notifikasi ds) {
        String[] whereArgs = {
                ds.getFilename()
        };
        String where = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " =? ";
        Cursor resultss = null;
        String[] proj = {
                InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH
        };
        String InstancePath1 = null;
        try {
            resultss = getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                    proj, where, whereArgs, null);
            resultss.moveToPosition(-1);
            while (resultss.moveToNext()) {
                InstancePath1 = resultss.getString(resultss.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
            }
        } catch (Exception ex) {
            Log.d("download", "" + ex);
        } finally {
            if (resultss != null) {
                resultss.close();
            }
        }
        if (InstancePath1 != null) {
            File dir = new File(getUpUrl(InstancePath1));
            deleteRecursive(dir);
            String whered = InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH + " =? ";
            String[] whereArgsd = {InstancePath1};
            Collect.getInstance().getContentResolver().delete(InstanceProviderAPI.InstanceColumns.CONTENT_URI, whered, whereArgsd);
        }
    }

    private void downloadAllLastNotif() {
        getallnotif(nim, idJabatan);
    }

    private void getallnotif(final String nim, final String idjabatan) {
        loadingState.setText("Sedang Memuat...");
        String urlpostnotif = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PreferencesActivity.KEY_HOST,
                        Collect.host) + "/post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlpostnotif, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saNotifikasi pcl", "" + response.toString());
                try {
                    JSONObject as = new JSONObject(response);
                    parseJson(as);
                } catch (JSONException a) {
                    Log.d("saNotifikasi cat pcl", "" + a);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("saNotifikasi Err pcl ", "" + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.d("saNotifikasi nim", nim);
                Log.d("saNotifikasi idjabatan", idjabatan);
                params.put("nim", nim);
                params.put("idjabatan", idjabatan);
                params.put("lastid", "" + 0);
                return params;
            }

        };
        Collect.getInstance2().addToRequestQueue(strReq);
    }

    public void parseJson(JSONObject response) {
        ItemsetDbAdapter dbas = new ItemsetDbAdapter();
        dbas.open();
        dbas.deleteNotif();
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                Notifikasi ms = new Notifikasi();
                ms.setId(Integer.valueOf(feedObj.getString("id")));
                ms.setUnique_id_instance(feedObj.getString("unique_id_instance"));
                ms.setNim(feedObj.getString("nim"));
                ms.setNama(feedObj.getString("nama"));
                ms.setStatus(feedObj.getString("perlakuan"));
                ms.setStatus_isian(feedObj.getString("status_isian"));
                ms.setForm_id(feedObj.getString("form_id"));
                ms.setKortim(feedObj.getString("koortim"));
                ms.setFilename(feedObj.getString("filename"));
                //ms.setDow(0);
                dbas.updateNotif(ms);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbas.close();
        getallnotif();
    }
}