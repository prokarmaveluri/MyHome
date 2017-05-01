package com.dignityhealth.myhome.features.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cmajji on 4/27/17.
 */


public class LoginResponse {

    private String expiresAt;
    private String status;
    private String sessionToken;
    @SerializedName(value = "_embedded")
    private Embedded embedded;

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Embedded getEmbedded() {
        return embedded;
    }

    public class Embedded {

        private User user;

        public User getUser() {
            return user;
        }
    }

    public class Profile {

        private String login;
        private String firstName;
        private String lastName;
        private String locale;
        private String timeZone;

        public String getLogin() {
            return login;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getLocale() {
            return locale;
        }

        public String getTimeZone() {
            return timeZone;
        }
    }

    public class User {

        private String id;
        private String passwordChanged;
        private Profile profile;

        public String getId() {
            return id;
        }

        public String getPasswordChanged() {
            return passwordChanged;
        }

        public Profile getProfile() {
            return profile;
        }
    }
}




