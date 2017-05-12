package com.dignityhealth.myhome.features.fad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cmajji on 5/12/17.
 */

public class LocationSuggestionsResponse {

    private String city;
    private String state;
    private String zipCode;
    private String lat;
    @SerializedName(value = "Long")
    private String lon;
    private String displayName;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return lon;
    }

    public void setLong(String lon) {
        this.lon = lon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}