package com.rtu.uberv.divinote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.rtu.uberv.divinote.DiviNoteApplication;

import java.util.Map;
import java.util.Set;

public class AppPrefsUtils implements SharedPreferences {

    public static final String KEY_IS_FIRST_LAUNCH = "KEY_IS_FIRST_LAUNCH";
    public static final String PREFS_FILE_NAME = "DIVINOTE_SETTINGS";

    private static final Object lock = new Object();

    private static AppPrefsUtils sInstance;
    private static SharedPreferences sPreferences;

    private AppPrefsUtils() {
        sPreferences = DiviNoteApplication.getAppContext()
                .getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static AppPrefsUtils getInstance() {
        if (sInstance == null) {
            synchronized (lock) {
                if (sInstance == null) {
                    sInstance = new AppPrefsUtils();
                }
            }
        }
        return sInstance;
    }

    public boolean isFirstLaunch() {
        return getBoolean(KEY_IS_FIRST_LAUNCH, false);
    }

    @Override
    public Map<String, ?> getAll() {
        if (sPreferences != null) {
            return sPreferences.getAll();
        }
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        if (sPreferences != null) {
            return sPreferences.getString(key, defValue);
        }
        return defValue;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (sPreferences != null) {
            return sPreferences.getStringSet(key, defValues);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (sPreferences != null) {
            return sPreferences.getInt(key, defValue);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (sPreferences != null) {
            return sPreferences.getLong(key, defValue);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (sPreferences != null) {
            return sPreferences.getFloat(key, defValue);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (sPreferences != null) {
            return sPreferences.getBoolean(key, defValue);
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        if (sPreferences != null) {
            return sPreferences.contains(key);
        }
        return false;
    }

    @Override
    public Editor edit() {
        if (sPreferences != null) {
            return sPreferences.edit();
        }
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
