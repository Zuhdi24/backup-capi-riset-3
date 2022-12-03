package org.odk.collect.android.pkl.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.UnitUsahaPariwisata;
import org.odk.collect.android.pkl.object.SampelRuta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isfann on 1/21/2017.
 */

public class RestoreTask extends AsyncTask<String, Void, String> {
    Context context;
    Gson gson;
    public static final String LAHAN = "lahan";
    public static final String RUTA = "ruta";
    public static final String SAMPEL = "sampel";
    public static final String BS = "bs";
    ProgressDialog progressDialog;

    public RestoreTask(Context context) {
        this.context = context;
        gson = new Gson();
    }

    @Override
    protected String doInBackground(String... params) {
        String message;
        if (params.length == 2) {
            message = restoreAll(params[0], params[1]);
        } else {
            message = restoreAll(params[0], params[1], params[2]);
        }

        return message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Mengembalikan...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    public String restoreAll(String json, String mode) {

        if (mode.equals(RUTA)) {
            ArrayList<UnitUsahaPariwisata> listRuta = gson.fromJson(json, new TypeToken<List<UnitUsahaPariwisata>>() {
            }.getType());
            Log.d("cobaUpload", gson.toJson(listRuta));
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.restoreAllRuta(listRuta)) {
                return "Berhasil mengembalikan data Rumah Tangga";
            } else {
                return "GAGAL mengembalikan data Rumah Tangga";
            }
        } else if (mode.equals(SAMPEL)) {
            ArrayList<SampelRuta> listSampel = new ArrayList<>();
            if (gson.fromJson(json, new TypeToken<ArrayList<SampelRuta>>() {
            }.getType()) != null) {
                listSampel.addAll(gson.fromJson(json, new TypeToken<ArrayList<SampelRuta>>() {
                }.getType()));
            }
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.restoreAllSampel(listSampel)) {
                return "Berhasil mengembalikan data Sampel";
            } else {
                return "GAGAL mengembalikan data Sampel";
            }
        } else if (mode.equals(BS)) {
            ArrayList<BlokSensus> listBs = gson.fromJson(json, new TypeToken<List<BlokSensus>>() {
            }.getType());
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.restoreAllBs(listBs)) {
                return "Berhasil mengembalikan data Blok Sensus";
            } else {
                return "GAGAL mengembalikan data Blok Sensus";
            }
        } else if (mode.equals(LAHAN)) {
            if (gson.fromJson(json, new TypeToken<ArrayList<SampelRuta>>() {
            }.getType()) != null) {
            }
            DatabaseSampling db = DatabaseSampling.getInstance();

        } else {
            return "GAGAL";
        }
        return json;
    }

    @NonNull
    public String restoreAll(String kodeBs, String json, String mode) {

        if (mode.equals(RUTA)) {
            Log.d( "cobaUpload ruta", "sini restore Ruta");
            Log.e("restore", json);
            ArrayList<UnitUsahaPariwisata> listRuta = gson.fromJson(json, new TypeToken<List<UnitUsahaPariwisata>>(){}.getType());


            DatabaseSampling db = DatabaseSampling.getInstance();
            String noBs = db.getBlokSensusByKode(kodeBs).getNoBs();
            if (db.restoreAllRuta(kodeBs, listRuta)) {
                return "Berhasil mengembalikan data Rumah Tangga " + noBs;
            } else {
                return "GAGAL mengembalikan data Rumah Tangga";
            }
        } else if (mode.equals(SAMPEL)) {
            ArrayList<SampelRuta> listSampel = gson.fromJson(json, new TypeToken<List<SampelRuta>>() {
            }.getType());
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.restoreAllSampel(kodeBs, listSampel)) {
                return "Berhasil mengembalikan data Sampel";
            } else {
                return "GAGAL mengembalikan data Sampel";
            }
        } else if (mode.equals(BS)) {
            ArrayList<BlokSensus> listBs = gson.fromJson(json, new TypeToken<List<BlokSensus>>() {
            }.getType());
            DatabaseSampling db = DatabaseSampling.getInstance();
            if (db.restoreAllBs(listBs)) {
                return "Berhasil mengembalikan data Blok Sensus";
            } else {
                return "GAGAL mengembalikan data Blok Sensus";
            }
        } else {
            return "Restore Gagal";
        }
    }

}
