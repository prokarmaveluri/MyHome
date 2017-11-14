package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentNextAvailableTime implements Parcelable {

    @SerializedName("appointmentType")
    @Expose
    private String appointmentType;
    @SerializedName("time")
    @Expose
    private String time;

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appointmentType);
        dest.writeString(this.time);
    }

    public AppointmentNextAvailableTime() {
    }

    protected AppointmentNextAvailableTime(Parcel in) {
        this.appointmentType = in.readString();
        this.time = in.readString();
    }

    public static final Parcelable.Creator<AppointmentNextAvailableTime> CREATOR = new Parcelable.Creator<AppointmentNextAvailableTime>() {
        @Override
        public AppointmentNextAvailableTime createFromParcel(Parcel source) {
            return new AppointmentNextAvailableTime(source);
        }

        @Override
        public AppointmentNextAvailableTime[] newArray(int size) {
            return new AppointmentNextAvailableTime[size];
        }
    };
}
