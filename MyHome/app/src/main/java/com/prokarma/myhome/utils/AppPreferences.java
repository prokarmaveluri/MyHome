package com.prokarma.myhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.prokarma.myhome.networking.auth.AuthManager;

/**
 * Created by aswin on 17/5/16.
 */
public class AppPreferences {

    private final static String APP_PREFERENCES = "APP_PREFERENCES";
    private static AppPreferences instance;
    private SharedPreferences mSharedPreferences;

    private AppPreferences() {
        mSharedPreferences = AuthManager.getInstance().getContext()
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    public static AppPreferences getInstance() {
        if (instance == null) {
            instance = new AppPreferences();
        }
        return instance;
    }

    public void setPreference(String key, String value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getPreference(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void setPreference(String key, int value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public int getIntPreference(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public void setBooleanPreference(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanPreference(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }


    public void setLongPreference(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLongPreference(String key) {
        return mSharedPreferences.getLong(key, 0);
    }
}
