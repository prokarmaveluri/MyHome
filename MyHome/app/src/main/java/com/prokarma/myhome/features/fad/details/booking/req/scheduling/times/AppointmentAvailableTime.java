package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentAvailableTime implements Parcelable {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("times")
    @Expose
    private List<AppointmentTime> times = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<AppointmentTime> getTimes() {
        return times;
    }

    public void setTimes(List<AppointmentTime> times) {
        this.times = times;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeTypedList(this.times);
    }

    public AppointmentAvailableTime() {
    }

    protected AppointmentAvailableTime(Parcel in) {
        this.date = in.readString();
        this.times = in.createTypedArrayList(AppointmentTime.CREATOR);
    }

    public static final Parcelable.Creator<AppointmentAvailableTime> CREATOR = new Parcelable.Creator<AppointmentAvailableTime>() {
        @Override
        public AppointmentAvailableTime createFromParcel(Parcel source) {
            return new AppointmentAvailableTime(source);
        }

        @Override
        public AppointmentAvailableTime[] newArray(int size) {
            return new AppointmentAvailableTime[size];
        }
    };
}
