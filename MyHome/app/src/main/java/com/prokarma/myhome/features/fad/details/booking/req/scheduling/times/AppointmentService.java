package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentService implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("serviceLine")
    @Expose
    private AppointmentServiceLine serviceLine;

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

    public AppointmentServiceLine getServiceLine() {
        return serviceLine;
    }

    public void setServiceLine(AppointmentServiceLine serviceLine) {
        this.serviceLine = serviceLine;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeParcelable(this.serviceLine, flags);
    }

    public AppointmentService() {
    }

    protected AppointmentService(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.serviceLine = in.readParcelable(AppointmentServiceLine.class.getClassLoader());
    }

    public static final Parcelable.Creator<AppointmentService> CREATOR = new Parcelable.Creator<AppointmentService>() {
        @Override
        public AppointmentService createFromParcel(Parcel source) {
            return new AppointmentService(source);
        }

        @Override
        public AppointmentService[] newArray(int size) {
            return new AppointmentService[size];
        }
    };
}
