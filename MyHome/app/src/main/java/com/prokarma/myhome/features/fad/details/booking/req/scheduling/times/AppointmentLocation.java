package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentLocation implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("zip")
    @Expose
    private Integer zip;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("services")
    @Expose
    private List<AppointmentService> services = null;
    @SerializedName("hash")
    @Expose
    private String hash;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<AppointmentService> getServices() {
        return services;
    }

    public void setServices(List<AppointmentService> services) {
        this.services = services;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.address);
        dest.writeString(this.state);
        dest.writeString(this.city);
        dest.writeValue(this.zip);
        dest.writeString(this.phone);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeTypedList(this.services);
        dest.writeString(this.hash);
    }

    public AppointmentLocation() {
    }

    protected AppointmentLocation(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.address = in.readString();
        this.state = in.readString();
        this.city = in.readString();
        this.zip = (Integer) in.readValue(Integer.class.getClassLoader());
        this.phone = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.services = in.createTypedArrayList(AppointmentService.CREATOR);
        this.hash = in.readString();
    }

    public static final Parcelable.Creator<AppointmentLocation> CREATOR = new Parcelable.Creator<AppointmentLocation>() {
        @Override
        public AppointmentLocation createFromParcel(Parcel source) {
            return new AppointmentLocation(source);
        }

        @Override
        public AppointmentLocation[] newArray(int size) {
            return new AppointmentLocation[size];
        }
    };
}
