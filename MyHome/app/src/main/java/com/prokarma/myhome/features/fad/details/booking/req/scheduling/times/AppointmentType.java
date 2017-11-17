package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentType implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("remoteId")
    @Expose
    private String remoteId;
    @SerializedName("id")
    @Expose
    private String id;

    public AppointmentType(String name, String remoteId, String id) {
        this.name = name;
        this.remoteId = remoteId;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.remoteId);
        dest.writeString(this.id);
    }

    public AppointmentType() {
    }

    protected AppointmentType(Parcel in) {
        this.name = in.readString();
        this.remoteId = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<AppointmentType> CREATOR = new Parcelable.Creator<AppointmentType>() {
        @Override
        public AppointmentType createFromParcel(Parcel source) {
            return new AppointmentType(source);
        }

        @Override
        public AppointmentType[] newArray(int size) {
            return new AppointmentType[size];
        }
    };
}
