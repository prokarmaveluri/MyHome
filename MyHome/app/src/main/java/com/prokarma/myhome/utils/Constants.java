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

    // tealium keys
    public static final String FAVORITE_PROVIDER_NPI = "providerNPI";
    public static final String FAD_PROVIDER_NPI = "fadProviderNPI";
    public static final String FAD_SEARCH_TERM = "fadSearchTerm";
    public static final String FAD_SEARCH_GEO = "fadSearchGeo";

    // screen views
    public static final String HOME_SCREEN = "my home|dashboard home";
    public static final String FAD_LIST_SCREEN = "my home|fad list";
    public static final String FAD_MAP_SCREEN = "my home|fad map";
    public static final String APPOINTMENTS_SCREEN = "my home|appointments";
    public static final String PROFILE_SCREEN = "my home|profile";
    public static final String TOS_SCREEN = "my home|tos";
    public static final String CONTACT_US_SCREEN = "my home|contact us";
    public static final String SETTINGS_SCREEN = "my home|settings";
    public static final String CHANGE_PASSWORD_SCREEN = "my home|change password";
    public static final String CHANGE_SEC_QUESTION_PASSWORD_SCREEN = "my home|change security question";
    public static final String TOUCH_ID_SETTINGS_SCREEN = "my home|touch id settings";
    public static final String PROVIDER_DETAILS_SCREEN = "my home|doctor details";

    // events
    public static final String SIGN_IN_EVENT = "loginSuccess";
    public static final String SIGN_OUT_EVENT = "logoutSuccess";
    public static final String APP_OPEN_EVENT = "appOpened";
    public static final String APP_CLOSE_EVENT = "appClosed";
    public static final String SCHEDULING_STARTED_EVENT = "schedulingInit";
    public static final String SCHEDULING_ENDED_EVENT = "schedulingComplete";
    public static final String SCHEDULING_FAILED_EVENT = "schedulingFailed";
    public static final String FAD_SEARCH_STARTED_EVENT = "fadSearchInit";
    public static final String ENROLLMENT_SUCCESS_EVENT = "enrollmentSuccess";
    public static final String FAVORITE_PROVIDER_EVENT = "favoriteDoctorSuccess";
    public static final String UNFAVORITE_PROVIDER_EVENT = "unfavoriteDoctorSuccess";
    public static final String BILLPAY_EVENT = "billPayTapped";
    public static final String MYCARE_EVENT = "myCareTapped";
    public static final String PROFILE_UPDATE_EVENT = "updateProfileSuccess";
    public static final String TOUCH_ID_ENABLED_EVENT = "touchIdEnabled";
    public static final String TOUCH_ID_DISABLED_EVENT = "touchIdDisabled";
    public static final String CHANGED_PASSWORD_EVENT = "changePasswordSuccess";
    public static final String CHANGED_SECURITY_QUESTION_EVENT = "changeSecurityQuestionSuccess";


    public static final String DID_YOU_KNOW_SEC1 = "https://hellohumankindness.org/story/the-power-of-time-off-why-vacations-are-essential/";
    public static final String DID_YOU_KNOW_SEC2 = "https://dignityhealth.org/articles/a-little-bit-of-color-dont-let-a-sunburn-get-you-down-this-summer";

    public static final String TEL = "tel:";
    public static final String SUPPORT_EMAIL = "hello@dignityhealth.org";

    //Preferences
    public static final String PREF_TIME_ZONE = "pref_time_zone";

}

