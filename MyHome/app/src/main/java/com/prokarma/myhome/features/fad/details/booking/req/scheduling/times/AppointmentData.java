package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentData implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("attributes")
    @Expose
    private AppointmentAttributes attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppointmentAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(AppointmentAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeParcelable(this.attributes, flags);
    }

    public AppointmentData() {
    }

    protected AppointmentData(Parcel in) {
        this.type = in.readString();
        this.id = in.readString();
        this.attributes = in.readParcelable(AppointmentAttributes.class.getClassLoader());
    }

    public static final Parcelable.Creator<AppointmentData> CREATOR = new Parcelable.Creator<AppointmentData>() {
        @Override
        public AppointmentData createFromParcel(Parcel source) {
            return new AppointmentData(source);
        }

        @Override
        public AppointmentData[] newArray(int size) {
            return new AppointmentData[size];
        }
    };
}
