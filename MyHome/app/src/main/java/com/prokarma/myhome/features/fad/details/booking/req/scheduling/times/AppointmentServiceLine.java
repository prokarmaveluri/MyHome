package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentServiceLine implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
    }

    public AppointmentServiceLine() {
    }

    protected AppointmentServiceLine(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<AppointmentServiceLine> CREATOR = new Parcelable.Creator<AppointmentServiceLine>() {
        @Override
        public AppointmentServiceLine createFromParcel(Parcel source) {
            return new AppointmentServiceLine(source);
        }

        @Override
        public AppointmentServiceLine[] newArray(int size) {
            return new AppointmentServiceLine[size];
        }
    };
}
