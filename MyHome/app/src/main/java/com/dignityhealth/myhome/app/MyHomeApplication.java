package com.dignityhealth.myhome.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.dignityhealth.myhome.BuildConfig;
import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.networking.NetworkManager;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyHomeApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        //Set up logging
        if (BuildConfig.REPORT_LOGS) {
            Timber.plant(new Timber.DebugTree());
        }

        //Set up hockey's crash reporting and have it auto send reports
        if(BuildConfig.REPORT_CRASHES){
            CrashManager.register(this, BuildConfig.HOCKEY_ID, new CrashManagerListener() {
                @Override
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });
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