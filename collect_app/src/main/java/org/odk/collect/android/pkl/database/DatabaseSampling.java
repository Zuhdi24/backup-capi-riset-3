package org.odk.collect.android.pkl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.object.BlokSensus;
import org.odk.collect.android.pkl.object.JumlahRutaBS;
import org.odk.collect.android.pkl.object.ObjekUpload;
import org.odk.collect.android.pkl.object.RumahTangga;
import org.odk.collect.android.pkl.object.SampelRuta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Tim PKL
 */

public class DatabaseSampling extends SQLiteOpenHelper {

    private static DatabaseSampling sInstance;
    private static final int DATABASE_VERSION = 22;
    private static final String DATABASE_NAME = "pkl62.db";
    private static final String TAG = "DATABASE";

    public static final String TABLE_BS = "bloksensus";
    public static final String TABLE_RT = "rumahtangga";
    public static final String TABLE_UUP_SAMPLING = "hasilsampling";

    //primary key dan foreign key Blok Sensus, Rumah Tangga, Hasil Sampel
    public static final String KODE_BS = "kode_bs";
    public static final String KODE_RUTA = "kode_ruta";
    public static final String STATUS = "status";
//    public static final String TIMESTAMP = "time";

    //Blok Sensus
    public static final String PROVINSI = "provinsi";
    public static final String KABUPATEN = "kabupaten";
    public static final String KECAMATAN = "kecamatan";
    public static final String DESA = "desa";
    public static final String STRATIFIKASI = "stratifiksai";
    public static final String NO_BS = "no_bs";
    public static final String SLS = "sls";
    public static final String NIM = "nim";
    public static final String NAMA_KABUPATEN = "nama_kabupaten";
    public static final String NAMA_KECAMATAN = "nama_kecamatan";
    public static final String NAMA_DESA = "nama_desa";

    //Usaha Pariwisata
    public static final String NO_SLS = "noSls";
    public static final String BF = "bf";
    public static final String BS = "bs";
    public static final String NO_RUTA = "noUrutRuta";
    public static final String NAMA_KRT = "namaKRT";
    public static final String ALAMAT = "alamat";
    public static final String JUMLAH_ART = "jumlahART";
    public static final String JUMLAH_ART10 = "jumlahART10";
    public static final String NO_HP = "noHp";
    public static final String KODE_ELIGIBLE = "kodeEligible";
    public static final String JUMLAH_ELIGIBLE = "jumlahEligible";
    public static final String LIST_ELIGIBLE = "listEligible";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String AKURASI = "akurasi";
    public static final String LOKASI_UP = "lokasiUP";
    public static final String JENIS_UUP = "jenisUUP";
    public static final String TIME = "time";

    private static final String CREATE_TABLE_BS = "CREATE TABLE " + TABLE_BS + " (" +
            KODE_BS + " text, " +
            PROVINSI + " text, " +
            KABUPATEN + " text, " +
            KECAMATAN + " text, " +
            DESA + " text, " +
            STRATIFIKASI + " text, " +
            NO_BS + " text, " +
            SLS + " text, " +
            NAMA_KABUPATEN + " text, " +
            NAMA_KECAMATAN + " text, " +
            NAMA_DESA + " text, " +
            NIM + " text, " +
            STATUS + " text)";

    private static final String CREATE_TABLE_UUP = "CREATE TABLE " + TABLE_RT + " (" +
            KODE_RUTA + " text, " +
            KODE_BS + " text, " +
            NO_SLS + " text, " +
            BF + " text, " +
            BS + " text, " +
            NO_RUTA + " text, " +
            NAMA_KRT + " text, " +
            ALAMAT + " text, " +
            JUMLAH_ART + " text, " +
            JUMLAH_ART10 + " text, " +
            NO_HP + " text, " +
            JUMLAH_ELIGIBLE + " text, " +
            LIST_ELIGIBLE + " text, " +
            LATITUDE + " text, " +
            LONGITUDE + " text, " +
            AKURASI + " text, " +
            STATUS + " text, " +
            TIME + " text)";

    private static final String CREATE_TABLE_RT_SAMPLING = "CREATE TABLE " + TABLE_UUP_SAMPLING + " (" +
            KODE_BS + " text, " +
            KODE_RUTA + " text)";

