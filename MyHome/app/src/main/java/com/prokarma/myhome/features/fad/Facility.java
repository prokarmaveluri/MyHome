package com.prokarma.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Facility implements Parcelable {

    public String Name;
    public String Address1;
    public String Address2;
    public String City;
    public String State;
    public String ZipCode;
    public String Phone;
    public String Fax;
    public String Url;
    public String Lat;
    @SerializedName(value = "Long")
    public String lon;
    public Integer SortRank;
    public String DistanceMilesFromSearch;
    public String DirectionsLink;
    public String Hash;
    public String LatLongHash;
    public List<Appointment> Appointments;   //TODO This will probably be Appointments, not Strings
    public Boolean LocationMatch;

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

    public String getFax() {
        return Fax;
    }

    public String getUrl() {
        return Url;
    }

    public String getLat() {
        return Lat;
    }

    public String getLon() {
        return lon;
    }

    public Integer getSortRank() {
        return SortRank;
    }

    public Object getDistanceMilesFromSearch() {
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

    public List<Appointment> getAppointments() {
        return Appointments;
    }

    public Boolean getLocationMatch() {
        return LocationMatch;
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

    public Facility() {
    }

    protected Facility(Parcel in) {
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

    public static final Parcelable.Creator<Facility> CREATOR = new Parcelable.Creator<Facility>() {
        @Override
        public Facility createFromParcel(Parcel source) {
            return new Facility(source);
        }

        @Override
        public Facility[] newArray(int size) {
            return new Facility[size];
        }
    };
}
