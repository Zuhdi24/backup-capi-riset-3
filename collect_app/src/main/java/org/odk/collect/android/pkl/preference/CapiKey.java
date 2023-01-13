package org.odk.collect.android.pkl.preference;

import java.util.HashMap;

/**
 * @author Mahendri Dwicahyo
 */

public class CapiKey {

    //key beban cacah
//    public final static int N_SAMPLE_D3 = 10;
    public final static int N_SAMPLE = 10;
    public final static int N_MINIMUM_RUTA = 0;

    // key profil
    public static final String IS_LOGIN         = "IsLoggedIn";
    public static final String KEY_NIM          = "nim";
    public static final String KEY_NAMA         = "nama";
    public static final String KEY_JABATAN      = "jabatan";
    public static final String KEY_ID_JABATAN   = "id_jabatan";  //0 pcl 1 koortim
    public static final String KEY_NIM_KORTIM   = "nim_kortim";
    public static final String KEY_NAMA_KORTIM  = "nama_kortim";
    public static final String KEY_NOMOR_KORTIM = "nomor_kortim";
    public static final String KEY_PASS_KORTIM  = "pass_kortim";
    public static final String KEY_PASSWORD     = "password";
    public static final String KEY_AVATAR       = "avatar";
    public static final String KEY_PROFIL       = "profil";
    public static final String KEY_NAMA_KABUPATEN = "nama_kab";
    public static final String NAMA_FORM_PODES = "nama_form_podes";
    public static final String KEY_NAMA_TIM = "nama_tim";
    /*
    SANDY filter download kues
     */

    // key beban kerja
    public static final String KEY_JUMLAH_BS    = "beban_bs";
    public static final String KEY_JUMLAH_RUTA  = "beban_ruta";

    private static HashMap<String, Object> getHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(IS_LOGIN,                   false);
        hashMap.put(KEY_NIM,                    "");
        hashMap.put(KEY_NAMA,                   "");
        hashMap.put(KEY_JABATAN,                "");
        hashMap.put(KEY_ID_JABATAN,             "");
        hashMap.put(KEY_NIM_KORTIM,             "");
        hashMap.put(KEY_NAMA_KORTIM,            "");
        hashMap.put(KEY_NOMOR_KORTIM,           "");
        hashMap.put(KEY_PASS_KORTIM,            "");
        hashMap.put(KEY_PASSWORD,               "");
        hashMap.put(KEY_AVATAR,                 "");
        hashMap.put(KEY_PROFIL,                 "");
        hashMap.put(KEY_NAMA_KABUPATEN,         "");

        hashMap.put(KEY_JUMLAH_BS,              "");
        hashMap.put(KEY_JUMLAH_RUTA,            "");
        hashMap.put(NAMA_FORM_PODES,            "");

        return hashMap;
    }

    static final HashMap<String, Object> CAPI_KEYS = getHashMap();
}