    public DatabaseSampling(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseSampling getInstance() {
        if (sInstance == null) {
            sInstance = new DatabaseSampling(Collect.getInstance());
        }
        return sInstance;
    }

    public void LogoutDropAllTable() {
        SQLiteDatabase db = getWritableDatabase();
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + TABLE_BS);
        db.execSQL(drop + TABLE_RT);
        db.execSQL(drop + TABLE_UUP_SAMPLING);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BS);
        db.execSQL(CREATE_TABLE_UUP);
        db.execSQL(CREATE_TABLE_RT_SAMPLING);
        Log.d(TAG, "iyaMasuk2");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + TABLE_BS);
        db.execSQL(drop + TABLE_RT);
        db.execSQL(drop + TABLE_UUP_SAMPLING);
        onCreate(db);
    }

    public boolean insertBlokSensus(BlokSensus bs) {
        try {
            SQLiteDatabase db = getInstance().getWritableDatabase();
            ContentValues v = new ContentValues();

            v.put(KODE_BS, bs.getKodeBs());
            v.put(PROVINSI, bs.getProvinsi());
            v.put(KABUPATEN, bs.getKabupaten());
            v.put(KECAMATAN, bs.getKecamatan());
            v.put(DESA, bs.getDesa());
            v.put(STRATIFIKASI, bs.getStratifikasi());
            v.put(NO_BS, bs.getNoBs());
            v.put(SLS, bs.getSls());
            v.put(NAMA_KABUPATEN, bs.getNamaKabupaten());
            v.put(NAMA_KECAMATAN, bs.getNamaKecamatan());
            v.put(NAMA_DESA, bs.getNamaDesa());
            v.put(NIM, bs.getNim());
            v.put(STATUS, bs.getStatus());

            db.insert(TABLE_BS, null, v);
            

            Log.d("STATUS", bs.getStatus());
            Log.d("INSERT DATABASE BS", "SUCCESS");
            return true;
        } catch (Exception e) {
            Log.d("INSERT DATABASE", e.toString());
            return false;
        }
    }

//    public boolean insertBlokSensus(BlokSensus bs, String status) {
//        try {
//            SQLiteDatabase db = getInstance().getWritableDatabase();
//            ContentValues v = new ContentValues();
//
//            v.put(KODE_BS, bs.getKodeBs());
//            v.put(PROVINSI, bs.getProvinsi());
//            v.put(KABUPATEN, bs.getKabupaten());
//            v.put(KECAMATAN, bs.getKecamatan());
//            v.put(DESA, bs.getDesa());
//            v.put(STRATIFIKASI, bs.getStratifikasi());
//            v.put(NO_BS, bs.getNoBs());
//            v.put(SLS, bs.getSls());
//            v.put(NAMA_KABUPATEN, bs.getNamaKabupaten());
//            v.put(NAMA_KECAMATAN, bs.getNamaKecamatan());
//            v.put(NAMA_DESA, bs.getNamaDesa());
//            v.put(NIM, bs.getNim());
//            v.put(STATUS, status);
//
//            db.insert(TABLE_BS, null, v);
//
//            Log.d("INSERT DATABASE", "SUCCESS");
//            return true;
//        } catch (Exception e) {
//            Log.d("INSERT DATABASE", e.toString());
//            return false;
//        }
//    }

    public ArrayList<BlokSensus> getListBlokSensus() {
        String sql = "SELECT * FROM " + TABLE_BS + " ORDER BY " + KODE_BS + " ASC";
        ArrayList<BlokSensus> listBlokSensus = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                BlokSensus blokSensus;
                do {
                    blokSensus = new BlokSensus(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                            cursor.getString(11), cursor.getString(12));
                    listBlokSensus.add(blokSensus);
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
        } catch (Exception e) {
            Log.d("Blok sensus ", "masalah di " + e);
        }
        return listBlokSensus;
    }

    public BlokSensus getBlokSensusByKode(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_BS + " WHERE " + KODE_BS + " = '" + kodeBs + "'";
        BlokSensus blokSensus = new BlokSensus();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                blokSensus = new BlokSensus(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12));
            }
            cursor.close();
            return blokSensus;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean UpdateBlokSensus(BlokSensus bsUpdate, String kodeBs) {
        try {
            SQLiteDatabase db = getInstance().getWritableDatabase();
            ContentValues v = new ContentValues();

            v.put(KODE_BS, bsUpdate.getKodeBs());
            v.put(PROVINSI, bsUpdate.getProvinsi());
            v.put(KABUPATEN, bsUpdate.getKabupaten());
            v.put(KECAMATAN, bsUpdate.getKecamatan());
            v.put(DESA, bsUpdate.getDesa());
            v.put(STRATIFIKASI, bsUpdate.getStratifikasi());
            v.put(NO_BS, bsUpdate.getNoBs());
            v.put(SLS, bsUpdate.getSls());
            v.put(NAMA_KABUPATEN, bsUpdate.getNamaKabupaten());
            v.put(NAMA_KECAMATAN, bsUpdate.getNamaKecamatan());
            v.put(NAMA_DESA, bsUpdate.getNamaDesa());
            v.put(NIM, bsUpdate.getNim());
            v.put(STATUS, bsUpdate.getStatus());
//            v.put(NKS, bsUpdate.getNks());

            db.update(TABLE_BS, v, KODE_BS + "=" + kodeBs, null);
            
            Log.d("UPDATE ", "BS SUCCESS");
            return true;
        } catch (Exception e) {
            Log.d("UPDATE ", "BS GAGAL " + e);
            return false;
        }
    }

