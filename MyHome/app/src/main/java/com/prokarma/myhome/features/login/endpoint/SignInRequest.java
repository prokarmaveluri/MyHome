package com.prokarma.myhome.features.login.endpoint;

/**
 * Created by cmajji on 9/22/17.
 */

public class SignInRequest {

    public String username;
    public String password;

    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
