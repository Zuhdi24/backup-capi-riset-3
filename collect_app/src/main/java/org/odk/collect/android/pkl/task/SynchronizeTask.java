package org.odk.collect.android.pkl.task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onesignal.OneSignal;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.activity.ActivityListBlokSensus;
import org.odk.collect.android.pkl.activity.ActivityListRumahTangga;
import org.odk.collect.android.pkl.activity.LoginActivity;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.database.DatabaseHandler;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.network.VolleySingleton;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.JumlahRutaBS;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.object.SampelRuta;
import org.odk.collect.android.pkl.object.UUIDContainer;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @author isfann
 */

public class SynchronizeTask extends AsyncTask<String, String, String> {

    private static final String TAG = "SynchronizeTask";

    //system feedback constant
    public static final String TOAST_LONG = "toast_long";
    public static final String TOAST_SHORT = "toast_short";
    public static final String PD_MESSAGE = "pd_message";

    //function methods
    public static final String MODE_SYNC_ALL = "sync_all";
    public static final String MODE_SYNC_BS = "sync_bs";
    public static final String MODE_SEND_SAMPLE = "send_sample";
    public static final String MODE_FINALIZE_BS = "finalize_bs";
    public static final String MODE_RETURN_BS = "return_bs";
    public static final String MODE_SYNC_LOGOUT = "sync_logout";
    public static final String MODE_SYNC_LAHAN = "sync_lahan";

    //requests parameter
    public static final String GET_ALL_BS = "gab";
    public static final String GET_ALL_SAMPLE = "gas";
    public static final String SEND_SAMPLE_BS = "ssb";
    public static final String CHANGE_BS_STATUS = "cbs";
    public static final String SYNC_RUTA_PCL = "srp";
    public static final String UPDATE_JUMLAH_RUTA = "ujr";
    public static final String GET_ALL_LAHAN = "gal";

    SharedPreferences preference;
    String serviceBaseUrl, serviceUrl;


    private static AppCompatActivity activity;
    ProgressDialog progressDialog;
    Gson gson;
    String nim, nimKortim;
    DatabaseSampling db;
    DefaultRetryPolicy drp;

    final DBhandler dba = DBhandler.getInstance();
    final DatabaseSampling dbs = DatabaseSampling.getInstance();
    DatabaseHandler dbProblem;

