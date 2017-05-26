package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwelsh on 5/24/17.
 * Simple Model to represent a time and if it was picked or not for booking an appointment
 */

public class BookingTimeSlot implements Parcelable {
    public String time;
    public boolean isPicked;

    public BookingTimeSlot(String time, boolean isPicked) {
        this.time = time;
        this.isPicked = isPicked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeByte(this.isPicked ? (byte) 1 : (byte) 0);
    }

    protected BookingTimeSlot(Parcel in) {
        this.time = in.readString();
        this.isPicked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BookingTimeSlot> CREATOR = new Parcelable.Creator<BookingTimeSlot>() {
        @Override
        public BookingTimeSlot createFromParcel(Parcel source) {
            return new BookingTimeSlot(source);
        }

        @Override
        public BookingTimeSlot[] newArray(int size) {
            return new BookingTimeSlot[size];
        }
    };
}