//    public boolean UpdateJumlahRutaBlokSensus(String kodeBs, String jumlahRutaBaru, String jumlahRutaInternet) {
//        try {
//            SQLiteDatabase db = getInstance().getWritableDatabase();
//            ContentValues v = new ContentValues();
//
////            v.put(JUMLAH_RUTA_BARU, jumlahRutaBaru);
////            v.put(JUMLAH_RUTA_INTERNET, jumlahRutaInternet);
//
//            db.update(TABLE_BS, v, KODE_BS + "= '" + kodeBs + "'", null);
//
//            Log.d("UPDATE ", "BS SUCCESS");
//            return true;
//        } catch (Exception e) {
//            Log.d("UPDATE ", "BS GAGAL " + e);
//            return false;
//        }
//    }

    public boolean updateStatusBlokSensus(String kodeBs, String statusAkhir) {
        try {
            ContentValues v = new ContentValues();

            SQLiteDatabase db = getInstance().getWritableDatabase();
            v.put(STATUS, statusAkhir);
            db.update(TABLE_BS, v, KODE_BS + " = '" + kodeBs + "'", null);

            Log.d("UPDATE ", "STATUS BLOK SENSUS SUKSES");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UPDATE ", " STATUS BLOK SENSUS GAGAL " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public void deleteBS(String kodeBs) {
        SQLiteDatabase db = getInstance().getReadableDatabase();
        try {
            db.delete(TABLE_BS, KODE_BS + " = '" + kodeBs + "'", null);
        } catch (Exception e) {
            Log.d("Hapus ", "Ruta " + e);
        }
    }

    public boolean insertUUP(RumahTangga uup) {
        try {
            SQLiteDatabase db = getInstance().getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(KODE_RUTA, UUID.randomUUID().toString());  //Jika Ruta merupakan ruta tambahan diluar list, keberadaan (baru)
            v.put(KODE_BS, uup.getKodeBs());
            v.put(NO_SLS, uup.getNoSLS());
            v.put(BF, uup.getBf());
            v.put(BS, uup.getBs());
            v.put(NO_RUTA, uup.getNoUrutRuta());
            v.put(NAMA_KRT, uup.getNamaKRT());
            v.put(ALAMAT, uup.getAlamat());
            v.put(JUMLAH_ART, uup.getJumlahART());
            v.put(JUMLAH_ART10, uup.getJumlahART10());
            v.put(NO_HP, uup.getNoHp());
            v.put(KODE_ELIGIBLE, uup.getKodeEligible());
            v.put(JUMLAH_ELIGIBLE, uup.getJumlahEligible());
            v.put(LIST_ELIGIBLE, uup.getListNamaEligible());
            v.put(LATITUDE, uup.getLatitude());
            v.put(LONGITUDE, uup.getLongitude());
            v.put(AKURASI, uup.getAkurasi());
            v.put(STATUS, RumahTangga.STATUS_INSERT);
            v.put(TIME, uup.getTime());
//
//
//            if (uup.getTime() != null) v.put(TIMESTAMP, uup.getTime());

            Log.d("CREATE TABLE", CREATE_TABLE_UUP);
            db.insert(TABLE_RT, null, v);


            Log.d("INSERT DATABASE", "SUCCESS");
            return true;
        } catch (Exception e) {
            Log.d("INSERT DATABASE", "FAIL");
            Log.d("INSERT", uup.getAlamat());
            return false;
        }
    }

    public void insertUUP(RumahTangga uup, String kodeRuta) {
        try {
            SQLiteDatabase db = getInstance().getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(KODE_RUTA, kodeRuta);
            v.put(KODE_BS, uup.getKodeBs());
            v.put(NO_SLS, uup.getNoSLS());
            v.put(BF, uup.getBf());
            v.put(BS, uup.getBs());
            v.put(NO_RUTA, uup.getNoUrutRuta());
            v.put(NAMA_KRT, uup.getNamaKRT());
            v.put(ALAMAT, uup.getAlamat());
            v.put(JUMLAH_ART, uup.getJumlahART());
            v.put(JUMLAH_ART10, uup.getJumlahART10());
            v.put(NO_HP, uup.getNoHp());
            v.put(KODE_ELIGIBLE, uup.getKodeEligible());
            v.put(JUMLAH_ELIGIBLE, uup.getJumlahEligible());
            v.put(LIST_ELIGIBLE, uup.getListNamaEligible());
            v.put(LATITUDE, uup.getLatitude());
            v.put(LONGITUDE, uup.getLongitude());
            v.put(AKURASI, uup.getAkurasi());
            v.put(STATUS, RumahTangga.STATUS_INSERT);
            v.put(TIME, uup.getTime());
//            if (uup.getTime() != null) v.put(TIMESTAMP, uup.getTime());

            db.insert(TABLE_RT, null, v);

            
            Log.d("INSERT DATABASE", "SUCCESS");
//            Log.d("INSERT DATABASE 2",uup.getKeterangan());
        } catch (Exception e) {
            Log.d("INSERT DATABASE", "FAIL");
        }
    }

    public RumahTangga getLastUUP() {
        String sql = "SELECT * FROM " + TABLE_RT + " ORDER BY " + NO_RUTA + " DESC LIMIT 1";

        RumahTangga rumahTangga = null;
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                rumahTangga = new RumahTangga();
                rumahTangga.setNoSLS(cursor.getString(cursor.getColumnIndex(NO_SLS)));
                rumahTangga.setBf(cursor.getString(cursor.getColumnIndex(BF)));
                rumahTangga.setBs(cursor.getString(cursor.getColumnIndex(BS)));
                rumahTangga.setNoUrutRuta(cursor.getString(cursor.getColumnIndex(NO_RUTA)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rumah tangga ", "masalah " + e);
        }
        return rumahTangga;
    }

    public ArrayList<RumahTangga> getListUnitUsahaPariwisata(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND "
                    + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' ORDER BY " + TIME + " ASC";
        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
                    listRumahTangga.add(rumahTangga);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rumah tangga ", "masalah di " + e);
        }
        return listRumahTangga;
    }

//    public ArrayList<RumahTanggaRiset4> getListRumahTanggaByType(String kodeBs, int type) {
//        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + " AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "' ORDER BY " + BS + " ASC";
//
//        ArrayList<RumahTanggaRiset4> listRumahTanggaRiset4 = new ArrayList<>();
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
////            Cursor findEntry = database.query(TABLE_RT, null, STATUS+"=? AND "+TYPE+"=?", new String[] { kodeBs, String.valueOf(type) }, null, null, ID_RT);
//            Cursor cursor = database.rawQuery(sql, null);
//            if (cursor.moveToFirst()) {
//                RumahTanggaRiset4 rumahTanggaRiset4 = null;
//                do {
//                    rumahTanggaRiset4 = new RumahTanggaRiset4(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
//                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
//                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
//                            cursor.getString(14), cursor.getString(15));
//                    listRumahTanggaRiset4.add(rumahTanggaRiset4);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("Rumah tangga Type ", "masalah di " + e);
//        }
//        return listRumahTanggaRiset4;
//    }

    public RumahTangga getRumahTanggaByKode(String kodeBs, String kodeUUP) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_RUTA + " = '" + kodeUUP + "' AND " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "'";
        RumahTangga rumahTangga;
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);

            cursor.moveToNext();
            rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                    cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                    cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
            cursor.close();
            return rumahTangga;
        } catch (Exception e) {
            Log.d("getRT rusak goro-goro ", e.getStackTrace().toString());
            e.printStackTrace();
            return null;
        }
    }

    public RumahTangga getLastRuta(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' ORDER BY " + TIME + " DESC LIMIT 1";
        Log.d("lastRuta", "" + kodeBs);
        try {
            RumahTangga rumahTangga = new RumahTangga();
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                        cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
            }
            cursor.close();
            return rumahTangga;

        } catch (Exception e) {
            Log.d("lastRuta", e.toString());
            return new RumahTangga();
        }
    }

    public void updateUnitUsahaPariwisata(String kodeBs, String kodeUUP, String statusAkhir) {
        try {
            ContentValues v = new ContentValues();

            SQLiteDatabase db = getInstance().getWritableDatabase();
            v.put(STATUS, statusAkhir);
            db.update(TABLE_RT, v, KODE_BS + " = '" + kodeBs + "' AND " + KODE_RUTA + " = '" + kodeUUP + "'", null);

            Log.d("UPDATE ", "STATUS RUTA SUKSES");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UPDATE ", "STATUS RUTA GAGAL " + Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean updateUUP(RumahTangga uup, String kodeBs, String kodeUUP) {
        try {
            ContentValues v = new ContentValues();

            SQLiteDatabase db = getInstance().getWritableDatabase();
            v.put(KODE_BS, uup.getKodeBs());
            v.put(NO_SLS, uup.getNoSLS());
            v.put(BF, uup.getBf());
            v.put(BS, uup.getBs());
            v.put(NO_RUTA, uup.getNoUrutRuta());
            v.put(NAMA_KRT, uup.getNamaKRT());
            v.put(ALAMAT, uup.getAlamat());
            v.put(JUMLAH_ART, uup.getJumlahART());
            v.put(JUMLAH_ART10, uup.getJumlahART10());
            v.put(NO_HP, uup.getNoHp());
            v.put(KODE_ELIGIBLE, uup.getKodeEligible());
            v.put(JUMLAH_ELIGIBLE, uup.getJumlahEligible());
            v.put(LIST_ELIGIBLE, uup.getListNamaEligible());
            v.put(LATITUDE, uup.getLatitude());
            v.put(LONGITUDE, uup.getLongitude());
            v.put(AKURASI, uup.getAkurasi());
            if (uup.getStatus().equals(RumahTangga.STATUS_UPLOADED)) {
                v.put(STATUS, RumahTangga.STATUS_UPDATE);
            }
            
            db.update(TABLE_RT, v, KODE_BS + " = '" + kodeBs + "' AND " + KODE_RUTA + " = '" + kodeUUP + "'", null);

            Log.d("UPDATE ", "RUTA SUKSES");
            
//        }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UPDATE ", " RUTA GAGAL " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    public ArrayList<RumahTangga> getListUUPByNoUrut(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' AND " + NO_RUTA + " <> '000" + "' ORDER BY " + TIME + " ASC";
        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
                    listRumahTangga.add(rumahTangga);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rumah tangga ", "masalah di " + e);
        }
        return listRumahTangga;
    }

    public ArrayList<RumahTangga> getListUUPForSampel(String kodeBs) {
        String sql = "SELECT *" +  //produk sql jahiliyah
//                KODE_RUTA + ", " +
//                KODE_BS + ", " +
//                NO_SLS + ", " +
//                BF + ", " +
//                BS + ", " +
//                NORUTA + ", " +
//                NAMA_KRT + ", " +
//                ALAMAT + ", " +
//                LATITUDE + ", " +
//                LONGITUDE + ", " +
//                AKURASI + ", " +
//                STATUS + ", " +
//                R4_RTUP + ", " +
//                R4_RTUP_TP + ", " +
//                R4_NO_RTUP_TP + ", " +
//                R4_CIRI_FISIK +
                " FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND "
                + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' AND " + NO_RUTA + " <> '000" +"' ORDER BY "
                 + TIME + " ASC, " + NO_RUTA + " ASC";
        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                            cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
                    listRumahTangga.add(rumahTangga);
//                    Log.d(TAG, "getListRumahTanggaForSampel: " + rumahTangga.getNamaPemilikUUP() + " " + rumahTangga.getNoUrutUUP());
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rumah tangga ", "masalah di " + e);
        }
        return listRumahTangga;
    }

    public ArrayList<RumahTangga> getListUUPForSampelLISTING(String kodeBs) {
        String sql = "SELECT *" + " FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + JUMLAH_ELIGIBLE + " != '00"
                + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE
                + "' AND " + NO_RUTA + " <> '000" +"' ORDER BY " + TIME + " ASC, " + NO_RUTA + " ASC";
        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                            cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
                    listRumahTangga.add(rumahTangga);
//                    Log.d(TAG, "getListRumahTanggaForSampel: " + rumahTangga.getNamaPemilikUUP() + " " + rumahTangga.getNoUrutUUP());
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rumah tangga ", "masalah di " + e);
        }
        return listRumahTangga;
    }


    public boolean clearkanNoUUP(String kodeBs) {
        ArrayList<RumahTangga> listUUP = getListUUPByNoUrut(kodeBs);
        int N = listUUP.size();
        Log.d("UPDATE_NO_RUTA ", "total " + N);
        int i = 0;
        for (RumahTangga rt : listUUP) {
            i = i + 1;
            int length = String.valueOf(i).length();
            String norut = "";
            switch (length) {
                case 1:
                    norut = "00" + i;
                    break;
                case 2:
                    norut = "0" + i;
                    break;
                case 3:
                    norut = String.valueOf(i);
            }
            Log.d(TAG, "clearkanNoUrutRuta: " + i + ", " + norut);
            UpdateNoUrutRuta(rt, norut);

        }
//        updateNoRtUp(kodeBs);
        return true;
    }

    public boolean clearkanNoUrutUUPForSampling(String kodeBs) {
        ArrayList<RumahTangga> listUUP = getListUUPByNoUrut(kodeBs);
        int N = listUUP.size();
        Log.d("[UPDATE_NO_RUTA] ", "total " + N);
        int i = 0;
        for (RumahTangga rt : listUUP) {
            i = i + 1;
            int length = String.valueOf(i).length();
            String norut = "";
            switch (length) {
                case 1:
                    norut = "00" + i;
                    break;
                case 2:
                    norut = "0" + i;
                    break;
                case 3:
                    norut = String.valueOf(i);
            }
            Log.d(TAG, "clearkanNoUrutRuta: " + i + ", " + norut);
            UpdateNoUrutRuta(rt, norut);

        }
        return true;
    }

    private void UpdateNoUrutRuta(RumahTangga uup, String noUrut) {
        try {
            SQLiteDatabase db = getInstance().getWritableDatabase();
            ContentValues v = new ContentValues();

            if (uup.getKodeRuta() == null) {
                v.put(KODE_RUTA, "aaa");
            }
            v.put(NO_RUTA, noUrut);
            if (uup.getStatus().equals(RumahTangga.STATUS_UPLOADED)) {
                v.put(STATUS, RumahTangga.STATUS_UPDATE);
            }

            db.update(TABLE_RT, v, KODE_RUTA + " = '" + uup.getKodeRuta() + "'", null);
            Log.d("--UPDATE_NO_RUTA [S] : ", uup.getKodeRuta() + ", " + noUrut);

        } catch (Exception e) {
            Log.d("--UPDATE_NO_RUTA_GAGAL ", uup.getKodeRuta() + ", " + noUrut + ", " + e.toString());
        }
    }

    public int getJumlahUUP(String kodeBs) {
        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_rt_update FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs +
                "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' AND " + NO_RUTA + " <> 0";
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            int i = 0;
            if (cursor.moveToFirst()) {
                i = cursor.getInt(0);
            }
            cursor.close();
            return i;
        } catch (Exception e) {
            Log.d("getRT rusak goro-goro ", e.toString());
            return 0;
        }
    }

