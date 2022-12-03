package org.odk.collect.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.pkl.object.Notifikasi;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ItemsetDbAdapter {

    public static final String KEY_ID = "_id";

    private static final String TAG = "ItemsetDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "itemsets.db";
    private static final String DATABASE_TABLE = "itemset_";
    private static final int DATABASE_VERSION = 2;

    private static final String ITEMSET_TABLE = "itemsets";
    private static final String KEY_ITEMSET_HASH = "hash";
    private static final String KEY_PATH = "path";


    private static final String CREATE_ITEMSET_TABLE =
            "create table " + ITEMSET_TABLE + " (_id integer primary key autoincrement, "
                    + KEY_ITEMSET_HASH + " text, "
                    + KEY_PATH + " text "
                    + ");";
    private static final String ITEMSET_TABLE_ERROR = "errorsets";
    private static final String CREATE_ITEMSET_TABLE_ERROR = "CREATE TABLE " + ITEMSET_TABLE_ERROR + " ("
            + InstanceProviderAPI.InstanceColumns._ID + " integer primary key AUTOINCREMENT , "
            + InstanceProviderAPI.InstanceColumns.JR_FORM_ID + " text not null, "
            + InstanceProviderAPI.InstanceColumns.JR_VERSION + " text, "
            + InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH + " text, "
            + InstanceProviderAPI.InstanceColumns.XPATH + " text not null, "
            + InstanceProviderAPI.InstanceColumns.NULLXPATH + " text not null, "
            + InstanceProviderAPI.InstanceColumns.UUID + " text not null, "
            + InstanceProviderAPI.InstanceColumns.CHECK + " text ,"
            + InstanceProviderAPI.InstanceColumns.KIND + " text )";


    private static final String CREATE_CONTACTS_TABLE_GAGAL = "CREATE TABLE " + Notifikasi.TASKS_TABLE_GAGAL + "("
            +
            Notifikasi.TASK_INSTANCE + " TEXT," +
            Notifikasi.TASK_NIM + " TEXT," +
            Notifikasi.TASK_NAMA + " TEXT, " +
            Notifikasi.TASK_KORTIM + " TEXT," +
            Notifikasi.TASK_STATUS_ISIAN + " TEXT ," +
            Notifikasi.TASK_STATUS + " TEXT," +
            Notifikasi.TASK_FORM + " TEXT," +
            Notifikasi.TASK_FILENAME + " TEXT "
            + ")";
    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Notifikasi.TASKS_TABLE + "("
            + Notifikasi.TASK_ID + " INTEGER PRIMARY KEY ," +
            Notifikasi.TASK_INSTANCE + " TEXT," +
            Notifikasi.TASK_NIM + " TEXT," +
            Notifikasi.TASK_NAMA + " TEXT, " +
            Notifikasi.TASK_KORTIM + " TEXT," +
            Notifikasi.TASK_STATUS_ISIAN + " TEXT ," +
            Notifikasi.TASK_STATUS + " TEXT," +
            Notifikasi.TASK_FORM + " TEXT," +
            Notifikasi.TASK_FILENAME + " TEXT "
            + ")";


    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends ODKSQLiteOpenHelper {
        DatabaseHelper() {
            super(Collect.METADATA_PATH, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("RAHADI", "Metadata Path in ItemsetDbAdapter : " + Collect.METADATA_PATH);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create table to keep track of the itemsets
            db.execSQL(CREATE_ITEMSET_TABLE);
            db.execSQL(CREATE_ITEMSET_TABLE_ERROR);
            db.execSQL(CREATE_CONTACTS_TABLE);
            db.execSQL(CREATE_CONTACTS_TABLE_GAGAL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            // first drop all of our generated itemset tables
            Cursor c = db.query(ITEMSET_TABLE, null, null, null, null, null, null);
            if (c != null) {
                c.move(-1);
                while (c.moveToNext()) {
                    String table = c.getString(c.getColumnIndex(KEY_ITEMSET_HASH));
                    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + table);
                }
                c.close();
            }

            // then drop the table tracking itemsets itself
            db.execSQL("DROP TABLE IF EXISTS " + ITEMSET_TABLE);
            onCreate(db);
        }
    }

    public ItemsetDbAdapter() {
    }

    /**
     * Open the database. If it cannot be opened, try to create a new instance
     * of the database. If it cannot be created, throw an exception to signal
     * the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ItemsetDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper();
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean createTable(String formHash, String pathHash, String[] columns, String path) {
        StringBuilder sb = new StringBuilder();


        // get md5 of the path to itemset.csv, which is unique per form
        // the md5 is easier to use because it doesn't have chars like '/'

        sb.append("create table " + DATABASE_TABLE + pathHash
                + " (_id integer primary key autoincrement ");
        for (int j = 0; j < columns.length; j++) {
            // add double quotes in case the column is of label:lang
            sb.append(" , \"" + columns[j] + "\" text ");
            // create database with first line
        }
        sb.append(");");

        String tableCreate = sb.toString();
        Log.i(TAG, "create string: " + tableCreate);
        mDb.execSQL(tableCreate);

        ContentValues cv = new ContentValues();
        cv.put(KEY_ITEMSET_HASH, formHash);
        cv.put(KEY_PATH, path);
        mDb.insert(ITEMSET_TABLE, null, cv);

        return true;
    }

    public boolean addRow(String tableName, String[] columns, String[] newRow) {
        ContentValues cv = new ContentValues();

        // rows don't necessarily use all the columns
        // but a column is guaranteed to exist for a row (or else blow up)
        for (int i = 0; i < newRow.length; i++) {
            cv.put("\"" + columns[i] + "\"", newRow[i]);
        }
        mDb.insert(DATABASE_TABLE + tableName, null, cv);
        return true;
    }

    public boolean tableExists(String tableName) {
        // select name from sqlite_master where type = 'table'
        String selection = "type=? and name=?";
        String selectionArgs[] = {
                "table", DATABASE_TABLE + tableName
        };
        Cursor c = mDb.query("sqlite_master", null, selection, selectionArgs,
                null, null, null);
        boolean exists = false;
        if (c.getCount() == 1) {
            exists = true;
        }
        c.close();
        return exists;

    }

    public void beginTransaction() {
        mDb.execSQL("BEGIN");
    }

    public void commit() {
        mDb.execSQL("COMMIT");
    }

    public Cursor query(String hash, String selection, String[] selectionArgs) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE + hash, null, selection, selectionArgs,
                null, null, null, null);
        return mCursor;
    }

    public void dropTable(String pathHash, String path) {
        // drop the table
        mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + pathHash);

        // and remove the entry from the itemsets table
        String where = KEY_PATH + "=?";
        String[] whereArgs = {
                path
        };
        mDb.delete(ITEMSET_TABLE, where, whereArgs);
    }

    public Cursor getItemsets(String path) {
        String selection = KEY_PATH + "=?";
        String[] selectionArgs = {
                path
        };
        Cursor c = mDb.query(ITEMSET_TABLE, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public void delete(String path) {
        Cursor c = getItemsets(path);
        if (c != null) {
            if (c.getCount() == 1) {
                c.moveToFirst();
                String table = getMd5FromString(c.getString(c.getColumnIndex(KEY_PATH)));
                mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + table);
            }
            c.close();
        }

        String where = KEY_PATH + "=?";
        String[] whereArgs = {
                path
        };
        mDb.delete(ITEMSET_TABLE, where, whereArgs);
    }

    public static String getMd5FromString(String toEncode) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("MD5", e.getMessage());
        }
        md.update(toEncode.getBytes());
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        return hashtext;
    }

    //baru
    //baru
    public ArrayList<DBErrorModel> getAllDBError() {
        String b = "SELECT * FROM " + ITEMSET_TABLE_ERROR;
        ArrayList<DBErrorModel> dbModel = new ArrayList<DBErrorModel>();
        try {
            Cursor c = mDb.rawQuery(b, null);
            DBErrorModel a = null;
            if (c.moveToFirst()) {
                do {
                    a = new DBErrorModel();
                    a.setJr_Form_Id(c.getString(1));
                    if (c.getString(2) != null) {
                        a.setJR_Forn_Version(c.getString(2));
                    }
                    a.setInstance_Path(c.getString(3));
                    a.setXPath(c.getString(4));
                    a.setNullXPath(c.getString(5));
                    a.setUUID(c.getString(6));

                    dbModel.add(a);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("budi", "" + ex);
        }
        Log.d("budi", "getAllError " + (mDb == null));
        return dbModel;
    }

    public ArrayList<DBErrorModel> getAllDBError(String Instancepath) {
        Log.d("budi", "" + (mDb == null));
        String b = "SELECT * FROM " + ITEMSET_TABLE_ERROR + " WHERE " + InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH + " = '" + Instancepath + "' ";
        Log.d("add get str", b);
        ArrayList<DBErrorModel> dbModel = new ArrayList<DBErrorModel>();
        try {
            Cursor c = mDb.rawQuery(b, null);
            DBErrorModel a = null;
            if (c.moveToFirst()) {
                do {
                    a = new DBErrorModel();
                    a.setJr_Form_Id(c.getString(1));
                    if (c.getString(2) != null) {
                        a.setJR_Forn_Version(c.getString(2));
                    }
                    a.setInstance_Path(c.getString(3));
                    a.setXPath(c.getString(4));
                    a.setNullXPath(c.getString(5));
                    a.setUUID(c.getString(6));
                    a.setCheck(c.getString(7));
                    a.setKind(c.getString(8));
                    dbModel.add(a);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("budi", "" + ex);
        }
        Log.d("budi", "getAll");
        return dbModel;
    }

    public boolean isErrorExist(DBErrorModel a) {
        ArrayList<DBErrorModel> dbModel = new ArrayList<DBErrorModel>();
        boolean y = false;
        dbModel = getAllDBError();
        Log.d("budi size", "" + dbModel.size());
        try {
            for (int x = 0; x < dbModel.size(); x++) {
                Log.d("budi e", dbModel.get(x).getJR_Forn_Id());
                Log.d("budi e", dbModel.get(x).getXPath());
                Log.d("budi e", dbModel.get(x).getInstance_Path());
                if ((dbModel.get(x).getJR_Forn_Id().equals(a.getJR_Forn_Id())) && (dbModel.get(x).getInstance_Path().equals(a.getInstance_Path())) && (dbModel.get(x).getNullXPath().equals(a.getNullXPath()))
                        && dbModel.get(x).getKind().equals(a.getKind())) {
                    y = true;
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d("budi", "" + ex);
        }
        Log.d("ex exis", "isexist?" + y);
        return y;
    }

    public void addRowErrors(ArrayList<DBErrorModel> es) {
        for (DBErrorModel e : es) {
            addRowError(e);
        }
    }

    public boolean addRowError(DBErrorModel deberr) {
        boolean ae = isErrorExist(deberr);

        if (!ae) {
            try {
                ContentValues value = new ContentValues();
                value.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, deberr.getJR_Forn_Id());
                if (deberr.getJR_Forn_Version() != null) {
                    value.put(InstanceProviderAPI.InstanceColumns.JR_VERSION, deberr.getJR_Forn_Version());
                }
                value.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, deberr.getInstance_Path());
                value.put(InstanceProviderAPI.InstanceColumns.XPATH, deberr.getXPath());
                value.put(InstanceProviderAPI.InstanceColumns.NULLXPATH, deberr.getNullXPath());
                value.put(InstanceProviderAPI.InstanceColumns.UUID, deberr.getUUID());
                value.put(InstanceProviderAPI.InstanceColumns.CHECK, deberr.isCheck());
                value.put(InstanceProviderAPI.InstanceColumns.KIND, deberr.getKind());
                mDb.insert(ITEMSET_TABLE_ERROR, null, value);

            } catch (Exception ex) {
                Log.d("addrow + addrow", "" + ex);
            }

            return true;
        } else {
            try {
                ContentValues value = new ContentValues();
                value.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, deberr.getJR_Forn_Id());
                if (deberr.getJR_Forn_Version() != null) {
                    value.put(InstanceProviderAPI.InstanceColumns.JR_VERSION, deberr.getJR_Forn_Version());
                }
                value.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, deberr.getInstance_Path());
                value.put(InstanceProviderAPI.InstanceColumns.XPATH, deberr.getXPath());
                value.put(InstanceProviderAPI.InstanceColumns.NULLXPATH, deberr.getNullXPath());
                value.put(InstanceProviderAPI.InstanceColumns.UUID, deberr.getUUID());
                value.put(InstanceProviderAPI.InstanceColumns.CHECK, deberr.isCheck());
                value.put(InstanceProviderAPI.InstanceColumns.KIND, deberr.getKind());
                mDb.update(ITEMSET_TABLE_ERROR, value, null, null);
            } catch (Exception ex) {
                Log.d("budi + update", "" + ex);
            }
            return true;
        }

    }

    public boolean deleterowerror(DBErrorModel db) {
        boolean ae = isErrorExist(db);
        if (ae) {
            String delete = "DELETE FROM " + ITEMSET_TABLE_ERROR + " WHERE " + InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH
                    + " = '" + db.getInstance_Path() + "' AND " + InstanceProviderAPI.InstanceColumns.JR_FORM_ID
                    + " = '" + db.getJR_Forn_Id() + "' AND " + InstanceProviderAPI.InstanceColumns.XPATH
                    + " = '" + db.getXPath() + "' ";
            try {
                mDb.execSQL(delete);
            } catch (Exception ex) {
                Log.d("budi", "Delete Error " + ex);
            }
            Log.d("budi", delete);
            return true;
        }
        return false;
    }

    public boolean deleterowerrors(String form_id, String Instance_id) {
        String delete = "DELETE FROM " + ITEMSET_TABLE_ERROR + " WHERE " + InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH
                + " = '" + Instance_id + "' AND " + InstanceProviderAPI.InstanceColumns.JR_FORM_ID
                + " = '" + form_id + "' ";
        try {
            mDb.execSQL(delete);
        } catch (Exception ex) {
            Log.d("budi", "Delete Error " + ex);
        }
        Log.d("budi", delete);
        return true;
    }

    public void onSaveData(Notifikasi getNotif) {
        ContentValues values = new ContentValues();
        values.put(Notifikasi.TASK_ID, getNotif.getId());
        values.put(Notifikasi.TASK_INSTANCE, getNotif.getUnique_id_instance());
        values.put(Notifikasi.TASK_NIM, getNotif.getNim());
        values.put(Notifikasi.TASK_NAMA, getNotif.getNama());
        values.put(Notifikasi.TASK_KORTIM, getNotif.getKortim());
        values.put(Notifikasi.TASK_STATUS_ISIAN, getNotif.getStatus_isian());
        values.put(Notifikasi.TASK_STATUS, getNotif.getStatus());
        values.put(Notifikasi.TASK_FORM, getNotif.getForm_id());
        values.put(Notifikasi.TASK_FILENAME, getNotif.getFilename());
        try {
            mDb.insert(Notifikasi.TASKS_TABLE, null, values);
//            Log.d("saNotifikasi","Save " + getNotif.getUnique_id_instance());
        } catch (Exception ex) {
            Log.d("saNotifikasi", "" + ex);
        }
    }

    public LinkedList<Notifikasi> getAllNotifikasi() {
        LinkedList<Notifikasi> allnotif = new LinkedList<>();
        String query = "SELECT * FROM " + Notifikasi.TASKS_TABLE;
        try {
            Cursor c = mDb.rawQuery(query, null);
            Log.d("RAHADI", "Cursor Size : " + c.getCount());
            Notifikasi a = null;
            if (c.moveToFirst()) {
                do {
                    a = new Notifikasi();
                    a.setId(Integer.valueOf(c.getString(c.getColumnIndex(Notifikasi.TASK_ID))));
                    a.setUnique_id_instance(c.getString(c.getColumnIndex(Notifikasi.TASK_INSTANCE)));
                    a.setStatus(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS)));
                    a.setStatus_isian(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS_ISIAN)));
                    a.setFilename(c.getString(c.getColumnIndex(Notifikasi.TASK_FILENAME)));
                    a.setForm_id(c.getString(c.getColumnIndex(Notifikasi.TASK_FORM)));
                    a.setNim(c.getString(c.getColumnIndex(Notifikasi.TASK_NIM)));
                    a.setNama(c.getString(c.getColumnIndex(Notifikasi.TASK_NAMA)));
                    a.setKortim(c.getString(c.getColumnIndex(Notifikasi.TASK_KORTIM)));
                    allnotif.addLast(a);
                    Log.d("saNotifikasi getall", a.getUnique_id_instance());
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return allnotif;
    }

    public void updateNotif(Notifikasi b) {
        String where = Notifikasi.TASK_FILENAME + " =? ";
        String whereargs[] = {
                b.getFilename()
        };
        try {
            mDb.delete(Notifikasi.TASKS_TABLE, where, whereargs);
            onSaveData(b);
        } catch (Exception ex) {
            Log.d("Notifikasi", "masalah di " + ex);
        }

    }

    public int getnotiflastid() {
        String que = "SELECT * FROM " + Notifikasi.TASKS_TABLE
                + " ORDER BY id DESC LIMIT 1";
        int last = 0;
        try {
            Cursor c = mDb.rawQuery(que, null);
            Notifikasi a = null;
            if (c.moveToFirst()) {
                do {
                    last = Integer.valueOf(c.getString(c.getColumnIndex(Notifikasi.TASK_ID)));
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return last;
    }

    //    public void updatedown(Notifikasi b){
//        String where = Notifikasi.TASK_INSTANCE + " =? ";
//        String whereargs[] = {
//                b.getUnique_id_instance()
//        };
//        ContentValues values = new ContentValues();
//        values.put(Notifikasi.TASK_DOWN,"1");
//        try {
//            mDb.update(Notifikasi.TASKS_TABLE,values, where, whereargs);
//            onSaveData(b);
//        } catch (Exception ex) {
//            Log.d("Notifikasi", "masalah di " + ex);
//        }
//    }
    public Notifikasi getNotifikasibyId(String UID) {
        Notifikasi a = null;
        String query = "SELECT * FROM " + Notifikasi.TASKS_TABLE + " WHERE " + Notifikasi.TASK_INSTANCE + " = '" + UID + "'";
        try {
            Cursor c = mDb.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    a = new Notifikasi();
                    a.setId(Integer.valueOf(c.getString(c.getColumnIndex(Notifikasi.TASK_ID))));
                    a.setUnique_id_instance(c.getString(c.getColumnIndex(Notifikasi.TASK_INSTANCE)));
                    a.setStatus(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS)));
                    a.setStatus_isian(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS_ISIAN)));
                    a.setFilename(c.getString(c.getColumnIndex(Notifikasi.TASK_FILENAME)));
                    a.setForm_id(c.getString(c.getColumnIndex(Notifikasi.TASK_FORM)));
                    a.setNim(c.getString(c.getColumnIndex(Notifikasi.TASK_NIM)));
                    a.setNama(c.getString(c.getColumnIndex(Notifikasi.TASK_NAMA)));
                    a.setKortim(c.getString(c.getColumnIndex(Notifikasi.TASK_KORTIM)));
                    Log.d("saNotifikasi getall", a.getUnique_id_instance());
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return a;
    }

    public Notifikasi getNotifikasibyname(String name) {
        Notifikasi a = null;
        String query = "SELECT * FROM " + Notifikasi.TASKS_TABLE + " WHERE " + Notifikasi.TASK_FILENAME + " = '" + name + "'";
        try {
            Cursor c = mDb.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    a = new Notifikasi();
                    a.setId(Integer.valueOf(c.getString(c.getColumnIndex(Notifikasi.TASK_ID))));
                    a.setUnique_id_instance(c.getString(c.getColumnIndex(Notifikasi.TASK_INSTANCE)));
                    a.setStatus(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS)));
                    a.setStatus_isian(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS_ISIAN)));
                    a.setFilename(c.getString(c.getColumnIndex(Notifikasi.TASK_FILENAME)));
                    a.setForm_id(c.getString(c.getColumnIndex(Notifikasi.TASK_FORM)));
                    a.setNim(c.getString(c.getColumnIndex(Notifikasi.TASK_NIM)));
                    a.setNama(c.getString(c.getColumnIndex(Notifikasi.TASK_NAMA)));
                    a.setKortim(c.getString(c.getColumnIndex(Notifikasi.TASK_KORTIM)));
                    Log.d("saNotifikasi getall", a.getUnique_id_instance());
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return a;
    }

    public void deleteNotif() {
        mDb.delete(Notifikasi.TASKS_TABLE, null, null);
    }

    public ArrayList<Notifikasi> getNotifikasibyids(String lastid) {
        ArrayList<Notifikasi> notifs = new ArrayList<Notifikasi>();
        String query = "SELECT * FROM " + Notifikasi.TASKS_TABLE + " WHERE " + Notifikasi.TASK_ID + " > '" + lastid + "'";
        try {
            Cursor c = mDb.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    Notifikasi a = new Notifikasi();
                    a.setId(Integer.valueOf(c.getString(c.getColumnIndex(Notifikasi.TASK_ID))));
                    a.setUnique_id_instance(c.getString(c.getColumnIndex(Notifikasi.TASK_INSTANCE)));
                    a.setStatus(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS)));
                    a.setStatus_isian(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS_ISIAN)));
                    a.setFilename(c.getString(c.getColumnIndex(Notifikasi.TASK_FILENAME)));
                    a.setForm_id(c.getString(c.getColumnIndex(Notifikasi.TASK_FORM)));
                    a.setNim(c.getString(c.getColumnIndex(Notifikasi.TASK_NIM)));
                    a.setNama(c.getString(c.getColumnIndex(Notifikasi.TASK_NAMA)));
                    a.setKortim(c.getString(c.getColumnIndex(Notifikasi.TASK_KORTIM)));
                    notifs.add(a);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return notifs;
    }

    public boolean changeStatus(String filename, String baru) {
        try {
            ContentValues value = new ContentValues();
            value.put(Notifikasi.TASK_STATUS, baru);
            String where = Notifikasi.TASK_FILENAME + " =? ";
            String[] whereargs = {filename};
            mDb.update(ITEMSET_TABLE_ERROR, value, where, whereargs);
        } catch (Exception ex) {
            Log.d("budi + update", "" + ex);
        }
        return true;
    }

    public void addNotifikasiGagal(ArrayList<Notifikasi> notifs) {
        for (int i = 0; i < notifs.size(); i++) {
            Notifikasi getNotif = notifs.get(i);
            ContentValues values = new ContentValues();
            values.put(Notifikasi.TASK_INSTANCE, getNotif.getUnique_id_instance());
            values.put(Notifikasi.TASK_NIM, getNotif.getNim());
            values.put(Notifikasi.TASK_NAMA, getNotif.getNama());
            values.put(Notifikasi.TASK_KORTIM, getNotif.getKortim());
            values.put(Notifikasi.TASK_STATUS_ISIAN, getNotif.getStatus_isian());
            values.put(Notifikasi.TASK_STATUS, getNotif.getStatus());
            values.put(Notifikasi.TASK_FORM, getNotif.getForm_id());
            values.put(Notifikasi.TASK_FILENAME, getNotif.getFilename());
            try {
                mDb.insert(Notifikasi.TASKS_TABLE_GAGAL, null, values);
            } catch (Exception ex) {
                Log.d("saNotifikasi", "" + ex);
            }
        }
    }

    public void deleteNotifikasiGagalS(ArrayList<Notifikasi> notifs) {
        for (int i = 0; i < notifs.size(); i++) {
            Notifikasi getNotif = notifs.get(i);
            String where = Notifikasi.TASK_INSTANCE + " =? ";
            String[] whereargs = {getNotif.getUnique_id_instance()};
            try {
                mDb.delete(Notifikasi.TASKS_TABLE_GAGAL, where, whereargs);
            } catch (Exception ex) {
                Log.d("saNotifikasi", "" + ex);
            }
        }
    }

    public void deleteNotifikasiGagal(Notifikasi notifs) {

        String where = Notifikasi.TASK_INSTANCE + " =? ";
        String[] whereargs = {notifs.getUnique_id_instance()};
        try {
            mDb.delete(Notifikasi.TASKS_TABLE_GAGAL, where, whereargs);
        } catch (Exception ex) {
            Log.d("saNotifikasi", "" + ex);
        }

    }

    public ArrayList<Notifikasi> getFailedNotifikasi() {
        ArrayList<Notifikasi> notifs = new ArrayList<Notifikasi>();
        String query = "SELECT * FROM " + Notifikasi.TASKS_TABLE_GAGAL;
        try {
            Cursor c = mDb.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    Notifikasi a = new Notifikasi();
                    a.setUnique_id_instance(c.getString(c.getColumnIndex(Notifikasi.TASK_INSTANCE)));
                    a.setStatus(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS)));
                    a.setStatus_isian(c.getString(c.getColumnIndex(Notifikasi.TASK_STATUS_ISIAN)));
                    a.setFilename(c.getString(c.getColumnIndex(Notifikasi.TASK_FILENAME)));
                    a.setForm_id(c.getString(c.getColumnIndex(Notifikasi.TASK_FORM)));
                    a.setNim(c.getString(c.getColumnIndex(Notifikasi.TASK_NIM)));
                    a.setNama(c.getString(c.getColumnIndex(Notifikasi.TASK_NAMA)));
                    a.setKortim(c.getString(c.getColumnIndex(Notifikasi.TASK_KORTIM)));
                    notifs.add(a);
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.d("Notifikasi ", "masalah di " + ex);
        }
        return notifs;
    }
}