    public SynchronizeTask(AppCompatActivity activity) {
        this.activity = activity;
        dbProblem = DatabaseHandler.getInstance(activity);
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        CapiPreference pref = CapiPreference.getInstance();
        nim = (String) pref.get(CapiKey.KEY_NIM);
        nimKortim = (String) pref.get(CapiKey.KEY_NIM_KORTIM);
        preference = PreferenceManager.getDefaultSharedPreferences(activity);
        //TODO COBA
        serviceBaseUrl = preference.getString("host", "https://capi62.stis.ac.id/web-service-62/public");
        serviceUrl = serviceBaseUrl + "/listingr4";
        drp = new DefaultRetryPolicy(8000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        db = DatabaseSampling.getInstance();
    }

    public boolean getAllBs() {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                serviceUrl,
                requestFuture,
                requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", GET_ALL_BS);
                params.put("nimKortim", nimKortim);
                params.put("nim", nim);
                Log.d(TAG, "berhasil kok getnya");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            String response = new RestoreTask(activity).restoreAll(requestFuture.get(), RestoreTask.BS);
            publishProgress(TOAST_SHORT, response);

            Log.e("MASUK", requestFuture.get());

            if (response.toLowerCase().contains("gagal")) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "getAllBs: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "getAllBs: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "getAllBs: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "getAllBs: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
                publishProgress(TOAST_SHORT, "Terjadi Kesalahan Jaringan / Server");
            }
            return false;
        }

    }

    public boolean getAllSample() {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                serviceUrl,
                requestFuture,
                requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", GET_ALL_SAMPLE);
                params.put("nimKortim", nimKortim);
                params.put("nim", nim);
                return params;
            }
        };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            String response = new RestoreTask(activity).restoreAll(requestFuture.get(), RestoreTask.SAMPEL);
            publishProgress(TOAST_SHORT, response);
            if (response.toLowerCase().contains("gagal")) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "getAllSample: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "getAllSample: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "getAllSample: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "getAllSample: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi Kesalahan Jaringan / Server");
            return false;
        }

    }

    public boolean sendSampleBs(final String kodeBs) {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        ArrayList<SampelRuta> listSample = db.getSampelRuta(kodeBs);

        while (listSample.isEmpty()) {
            Log.d(TAG, "sendSampleBs: list sampel empty");
            listSample = db.getSampelRuta(kodeBs);
        }

        final ArrayList<SampelRuta> finalListSample = listSample;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                serviceUrl,
                requestFuture,
                requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                ArrayList<UUIDContainer> listUUID = new ArrayList<>();
                for (SampelRuta sr : finalListSample) {
                    listUUID.add(new UUIDContainer(sr.getKodeUUP()));
                }
                params.put("k", SEND_SAMPLE_BS);
                params.put("kodeBs", kodeBs);
                params.put("json", gson.toJson(listUUID));
                params.put("nimKortim", nimKortim);
                params.put("nim", nim);
                Log.d(TAG, "List Sample" + gson.toJson(finalListSample));
                Log.d(TAG, "getParams: " + gson.toJson(listUUID));
                return params;
            }
        };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            if ("sukses".equals(requestFuture.get())) {
                db.updateStatusBlokSensus(kodeBs, BlokSensus.FLAG_BS_SAMPLE_UPLOADED);
                publishProgress(TOAST_SHORT, "Data Sampel Berhasil Dikirim");
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "sendSampleBs: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "sendSampleBs: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "sendSampleBs: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "sendSampleBs: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi Kesalahan Jaringan / Server");
            return false;
        }
    }

    public boolean changeBsStatus(final String kodeBs, final String status) {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                serviceUrl,
                requestFuture,
                requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", CHANGE_BS_STATUS);
                params.put("nimKortim", nimKortim);
                params.put("nim", nim);
                params.put("kodeBs", kodeBs);
                params.put("status", status);
                return params;
            }
        };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            if ("sukses".equals(requestFuture.get())) {
                if (db.updateStatusBlokSensus(kodeBs, status)) {
                    publishProgress(TOAST_SHORT, "Status Blok Sensus Berhasil Diubah");
                    return true;
                } else {
                    publishProgress(TOAST_SHORT, "Status Blok Sensus GAGAL Diubah");
                    return false;
                }
            } else {
                publishProgress(TOAST_SHORT, "Status Blok Sensus GAGAL Diubah");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "changeBsStatus: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "changeBsStatus: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "changeBsStatus: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "changeBsStatus: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi Kesalahan Jaringan / Server");
            return false;
        }


    }

    public boolean syncRuta(final String kodeBs) {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        final ArrayList<UnitUsahaPariwisata> listRutaToUpload = db.getListRutaToUpload(kodeBs);

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        serviceUrl,
                        requestFuture,
                        requestFuture) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("k", SYNC_RUTA_PCL);
                        params.put("kodeBs", kodeBs);
                        params.put("json", gson.toJson(listRutaToUpload));

                        params.put("nimKortim", nimKortim);
                        params.put("nim", nim);
                        Log.d("cobaUpload ruta", gson.toJson(listRutaToUpload).toString());
                        Log.d("cobaUpload ruta", kodeBs + nim + nimKortim);
                        return params;
                    }
                };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            Log.d("cobaUpload ruta", "sini");
            String response = new RestoreTask(activity).restoreAll(kodeBs, requestFuture.get(), RestoreTask.RUTA);
            publishProgress(TOAST_SHORT, response);

            Log.d("cobaa", response.toLowerCase().toString());

            if (response.toLowerCase().contains("gagal")) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "syncRuta: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "syncRuta: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "syncRuta: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "syncRuta: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi kesalahan Jaringan / Server");
            return false;
        }


    }

    public boolean updateJumlahRuta(final String kodeBs) {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        final JumlahRutaBS jumlah_rt_update = db.getJumlahRutaBS(kodeBs);
        final String json = "[" + gson.toJson(jumlah_rt_update) + "]";

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        serviceUrl,
                        requestFuture,
                        requestFuture) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("k", UPDATE_JUMLAH_RUTA);
                        params.put("kodeBs", kodeBs);
                        params.put("json", json);

                        Log.d("cobaUpload jumlahruta", kodeBs + " dan " + json);
                        return params;
                    }
                };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            Log.d("cobaUpload ruta", "sini");
//            String response = new RestoreTask(activity).restoreAll(kodeBs, requestFuture.get(), RestoreTask.RUTA);
//            publishProgress(TOAST_SHORT, response);

