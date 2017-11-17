package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentFacility implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("homepageUrl")
    @Expose
    private String homepageUrl;
    @SerializedName("inventoryUrl")
    @Expose
    private String inventoryUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public String getInventoryUrl() {
        return inventoryUrl;
    }

    public void setInventoryUrl(String inventoryUrl) {
        this.inventoryUrl = inventoryUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.homepageUrl);
        dest.writeString(this.inventoryUrl);
    }

    public AppointmentFacility() {
    }

    protected AppointmentFacility(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.homepageUrl = in.readString();
        this.inventoryUrl = in.readString();
    }

    public static final Parcelable.Creator<AppointmentFacility> CREATOR = new Parcelable.Creator<AppointmentFacility>() {
        @Override
        public AppointmentFacility createFromParcel(Parcel source) {
            return new AppointmentFacility(source);
        }

        @Override
        public AppointmentFacility[] newArray(int size) {
            return new AppointmentFacility[size];
        }
    };
}
