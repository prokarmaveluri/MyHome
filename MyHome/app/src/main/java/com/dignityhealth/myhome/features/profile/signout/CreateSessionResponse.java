package com.dignityhealth.myhome.features.profile.signout;

/**
 * Created by cmajji on 5/5/17.
 */


public class CreateSessionResponse {

    private String id;
    private String userId;
    private String login;
    private String createdAt;
    private String expiresAt;
    private String status;
    private String cookieToken;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getCookieToken() {
        return cookieToken;
    }
}
