package com.dignityhealth.myhome.features.fad.details;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Appointment implements Parcelable {
    public String Time;
    public boolean Selected;
    public String FacilityId;
    public String FacilityAddress;
    public String FacilityCity;
    public String FacilityState;
    public String FacilityZip;
    public String FacilityLat;
    public String FacilityLong;
    public ArrayList<String> RegistrationUrl;
    public String FullAddress;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Time);
        dest.writeByte(this.Selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.FacilityId);
        dest.writeString(this.FacilityAddress);
        dest.writeString(this.FacilityCity);
        dest.writeString(this.FacilityState);
        dest.writeString(this.FacilityZip);
        dest.writeString(this.FacilityLat);
        dest.writeString(this.FacilityLong);
        dest.writeStringList(this.RegistrationUrl);
        dest.writeString(this.FullAddress);
    }

    public Appointment() {
    }

    protected Appointment(Parcel in) {
        this.Time = in.readString();
        this.Selected = in.readByte() != 0;
        this.FacilityId = in.readString();
        this.FacilityAddress = in.readString();
        this.FacilityCity = in.readString();
        this.FacilityState = in.readString();
        this.FacilityZip = in.readString();
        this.FacilityLat = in.readString();
        this.FacilityLong = in.readString();
        this.RegistrationUrl = in.createStringArrayList();
        this.FullAddress = in.readString();
    }

    public static final Parcelable.Creator<Appointment> CREATOR = new Parcelable.Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel source) {
            return new Appointment(source);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
