package com.prokarma.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwelsh on 6/1/17.
 */

public class AppointmentType implements Parcelable {
    public String Id;
    public String Description;
    public String WellKnown;

    public AppointmentType() {
    }

    public AppointmentType(String id, String description, String wellKnown) {
        Id = id;
        Description = description;
        WellKnown = wellKnown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Description);
        dest.writeString(this.WellKnown);
    }

    protected AppointmentType(Parcel in) {
        this.Id = in.readString();
        this.Description = in.readString();
        this.WellKnown = in.readString();
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

    @Override
    public String toString() {
        return "AppointmentType{" +
                "Id='" + Id + '\'' +
                ", Description='" + Description + '\'' +
                ", WellKnown='" + WellKnown + '\'' +
                '}';
    }
}
