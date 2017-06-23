package com.prokarma.myhome.features.fad.details;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwelsh on 6/5/17.
 */

public class Image implements Parcelable {
    public String Url;
    public String Size;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Url);
        dest.writeString(this.Size);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.Url = in.readString();
        this.Size = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public String toString() {
        return "Image{" +
                "Url='" + Url + '\'' +
                ", Size='" + Size + '\'' +
                '}';
    }
}
