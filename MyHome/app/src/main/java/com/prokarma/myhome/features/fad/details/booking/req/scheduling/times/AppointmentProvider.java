package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentProvider implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("credentials")
    @Expose
    private List<String> credentials = null;
    @SerializedName("languages")
    @Expose
    private List<String> languages = null;
    @SerializedName("services")
    @Expose
    private List<String> services = null;

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

    public List<String> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<String> credentials) {
        this.credentials = credentials;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeStringList(this.credentials);
        dest.writeStringList(this.languages);
        dest.writeStringList(this.services);
    }

    public AppointmentProvider() {
    }

    protected AppointmentProvider(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.credentials = in.createStringArrayList();
        this.languages = in.createStringArrayList();
        this.services = in.createStringArrayList();
    }

    public static final Parcelable.Creator<AppointmentProvider> CREATOR = new Parcelable.Creator<AppointmentProvider>() {
        @Override
        public AppointmentProvider createFromParcel(Parcel source) {
            return new AppointmentProvider(source);
        }

        @Override
        public AppointmentProvider[] newArray(int size) {
            return new AppointmentProvider[size];
        }
    };
}
