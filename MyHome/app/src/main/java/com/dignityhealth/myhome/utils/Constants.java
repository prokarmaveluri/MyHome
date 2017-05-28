package com.dignityhealth.myhome.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

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

    //Date formats
    public static final String DATE_FORMAT = "MM/dd/yy";
    public static final String DATE_SHORT_WORDS_FORMAT = "EEE MMM dd";
    public static final String DATE_WORDS_1st_FORMAT = "MMMM d'st,' yyyy";   //Adds a "st" to the day (like 1st)
    public static final String DATE_WORDS_2nd_FORMAT = "MMMM d'nd,' yyyy";   //Adds a "nd" to the day (like 2nd)
    public static final String DATE_WORDS_3rd_FORMAT = "MMMM d'rd,' yyyy";   //Adds a "rd" to the day (like 3rd)
    public static final String DATE_WORDS_4th_FORMAT = "MMMM d'th,' yyyy";   //Adds a "th" to the day (like 4th)
    public static final String TIME_FORMAT = "h:mm a";
    //public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";    //For Profile to work, need to remove milliseconds
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";    //Some appointments will appear ugly without milliseconds...
    public static final String DATE_RANGE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss-HH:mm";    //For appointments with time slots

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_SHORT_WORDS_FORMAT = new SimpleDateFormat(DATE_SHORT_WORDS_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_UTC = new SimpleDateFormat(DATE_FORMAT_UTC, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_RANGE_FORMAT_UTC = new SimpleDateFormat(DATE_RANGE_FORMAT_UTC, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_1st_FORMAT = new SimpleDateFormat(DATE_WORDS_1st_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_2nd_FORMAT = new SimpleDateFormat(DATE_WORDS_2nd_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_3rd_FORMAT = new SimpleDateFormat(DATE_WORDS_3rd_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_4th_FORMAT = new SimpleDateFormat(DATE_WORDS_4th_FORMAT, Locale.US);

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character !@#$%^&*
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!#^%*&])[A-Za-z\\d$@$!#^%*&]{8,}";

    public static final String auth2Url = "https://dignityhealth.oktapreview.com/oauth2/v1/authorize?client_id=NtFXqaF8iMnVbfdtJfsF&redirect_uri=https%3A%2F%2Fdev.ciam.dignityhealth.org%2Fwidgets%2Fenrollment%2Fdist%2F&response_type=id_token&response_mode=fragment&state=%7B%22returnUrl%22%3A%22foobar%22%2C%22step%22%3A%22enrollment%22%2C%22stepProgress%22%3A%22confirmation%22%7D&nonce=o4VJePCJeaAE17PKtyHpXZK5xwIhvgNpOzaq80hucpd6DTYELlD8jt46D2Ex2WRu&scope=openid%20email%20profile%20phone%20groups&sessionToken=";

    //data Keys
    public static final String ENROLLMENT_QUESTION_ID = "ENROLLMENT_QUESTION_ID";
    public static final String ENROLLMENT_QUESTION = "ENROLLMENT_QUESTION";
    public static final String ENROLLMENT_QUESTION_ANS = "ENROLLMENT_QUESTION_ANS";
    public static final String ENROLLMENT_REQUEST = "ENROLLMENT_REQUEST";

}

