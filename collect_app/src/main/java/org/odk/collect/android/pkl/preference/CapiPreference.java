package org.odk.collect.android.pkl.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.odk.collect.android.application.Collect;

import java.util.Set;

import static org.odk.collect.android.pkl.preference.CapiKey.CAPI_KEYS;

/**
 * @author Mahendri Dwicahyo
 */

public class CapiPreference {

    private static final String CAPI_PREF = "capi_pref";

    private static CapiPreference capiPreference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private CapiPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(CAPI_PREF, Context.MODE_PRIVATE);
    }

    public static synchronized CapiPreference getInstance() {
        if (capiPreference == null)
            capiPreference = new CapiPreference(Collect.getInstance());
        return capiPreference;
    }

    public Object get(String key) {
        Object defaultValue = null;
        Object value = null;

        try {
            defaultValue = CAPI_KEYS.get(key);
        } catch (Exception e) {
            Log.e(CapiPreference.class.getSimpleName(), "default not found");
        }

        if (defaultValue == null || defaultValue instanceof String) {
            value = sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            value = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Long) {
            value = sharedPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Integer) {
            value = sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Float) {
            value = sharedPreferences.getFloat(key, (Float) defaultValue);
        }
        return value;
    }

    public boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public void save(String key, Object value) {
        editor = sharedPreferences.edit();
        if (value == null || value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        } else {
            throw new RuntimeException("Unhandled preference value type: " + value);
        }
        editor.apply();
    }

    /**
     * clear all data in capi preference
     */
    public void clearCapiData() {
        if (editor == null) editor = sharedPreferences.edit();
        editor.clear().apply();
    }
}
