package com.prokarma.myhome.utils;

/**
 * Created by cmajji on 4/26/17.
 */

public class RESTConstants {


    //TEST
    public static final String auth2Url = "oauth2/v1/authorize?client_id=NtFXqaF8iMnVbfdtJfsF&redirect_uri=https%3A%2F%2Fdev.ciam.dignityhealth.org%2Fwidgets%2Fenrollment%2Fdist%2F&response_type=id_token&response_mode=fragment&state=%7B%22returnUrl%22%3A%22foobar%22%2C%22step%22%3A%22enrollment%22%2C%22stepProgress%22%3A%22confirmation%22%7D&nonce=o4VJePCJeaAE17PKtyHpXZK5xwIhvgNpOzaq80hucpd6DTYELlD8jt46D2Ex2WRu&scope=openid%20email%20profile%20phone%20groups&sessionToken=";

    public static final String FETCH_CODE = "oauth2/%s/v1/authorize?response_type=code&client_id=%s&redirect_uri=%s&response_mode=fragment&scope=%s&state=foo&nonce=bar&code_challenge=%s&code_challenge_method=S256&sessionToken=%s";

    public static final String SCHEDULING_VISIT = "api/v1/visit/";
    public static final String SCHEDULING_VALIDATION = "api/v1/visit/schedule/{scheduleID}/visit-settings";

    public static final String PROVIDER_PAGE_NO = "1";
    public static final String PROVIDER_PAGE_SIZE = "25";
    public static final String PROVIDER_DISTANCE = "100";


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
            "     facilityAddress {" +
            "        city" +
            "        countryCode" +
            "        line1" +
            "        line2" +
            "        stateOrProvince" +
            "        zipCode" +
            "      }" +
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

    public static final String MY_PROFILE = "{" +
            " user {" +
            "   username" +
            "   firstName" +
            "   lastName" +
            "   phoneNumber" +
            "   dateOfBirth" +
            "   email" +
            "   isVerified" +
            "   isTermsAccepted" +
            "   preferredName" +
            "   gender" +
            "   idLevel" +
            "   securityQuestion" +
            "  address {" +
            "    city" +
            "    countryCode" +
            "    line1" +
            "    line2" +
            "    stateOrProvince" +
            "    zipCode" +
            "  }" +
            "  insuranceProvider {" +
            "    groupNumber" +
            "    insurancePlan" +
            "    memberNumber" +
            "    providerName" +
            "  }" +
            " }" +
            "}";


}
