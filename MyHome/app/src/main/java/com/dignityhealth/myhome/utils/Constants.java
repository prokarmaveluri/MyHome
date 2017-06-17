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
        APPOINTMENTS_DETAILS,
        PROFILE_VIEW,
        PROFILE_EDIT,
        CONTACT_US,
        SETTINGS,
        HELP,
        DEVELOPER,
        PREFERENCES,
        TERMS_OF_SERVICE,
        FAD_LIST,
        FAD_MAP,
        PROVIDER_DETAILS,
        PROVIDERS_FILTER
    }

    public enum INPUT_TYPE {
        TEXT,
        EMAIL,
        PASSWORD
    }

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character !@#$%^&*
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!#^%*&])[A-Za-z\\d$@$!#^%*&]{8,}";

    public static final String auth2Url = "https://dignityhealth.oktapreview.com/oauth2/v1/authorize?client_id=NtFXqaF8iMnVbfdtJfsF&redirect_uri=https%3A%2F%2Fdev.ciam.dignityhealth.org%2Fwidgets%2Fenrollment%2Fdist%2F&response_type=id_token&response_mode=fragment&state=%7B%22returnUrl%22%3A%22foobar%22%2C%22step%22%3A%22enrollment%22%2C%22stepProgress%22%3A%22confirmation%22%7D&nonce=o4VJePCJeaAE17PKtyHpXZK5xwIhvgNpOzaq80hucpd6DTYELlD8jt46D2Ex2WRu&scope=openid%20email%20profile%20phone%20groups&sessionToken=";

    //data Keys
    public static final String ENROLLMENT_QUESTION_ID = "ENROLLMENT_QUESTION_ID";
    public static final String ENROLLMENT_QUESTION = "ENROLLMENT_QUESTION";
    public static final String ENROLLMENT_QUESTION_ANS = "ENROLLMENT_QUESTION_ANS";
    public static final String ENROLLMENT_REQUEST = "ENROLLMENT_REQUEST";

    public static final String DEFAULT_FAD_QUERY = "Primary Care";

    public static int DEV_UPDATE_VERSION = 1;

    // screen views
    public static String HOME_SCREEN = "home_screen";
    public static String FAD_LIST_SCREEN = "fad_list_screen";
    public static String FAD_MAP_SCREEN = "fad_map_screen";
    public static String APPOINTMENTS_SCREEN = "appointments_screen";
    public static String PROFILE_SCREEN = "profile_screen";
    public static String TOS_SCREEN = "home_screen";
    public static String CONTACT_US_SCREEN = "contact_us_screen";
    public static String SETTINGS_SCREEN = "settings_screen";

    // events
    public static String SIGN_IN_EVENT = "sign_in_event";
    public static String SIGN_OUT_EVENT = "sign_out_event";
    public static String APP_OPEN_EVENT = "app_open_event";
    public static String APP_CLOSE_EVENT = "app_open_event";

    public static String DID_YOU_KNOW_SEC1 = "https://hellohumankindness.org/story/the-power-of-time-off-why-vacations-are-essential/";
    public static String DID_YOU_KNOW_SEC2 = "https://dignityhealth.org/articles/a-little-bit-of-color-dont-let-a-sunburn-get-you-down-this-summer";
}

