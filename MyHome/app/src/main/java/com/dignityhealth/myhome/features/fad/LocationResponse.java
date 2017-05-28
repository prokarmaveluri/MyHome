package com.dignityhealth.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cmajji on 5/13/17.
 */

public class LocationResponse implements Parcelable {

    private String City;
    private String State;
    private String ZipCode;
    private String Lat;
    @SerializedName(value = "Long")
    private String lon;
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

    public String getLong() {
        return lon;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.City);
        dest.writeString(this.State);
        dest.writeString(this.ZipCode);
        dest.writeString(this.Lat);
        dest.writeString(this.lon);
        dest.writeString(this.DisplayName);
    }

    public LocationResponse() {
    }

    public LocationResponse(String city, String state, String zipCode, String lat, String lon, String displayName) {
        City = city;
        State = state;
        ZipCode = zipCode;
        Lat = lat;
        this.lon = lon;
        DisplayName = displayName;
    }

    protected LocationResponse(Parcel in) {
        this.City = in.readString();
        this.State = in.readString();
        this.ZipCode = in.readString();
        this.Lat = in.readString();
        this.lon = in.readString();
        this.DisplayName = in.readString();
    }

    public static final Parcelable.Creator<LocationResponse> CREATOR = new Parcelable.Creator<LocationResponse>() {
        @Override
        public LocationResponse createFromParcel(Parcel source) {
            return new LocationResponse(source);
        }

        @Override
        public LocationResponse[] newArray(int size) {
            return new LocationResponse[size];
        }
    };

    @Override
    public String toString() {
        return "LocationResponse{" +
                "City='" + City + '\'' +
                ", State='" + State + '\'' +
                ", ZipCode='" + ZipCode + '\'' +
                ", Lat='" + Lat + '\'' +
                ", lon='" + lon + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                '}';
    }
}