//    public String getJumlahRutaUpdate(String kodeBs) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_rt_update FROM " + TABLE_RT_R4 + " WHERE " + KODE_BS + " = '" + kodeBs +
//                "' AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "' AND " + NORUTA + " <> 0";
//            String jumlahRutaBS = null;
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//            if (cursor.moveToFirst()) {
//                jumlahRutaBS = (cursor.getString(0));
//            }
//            cursor.close();
//            return jumlahRutaBS;
//        } catch (Exception e) {
//            Log.d("getRT rusak goro-goro ", e.toString());
//            return null;
//        }
//    }

    public String getJumlahUUPByType(String kodeBs, int type) {
        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_uup FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE +
                "' AND " + JENIS_UUP + " = " + type ;
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);

            String i = "";
            if (cursor.moveToFirst()) {
                i = cursor.getString(0);
            }
            cursor.close();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public JumlahRutaBS getJumlahRutaBS(String kodeBs){
//        String allRuta = getJumlahRutaUpdate(kodeBs);
//        String rutaInternet = getJumlahRutaInternetByType(kodeBs,1);

        return new JumlahRutaBS(kodeBs);
    }

//    public int getJumlahRutaTypeNol(String kodeBs) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND "  + KEBERADAAN + " <>  7  AND " + KEBERADAAN + " <>  6  AND " + KEBERADAAN + " <>  5  AND " + KEBERADAAN + " <> 0  AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//        return 0;
//    }

