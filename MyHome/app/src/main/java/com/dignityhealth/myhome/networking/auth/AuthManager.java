package com.dignityhealth.myhome.networking.auth;

import android.content.Context;

import com.dignityhealth.myhome.features.login.LoginActivity;
import com.dignityhealth.myhome.utils.AppPreferences;

/**
 * Created by cmajji on 5/1/17.
 */

public class AuthManager {

    private static String bearerToken;
    private static String sessionToken;
    private static String idTokenForSignOut;

    private static int count = 0;
    private static Context context;

    private static long prevTimestamp = 0;
    private static long MINITUES_5 = 5 * 60 * 1000;

    private static final AuthManager ourInstance = new AuthManager();

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        AuthManager.context = context;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        AuthManager.count = count;
    }

    public long getPrevTimestamp() {
        return prevTimestamp;
    }

    public void setPrevTimestamp(long prevTimestamp) {
        AuthManager.prevTimestamp = prevTimestamp;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        AuthManager.bearerToken = bearerToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        AuthManager.sessionToken = sessionToken;
    }

    public String getIdTokenForSignOut() {
        return idTokenForSignOut;
    }

    public void setIdTokenForSignOut(String idTokenForSignOut) {
        AuthManager.idTokenForSignOut = idTokenForSignOut;
    }

    public void setFailureAttempt() {
        if (System.currentTimeMillis() - prevTimestamp >= MINITUES_5) {
            prevTimestamp = System.currentTimeMillis();
            count = 1;
        } else {
            count++;
        }
        storeLockoutInfo();
    }

    public boolean isTimeStampGreaterThan5Mins() {
        return (System.currentTimeMillis() - prevTimestamp >= MINITUES_5);
    }

    public boolean isMaxFailureAttemptsReached() {
        if (count >= 3) {
            return true;
        }
        return false;
    }

    public void storeLockoutInfo() {
        AppPreferences.getInstance().setPreference(LoginActivity.FAILURE_COUNT, getCount());
        AppPreferences.getInstance().setLongPreference(LoginActivity.FAILURE_TIME_STAMP,
                getPrevTimestamp());
    }

    public void fetchLockoutInfo() {
        count = AppPreferences.getInstance().getIntPreference(LoginActivity.FAILURE_COUNT);
        prevTimestamp = AppPreferences.getInstance().getLongPreference(LoginActivity.FAILURE_TIME_STAMP);
    }
}
