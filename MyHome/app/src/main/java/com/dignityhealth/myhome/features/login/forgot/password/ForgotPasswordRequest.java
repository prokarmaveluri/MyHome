package com.dignityhealth.myhome.features.login.forgot.password;

/**
 * Created by cmajji on 5/2/17.
 */

public class ForgotPasswordRequest {
    private String username;
    private String factorType;
    private String relayState;

    public ForgotPasswordRequest(String username, String factorType, String relayState) {
        this.username = username;
        this.factorType = factorType;
        this.relayState = relayState;
    }
}
