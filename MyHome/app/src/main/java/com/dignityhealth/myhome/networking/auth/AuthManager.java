package com.dignityhealth.myhome.networking.auth;

/**
 * Created by cmajji on 5/1/17.
 */

public class AuthManager {

    private static String bearerToken;
    private static final AuthManager ourInstance = new AuthManager();

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
    }


    public static String getBearerToken() {
        return bearerToken;
    }

    public static void setBearerToken(String bearerToken) {
        AuthManager.bearerToken = bearerToken;
    }
}
