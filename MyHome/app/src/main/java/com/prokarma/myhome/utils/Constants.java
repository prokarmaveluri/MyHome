package com.prokarma.myhome.utils;

/**
 * Created by kwelsh on 4/25/17.
 */

public class Constants {
    public enum ActivityTag {
        NONE,
        HOME,
        FAD,
        FAD_DASH_BOARD,
        APPOINTMENTS,
        APPOINTMENTS_DETAILS,
        PROFILE_VIEW,
        PROFILE_EDIT,
        CONTACT_US,
        FAQ,
        MY_CARE,
        SETTINGS,
        HELP,
        DEVELOPER,
        PREFERENCES,
        TERMS_OF_SERVICE,
        FAD_LIST,
        FAD_MAP,
        PROVIDER_DETAILS,
        PROVIDERS_FILTER,
        CHANGE_PASSWORD,
        ENTER_PASSWORD_SEC_QUESTION,
        CHANGE_SEC_QUESTION,
        TOUCH_ID
    }

    public enum INPUT_TYPE {
        TEXT,
        FIRST_NAME,
        LAST_NAME,
        EMAIL_LOGIN,
        EMAIL_ENROLL,
        PASSWORD
    }

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character !@#$%^&*
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!#^%*&])[A-Za-z\\d$@$!#^%*&]{8,}";

    //data Keys
    public static final String ENROLLMENT_QUESTION_ID = "ENROLLMENT_QUESTION_ID";
    public static final String ENROLLMENT_QUESTION = "ENROLLMENT_QUESTION";
    public static final String ENROLLMENT_QUESTION_ANS = "ENROLLMENT_QUESTION_ANS";
    public static final String ENROLLMENT_REQUEST = "ENROLLMENT_REQUEST";

    public static final String DEFAULT_FAD_QUERY = "Primary Care";

    public static int DEV_UPDATE_VERSION = 1;

    // screen views
    public static final String HOME_SCREEN = "my home|dashboard home";
    public static final String FAD_LIST_SCREEN = "fad_list_screen";
    public static final String FAD_MAP_SCREEN = "fad_map_screen";
    public static final String APPOINTMENTS_SCREEN = "appointments_screen";
    public static final String PROFILE_SCREEN = "profile_screen";
    public static final String TOS_SCREEN = "home_screen";
    public static final String CONTACT_US_SCREEN = "contact_us_screen";
    public static final String SETTINGS_SCREEN = "settings_screen";
    public static final String CHANGE_PASSWORD_SCREEN = "change_password_screen";
    public static final String CHANGE_SEC_QUESTION_PASSWORD_SCREEN = "change_sec_question_password_screen";
    public static final String TOUCH_ID_SETTINGS = "touch_id_settings_screen";
    public static final String PROVIDER_DETAILS_SCREEN = "my home|doctor details";

    // events
    public static final String SIGN_IN_EVENT = "sign_in_event";
    public static final String SIGN_OUT_EVENT = "sign_out_event";
    public static final String APP_OPEN_EVENT = "app_open_event";
    public static final String APP_CLOSE_EVENT = "app_open_event";
    public static final String SCHEDULING_STARTED_EVENT = "scheduling_started_event";
    public static final String SCHEDULING_ENDED_EVENT = "scheduling_ended_event";

    public static final String DID_YOU_KNOW_SEC1 = "https://hellohumankindness.org/story/the-power-of-time-off-why-vacations-are-essential/";
    public static final String DID_YOU_KNOW_SEC2 = "https://dignityhealth.org/articles/a-little-bit-of-color-dont-let-a-sunburn-get-you-down-this-summer";

    public static final String TEL = "tel:";
    public static final String SUPPORT_EMAIL = "hello@dignityhealth.org";

    //Preferences
    public static final String PREF_TIME_ZONE = "pref_time_zone";

}

