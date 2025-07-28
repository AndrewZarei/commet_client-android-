package com.blockchain.commet.data.sharepref;

import android.content.Context;
import android.content.SharedPreferences;

import com.blockchain.commet.MyApplication;

import java.util.Set;

public class SharedPrefsHelper {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPrefsHelper sharedPrefsHelper;

    public SharedPrefsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        editor=mSharedPreferences.edit();
    }

    public static SharedPrefsHelper getSharedPrefsHelper() {
        if (sharedPrefsHelper == null) {
            sharedPrefsHelper = new SharedPrefsHelper(MyApplication.Companion.getAppContext());
        }
        return sharedPrefsHelper;
    }
    public void Clear(){
        editor.clear();
        editor.commit();
    }

    public SharedPrefsHelper put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
        return this;
    }

    public SharedPrefsHelper put(String key, Set<String> value) {
        mSharedPreferences.edit().putStringSet(key, value).apply();
        return this;
    }

    public SharedPrefsHelper put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
        return this;
    }
    public SharedPrefsHelper putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
        return this;
    }

    public SharedPrefsHelper put(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
        return this;
    }

    public SharedPrefsHelper put(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
        return this;
    }

    public String get(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public String get(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public Set<String> get(String key, Set<String> defaultValue) {
        return mSharedPreferences.getStringSet(key, defaultValue);
    }

    public Integer get(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public Long get(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public Long getLong(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public Float get(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public Boolean get(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public void deleteSavedData(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }
}