package com.dignityhealth.myhome.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dignityhealth.myhome.BuildConfig;
import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.networking.NetworkManager;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyHomeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Set up logging
        if (BuildConfig.REPORT_LOGS) {
            Timber.plant(new Timber.DebugTree());
        }

        //init retrofit service
        NetworkManager.getInstance().initService();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.pref_key_first_run), true)) {
            //First Run Stuff Here...
            prefs.edit().putBoolean(getString(R.string.pref_key_first_run), false).apply();
        }
    }
}
