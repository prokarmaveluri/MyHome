package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 4/28/17.
 */

public class ProfileResponse {

    private String firstName;
    private String lastName;
    private String preferredName;
    private String email;

    public ProfileResponse(String firstName, String lastName, String preferredName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.email = email;
    }

    @Override
    public String toString() {
        return "ProfileResponse{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredName='" + preferredName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

