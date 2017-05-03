package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 5/2/17.
 */

public class ProfileManager {
    private static ProfileResponse profile = null;

    public static ProfileResponse getProfile() {
        return profile;
    }

    public static void setProfile(ProfileResponse profile) {
        ProfileManager.profile = profile;
    }
}
