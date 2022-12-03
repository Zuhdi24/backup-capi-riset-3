package org.odk.collect.android.pkl.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.pkl.activity.LoginActivity;
import org.odk.collect.android.pkl.database.DBhandler;
import org.odk.collect.android.pkl.database.DatabaseSampling;
import org.odk.collect.android.pkl.network.HttpHandler;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.ProgressBlokPcl;
import org.odk.collect.android.pkl.preference.CapiKey;
import org.odk.collect.android.pkl.preference.CapiPreference;
import org.odk.collect.android.pkl.preference.StaticFinal;

/**
 * mendapatkan data profil dan data pekerjaan
 * @author Tim PKL
 */

public class LoginTask extends AsyncTask<String, Void, String> {

    public interface LoginListener {
        void onLoginFinish(String result);
    }

    public static final String LOGIN_SUCCESS = "success";
    public static final String LOGIN_FAIL_URL = "fail_url";
    public static final String LOGIN_FAIL_USER = "fail_user";
    public static final String LOGIN_FAIL_EXTRA = "fail_extra";

    private final LoginListener loginListener;

    public LoginTask(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @Override
    protected String doInBackground(String... param) {
        String loginUrl = param[0];
        String extraUrl = param[1];
        String password = param[2];

        // Making a request to url and getting response
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(loginUrl);
        String jsonStrExtra = sh.makeServiceCall(extraUrl);
        try{
            Log.d("jsonStrExtra", jsonStrExtra);
            Log.d("jsonStr", jsonStr);
        } catch (Exception e) {
            Log.e("LoginActivity", "Couldn't get json from server.");
            return LOGIN_FAIL_URL;
        }

        // jika isi login JSON kosong
        if (jsonStr == null) {
            Log.e("LoginActivity", "Couldn't get json from server.");
            return LOGIN_FAIL_USER;
        }

        try {
            // Proses login data
            JSONObject jsonObj = new JSONObject(jsonStr);
            String status = jsonObj.getString(StaticFinal.JSON_STATUS);

            if (status.equals(LOGIN_FAIL_USER)) return LOGIN_FAIL_USER;

            String nim = jsonObj.getString(StaticFinal.JSON_NIM);
            String nama = jsonObj.getString(StaticFinal.JSON_NAMA);
            String namaTim = jsonObj.getString(StaticFinal.JSON_NAMA_TIM);
            boolean isKoorTim = jsonObj.optBoolean(StaticFinal.JSON_IS_KOOR);
            String avatar = jsonObj.getString(StaticFinal.JSON_AVATAR);

            String jabatan = isKoorTim? StaticFinal.JSON_KORTIM : StaticFinal.JSON_PENCACAH;
            String passKortim;
            String namaKortim;
            String nomorKortim;
            String nimKortim;
            String idJabatan;
            if (isKoorTim) {
                Log.d("is koor", "koor");
                passKortim = password;
                nomorKortim = StaticFinal.JSON_NO_KORTIM;
                idJabatan = StaticFinal.JABATAN_KORTIM_ID;
                namaKortim = nama;
                nimKortim = nim;
            } else {
                Log.d("is koor", "pcl");
                idJabatan = StaticFinal.JABATAN_PENCACAH_ID;
                nimKortim = jsonObj.getString(StaticFinal.JSON_NIM_KOOR);
                passKortim = jsonObj.getString(StaticFinal.JSON_PASS_KOOR);
                namaKortim = jsonObj.getString(StaticFinal.JSON_NAMA_KOOR);
                nomorKortim = jsonObj.getString(StaticFinal.JSON_NOMOR_KOOR);
                Log.d("namaKoorJSon", jsonObj.getString(StaticFinal.JSON_NAMA_KOOR));
                Log.d("nimKoorJSon", jsonObj.getString(StaticFinal.JSON_NIM_KOOR));
                Log.d("nomorTeleponJSon", jsonObj.getString(StaticFinal.JSON_NOMOR_KOOR));
            }

            // Simpan data profil pada SharedPref
            CapiPreference pref = CapiPreference.getInstance();
            pref.save(CapiKey.IS_LOGIN, true);
            pref.save(CapiKey.KEY_NIM_KORTIM, nimKortim);
            pref.save(CapiKey.KEY_NAMA_KORTIM, namaKortim);
            pref.save(CapiKey.KEY_PASS_KORTIM, passKortim);
            pref.save(CapiKey.KEY_NOMOR_KORTIM, nomorKortim);
            Log.d("nomorTeleponPref", nomorKortim);
            pref.save(CapiKey.KEY_NIM, nim);
            pref.save(CapiKey.KEY_NAMA, nama);
            pref.save(CapiKey.KEY_PASSWORD, password);
            pref.save(CapiKey.KEY_JABATAN, jabatan);
            pref.save(CapiKey.KEY_NAMA_TIM, namaTim);
            pref.save(CapiKey.KEY_ID_JABATAN, idJabatan);
            pref.save(CapiKey.KEY_AVATAR, avatar);
            Log.d("key_ID_JABATAN",idJabatan+ " "+pref.get(CapiKey.KEY_ID_JABATAN));

            // Proses login tambahan
            JSONObject jsonExtra = new JSONObject(jsonStrExtra);
            if (jsonExtra.isNull("wilayah_kerja")
                    && jsonExtra.isNull("anggota_tim")) return LOGIN_FAIL_EXTRA;

            //jika bukan kortim
            if (!isKoorTim) {
                DBhandler dbpcl = DBhandler.getInstance();
                JSONArray jsonArray = jsonExtra.getJSONArray("wilayah_kerja");
                DatabaseSampling ds = DatabaseSampling.getInstance();

                int jumlah_rt = 0;
                /*
                SANDY filter download kues
                 */
                String nama_kabupaten = "";
                String sls = "BLOM ADA SLSNYA CUY";
                for (int j = 0; j < jsonArray.length(); j++) {
                    // Belum diperlukan
//                    String nks = jsonArray.getJSONObject(j).getString("nks");
//                    String sls = jsonArray.getJSONObject(j).getString("sls");

                    String kode_bs = jsonArray.getJSONObject(j).getString("kode_bs");
                    String kode_desa = jsonArray.getJSONObject(j).getString("kode_desa");
                    String kode_kec = jsonArray.getJSONObject(j).getString("kode_kecamatan");
                    String kode_kab = jsonArray.getJSONObject(j).getString("kode_kabupaten");
                    String nama_desa = jsonArray.getJSONObject(j).getString("nama_desa");
                    String nama_kecamatan = jsonArray.getJSONObject(j).getString("nama_kecamatan");
                           nama_kabupaten = jsonArray.getJSONObject(j).getString("nama_kabupaten");
                    String noBs = jsonArray.getJSONObject(j).getString("nama_bs");
                    String jumlah = jsonArray.getJSONObject(j).getString("jumlah");
                    int beban_cacah = jsonArray.getJSONObject(j).getInt("beban_cacah");
                    String statusBs = jsonArray.getJSONObject(j).getString("status");
                    String stratifikasi = jsonArray.getJSONObject(j).getString("stratifikasi");
//                    String jumlahRTLama = jsonArray.getJSONObject(j).getString("jumlahRTLama");
//                    String jumlahRTBaru = jsonArray.getJSONObject(j).getString("jumlahRTBaru");
//                    String jumlahRTInternet = jsonArray.getJSONObject(j).getString("jumlahRTInternet");
//                    String nks = "81";
//                    sls = "B108";
//                    int jmlhRutaBaru = 0;
                    String progres;
                    jumlah_rt += beban_cacah;
                    if (jumlah.equalsIgnoreCase("null")) {
                        progres = "0/" + beban_cacah;
                    } else {
                        progres = jumlah + "/" + beban_cacah;
                    }
                    Log.e("Jumlah", jumlah);
                    BlokSensus bs = new BlokSensus(kode_bs, "32", kode_kab, kode_kec, kode_desa, stratifikasi, noBs, sls, nama_kabupaten,
                            nama_kecamatan, nama_desa, nim, statusBs);

                    ds.deleteBS(bs.getKodeBs());
                    ds.insertBlokSensus(bs);

                    ProgressBlokPcl pb = new ProgressBlokPcl(noBs, progres);
                    dbpcl.addProgresBs(pb);
                }
                Log.e("BEBAN BS", String.valueOf(jsonArray.length()));
                Log.e("BEBAN RUTA", String.valueOf(jumlah_rt));
                /*
                SANDY filter download kues
                 */
                pref.save(CapiKey.KEY_NAMA_KABUPATEN,nama_kabupaten);
                pref.save(CapiKey.KEY_JUMLAH_BS, String.valueOf(jsonArray.length()));
                pref.save(CapiKey.KEY_JUMLAH_RUTA, String.valueOf(jumlah_rt));


            } else {  //kalau kortim
                DBhandler dbkortim = DBhandler.getInstance();
                JSONArray jsonArray = jsonExtra.getJSONArray("anggota_tim");
                int bebanBStim = 0;
                int bebanRutaTim = 0;
                String nama_kabupaten = "";
                String sls = "BELOM ADA SLSNYA CUY";
                for (int j = 0; j < jsonArray.length(); j++) {
                    String namaAnggota = jsonArray.getJSONObject(j).getString("nama");
                    String nimAnggota = jsonArray.getJSONObject(j).getString("nim");
                    String totalProgres = jsonArray.getJSONObject(j).getString("total_progress");
                    String nomorTelepon = jsonArray.getJSONObject(j).getString("no_hp");
                    AnggotaTim at = new AnggotaTim(nimAnggota, namaAnggota, totalProgres, nomorTelepon);
                    dbkortim.addAnggota(at);

                    JSONArray wilKerja = jsonArray.getJSONObject(j).getJSONArray("wilayah_kerja");
                    DatabaseSampling ds = DatabaseSampling.getInstance();
                    for (int k = 0; k < wilKerja.length(); k++) {
                        String kode_bs = wilKerja.getJSONObject(k).getString("kode_bs");
                        String kode_desa = wilKerja.getJSONObject(k).getString("kode_desa");
                        String noBs = wilKerja.getJSONObject(k).getString("nama_bs");
                        String kode_kec = wilKerja.getJSONObject(k).getString("kode_kecamatan");
                        String kode_kab = wilKerja.getJSONObject(k).getString("kode_kabupaten");
                        String nama_desa = wilKerja.getJSONObject(k).getString("nama_desa");
                        String nama_kecamatan = wilKerja.getJSONObject(k).getString("nama_kecamatan");
                               nama_kabupaten = wilKerja.getJSONObject(k).getString("nama_kabupaten");
                        int beban_cacah = wilKerja.getJSONObject(k).getInt("beban_cacah");
                        String statusBs = wilKerja.getJSONObject(k).getString("status");
                        String stratifikasi = wilKerja.getJSONObject(k).getString("stratifikasi");
//                        String jumlahRTLama = wilKerja.getJSONObject(k).getString("jumlahRTLama");
//                        String jumlahRTBaru = wilKerja.getJSONObject(k).getString("jumlahRTBaru");
//                        String jumlahRTInternet = wilKerja.getJSONObject(k).getString("jumlahRTInternet");
//                        String nks = "81";
//                        sls = "001B";
//                        int jmlhRutaLama = jsonArray.getJSONObject(j).getInt("jumlahRuta");
//                        int jmlhRutaBaru = 0;

                        bebanRutaTim += beban_cacah;
                        Log.e("kode bs", kode_bs);
                        BlokSensus bs = new BlokSensus(kode_bs, "34", kode_kab, kode_kec, kode_desa, stratifikasi, noBs, sls, nama_kabupaten,
                                nama_kecamatan, nama_desa, nim, statusBs);
                        ds.insertBlokSensus(bs);
                    }
                    bebanBStim += wilKerja.length();
                }
                Log.e("BEBAN BS", String.valueOf(bebanBStim));
                Log.e("BEBAN RUTA", String.valueOf(bebanRutaTim));
                Log.e("Nama Kabupaten", nama_kabupaten);
                /*
                SANDY filter download kues
                 */
                pref.save(CapiKey.KEY_NAMA_KABUPATEN, nama_kabupaten);
                pref.save(CapiKey.KEY_JUMLAH_BS, String.valueOf(bebanBStim));
                pref.save(CapiKey.KEY_JUMLAH_RUTA, String.valueOf(bebanRutaTim));
            }

            return LOGIN_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("error gk tau napa dah", e.getMessage());
            return LOGIN_FAIL_USER;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!result.equals(LOGIN_SUCCESS)) CapiPreference.getInstance().clearCapiData();
        loginListener.onLoginFinish(result);
    }

}
