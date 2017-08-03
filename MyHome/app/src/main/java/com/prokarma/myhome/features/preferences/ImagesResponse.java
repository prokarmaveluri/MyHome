package com.prokarma.myhome.features.preferences;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cmajji on 8/2/17.
 */

public class ImagesResponse implements Parcelable {

    private String imageType;
    private String url;
    private Integer width;
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

    public ImagesResponse() {
    }

    protected ImagesResponse(Parcel in) {
        this.imageType = in.readString();
        this.url = in.readString();
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImagesResponse> CREATOR = new Parcelable.Creator<ImagesResponse>() {
        @Override
        public ImagesResponse createFromParcel(Parcel source) {
            return new ImagesResponse(source);
        }

        @Override
        public ImagesResponse[] newArray(int size) {
            return new ImagesResponse[size];
        }
    };
}
