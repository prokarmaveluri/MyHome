package com.dignityhealth.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cmajji on 5/16/17.
 */


public class CommonModel implements Parcelable {

    private String Label;
    private String Value;
    private Integer Count;
    private Boolean Selected;
    private String GroupLabel;

    public String getLabel() {
        return Label;
    }

    public String getValue() {
        return Value;
    }

    public Integer getCount() {
        return Count;
    }

    public Boolean getSelected() {
        return Selected;
    }

    public String getGroupLabel() {
        return GroupLabel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Label);
        dest.writeString(this.Value);
        dest.writeValue(this.Count);
        dest.writeValue(this.Selected);
        dest.writeString(this.GroupLabel);
    }

    public CommonModel() {
    }

    protected CommonModel(Parcel in) {
        this.Label = in.readString();
        this.Value = in.readString();
        this.Count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.Selected = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.GroupLabel = in.readString();
    }

    public static final Parcelable.Creator<CommonModel> CREATOR = new Parcelable.Creator<CommonModel>() {
        @Override
        public CommonModel createFromParcel(Parcel source) {
            return new CommonModel(source);
        }

        @Override
        public CommonModel[] newArray(int size) {
            return new CommonModel[size];
        }
    };
}
