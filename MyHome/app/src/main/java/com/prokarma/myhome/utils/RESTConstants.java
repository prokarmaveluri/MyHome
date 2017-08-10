package com.prokarma.myhome.utils;

import com.prokarma.myhome.BuildConfig;

/**
 * Created by cmajji on 4/26/17.
 */

public class RESTConstants {


    //TEST
    public static final String auth2Url = "oauth2/v1/authorize?client_id=NtFXqaF8iMnVbfdtJfsF&redirect_uri=https%3A%2F%2Fdev.ciam.dignityhealth.org%2Fwidgets%2Fenrollment%2Fdist%2F&response_type=id_token&response_mode=fragment&state=%7B%22returnUrl%22%3A%22foobar%22%2C%22step%22%3A%22enrollment%22%2C%22stepProgress%22%3A%22confirmation%22%7D&nonce=o4VJePCJeaAE17PKtyHpXZK5xwIhvgNpOzaq80hucpd6DTYELlD8jt46D2Ex2WRu&scope=openid%20email%20profile%20phone%20groups&sessionToken=";

    public static final String FETCH_CODE = "oauth2/%s/v1/authorize?response_type=code&client_id=%s&redirect_uri=%s&response_mode=fragment&scope=%s&state=foo&nonce=bar&code_challenge=%s&code_challenge_method=S256&sessionToken=%s";

    public static final String VERSIONING_URL = BuildConfig.VERSIONING_URL;
    public static final String OKTA_BASE_URL = BuildConfig.OKTA_BASE_URL;
    public static final String CIAM_BASE_URL = BuildConfig.CIAM_BASE_URL;
    public static final String S2_BASE_URL = BuildConfig.S2_BASE_URL;
    public static final String SCHEDULING_BASE = BuildConfig.SCHEDULING_BASE;


    public static final String SCHEDULING_VISIT = "api/v1/visit/";
    public static final String SCHEDULING_VALIDATION = "api/v1/visit/schedule/{scheduleID}/visit-settings";

    public static final String PROVIDER_PAGE_NO = "1";
    public static final String PROVIDER_PAGE_SIZE = "25";
    public static final String PROVIDER_DISTANCE = "100";


    public static final String AUTH_CLIENT_ID = BuildConfig.AUTH_CLIENT_ID;
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public static final String AUTH_SCOPE = BuildConfig.AUTH_SCOPE;
    public static final String AUTH_REDIRECT_URI = BuildConfig.AUTH_REDIRECT_URI;
    public static final String GRANT_TYPE_AUTH = BuildConfig.GRANT_TYPE_AUTH;
    public static final String GRANT_TYPE_REFRESH = BuildConfig.GRANT_TYPE_REFRESH;


    //v1.1 graphql requests
    public static final String MY_SAVED_DOCS = "{" +
            "  user {" +
            "  favoriteProviders {" +
            "    firstName" +
            "    lastName" +
            "    npi" +
            "    displayName" +
            "    displayLastName" +
            "    displayLastNamePlural" +
            "    middleName" +
            "    suffix" +
            "    title" +
            "    philosophy" +
            "    supportsOnlineBooking" +
            "    providerDetailsUrl" +
            "    primarySpecialities" +
            "    images {" +
            "          imageType" +
            "          url" +
            "          width" +
            "          height" +
            "        }" +
            "  }" +
            "  }" +
            "}";


    public static final String APPOINTMENTS_DATE = "\"04-01-17\"";
    public static final String MY_APPOINTMENTS_INCLUDE_INACTIVE = "{" +
            "  user {" +
            "      appointments(beginDate:" + APPOINTMENTS_DATE + ", includeInactive: true) {" +
            "      appointmentId" +
            "      secureId" +
            "      appointmentStart" +
            "      appointmentStatus" +
            "      appointmentType" +
            "      caregiverName" +
            "      comments" +
            "      doctorName" +
            "      doctorSpecialty" +
            "      visitReason" +
            "      facilityName" +
            "      facilityAddress " +
            "      facilityPhoneNumber" +
            "      provider {" +
            "        firstName" +
            "        lastName" +
            "        npi" +
            "        displayName" +
            "        displayLastName" +
            "        displayLastNamePlural" +
            "        middleName" +
            "        suffix" +
            "        title" +
            "        philosophy" +
            "        supportsOnlineBooking" +
            "        providerDetailsUrl" +
            "        primarySpecialities" +
            "    images {" +
            "          imageType" +
            "          url" +
            "          width" +
            "          height" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}";

}