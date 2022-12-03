package org.odk.collect.android.pkl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.object.AnggotaTim;
import org.odk.collect.android.pkl.object.BebanBlokSensus;
import org.odk.collect.android.pkl.object.ProgressBlokPcl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TeamHode
 */

public class DBhandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version

    private Context context;
    private static final int DATABASE_VERSION = 29;

    private static DBhandler sInstance;

    // Database Name
    private static final String DATABASE_NAME = "beranda";

    // Contacts table name
    private static final String TABLE_KORTIM = "anggota_kortim";

    private static final String TABLE_PCL = "progres_pcl";

    private static final String TABLE_BS = "beban_bs";

    private static final String KEY_KEl = "kelurahan";
    private static final String KEY_KEC = "kecamatan";

    // Contacts Table Columns names
    private static final String KEY_NIM = "nim";
    private static final String KEY_NAME = "name";
    private static final String KEY_PROGRES = "progres";
    private static final String KEY_PASS = "password";
    private static final String KEY_NOMOR = "nomor_ang";

    private static final String KEY_BS = "no_bs";
    private static final String KEY_PROGRES_PCL = "progres_pcl";

    public static synchronized DBhandler getInstance() {
        if (sInstance == null) {
            sInstance = new DBhandler(Collect.getInstance());
        }
        return sInstance;
    }


    private DBhandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_ANG_TABLE = "CREATE TABLE " + TABLE_KORTIM + "("
                + KEY_NIM + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PROGRES + " TEXT," + KEY_PASS + " TEXT," + KEY_NOMOR + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_ANG_TABLE);

        String CREATE_PCL_TABLE = "CREATE TABLE " + TABLE_PCL + "("
                + KEY_BS + " TEXT," + KEY_PROGRES_PCL + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_PCL_TABLE);

        String CREATE_BEBAN_TABLE = "CREATE TABLE " + TABLE_BS + "("
                + KEY_NIM + " TEXT," + KEY_KEl + " TEXT," + KEY_KEC + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_BEBAN_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_KORTIM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PCL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KORTIM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PCL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BS);

        // Create tables again
        onCreate(db);

    }

    public void addBebanBs(BebanBlokSensus bbs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NIM, bbs.getNim());
        values.put(KEY_KEl, bbs.getKel());
        values.put(KEY_KEC, bbs.getKec());

        // Inserting Row
        db.insert(TABLE_KORTIM, null, values);
        db.close(); // Closing database connection
    }


    public void addAnggota(AnggotaTim anggotaTim) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NIM, anggotaTim.getNimAng());
        values.put(KEY_NAME, anggotaTim.getNamaAng());
        values.put(KEY_PROGRES, anggotaTim.getProgres());
        values.put(KEY_NOMOR, anggotaTim.getNomorAng());

        // Inserting Row
        db.insert(TABLE_KORTIM, null, values);
        db.close(); // Closing database connection
    }

    public void addProgresBs(ProgressBlokPcl pbp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BS, pbp.getNoBlokSensus());
        values.put(KEY_PROGRES_PCL, pbp.getProgressBlokSensus());


        // Inserting Row

        db.insert(TABLE_PCL, null, values);
        db.close(); // Closing database connection
    }

    public List<AnggotaTim> getAllAnggota() {
        List<AnggotaTim> listAnggota = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_KORTIM;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AnggotaTim a = new AnggotaTim(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(4));

                // Adding contact to list
                listAnggota.add(a);
            } while (cursor.moveToNext());
        }

        // return contact list
        return listAnggota;
    }

    public List<BebanBlokSensus> getBbsByNim(String nim) {
        List<BebanBlokSensus> bbs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BS + " WHERE " + KEY_NIM + " = '" + nim + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    BebanBlokSensus b = new BebanBlokSensus(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                    bbs.add(b);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("getBbsByNim", "Error", e);
        }

        return bbs;
    }

    public String getNamaByNim(String nim) {
        String nama = "";
        String selectQuery = "SELECT " + KEY_NAME + " FROM " + TABLE_KORTIM + " WHERE " + KEY_NIM + " = '" + nim + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    nama = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("getNamaByNim", "Error", e);
        }

        return nama;
    }

    public List<String> getAllNim() {
        List<String> nims = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_NIM + " FROM " + TABLE_KORTIM;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    nims.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("getAllNim", "Error", e);
        }
        return nims;
    }

    public List<ProgressBlokPcl> getAllProgres() {

        List<ProgressBlokPcl> listProgres = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PCL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProgressBlokPcl p = new ProgressBlokPcl(cursor.getString(0), cursor.getString(1));

                // Adding contact to list
                listProgres.add(p);
            } while (cursor.moveToNext());
        }

        // return contact list
        return listProgres;
    }

    public boolean updateProgresBsPcl(ProgressBlokPcl pbp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROGRES_PCL, pbp.getProgressBlokSensus());

        // updating row
        int a = db.update(TABLE_PCL, values, KEY_BS + " = ?",
                new String[]{String.valueOf(pbp.getNoBlokSensus())});
        return a > 0;

    }

    public boolean updateProgresTim(AnggotaTim at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, at.getNamaAng());
        values.put(KEY_PROGRES, at.getProgres());

        // updating row
        int a = db.update(TABLE_KORTIM, values, KEY_NIM + " = ?",
                new String[]{String.valueOf(at.getNimAng())});
        return a > 0;
    }
}
