package com.prokarma.myhome.features.login;

/**
 * Created by cmajji on 4/27/17.
 */

public class LoginRequest {

    private String username;
    private String password;
    private Options options;

    public LoginRequest(String username, String password, Options options) {
        this.username = username;
        this.password = password;
        this.options = options;
    }

    static public class Options {

        private Boolean warnBeforePasswordExpired;
        private Boolean multiOptionalFactorEnroll;

        public Options(boolean warnBeforePasswordExpired,
                       boolean multiOptionalFactorEnroll) {
            this.warnBeforePasswordExpired = warnBeforePasswordExpired;
            this.multiOptionalFactorEnroll = multiOptionalFactorEnroll;
        }

    }
}