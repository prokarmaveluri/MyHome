package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prokarma.myhome.networking.Jsonapi;

import java.util.ArrayList;
import java.util.List;

public class AppointmentTimeSlots implements Parcelable {

    @SerializedName("jsonapi")
    @Expose
    private Jsonapi jsonapi;
    @SerializedName("data")
    @Expose
    private List<AppointmentData> data = null;

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public List<AppointmentData> getData() {
        return data;
    }

    public void setData(List<AppointmentData> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.jsonapi, flags);
        dest.writeList(this.data);
    }

    public AppointmentTimeSlots() {
    }

    protected AppointmentTimeSlots(Parcel in) {
        this.jsonapi = in.readParcelable(Jsonapi.class.getClassLoader());
        this.data = new ArrayList<AppointmentData>();
        in.readList(this.data, AppointmentData.class.getClassLoader());
    }

    public static final Parcelable.Creator<AppointmentTimeSlots> CREATOR = new Parcelable.Creator<AppointmentTimeSlots>() {
        @Override
        public AppointmentTimeSlots createFromParcel(Parcel source) {
            return new AppointmentTimeSlots(source);
        }

        @Override
        public AppointmentTimeSlots[] newArray(int size) {
            return new AppointmentTimeSlots[size];
        }
    };
}
