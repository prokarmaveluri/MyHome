package com.dignityhealth.myhome.networking.auth;

/**
 * Created by cmajji on 5/1/17.
 */

public class AuthManager {

    private static String bearerToken;
    private static String sessionToken;
    private static String idTokenForSignOut;

    private static int count = 0;
    private static long prevTimestamp = 0;
    private static long MINITUES_5 = 5 * 60 * 1000;


    private static final AuthManager ourInstance = new AuthManager();

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        AuthManager.count = count;
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
}
