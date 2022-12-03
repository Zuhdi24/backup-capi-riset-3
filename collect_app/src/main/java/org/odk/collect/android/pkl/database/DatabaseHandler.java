package org.odk.collect.android.pkl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.odk.collect.android.pkl.network.RequestHandler;
import org.odk.collect.android.pkl.object.Kondef;
import org.odk.collect.android.pkl.object.NotificationModel;

import java.util.LinkedList;

/**
 * @author Rahadi
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;

    private static DatabaseHandler sInstance;
    private static final int DATABASE_VERSION = 13;
    private static final String DATABASE_NAME = "pkl_jarkom";

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context);
        }
        return sInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_JARKOM);
        db.execSQL(CREATE_TABLE_KATEGORI_LIST);
        db.execSQL(CREATE_TABLE_STATUS);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + Kondef.TABLE_JARKOM);
        db.execSQL(drop + Kondef.TABLE_KATEGORI_LIST);
        db.execSQL(drop + Kondef.TABLE_STATUS);
        db.execSQL(drop + Kondef.TABLE_NOTIFICATION);
        onCreate(db);
    }

    public void dropAllTables() {
        String drop = "DROP TABLE IF EXISTS ";
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(drop + Kondef.TABLE_JARKOM);
        db.execSQL(drop + Kondef.TABLE_KATEGORI_LIST);
        db.execSQL(drop + Kondef.TABLE_STATUS);
        db.execSQL(drop + Kondef.TABLE_NOTIFICATION);

        onCreate(db);
    }

    public LinkedList<NotificationModel> getNotifications(String nim, boolean isOnline) {
        LinkedList<NotificationModel> notificationModels;
        Log.d("TEMPDEBUGJARKOM", "is Online : " + isOnline);
        if (!isOnline) {
            notificationModels = new LinkedList<>();
            try {
                String sql = "SELECT * FROM " + Kondef.TABLE_NOTIFICATION;
                SQLiteDatabase db = getInstance(context).getReadableDatabase();
                Cursor c = db.rawQuery(sql, null);
                if (c.moveToFirst()) {
                    NotificationModel notificationModel;
                    do {
                        notificationModel = new NotificationModel(c.getInt(c.getColumnIndex(Kondef.AT_ID)),
                                c.getString(c.getColumnIndex(Kondef.AT_KATEGORI)), c.getString(c.getColumnIndex(Kondef.AT_JUDUL)),
                                c.getString(c.getColumnIndex(Kondef.AT_KONTEN)), c.getString(c.getColumnIndex(Kondef.AT_TIMESTAMP)),
                                c.getString(c.getColumnIndex(Kondef.AT_NIM)), c.getInt(c.getColumnIndex(Kondef.AT_READ)));
                        notificationModels.addFirst(notificationModel);
                    } while (c.moveToNext());
                }
                c.close();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return notificationModels;
        } else {
            notificationModels = RequestHandler.getInstance(context).getNotifications(nim);
            if (notificationModels != null) {
                SQLiteDatabase dbupdateshow = getInstance(context).getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(Kondef.AT_SHOWED, "0");
                dbupdateshow.update(Kondef.TABLE_NOTIFICATION, v, null, null);

                for (NotificationModel n : notificationModels) {
                    try {
                        SQLiteDatabase db = getInstance(context).getWritableDatabase();
                        ContentValues v1 = new ContentValues();

                        v1.put(Kondef.AT_ID, n.getId());
                        v1.put(Kondef.AT_KATEGORI, n.getKategori());
                        v1.put(Kondef.AT_JUDUL, n.getJudul());
                        v1.put(Kondef.AT_KONTEN, n.getKonten());
                        v1.put(Kondef.AT_NIM, n.getNim());
                        v1.put(Kondef.AT_TIMESTAMP, n.getDate());
                        v1.put(Kondef.AT_SHOWED, "1");

                        if (recordExists(Kondef.TABLE_NOTIFICATION,
                                String.valueOf(n.getId()))) {
                            db.update(Kondef.TABLE_NOTIFICATION, v1, Kondef.AT_ID + " = ?",
                                    new String[]{String.valueOf(n.getId())});
                        } else {
                            db.insert(Kondef.TABLE_NOTIFICATION, null, v1);
                        }
                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SQLiteDatabase dbdelete = getInstance(context).getWritableDatabase();
                dbdelete.delete(Kondef.TABLE_NOTIFICATION, Kondef.AT_SHOWED + " = ?", new String[]{"0"});

            }
        }
        return getNotifications(nim, false);
    }

    public int getNotificationUnreadCount() {
        String sql = "SELECT COUNT(*) FROM " + Kondef.TABLE_NOTIFICATION + " WHERE " + Kondef.AT_READ + " IS NULL";
        try {
            SQLiteDatabase sqLiteDatabase = getInstance(context).getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean setRead(int notificationId, boolean read) {
        if (recordExists(Kondef.TABLE_NOTIFICATION, String.valueOf(notificationId))) {
            SQLiteDatabase db = getInstance(context).getWritableDatabase();
            ContentValues v1 = new ContentValues();

            v1.put(Kondef.AT_READ, read ? 1 : 0);
            db.update(Kondef.TABLE_NOTIFICATION, v1, Kondef.AT_ID + " = ?",
                    new String[]{String.valueOf(notificationId)});
            return true;
        }
        return false;
    }

    public boolean getIsReadStatusById(int id) {
        try {
            String sql = "SELECT " + Kondef.AT_READ + " FROM " + Kondef.TABLE_NOTIFICATION + " WHERE " +
                    Kondef.AT_ID + " = '" + id + "'";
            SQLiteDatabase db = getInstance(context).getReadableDatabase();
            Cursor c = db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                Log.d("TEMPJARKOM", "Id : " + id);
                Log.d("TEMPJARKOM", "Int : " + c.getInt(0));
                return c.getInt(0) == 1;
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean recordExists(String table, String id) {
        String sql = "SELECT * FROM " + table + " WHERE " + Kondef.AT_ID + " = '" + id + "'";
        try {
            SQLiteDatabase sqLiteDatabase = getInstance(context).getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String CREATE_TABLE_JARKOM = "CREATE TABLE " +
            Kondef.TABLE_JARKOM + "(" +
            Kondef.AT_ID + " INT(10), " +
            Kondef.AT_NIM + " VARCHAR(7), " +
            Kondef.AT_NIM_KORTIM + " VARCHAR(7), " +
            Kondef.AT_WILAYAH + " VARCHAR(10), " +
            Kondef.AT_KATEGORI + " INT(2), " +
            Kondef.AT_PERTANYAAN + " VARCHAR(160), " +
            Kondef.AT_JAWABAN + " TEXT, " +
            Kondef.AT_GOLONGAN + " VARCHAR(10), " +
            Kondef.AT_STATUS + " INT(1), " +
            Kondef.AT_TIMESTAMP + " TIMESTAMP," +
            Kondef.AT_BOOKMARKED + " INT(1)," +
            Kondef.AT_SHOWED + " INT(1))"; //>>Local Attribute

    private static final String CREATE_TABLE_KATEGORI_LIST = "CREATE TABLE " +
            Kondef.TABLE_KATEGORI_LIST + "(" +
            Kondef.AT_ID + " INT(2), " +
            Kondef.AT_KATEGORI + " VARCHAR(20), " +
            Kondef.AT_DESKRIPSI + " TEXT, " +
            Kondef.AT_PENANGGUNG_JAWAB + " VARCHAR(10)," +
            Kondef.AT_SHOWED + " INT(1))"; //>>Local Attribute

    private static final String CREATE_TABLE_STATUS = "CREATE TABLE " +
            Kondef.TABLE_STATUS + "(" +
            Kondef.AT_ID + " INT(2), " +
            Kondef.AT_STATUS + " TEXT, " +
            Kondef.AT_SHOWED + " INT(1))"; //>>Local Attribute

    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " +
            Kondef.TABLE_NOTIFICATION + "(" +
            Kondef.AT_ID + " INT(10), " +
            Kondef.AT_JUDUL + " TEXT, " +
            Kondef.AT_KATEGORI + " TEXT, " +
            Kondef.AT_KONTEN + " TEXT, " +
            Kondef.AT_NIM + " VARCHAR(7), " +
            Kondef.AT_TIMESTAMP + " TIMESTAMP, " +
            Kondef.AT_READ + " INT(1), " + //>>Local Attribute
            Kondef.AT_SHOWED + " INT(1))"; //>>Local Attribute
}
