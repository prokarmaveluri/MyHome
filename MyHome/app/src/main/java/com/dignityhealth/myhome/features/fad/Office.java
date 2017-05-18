package com.dignityhealth.myhome.features.fad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Office {

    private String Name;
    private String Address1;
    private Object Address2;
    private String City;
    private String State;
    private String ZipCode;
    private String Phone;
    private Object Fax;
    private Object Url;
    private String Lat;
    @SerializedName(value = "Long")
    private String lon;
    private Integer SortRank;
    private String DistanceMilesFromSearch;
    private String DirectionsLink;
    private String Hash;
    private String LatLongHash;
    private Object Appointments;
    private Boolean LocationMatch;

    public String getName() {
        return Name;
    }

    public String getAddress1() {
        return Address1;
    }

    public Object getAddress2() {
        return Address2;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public String getPhone() {
        return Phone;
    }

    public Object getFax() {
        return Fax;
    }

    public Object getUrl() {
        return Url;
    }

    public String getLat() {
        return Lat;
    }

    public String getLong() {
        return lon;
    }

    public Integer getSortRank() {
        return SortRank;
    }

    public String getDistanceMilesFromSearch() {
        return DistanceMilesFromSearch;
    }

    public String getDirectionsLink() {
        return DirectionsLink;
    }

    public String getHash() {
        return Hash;
    }

    public String getLatLongHash() {
        return LatLongHash;
    }

    public Object getAppointments() {
        return Appointments;
    }

    public Boolean getLocationMatch() {
        return LocationMatch;
    }

    public String getAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (null != City)
            fullAddress.append(City);
        if (null != State)
            fullAddress.append(", " + State);
        if (null != ZipCode)
            fullAddress.append(" " + ZipCode);
        return fullAddress.toString();
    }
}
