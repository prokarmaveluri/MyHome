package com.prokarma.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.prokarma.myhome.utils.CommonUtil;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Office implements Parcelable {

    private String Name;
    private String Address1;
    private String Address2;
    private String City;
    private String State;
    private String ZipCode;
    private String Phone;
    private String Fax;
    private String Url;
    private String Lat;
    @SerializedName(value = "Long")
    private String lon;
    private Integer SortRank;
    private String DistanceMilesFromSearch;
    private String DirectionsLink;
    private String Hash;
    private String LatLongHash;
    private ArrayList<Appointment> Appointments;
    private Boolean LocationMatch;

    public String getName() {
        return Name;
    }

    public String getAddress1() {
        return Address1;
    }

    public String getAddress2() {
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

    public String getFax() {
        return Fax;
    }

    public String getUrl() {
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

    public ArrayList<Appointment> getAppointments() {
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
            fullAddress.append(", ").append(State);
        if (null != ZipCode)
            fullAddress.append(" ").append(ZipCode);
        return fullAddress.toString();
    }

    public String getAddressLine(){
        StringBuilder fullAddress = new StringBuilder();
        if (null != Address1)
            fullAddress.append(Address1);
        if (null != Address2)
            fullAddress.append(", ").append(Address2);
        return fullAddress.toString();
    }

    @Override
    public String toString() {
        String officeString = "";
        officeString = officeString +
                (Name != null && !Name.isEmpty() ? Name + "\n" : "") + CommonUtil.constructAddress(Address1, Address2, City, State, ZipCode);
        return officeString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Name);
        dest.writeString(this.Address1);
        dest.writeString(this.Address2);
        dest.writeString(this.City);
        dest.writeString(this.State);
        dest.writeString(this.ZipCode);
        dest.writeString(this.Phone);
        dest.writeString(this.Fax);
        dest.writeString(this.Url);
        dest.writeString(this.Lat);
        dest.writeString(this.lon);
        dest.writeValue(this.SortRank);
        dest.writeString(this.DistanceMilesFromSearch);
        dest.writeString(this.DirectionsLink);
        dest.writeString(this.Hash);
        dest.writeString(this.LatLongHash);
        dest.writeTypedList(this.Appointments);
        dest.writeValue(this.LocationMatch);
    }

    public Office() {
    }

    protected Office(Parcel in) {
        this.Name = in.readString();
        this.Address1 = in.readString();
        this.Address2 = in.readString();
        this.City = in.readString();
        this.State = in.readString();
        this.ZipCode = in.readString();
        this.Phone = in.readString();
        this.Fax = in.readString();
        this.Url = in.readString();
        this.Lat = in.readString();
        this.lon = in.readString();
        this.SortRank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.DistanceMilesFromSearch = in.readString();
        this.DirectionsLink = in.readString();
        this.Hash = in.readString();
        this.LatLongHash = in.readString();
        this.Appointments = in.createTypedArrayList(Appointment.CREATOR);
        this.LocationMatch = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Office> CREATOR = new Parcelable.Creator<Office>() {
        @Override
        public Office createFromParcel(Parcel source) {
            return new Office(source);
        }

        @Override
        public Office[] newArray(int size) {
            return new Office[size];
        }
    };
}
