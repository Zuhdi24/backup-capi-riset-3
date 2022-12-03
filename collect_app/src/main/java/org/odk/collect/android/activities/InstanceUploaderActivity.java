/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.ItemsetDbAdapter;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.tasks.InstanceUploaderTask;
import org.odk.collect.android.utilities.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Activity to upload completed forms.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * edit Mahendri Dwicahyo
 */
public class InstanceUploaderActivity extends Activity implements InstanceUploaderListener {
    private final static String t = "InstanceUploader";
    private final static int PROGRESS_DIALOG = 1;
    private final static int AUTH_DIALOG = 2;

    private final static String AUTH_URI = "auth";
    private static final String ALERT_MSG = "alertmsg";
    private static final String ALERT_SHOWING = "alertshowing";
    private static final String TO_SEND = "tosend";

    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    private String mAlertMsg;
    private boolean mAlertShowing;

    private InstanceUploaderTask mInstanceUploaderTask;

    // maintain a list of what we've yet to send, in case we're interrupted by auth requests
    private Long[] mInstancesToSend;

    // maintain a list of what we've sent, in case we're interrupted by auth requests
    private HashMap<String, String> mUploadedInstances;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(t, "onCreate: " + ((savedInstanceState == null) ? "creating" : "re-initializing"));

        mAlertMsg = getString(R.string.please_wait);
        mAlertShowing = false;

        mUploadedInstances = new HashMap<String, String>();

        setTitle(getString(R.string.send_data));

