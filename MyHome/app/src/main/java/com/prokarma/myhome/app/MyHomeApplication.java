package com.prokarma.myhome.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.TealiumUtil;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyHomeApplication extends MultiDexApplication {
    public static final String DEFAULT = "DEFAULT";
    public static final String SANS_SERIF = "SANS_SERIF";
    public static final String MONOSPACE = "MONOSPACE";
    public static final String FONTS_TRADE_GOTHIC_LTSTD_TTF = "fonts/TradeGothicLTStd.ttf";
    public static final String FONTS_TRADE_GOTHIC_LTSTD_LIGHT_TTF = "fonts/TradeGothicLTStd-Light.ttf";

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

                @Override
                public String getUserID() {
                    if (!BuildConfig.FLAVOR.equals("release")) {
                        String email = AppPreferences.getInstance().getPreference("EMAIL_PREF");
                        return email != null && !email.isEmpty() ? email : "User Unknown";
                    }

                    return "User Unknown";
                }
            });
        }

        //init Analytics
        if (BuildConfig.REPORT_ANALYTICS) {
            TealiumUtil.initialize(this);
        }

        //init retrofit service
//        NetworkManager.getInstance().initService();
        AuthManager.getInstance().setContext(this);
        FadManager.getInstance().setLocation(null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.pref_key_first_run), true)) {
            //First Run Stuff Here...
            prefs.edit().putBoolean(getString(R.string.pref_key_first_run), false).apply();
        }

        FontsOverride.setDefaultFont(this, DEFAULT, FONTS_TRADE_GOTHIC_LTSTD_TTF);
        FontsOverride.setDefaultFont(this, SANS_SERIF, FONTS_TRADE_GOTHIC_LTSTD_TTF);
        FontsOverride.setDefaultFont(this, MONOSPACE, FONTS_TRADE_GOTHIC_LTSTD_LIGHT_TTF);
        AuthManager.getInstance().fetchLockoutInfo();
    }
}