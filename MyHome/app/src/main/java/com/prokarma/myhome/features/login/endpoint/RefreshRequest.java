package com.prokarma.myhome.features.login.endpoint;

/**
 * Created by cmajji on 9/22/17.
 */

public class RefreshRequest {
    public String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
