package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentTime implements Parcelable, Comparable {

    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("appointmentTypes")
    @Expose
    private List<AppointmentType> appointmentTypes = null;

    public AppointmentTime(String time, List<AppointmentType> appointmentTypes) {
        this.time = time;
        this.appointmentTypes = appointmentTypes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AppointmentType> getAppointmentTypes() {
        return appointmentTypes;
    }

    public void setAppointmentTypes(List<AppointmentType> appointmentTypes) {
        this.appointmentTypes = appointmentTypes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeTypedList(this.appointmentTypes);
    }

    public AppointmentTime() {
    }

    protected AppointmentTime(Parcel in) {
        this.time = in.readString();
        this.appointmentTypes = in.createTypedArrayList(AppointmentType.CREATOR);
    }

    public static final Parcelable.Creator<AppointmentTime> CREATOR = new Parcelable.Creator<AppointmentTime>() {
        @Override
        public AppointmentTime createFromParcel(Parcel source) {
            return new AppointmentTime(source);
        }

        @Override
        public AppointmentTime[] newArray(int size) {
            return new AppointmentTime[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object anotherAppointment) {
        String compareDate = ((AppointmentTime) anotherAppointment).time;
        return this.time.compareTo(compareDate);
    }
}