        // get any simple saved state...
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ALERT_MSG)) {
                mAlertMsg = savedInstanceState.getString(ALERT_MSG);
            }
            if (savedInstanceState.containsKey(ALERT_SHOWING)) {
                mAlertShowing = savedInstanceState.getBoolean(ALERT_SHOWING, false);
            }

            mUrl = savedInstanceState.getString(AUTH_URI);
        }

        // and if we are resuming, use the TO_SEND list of not-yet-sent submissions
        // Otherwise, construct the list from the incoming intent value
        long[] selectedInstanceIDs = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(TO_SEND)) {
            selectedInstanceIDs = savedInstanceState.getLongArray(TO_SEND);
        } else {
            // get instances to upload...
            Intent intent = getIntent();
            selectedInstanceIDs = intent.getLongArrayExtra(FormEntryActivity.KEY_INSTANCES);
        }

        mInstancesToSend = new Long[(selectedInstanceIDs == null) ? 0 : selectedInstanceIDs.length];
        if (selectedInstanceIDs != null) {
            for (int i = 0; i < selectedInstanceIDs.length; ++i) {
                mInstancesToSend[i] = selectedInstanceIDs[i];
            }
        }

        // at this point, we don't expect this to be empty...
        if (mInstancesToSend.length == 0) {
            Log.e(t, "onCreate: No instances to upload");
            // drop through -- everything will process through OK
        } else {
            Log.i(t, "onCreate: Beginning upload of " + mInstancesToSend.length + " instances!");
        }

        // get the task if we've changed orientations. If it's null it's a new upload.
        mInstanceUploaderTask = (InstanceUploaderTask) getLastNonConfigurationInstance();
        if (mInstanceUploaderTask == null) {
            // setup dialog and upload task
            showDialog(PROGRESS_DIALOG);
            mInstanceUploaderTask = new InstanceUploaderTask();

            // register this activity with the new uploader task
            mInstanceUploaderTask.setUploaderListener(InstanceUploaderActivity.this);

            mInstanceUploaderTask.execute(mInstancesToSend);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(this);
    }

    @Override
    protected void onResume() {
        Log.i(t, "onResume: Resuming upload of " + mInstancesToSend.length + " instances!");
        if (mInstanceUploaderTask != null) {
            mInstanceUploaderTask.setUploaderListener(this);
        }
        if (mAlertShowing) {
            createAlertDialog(mAlertMsg);
        }
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ALERT_MSG, mAlertMsg);
        outState.putBoolean(ALERT_SHOWING, mAlertShowing);
        outState.putString(AUTH_URI, mUrl);

        long[] toSend = new long[mInstancesToSend.length];
        for (int i = 0; i < mInstancesToSend.length; ++i) {
            toSend[i] = mInstancesToSend[i];
        }
        outState.putLongArray(TO_SEND, toSend);
    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        return mInstanceUploaderTask;
    }

    @Override
    protected void onPause() {
        Log.i(t, "onPause: Pausing upload of " + mInstancesToSend.length + " instances!");
        super.onPause();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }


    @Override
    protected void onStop() {
        Collect.getInstance().getActivityLogger().logOnStop(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mInstanceUploaderTask != null) {
            mInstanceUploaderTask.setUploaderListener(null);
        }
        super.onDestroy();
    }

    @Override
    public void uploadingComplete(HashMap<String, String> result) {
        Log.i(t, "uploadingComplete: Processing results (" + result.size() + ") from upload of " + mInstancesToSend.length + " instances!");

        try {
            dismissDialog(PROGRESS_DIALOG);
        } catch (Exception e) {
            // tried to close a dialog not open. don't care.
        }

        StringBuilder selection = new StringBuilder();
        Set<String> keys = result.keySet();
        Iterator<String> it = keys.iterator();

        String[] selectionArgs = new String[keys.size()];
        int i = 0;
        while (it.hasNext()) {
            String id = it.next();
            selection.append(InstanceColumns._ID + "=?");
            selectionArgs[i++] = id;
            if (i != keys.size()) {
                selection.append(" or ");
            }
        }

        StringBuilder message = new StringBuilder();
        {
            Cursor results = null;
            try {
                results = getContentResolver().query(InstanceColumns.CONTENT_URI,
                        null, selection.toString(), selectionArgs, null);
                if (results.getCount() > 0) {
                    results.moveToPosition(-1);
                    while (results.moveToNext()) {
                        String name =
                                results.getString(results.getColumnIndex(InstanceColumns.DISPLAY_NAME));
                        String id = results.getString(results.getColumnIndex(InstanceColumns._ID));
                        message.append(name + " - " + result.get(id) + "\n\n");
                    }
                } else {
                    message.append(getString(R.string.no_forms_uploaded));
                }
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }
        Iterator<String> its = keys.iterator();

        StringBuilder selection1 = new StringBuilder();
        String[] selectionArgs1 = new String[keys.size()];
        int i1 = 0;
        while (its.hasNext()) {
            String id = its.next();
            if (result.get(id).equals(Collect.getInstance().getString(R.string.success))) {
                selection1.append(InstanceColumns._ID + "=?");
                selectionArgs1[i1++] = id;
                if (i1 != keys.size()) {
                    selection1.append(" or ");
                }
            }
        }

        getParameter(selection1, selectionArgs1);
        createAlertDialog(message.toString().trim());
    }


    @Override
    public void progressUpdate(int progress, int total) {
        mAlertMsg = getString(R.string.sending_items, String.valueOf(progress), String.valueOf(total));
        mProgressDialog.setMessage(mAlertMsg);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG:
                Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.PROGRESS_DIALOG", "show");

                mProgressDialog = new ProgressDialog(this);
                DialogInterface.OnClickListener loadingButtonListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.PROGRESS_DIALOG", "cancel");
                                dialog.dismiss();
                                mInstanceUploaderTask.cancel(true);
                                mInstanceUploaderTask.setUploaderListener(null);
                                finish();
                            }
                        };
                mProgressDialog.setTitle(getString(R.string.uploading_data));
                mProgressDialog.setMessage(mAlertMsg);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setButton(getString(R.string.cancel), loadingButtonListener);
                return mProgressDialog;
            case AUTH_DIALOG:
                Log.i(t, "onCreateDialog(AUTH_DIALOG): for upload of " + mInstancesToSend.length + " instances!");
                Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.AUTH_DIALOG", "show");
                AlertDialog.Builder b = new AlertDialog.Builder(this);

                LayoutInflater factory = LayoutInflater.from(this);
                final View dialogView = factory.inflate(R.layout.server_auth_dialog, null);

                // Get the server, username, and password from the settings
                SharedPreferences settings =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                String server = mUrl;
                if (server == null) {
                    Log.e(t, "onCreateDialog(AUTH_DIALOG): No failing mUrl specified for upload of " + mInstancesToSend.length + " instances!");
                    // if the bundle is null, we're looking for a formlist
                    String submissionUrl = getString(R.string.default_odk_submission);
                    server =
                            settings.getString(PreferencesActivity.KEY_SERVER_URL,
                                    getString(R.string.default_server_url))
                                    + settings.getString(PreferencesActivity.KEY_SUBMISSION_URL, submissionUrl);
                }

                final String url = server;

                Log.i(t, "Trying connecting to: " + url);

                EditText username = dialogView.findViewById(R.id.username_edit);
                String storedUsername = settings.getString(PreferencesActivity.KEY_USERNAME, getResources().getString(R.string.default_username));
                username.setText(storedUsername);

                EditText password = dialogView.findViewById(R.id.password_edit);
                String storedPassword = settings.getString(PreferencesActivity.KEY_PASSWORD, getResources().getString(R.string.default_password));
                password.setText(storedPassword);

                try {
                    Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.AUTH_DIALOG", "OK");

                    Uri u = Uri.parse(url);
                    WebUtils.addCredentials(username.getText().toString(), password.getText()
                            .toString(), u.getHost());

                    showDialog(PROGRESS_DIALOG);
                    mInstanceUploaderTask = new InstanceUploaderTask();

                    // register this activity with the new uploader task
                    mInstanceUploaderTask.setUploaderListener(InstanceUploaderActivity.this);

                    mInstanceUploaderTask.execute(mInstancesToSend);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan, gagal mengunduh list kuesioner.", Toast.LENGTH_SHORT).show();
                    Log.e(t, "Gagal mengunduh list kuesioner");
                }


