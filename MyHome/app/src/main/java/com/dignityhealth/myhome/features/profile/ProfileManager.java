package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 5/2/17.
 */

public class ProfileManager {
    private static Profile profile = null;

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        ProfileManager.profile = profile;
    }
}
