package com.prokarma.myhome.features.login.endpoint;

/**
 * Created by cmajji on 9/22/17.
 */

public class SignOutRequest {

    public String sessionId;
    public String accessToken;
    public String refreshToken;

    public SignOutRequest(String sessionId, String accessToken, String refreshToken) {
        this.sessionId = sessionId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
