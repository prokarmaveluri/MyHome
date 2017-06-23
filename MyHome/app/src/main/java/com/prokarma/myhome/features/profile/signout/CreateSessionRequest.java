package com.prokarma.myhome.features.profile.signout;

/**
 * Created by cmajji on 5/5/17.
 */


public class CreateSessionRequest {

    private String sessionToken;

    public CreateSessionRequest(String token){
        sessionToken = token;
    }
}
