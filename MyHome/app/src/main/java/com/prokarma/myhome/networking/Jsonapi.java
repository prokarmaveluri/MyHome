package com.prokarma.myhome.networking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kwelsh on 6/12/17.
 */

public class Jsonapi implements Parcelable {

    @SerializedName("version")
    @Expose
    private String version;

    public Jsonapi(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
    }

    protected Jsonapi(Parcel in) {
        this.version = in.readString();
    }

    public static final Parcelable.Creator<Jsonapi> CREATOR = new Parcelable.Creator<Jsonapi>() {
        @Override
        public Jsonapi createFromParcel(Parcel source) {
            return new Jsonapi(source);
        }

        @Override
        public Jsonapi[] newArray(int size) {
            return new Jsonapi[size];
        }
    };
}