//            Log.d( "cobaa", response.toLowerCase().toString()  );
            if ("sukses".equals(requestFuture.get())) {
                Log.d(TAG, "updateJumlahRuta: SUKSES");
                publishProgress(TOAST_SHORT, "Update Jumlah Ruta berhasil");
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "syncRuta: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "syncRuta: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "syncRuta: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "syncRuta: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi kesalahan Jaringan / Server");
            return false;
        }


    }

    public boolean getAllLahan() {
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
//        final ArrayList<DataTanah> listLahanToUpload = db.getListLahanToUpload(noSegmen);

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        serviceUrl,
                        requestFuture,
                        requestFuture) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("k", GET_ALL_LAHAN);
                        params.put("nimKortim", nimKortim);
                        params.put("nim", nim);
//                        Log.d( "cobaUpload ruta", gson.toJson( listRutaToUpload ).toString() );
//                        Log.d( "cobaUpload ruta", kodeBs + nim +nimKortim );
                        return params;
                    }
                };

        request.setRetryPolicy(drp);

        VolleySingleton.getInstance(activity).addToRequestQueue(request);

        try {
            String response = new RestoreTask(activity).restoreAll(requestFuture.get(), RestoreTask.LAHAN);
            publishProgress(TOAST_SHORT, response);
            if (response.toLowerCase().contains("gagal")) {
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError ve = (VolleyError) e.getCause();
                Log.e(TAG, "getAllSample: ve = " + ve.toString() + drp.getCurrentTimeout());
                if (ve.networkResponse != null) {
                    Log.e(TAG, "getAllSample: ve.networkResponse = " + ve.networkResponse.toString());
                    Log.e(TAG, "getAllSample: ve.networkResponse.statusCode = " + ve.networkResponse.statusCode);
                    Log.e(TAG, "getAllSample: ve.networkResponse.data = " + new String(ve.networkResponse.data));
                }
            }
            publishProgress(TOAST_SHORT, "Terjadi Kesalahan Jaringan / Server");
            return false;
        }


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memuat...");
        progressDialog.show();

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 20000);
    }

    @Override
    protected void onPostExecute(String s) {
        switch (s) {
            case MODE_SYNC_ALL:
                ((ActivityListBlokSensus) activity).showAllBs();
                break;
            case MODE_SYNC_BS:
                ((ActivityListRumahTangga) activity).showAllRuta();
                break;
            case MODE_SEND_SAMPLE:
                break;
            case MODE_FINALIZE_BS:
                ((ActivityListRumahTangga) activity).showAllRuta();
                break;
            case MODE_RETURN_BS:
                break;
            case MODE_SYNC_LOGOUT:
                if (!isCancelled()) {
                    Intent toLogin = new Intent(this.activity, LoginActivity.class);
                    toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.activity.startActivity(toLogin);
                    this.activity.finish();
                }
                break;
            case MODE_SYNC_LAHAN:
                ((ActivityListRumahTangga) activity).showAllRuta();
                break;

        }
        progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        switch (values[0]) {
            case TOAST_LONG:
                Toast.makeText(activity, values[1], Toast.LENGTH_LONG).show();
                break;
            case TOAST_SHORT:
                Toast.makeText(activity, values[1], Toast.LENGTH_SHORT).show();
                break;
            case PD_MESSAGE:
                progressDialog.setMessage(values[1]);
                break;

        }
    }

    @Override
    protected String doInBackground(String... params) {
        BlokSensus bs;
        boolean stop = false;
        switch (params[0]) {
            case MODE_SYNC_ALL:
                loop:
                for (BlokSensus blokSensus : db.getListBlokSensus()) {
                    Log.d("sychronizeTask", blokSensus.getKodeBs());
                    if (BlokSensus.FLAG_BS_SAMPLED.equals(blokSensus.getStatus())) {
                        publishProgress(PD_MESSAGE, "Mengirim data Sample " + blokSensus.getNoBs() + " ...");
                        if (!sendSampleBs(blokSensus.getKodeBs())) {
                            stop = true;
                            break loop;
                        }
                    }

                    publishProgress(PD_MESSAGE, "Mengambil data Rumah Tangga Blok Sensus " + blokSensus.getNoBs() + " ...");
                    if (!syncRuta(blokSensus.getKodeBs())) {
                        stop = true;
                        break loop;
                    }
                }

                if (stop) {
                    break;
                }

                publishProgress(PD_MESSAGE, "Memperbarui Data Blok Sensus...");
                if (!getAllBs()) {
                    break;
                }

                publishProgress(PD_MESSAGE, "Mengambil Data Sample...");
                if (!getAllSample()) {
                    break;
                }

                publishProgress(PD_MESSAGE, "Resolving BS...");
                for (BlokSensus blokSensus : db.getListBlokSensus()) {
                    if (BlokSensus.FLAG_BS_SAMPLED.equals(blokSensus.getStatus()) ||
                            BlokSensus.FLAG_BS_SAMPLE_UPLOADED.equals(blokSensus.getStatus())) {
                        if (blokSensus.getKodeBs().substring(2, 4).equals("02")) {
                            if (db.getRutaTerpilih(blokSensus.getKodeBs()).size() != CapiKey.N_SAMPLE_D3) {
                                db.updateStatusBlokSensus(blokSensus.getKodeBs(), BlokSensus.FLAG_BS_READY);
                            }
                        } else {
                            if (db.getRutaTerpilih(blokSensus.getKodeBs()).size() > CapiKey.N_SAMPLE_D4) {
                                db.updateStatusBlokSensus(blokSensus.getKodeBs(), BlokSensus.FLAG_BS_READY);
                            }
                        }
                    }
                }
                break;
            case MODE_SYNC_BS:
                bs = db.getBlokSensusByKode(params[1]);
                publishProgress(PD_MESSAGE, "Sinkronisasi Rumah Tangga Blok Sensus " + bs.getNoBs() + " ...");
                if (!syncRuta(params[1])) {
                    break;
                }
                if (!updateJumlahRuta(params[1])) {
                    break;
                }
                publishProgress(PD_MESSAGE, "Mengambil Data Sampel...");
                getAllSample();
                break;
            case MODE_SEND_SAMPLE:
                publishProgress(PD_MESSAGE, "Mengirim Data Sampel...");
                sendSampleBs(params[1]);
                break;
            case MODE_FINALIZE_BS:
                bs = db.getBlokSensusByKode(params[1]);
                publishProgress(PD_MESSAGE, "Sinkronisasi Rumah Tangga Blok Sensus " + bs.getNoBs() + " ...");
                if (!syncRuta(params[1])) {
                    break;
                }
                publishProgress(PD_MESSAGE, "Finalisasi Blok Sensus " + bs.getNoBs() + " ...");
                changeBsStatus(params[1], BlokSensus.FLAG_BS_READY);
                break;
            case MODE_RETURN_BS:
                bs = db.getBlokSensusByKode(params[1]);
                publishProgress(PD_MESSAGE, "Mengembalikan Blok Sensus " + bs.getNoBs() + " ...");
                changeBsStatus(params[1], BlokSensus.FLAG_BS_PROSES_LISTING);
                break;
            case MODE_SYNC_LOGOUT:
                loop:
                for (BlokSensus blokSensus : db.getListBlokSensus()) {
                    if (BlokSensus.FLAG_BS_SAMPLED.equals(blokSensus.getStatus())) {
                        if (!sendSampleBs(blokSensus.getKodeBs())) {
                            stop = true;
                            break loop;
                        }
                        publishProgress(PD_MESSAGE, "Mengirim data Sample " + blokSensus.getNoBs() + " ...");
                    }

                    if (!syncRuta(blokSensus.getKodeBs())) {
                        stop = true;
                        break loop;
                    }
                    publishProgress(PD_MESSAGE, "Mengambil data Rumah Tangga Blok Sensus " + blokSensus.getNoBs() + " ...");
                }

                if (stop) {
                    cancel(true);
                    break;
                }

                if (!getAllBs()) {
                    cancel(true);
                    break;
                }
                publishProgress(PD_MESSAGE, "Memperbarui Data Blok Sensus...");

                if (!getAllSample()) {
                    cancel(true);
                    break;
                }
                publishProgress(PD_MESSAGE, "Mengambil Data Sample...");

                for (BlokSensus blokSensus : db.getListBlokSensus()) {
                    if (BlokSensus.FLAG_BS_SAMPLED.equals(blokSensus.getStatus()) || BlokSensus.FLAG_BS_SAMPLE_UPLOADED.equals(blokSensus.getStatus())) {
                        if (db.getRutaTerpilih(blokSensus.getKodeBs()).size() != 10) {
                            db.updateStatusBlokSensus(blokSensus.getKodeBs(), BlokSensus.FLAG_BS_READY);
                        }
                    }
                }
                publishProgress(PD_MESSAGE, "Resolving BS...");

                CapiPreference preference = CapiPreference.getInstance();
                preference.clearCapiData();
                dba.dropTable();
                dbs.LogoutDropAllTable();
                dbProblem.dropAllTables();
                Collect.setOldDirectory();

                OneSignal.deleteTag("nim");
                OneSignal.deleteTag("isKoor");
                OneSignal.clearOneSignalNotifications();
                break;


        }

        return params[0];
    }

}