//    public int getJumlahRutaLahanKat1(String kodeBs) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT_R4 + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    public int getJumlahRutaLahanKat2(String kodeBs) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    public int getJumlahRutaLahanKat3(String kodeBs) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    public int getJumlahRutaLainnya(String kodeBs, int type) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + RumahTanggaRiset4.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

//    public int getJumlahRutaByPendidikan(String kodeBs, String pendidikan) {
//        String sql = "SELECT count(" + KODE_RUTA + ") as jumlah_ruta FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + " AND " + STATUS + " <> '" + RumahTanggaRiset4.STATUS_DELETE + "'";
//        try {
//            SQLiteDatabase database = getInstance().getReadableDatabase();
//            Cursor cursor = database.rawQuery(sql, null);
//            int i = 0;
//            if (cursor.moveToFirst()) {
//                i = cursor.getInt(0);
//            }
//            cursor.close();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

    public void deleteRumahtangga(RumahTangga ruta) {

        SQLiteDatabase db = getInstance().getReadableDatabase();
        try {
            if (ruta.getStatus().equals(RumahTangga.STATUS_UPLOADED) || ruta.getStatus().equals(RumahTangga.STATUS_UPDATE)) {
                updateUnitUsahaPariwisata(ruta.getKodeBs(), ruta.getKodeRuta(), RumahTangga.STATUS_DELETE);
            } else {
                db.delete(TABLE_RT, KODE_RUTA + " = '" + ruta.getKodeRuta() + "'", null);
            }
        } catch (Exception e) {
            Log.d("Hapus ", "Ruta " + e);
        }
    }

