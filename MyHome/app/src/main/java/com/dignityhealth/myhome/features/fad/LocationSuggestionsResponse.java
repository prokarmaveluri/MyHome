package com.dignityhealth.myhome.features.fad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cmajji on 5/12/17.
 */

public class LocationSuggestionsResponse {

    private String City;
    private String State;
    private String ZipCode;
    private String Lat;
    @SerializedName(value = "Long")
    private String Lon;
    private String DisplayName;

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public String getLat() {
        return Lat;
    }

    public String getLon() {
        return Lon;
    }

    public String getDisplayName() {
        return DisplayName;
    }
}