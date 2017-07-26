package com.prokarma.myhome.features.preferences;

/**
 * Created by cmajji on 7/25/17.
 */

public class MySavedDoctorsRequest {

    private String query = "{" +
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
            "  }" +
            "  }" +
            "}";
}