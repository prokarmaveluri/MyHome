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
        API,
        PREFERENCES,
        TERMS_OF_SERVICE,
        FAD_LIST,
        FAD_MAP,
        PROVIDER_DETAILS,
        PROVIDERS_FILTER,
        CHANGE_PASSWORD,
        ENTER_PASSWORD_SEC_QUESTION,
        CHANGE_SEC_QUESTION,
        TOUCH_ID,
        HOME_DID_YOU_KNOW_SEC_1,
        HOME_DID_YOU_KNOW_SEC_2,
        MY_CARE_NOW,
        MY_MED_HISTORY,
        MY_MEDICATIONS,
        MY_PHARMACY,
        MY_PHARMACY_DETAILS,
        MY_CARE_SERVICES,
        MY_CARE_PROVIDERS,
        MY_CARE_COST,
        MY_CARE_WAITING_ROOM,
        PREVIOUS_VISITS_SUMMARY,
        VISIT_SUMMARY,
        VISIT_FEEDBACK
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
    //public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!#^%*&])[A-Za-z\\d$@$!#^%*&]{8,}";

//    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character (ie non-word)
//    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[A-Za-z\\d\\W]{8,}";

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character (ie non-word, underscore counts as special)
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])[A-Za-z\\d[^a-zA-Z0-9]]{8,}";

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
    public static final String HOME_DID_YOU_KNOW_SCREEN = "my home|home, did you know";
    public static final String RESEND_EMAIL = "my home|resendEmail";

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

    public static final String DID_YOU_KNOW_SEC1 = "https://dignityhealth.org/cm/content/pages/hit-the-slopes-mountain-safety-tips-for-skiers-and-snowboarders.asp";
    public static final String DID_YOU_KNOW_SEC2 = "https://www.dignityhealth.org/Articles/Moderate-Drinking-How-to-Avoid-Overindulging-This-Winter";

    public static final String TEL = "tel:";
    public static final String SUPPORT_EMAIL = "hello@dignityhealth.org";

    //Preferences
    public static final String PREF_TIME_ZONE = "pref_time_zone";

    //Coach Marks Constants
    public static final String BOOKING_DONE_SKIP_COACH_MARKS = "BOOKING_DONE_SKIP_COACH_MARKS";
    public static final String BOOKING_DATE_SKIP_COACH_MARKS = "BOOKING_DATE_SKIP_COACH_MARKS";
    public static final String PROVIDER_DETAILS_BOOK_SKIP_COACH_MARKS = "PROVIDER_DETAILS_BOOK_SKIP_COACH_MARKS";
    public static final String PROVIDER_DETAILS_LOCATION_SKIP_COACH_MARKS = "PROVIDER_DETAILS_LOCATION_SKIP_COACH_MARKS";
    public static final String FAD_ACTIONBAR_SKIP_COACH_MARKS = "FAD_ACTIONBAR_SKIP_COACH_MARKS";
    public static final String FAD_LIST_SKIP_COACH_MARKS = "FAD_LIST_SKIP_COACH_MARKS";
    public static final String FAD_SKIP_COACH_MARKS = "FAD_SKIP_COACH_MARKS";
    public static final String HOME_SKIP_COACH_MARKS = "HOME_SKIP_COACH_MARKS";
    public static final String APT_DETAILS_SKIP_COACH_MARKS = "APT_DETAILS_SKIP_COACH_MARKS";
    public static final String APT_DETAILS_PAST_SKIP_COACH_MARKS = "APT_DETAILS_PAST_SKIP_COACH_MARKS";
    public static final String APT_DETAILS_UPCOMING_SKIP_COACH_MARKS = "APT_DETAILS_UPCOMING_SKIP_COACH_MARKS";

    //Preference Keys for API Error Toggling
    public static final String API_HIDE_API_ERROR_INFO = "API_HIDE_API_ERROR_INFO";
    public static final String API_SIGN_IN_FORCE_ERROR = "API_SIGN_IN_FORCE_ERROR";
    public static final String API_SIGN_IN_REFRESH_FORCE_ERROR = "API_SIGN_IN_REFRESH_FORCE_ERROR";
    public static final String API_SIGN_OUT_FORCE_ERROR = "API_SIGN_OUT_FORCE_ERROR";
    public static final String API_PROFILE_GET_FORCE_ERROR = "API_PROFILE_GET_FORCE_ERROR";
    public static final String API_PROFILE_UPDATE_FORCE_ERROR = "API_PROFILE_UPDATE_FORCE_ERROR";
    public static final String API_GET_MY_APPOINTMENTS_FORCE_ERROR = "API_GET_MY_APPOINTMENTS_FORCE_ERROR";
    public static final String API_CREATE_APPOINTMENT_FORCE_ERROR = "API_CREATE_APPOINTMENT_FORCE_ERROR";
    public static final String API_GET_VALIDATION_RULES_FORCE_ERROR = "API_GET_VALIDATION_RULES_FORCE_ERROR";
    public static final String API_GET_PROVIDER_DETAILS_FORCE_ERROR = "API_GET_PROVIDER_DETAILS_FORCE_ERROR";
    public static final String API_GET_APPOINTMENT_TIMES_FORCE_ERROR = "API_GET_APPOINTMENT_TIMES_FORCE_ERROR";
    public static final String API_REGISTER_FORCE_ERROR = "API_REGISTER_FORCE_ERROR";
    public static final String API_CHANGE_PASSWORD_FORCE_ERROR = "API_CHANGE_PASSWORD_FORCE_ERROR";
    public static final String API_CHANGE_SECURITY_QUESTION_FORCE_ERROR = "API_CHANGE_SECURITY_QUESTION_FORCE_ERROR";
    public static final String API_GET_SAVED_DOCTORS_FORCE_ERROR = "API_GET_SAVED_DOCTORS_FORCE_ERROR";
    public static final String API_SAVE_DOCTOR_FORCE_ERROR = "API_SAVE_DOCTOR_FORCE_ERROR";
    public static final String API_DELETE_SAVED_DOCTOR_FORCE_ERROR = "API_DELETE_SAVED_DOCTOR_FORCE_ERROR";
    public static final String API_FORGOT_PASSWORD_FORCE_ERROR = "API_FORGOT_PASSWORD_FORCE_ERROR";
    public static final String API_GET_PROVIDERS_FORCE_ERROR = "API_GET_PROVIDERS_FORCE_ERROR";
    public static final String API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR = "API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR";
    public static final String API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR = "API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR";
    public static final String API_GET_LOCATION_FORCE_ERROR = "API_GET_LOCATION_FORCE_ERROR";
    public static final String API_FIND_EMAIL_FORCE_ERROR = "API_FIND_EMAIL_FORCE_ERROR";
    public static final String API_VERSION_CHECK_FORCE_ERROR = "API_VERSION_CHECK_FORCE_ERROR";
    public static final String API_RESEND_EMAIL_FORCE_ERROR = "API_RESEND_EMAIL_FORCE_ERROR";
}

