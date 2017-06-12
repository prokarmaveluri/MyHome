package com.dignityhealth.myhome.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.dignityhealth.myhome.BuildConfig;
import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.utils.TealiumUtil;

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
        if (BuildConfig.REPORT_CRASHES) {
            CrashManager.register(this, BuildConfig.HOCKEY_ID, new CrashManagerListener() {
                @Override
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });
        }

        //init Analytics
        TealiumUtil.initialize(this);

        //init retrofit service
        NetworkManager.getInstance().initService();
        AuthManager.getInstance().setContext(this);
        FadManager.getInstance().setLocation(null);
        DeviceDisplayManager.getInstance().setContext(getApplicationContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.pref_key_first_run), true)) {
            //First Run Stuff Here...
            prefs.edit().putBoolean(getString(R.string.pref_key_first_run), false).apply();
        }

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/TradeGothicLTStd.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/TradeGothicLTStd.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/TradeGothicLTStd-Light.ttf");
        AuthManager.getInstance().fetchLockoutInfo();
    }
}