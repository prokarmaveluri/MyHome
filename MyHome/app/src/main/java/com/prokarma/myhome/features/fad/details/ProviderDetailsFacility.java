package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 11/4/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsFacility implements Parcelable {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Addresses")
    @Expose
    private List<ProviderDetailsAddress> addresses = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ProviderDetailsAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ProviderDetailsAddress> addresses) {
        this.addresses = addresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.url);
        dest.writeTypedList(this.addresses);
    }

    public ProviderDetailsFacility() {
    }

    protected ProviderDetailsFacility(Parcel in) {
        this.name = in.readString();
        this.phone = in.readString();
        this.url = in.readString();
        this.addresses = in.createTypedArrayList(ProviderDetailsAddress.CREATOR);
    }

    public static final Parcelable.Creator<ProviderDetailsFacility> CREATOR = new Parcelable.Creator<ProviderDetailsFacility>() {
        @Override
        public ProviderDetailsFacility createFromParcel(Parcel source) {
            return new ProviderDetailsFacility(source);
        }

        @Override
        public ProviderDetailsFacility[] newArray(int size) {
            return new ProviderDetailsFacility[size];
        }
    };
}
