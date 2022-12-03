package org.odk.collect.android.pkl.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author TimHode
 */

public class SyncManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // nama sharepreference
    private static final String PREF_NAME = "sync_pkl56";

    // all key pref
    private static final String IS_SYNC = "IsSync";
    private static final String KEY_LSYNC = "last_sync";
    private static final String KEY_DEVICEID = "device_id";

    public SyncManager(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //def isSync = false
    public void createSync(String last_sync, String device_id) {
        //setting is_sync firt on login is false
        editor.putBoolean(IS_SYNC, false);
        editor.putString(KEY_LSYNC, last_sync);
        editor.putString(KEY_DEVICEID, device_id);
        editor.commit();
    }

    public void setSync(boolean sync) {
        editor.putBoolean(IS_SYNC, sync);
        editor.commit();
    }

    public boolean isSync() {
        return pref.getBoolean(IS_SYNC, false);
    }

    public void setLastSync(String date) {
        editor.putString(KEY_LSYNC, date);
        editor.commit();
    }

    public String getLastSync() {
        return pref.getString(KEY_LSYNC, null);
    }

    public String getDeviceId() {
        return pref.getString(KEY_DEVICEID, null);
    }
}

