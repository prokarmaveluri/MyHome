package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProviderDetailsImage implements Parcelable {

    @SerializedName("ImageType")
    @Expose
    private String imageType;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Width")
    @Expose
    private Integer width;
    @SerializedName("Height")
    @Expose
    private Integer height;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageType);
        dest.writeString(this.url);
        dest.writeValue(this.width);
        dest.writeValue(this.height);
    }

    public ProviderDetailsImage() {
    }

    protected ProviderDetailsImage(Parcel in) {
        this.imageType = in.readString();
        this.url = in.readString();
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProviderDetailsImage> CREATOR = new Parcelable.Creator<ProviderDetailsImage>() {
        @Override
        public ProviderDetailsImage createFromParcel(Parcel source) {
            return new ProviderDetailsImage(source);
        }

        @Override
        public ProviderDetailsImage[] newArray(int size) {
            return new ProviderDetailsImage[size];
        }
    };
}