//                b.setTitle(getString(R.string.server_requires_auth));
//                b.setMessage(getString(R.string.server_auth_credentials, url));
//                b.setView(dialogView);
//                b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.AUTH_DIALOG", "OK");
//                        EditText username = (EditText) dialogView.findViewById(R.id.username_edit);
//                        EditText password = (EditText) dialogView.findViewById(R.id.password_edit);
//
//                        Uri u = Uri.parse(url);
//                        WebUtils.addCredentials(username.getText().toString(), password.getText()
//                                .toString(), u.getHost());
//
//                        showDialog(PROGRESS_DIALOG);
//                        mInstanceUploaderTask = new InstanceUploaderTask();
//
//                        // register this activity with the new uploader task
//                        mInstanceUploaderTask.setUploaderListener(InstanceUploaderActivity.this);
//
//                        mInstanceUploaderTask.execute(mInstancesToSend);
//                    }
//                });
//                b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.AUTH_DIALOG", "cancel");
//                        finish();
//                    }
//                });
//
//                b.setCancelable(false);
//                return b.create();
        }
        return null;
    }


    @Override
    public void authRequest(Uri url, HashMap<String, String> doneSoFar) {
        if (mProgressDialog.isShowing()) {
            // should always be showing here
            mProgressDialog.dismiss();
        }

        // add our list of completed uploads to "completed"
        // and remove them from our toSend list.
        ArrayList<Long> workingSet = new ArrayList<Long>();
        Collections.addAll(workingSet, mInstancesToSend);
        if (doneSoFar != null) {
            Set<String> uploadedInstances = doneSoFar.keySet();
            Iterator<String> itr = uploadedInstances.iterator();

            while (itr.hasNext()) {
                Long removeMe = Long.valueOf(itr.next());
                boolean removed = workingSet.remove(removeMe);
                if (removed) {
                    Log.i(t, removeMe
                            + " was already sent, removing from queue before restarting task");
                }
            }
            mUploadedInstances.putAll(doneSoFar);
        }

        // and reconstruct the pending set of instances to send
        Long[] updatedToSend = new Long[workingSet.size()];
        for (int i = 0; i < workingSet.size(); ++i) {
            updatedToSend[i] = workingSet.get(i);
        }
        mInstancesToSend = updatedToSend;

        mUrl = url.toString();
        showDialog(AUTH_DIALOG);
    }


    private void createAlertDialog(String message) {
        Collect.getInstance().getActivityLogger().logAction(this, "createAlertDialog", "show");

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setTitle(getString(R.string.upload_results));
        mAlertDialog.setMessage(message);
        DialogInterface.OnClickListener quitListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE: // ok
                        Collect.getInstance().getActivityLogger().logAction(this, "createAlertDialog", "OK");
                        // always exit this activity since it has no interface
                        mAlertShowing = false;
                        finish();
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), quitListener);
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertShowing = true;
        mAlertMsg = message;
        mAlertDialog.show();
    }

    //baru Budi
    private ArrayList<String> err;

    public void getParameter(StringBuilder as, String[] ids) {
        err = new ArrayList<String>();
        JSONObject js = new JSONObject();
        //Log.d("UploadErrorDanIns ", ids[0]);

        String[] proj = new String[9];
        CapiPreference pref = CapiPreference.getInstance();
        String nimKortim = (String) pref.get(CapiKey.KEY_NIM_KORTIM);
        String nim = (String) pref.get(CapiKey.KEY_NIM);
        String nama = (String) pref.get(CapiKey.KEY_NAMA);
        Log.d("Perlakuan", "1 " + (nim.equals(nimKortim)));

        ArrayList<Notifikasi> notifs = new ArrayList<Notifikasi>();

        proj[0] = InstanceColumns.UUID;
        proj[1] = InstanceColumns.JR_FORM_ID;
        proj[2] = InstanceColumns.STATUS;
        proj[3] = InstanceColumns.STATUS_ISIAN;
        proj[4] = InstanceColumns._ID;
        proj[5] = InstanceColumns.DISPLAY_NAME;
        proj[6] = InstanceColumns.NIM;
        proj[7] = InstanceColumns.KORTIM;
        proj[8] = InstanceColumns.PERLAKUAN;
        Cursor results = null;
        try {
            results = getContentResolver().query(InstanceColumns.CONTENT_URI,
                    proj, as.toString(), ids, null);
            if (results.getCount() > 0) {
                JSONArray array = new JSONArray();
//                int j=0;
                results.moveToPosition(-1);
                while (results.moveToNext()) {
//                   UUIDS[j++]=results.getString(results.getColumnIndex(InstanceColumns.UUID));
                    JSONObject baru = new JSONObject();
                    Notifikasi barun = new Notifikasi();
//                    if(results.getString(results.getColumnIndex(InstanceColumns.NIM))!=null
//                            &&!results.getString(results.getColumnIndex(InstanceColumns.NIM)).equals("")){
//                        baru.put("nim",results.getString(results.getColumnIndex(InstanceColumns.NIM)));
//                        baru.put("perlakuan", InstanceProviderAPI.STATUS_CLEARED);
//                    }else {
//                        baru.put("nim",nim);
//                        baru.put("perlakuan", InstanceProviderAPI.STATUS_SUBMITTED);
//                    }
//                    if(results.getString(results.getColumnIndex(InstanceColumns.KORTIM))!=null
//                            &&!results.getString(results.getColumnIndex(InstanceColumns.KORTIM)).equals("")){
//                        baru.put("koortim",results.getString(results.getColumnIndex(InstanceColumns.KORTIM)));
//                        barun.setKortim(results.getString(results.getColumnIndex(InstanceColumns.KORTIM)));
//                    }else {
                    baru.put("koortim", nimKortim);
                    barun.setKortim(nimKortim);
//                    }
                    if (results.getString(results.getColumnIndex(InstanceColumns.PERLAKUAN)) != null) {
                        baru.put("perlakuan", results.getString(results.getColumnIndex(InstanceColumns.PERLAKUAN)));
                        barun.setStatus(results.getString(results.getColumnIndex(InstanceColumns.PERLAKUAN)));
                        Log.d("Perlakuan", "1");
                        Log.d("RAHADI", "NIM in Current Instance : " + results.getString(results.getColumnIndex(InstanceColumns.NIM)));
                        if (results.getString(results.getColumnIndex(InstanceColumns.NIM)) != null
                                && !results.getString(results.getColumnIndex(InstanceColumns.NIM)).equals("")) {
                            baru.put("nim", results.getString(results.getColumnIndex(InstanceColumns.NIM)));
                            barun.setNim(results.getString(results.getColumnIndex(InstanceColumns.NIM)));
                        } else {
                            baru.put("nim", nim);
                            barun.setNim(nim);
                        }
                    } else {
//                        if(results.getString(results.getColumnIndex(InstanceColumns.NIM))!=null
//                                &&!results.getString(results.getColumnIndex(InstanceColumns.NIM)).equals("")){
//                            if(results.getString(results.getColumnIndex(InstanceColumns.NIM)).equals(nim)){
//                                baru.put("perlakuan", InstanceProviderAPI.STATUS_SUBMITTED);
//                                barun.setStatus(InstanceProviderAPI.STATUS_SUBMITTED);
//                            }else{
//                                baru.put("perlakuan", InstanceProviderAPI.STATUS_CLEARED);
//                                barun.setStatus(InstanceProviderAPI.STATUS_CLEARED);
//                            }
//                            baru.put("nim",results.getString(results.getColumnIndex(InstanceColumns.NIM)));
//                            barun.setNim(results.getString(results.getColumnIndex(InstanceColumns.NIM)));
//
//                        }else {
                        baru.put("nim", nim);
                        barun.setNim(nim);
                        if (nim.equals(nimKortim)) {
                            baru.put("perlakuan", InstanceProviderAPI.STATUS_CLEARED);
                            barun.setStatus(InstanceProviderAPI.STATUS_CLEARED);
                        } else {
                            baru.put("perlakuan", InstanceProviderAPI.STATUS_SUBMITTED);
                            barun.setStatus(InstanceProviderAPI.STATUS_SUBMITTED);
                        }
//                        }
                    }
                    baru.put("nama", encode(nama));
                    barun.setNama(encode(nama));
                    baru.put("status_isian", results.getString(results.getColumnIndex(InstanceColumns.STATUS_ISIAN)));
                    barun.setStatus_isian(results.getString(results.getColumnIndex(InstanceColumns.STATUS_ISIAN)));
                    if (results.getString(results.getColumnIndex(InstanceColumns.UUID)) != null
                            && !results.getString(results.getColumnIndex(InstanceColumns.UUID)).equals("")) {
                        baru.put("unique_id_instance", results.getString(results.getColumnIndex(InstanceColumns.UUID)));
                        barun.setUnique_id_instance(results.getString(results.getColumnIndex(InstanceColumns.UUID)));
                    }
                    baru.put("form_id", results.getString(results.getColumnIndex(InstanceColumns.JR_FORM_ID)));
                    barun.setForm_id(results.getString(results.getColumnIndex(InstanceColumns.JR_FORM_ID)));
                    baru.put("fileName", results.getString(results.getColumnIndex(InstanceColumns.DISPLAY_NAME)));
                    barun.setFilename(results.getString(results.getColumnIndex(InstanceColumns.DISPLAY_NAME)));
                    notifs.add(barun);
                    array.put(baru);
                }
                js.put("data", array);
            } else {
                Log.d("UploadErrorDanIns ", "tidak ada result");
            }
        } catch (Exception ex) {
            Log.d("Json", "" + ex);
        } finally {
            if (results != null) {
                results.close();
            }
        }
//        ArrayList<Notifikasi> uids= new ArrayList<Notifikasi>();
//        ItemsetDbAdapter dbas= new ItemsetDbAdapter();
//        dbas.open();
//        for(int i=0;i<ids.length;i++){
//            Notifikasi bar=dbas.getNotifikasibyId(UUIDS[i]);
//            if(bar!=null){
//                uids.add(bar);
//            }
//        }
//
//        dbas.close();
//        try {
//
//            if (ids.length > 0) {
//                JSONArray array = new JSONArray();
//                for(int i=0;i<uids.size();i++){
//                    Notifikasi a= uids.get(i);
//                    JSONObject baru = new JSONObject();
//                    baru.put("nim",a.getNim());
//                    baru.put("koortim",a.getKortim());
//                    baru.put("status_isian",a.getStatus_isian());
//                    baru.put("unique_id_instance", a.getUnique_id_instance());
//                    baru.put("perlakuan", InstanceProviderAPI.STATUS_CLEARED);
//                    baru.put("form_id",a.getForm_id());
//                    baru.put("fileName",a.getFilename());
//                    array.put(baru);
//
//                }
//                js.put("data",array);
//            }
//            else{
//                Log.d("UploadErrorDanIns ", "tidak ada result");
//            }
//        }catch (JSONException ex){
//            Log.d("UploadErrorDanIns",""+ex);
//        }
        Log.d("UploadErrorDanIns ", js.toString());
        upoaderrordanins(js.toString(), notifs);
    }

    public void upoaderrordanins(final String isi, final ArrayList<Notifikasi> notifs) {
        //String tag_json_obj = "json_obj_req";
        String urlpostnotif = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PreferencesActivity.KEY_HOST,
                        Collect.host) + "/post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlpostnotif, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("UploadErrorDanIns", "" + response);
                try {
                    JSONObject as = new JSONObject(response);
                    parseJson(as);
                } catch (JSONException a) {
                    Log.d("UploadErrorDanIns cat ", "" + a);
                }
                onSuccessSend(notifs);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("UploadErrorDanIns Err ", "" + error);
                onFailedSend(notifs);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("json2", isi);

                return params;
            }

        };
        Collect.getInstance2().addToRequestQueue(strReq);
    }

    public void onFailedSend(ArrayList<Notifikasi> notifs) {
        ItemsetDbAdapter dbas = new ItemsetDbAdapter();
        dbas.open();
        dbas.addNotifikasiGagal(notifs);
        dbas.close();
    }

    public void onSuccessSend(ArrayList<Notifikasi> notifs) {
        ItemsetDbAdapter dbas = new ItemsetDbAdapter();
        dbas.open();
        dbas.deleteNotifikasiGagalS(notifs);
        dbas.close();
    }

    public void parseJson(JSONObject response) {
        try {
            if (response.getString("success").equals("0")) {
                JSONArray feedArray = response.getJSONArray("gagal");
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    err.add(feedObj.getString("gagal1"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String encode(String text) {
        String ada = text;
        return ada.replace("'", "");
    }

    private void onsuccessall(String message, String[] ids) {

    }
}
