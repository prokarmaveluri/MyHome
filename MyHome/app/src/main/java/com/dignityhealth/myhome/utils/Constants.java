package com.dignityhealth.myhome.utils;

/**
 * Created by kwelsh on 4/25/17.
 */

public class Constants {
    public enum ActivityTag {
        NONE,
        HOME,
        FAD,
        APPOINTMENTS,
        PROFILE,
        CONTACT_US,
        SETTINGS,
        MORE,
    }

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
}