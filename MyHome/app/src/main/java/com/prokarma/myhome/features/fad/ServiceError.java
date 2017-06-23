package com.prokarma.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwelsh on 6/1/17.
 */

public class ServiceError implements Parcelable {

    public String Source;
    public String Url;
    public String StatusCode;
    public String Message;
    public String Data;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Source);
        dest.writeString(this.Url);
        dest.writeString(this.StatusCode);
        dest.writeString(this.Message);
        dest.writeString(this.Data);
    }

    public ServiceError() {
    }

    protected ServiceError(Parcel in) {
        this.Source = in.readString();
        this.Url = in.readString();
        this.StatusCode = in.readString();
        this.Message = in.readString();
        this.Data = in.readString();
    }

    public static final Parcelable.Creator<ServiceError> CREATOR = new Parcelable.Creator<ServiceError>() {
        @Override
        public ServiceError createFromParcel(Parcel source) {
            return new ServiceError(source);
        }

        @Override
        public ServiceError[] newArray(int size) {
            return new ServiceError[size];
        }
    };

    @Override
    public String toString() {
        return "ServiceError{" +
                "Source='" + Source + '\'' +
                ", Url='" + Url + '\'' +
                ", StatusCode='" + StatusCode + '\'' +
                ", Message='" + Message + '\'' +
                ", Data='" + Data + '\'' +
                '}';
    }
}
