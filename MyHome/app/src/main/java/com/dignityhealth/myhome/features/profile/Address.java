package com.dignityhealth.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwelsh on 5/2/17.
 */

public class Address implements Parcelable {
    public String line1;
    public String line2;
    public String city;
    public String stateOrProvince;
    public String zipCode;
    public String countryCode;

    @Override
    public String toString() {
        return "Address{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", city='" + city + '\'' +
                ", stateOrProvince='" + stateOrProvince + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.line1);
        dest.writeString(this.line2);
        dest.writeString(this.city);
        dest.writeString(this.stateOrProvince);
        dest.writeString(this.zipCode);
        dest.writeString(this.countryCode);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.line1 = in.readString();
        this.line2 = in.readString();
        this.city = in.readString();
        this.stateOrProvince = in.readString();
        this.zipCode = in.readString();
        this.countryCode = in.readString();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