//    public boolean deleteAllRumahtanggaByKodeBs(String kodeBs) {
//
//        SQLiteDatabase db = getInstance().getReadableDatabase();
//        try {
//            db.delete(TABLE_RT, KODE_BS + " = '" + kodeBs + "'", null);
//
//            return true;
//        } catch (Exception e) {
//            Log.d("Hapus ", "Semua Ruta " + e);
//            return false;
//        }
//    }


    public boolean isNoBfExist(String kodeBs, String noBf) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + BF + " = '" + noBf + "' AND " + KODE_BS + " = '" + kodeBs + "'";
        try {
            SQLiteDatabase db = getInstance().getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                cursor.close();
                
                return true;
            } else {
                cursor.close();
                
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "isNoBfExist rusak goro goro" + e);
        }
        return true;
    }

//    public boolean isNoBsExist(String kodeBs, String noBs) {
//        String sql = "SELECT * FROM " + TABLE_RT_R4 + " WHERE " + BS + " = '" + noBs + "' AND " + KODE_BS + " = '" + kodeBs + "'";
//        try {
//            SQLiteDatabase db = getInstance().getReadableDatabase();
//            Cursor cursor = db.rawQuery(sql, null);
//            if (cursor.moveToFirst()) {
//                cursor.close();
//
//                Log.d(TAG, "isNoBsExist: Exist");
//                return true;
//            } else {
//                cursor.close();
//
//                Log.d(TAG, "isNoBsExist: not Exist " + noBs);
//                return false;
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "isNoBfExist rusak goro goro" + e);
//        }
//        return true;
//    }

    public boolean insertSampel(ArrayList<SampelRuta> listSampel) {
        try {
            SQLiteDatabase db = DatabaseSampling.getInstance().getWritableDatabase();

            for (SampelRuta x : listSampel) {
                ContentValues v = new ContentValues();
                v.put(KODE_BS, x.getKodeBs());
                v.put(KODE_RUTA, x.getKodeRuta());

                db.insert(TABLE_UUP_SAMPLING, null, v);
                Log.i(TAG, "insertSampel: Sampel ditambahkan");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSampel(String kodeBs) {
        SQLiteDatabase db = getInstance().getReadableDatabase();
        try {
            db.delete(TABLE_UUP_SAMPLING, KODE_BS + " = '" + kodeBs + "'", null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public boolean deleteAllSampelByKodeBs(String kodeBs) {
//
//        SQLiteDatabase db = getInstance().getReadableDatabase();
//        try {
//            db.delete(TABLE_RT_SAMPLING, KODE_BS + " = '" + kodeBs + "'", null);
//
//            return true;
//        } catch (Exception e) {
//            Log.d("Hapus ", "Semua Sampel " + e);
//            return false;
//        }
//    }

    public ArrayList<RumahTangga> getRutaTerpilih(String kodeBs) {
        ArrayList<RumahTangga> sampelRuta = new ArrayList<>();
        String sql = "SELECT " + KODE_RUTA + " FROM " + TABLE_UUP_SAMPLING + " WHERE " + KODE_BS + " = '" + kodeBs + "' ORDER BY " + KODE_BS + " ASC";
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    sampelRuta.add(getRumahTanggaByKode(kodeBs, cursor.getString(0)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sampelRuta;
    }

    public ArrayList<SampelRuta> getAllSampelRuta() {
        String sql = "SELECT * FROM " + TABLE_UUP_SAMPLING + " ORDER BY " + KODE_BS + " ASC";
        ArrayList<SampelRuta> listSampel = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                SampelRuta ruta;
                do {
                    ruta = new SampelRuta(cursor.getString(0), cursor.getString(1));
                    listSampel.add(ruta);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("ALL Sampel ", "masalah di " + e);
        }
        return listSampel;
    }

    public ArrayList<SampelRuta> getSampelRuta(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_UUP_SAMPLING + " WHERE " + KODE_BS + " = '" + kodeBs + "'";
        ArrayList<SampelRuta> listSampel = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                SampelRuta ruta;
                do {
                    ruta = new SampelRuta(cursor.getString(0), cursor.getString(1));
                    listSampel.add(ruta);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("ALL Sampel ", "masalah di " + e);
        }
        return listSampel;
    }

    public ArrayList<RumahTangga> getAllRumahTangga() {
        String sql = "SELECT* FROM " + TABLE_RT + " ORDER BY " + TIME + " ASC";
        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));
                    listRumahTangga.add(rumahTangga);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("ALL Rumah tangga ", "masalah di " + e);
        }
        return listRumahTangga;
    }

    public boolean restore(ObjekUpload container) {
        DatabaseSampling db = getInstance();
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();

        ArrayList<BlokSensus> listBs = container.getDataBs();
        ArrayList<RumahTangga> listRuta = container.getDataRt();
        ArrayList<SampelRuta> listSampel = container.getDataSt();

        try {
            sqDb.delete(TABLE_RT, null, null);
            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_RT});

            sqDb.delete(TABLE_BS, null, null);
            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_BS});

            sqDb.delete(TABLE_UUP_SAMPLING, null, null);
            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_UUP_SAMPLING});

            for (BlokSensus bs : listBs) {
                db.insertBlokSensus(bs);
            }

            for (RumahTangga rt : listRuta) {
                db.insertUUP(rt, rt.getKodeRuta());
            }

            for (SampelRuta st : listSampel) {
                ContentValues v = new ContentValues();
                v.put(KODE_BS, st.getKodeBs());
                v.put(KODE_RUTA, st.getKodeRuta());
                sqDb.insert(TABLE_UUP_SAMPLING, null, v);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    public boolean restoreAllRuta(ArrayList<RumahTangga> listRuta) {
        DatabaseSampling db = getInstance();
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();

        try {
            sqDb.delete(TABLE_RT, null, null);
            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_RT});

            for (RumahTangga rt : listRuta) {
                db.insertUUP(rt, rt.getKodeRuta());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreAllSampel(ArrayList<SampelRuta> listSampel) {
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();
        Log.d(TAG, "restoreAllSampel: " + listSampel.size());
        try {
            sqDb.delete(TABLE_UUP_SAMPLING, null, null);

            for (SampelRuta st : listSampel) {
                ContentValues v = new ContentValues();
                v.put(KODE_BS, st.getKodeBs());
                v.put(KODE_RUTA, st.getKodeRuta());
                sqDb.insert(TABLE_UUP_SAMPLING, null, v);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    public boolean restoreAllRuta(String kodeBs, ArrayList<RumahTangga> listRuta) {
//        Log.d(TAG, "restoreAllRuta: " + listRuta.size());
//        Log.d(TAG, "kodeBS: " + listRuta.get(0).getKodeBs() + kodeBs);
//        Log.d(TAG, "restoreAllRuta: " + listRuta.get(0).getStatus());
        DatabaseSampling db = getInstance();
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();

        try {
            sqDb.delete(TABLE_RT, KODE_BS + " = '" + kodeBs + "'", null);
//            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_RT});

            for (RumahTangga rt : listRuta) {
                db.insertUUP(rt, rt.getKodeRuta());
//                Log.d(TAG, "restoreAllRT: " + rt.getStatus());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreAllSampel(String kodeBs, ArrayList<SampelRuta> listSampel) {
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();

        try {
            sqDb.delete(TABLE_UUP_SAMPLING, KODE_BS + " = '" + kodeBs + "'", null);

            for (SampelRuta st : listSampel) {
                ContentValues v = new ContentValues();
                v.put(KODE_RUTA, st.getKodeRuta());
                v.put(KODE_BS, st.getKodeBs());
                sqDb.insert(TABLE_UUP_SAMPLING, null, v);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

//    public boolean updateStatusSampel(SampelRuta sr, String statusAkhir) {
//        try {
//            ContentValues v = new ContentValues();
//
//            SQLiteDatabase db = getInstance().getWritableDatabase();
//            v.put(STATUS, statusAkhir);
//            db.update(TABLE_RT_SAMPLING, v, KODE_BS + " = '" + sr.getKodeBs() + "' AND " + KODE_RUTA + " = '" + sr.getKodeRuta() + "'", null);
//
//            Log.d("UPDATE ", "STATUS BLOK SENSUS SUKSES");
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("UPDATE ", " STATUS BLOK SENSUS GAGAL " + e.getStackTrace());
//            return false;
//        }
//    }

    public boolean restoreAllBs(ArrayList<BlokSensus> listBs) {
        DatabaseSampling db = getInstance();
        SQLiteDatabase sqDb = getInstance().getWritableDatabase();

        try {
            sqDb.delete(TABLE_BS, null, null);
//            sqDb.delete("SQLITE_SEQUENCE", "name=?", new String[]{TABLE_BS});

            for (BlokSensus bs : listBs) {
                db.insertBlokSensus(bs);
                Log.d("apaAjaDeh", bs.getStatus());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<RumahTangga> getListRutaToUpload(String kodeBs) {
        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + STATUS + " <> '" + RumahTangga.STATUS_UPLOADED + "' ORDER BY " + TIME + " ASC";

        ArrayList<RumahTangga> listRumahTangga = new ArrayList<>();
        try {
            SQLiteDatabase database = getInstance().getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                RumahTangga rumahTangga;
                do {
                    Log.d("ruta internet :  ", String.valueOf(cursor.getInt(9)));
                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18));

                    listRumahTangga.add(rumahTangga);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Rumah tangga Type ", "masalah di " + e);
        }
        return listRumahTangga;
    }

//    public ArrayList<RumahTanggaRiset4> getListRumahTanggaByKeberadaan(String kodeBs) {
////        String sql = "SELECT * FROM " + TABLE_RT + " WHERE " + KODE_BS + " = '" + kodeBs + "' AND " + KEBERADAAN + " = " + 0 + " AND " + STATUS + " <> '" + RumahTangga.STATUS_DELETE + "' ORDER BY " + BS + " ASC";
//        ArrayList<RumahTanggaRiset4> listRumahTanggaRiset4 = new ArrayList<>();
////        try {
////            SQLiteDatabase database = getInstance().getReadableDatabase();
//////            Cursor findEntry = database.query(TABLE_RT, null, STATUS+"=? AND "+TYPE+"=?", new String[] { kodeBs, String.valueOf(type) }, null, null, ID_RT);
////            Cursor cursor = database.rawQuery(sql, null);
////            if (cursor.moveToFirst()) {
////                RumahTangga rumahTangga = null;
////                do {
////                    rumahTangga = new RumahTangga(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
////                            cursor.getInt(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8),
////                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getInt(13),cursor.getInt(14));
////                    listRumahTangga.add(rumahTangga);
////                } while (cursor.moveToNext());
////            }
////            cursor.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////            Log.d("Rumah tangga Type ", "masalah di " + e);
////        }
//        return listRumahTanggaRiset4;
//    }
//
//    public boolean updateNoRtUp(String kodeBs) {
//        SQLiteDatabase database = getInstance().getReadableDatabase();
//        String setNullSql = "UPDATE " + TABLE_UUP + " SET " + R4_NO_RTUP_TP + " = ''" + " WHERE " + KODE_BS + " = '" + kodeBs + "'";
//        database.rawQuery(setNullSql, null);
//        String sql = "SELECT * FROM " + TABLE_UUP + " WHERE " + KODE_BS + " = '" + kodeBs + "' ORDER BY " + NO_UUP + " ASC";
//        ArrayList<UnitUsahaPariwisata> listUnitUsahaPariwisata = new ArrayList<>();
//        try {
//            Cursor cursor = database.rawQuery(sql, null);
//            if (cursor.moveToFirst()) {
//                UnitUsahaPariwisata unitUsahaPariwisata;
//                do {
//                    unitUsahaPariwisata = new UnitUsahaPariwisata(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
//                            cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
//                            cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
//                            cursor.getString(14), cursor.getString(15), cursor.getString(16));
//                    listUnitUsahaPariwisata.add(unitUsahaPariwisata);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//
//            int count = 0;
//
//            for(UnitUsahaPariwisata ruta : listUnitUsahaPariwisata) {
//                if (ruta.getRtup().equals("1")) {
//                    Log.d("ISRTUP", "YA");
//                    count++;
//
//                    int length = String.valueOf(count).length();
//                    String norut = "";
//                    switch (length) {
//                        case 1:
//                            norut = "00" + String.valueOf(count);
//                            break;
//                        case 2:
//                            norut = "0" + String.valueOf(count);
//                            break;
//                        case 3:
//                            norut = String.valueOf(count);
//                    }
//
//                    SQLiteDatabase db = getInstance().getWritableDatabase();
//                    ContentValues v = new ContentValues();
//
//                    v.put(R4_NO_RTUP_TP, norut);
//                    if (ruta.getStatus().equals(UnitUsahaPariwisata.STATUS_UPLOADED)) {
//                        v.put(STATUS, UnitUsahaPariwisata.STATUS_UPDATE);
//                    }
//
//                    db.update(TABLE_UUP, v, KODE_UUP + " = '" + ruta.getKodeUUP() + "'", null);
//                    Log.d("--UPDATE_NO_RTUP [S] : ", ruta.getKodeUUP() + ", " + ruta.getNoRtUpTp());
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            Log.d("UPDATE NORTUP", "masalah di " + e);
//            return false;
//        }
//    }
}

