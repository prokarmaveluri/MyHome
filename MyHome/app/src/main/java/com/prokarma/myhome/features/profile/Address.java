package com.prokarma.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Objects;

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

    public Address(String line1, @Nullable String line2, String city, String stateOrProvince, String zipCode, @Nullable String countryCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.stateOrProvince = stateOrProvince;
        this.zipCode = zipCode;
        this.countryCode = countryCode;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(line1, address.line1) &&
                Objects.equals(line2, address.line2) &&
                Objects.equals(city, address.city) &&
                Objects.equals(stateOrProvince, address.stateOrProvince) &&
                Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(countryCode, address.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, city, stateOrProvince, zipCode, countryCode);
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton
     *
     * @param otherAddress
     * @return
     */
    public static Address copy(Address otherAddress) {
        Address address = new Address();

        if (otherAddress != null) {
            address.line1 = otherAddress.line1;
            address.line2 = otherAddress.line2;
            address.city = otherAddress.city;
            address.stateOrProvince = otherAddress.stateOrProvince;
            address.zipCode = otherAddress.zipCode;
            address.countryCode = otherAddress.countryCode;
        }

        return address;
    }
}
