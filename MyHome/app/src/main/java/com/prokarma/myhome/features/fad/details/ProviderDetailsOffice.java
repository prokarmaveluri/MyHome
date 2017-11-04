package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProviderDetailsOffice implements Parcelable {

    @SerializedName("Guid")
    @Expose
    private String guid;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("LegacyId")
    @Expose
    private String legacyId;
    @SerializedName("ProviderCount")
    @Expose
    private Integer providerCount;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Addresses")
    @Expose
    private List<ProviderDetailsAddress> addresses = null;
    @SerializedName("IsPDC")
    @Expose
    private Boolean isPDC;
    @SerializedName("YearsExist")
    @Expose
    private Integer yearsExist;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public Integer getProviderCount() {
        return providerCount;
    }

    public void setProviderCount(Integer providerCount) {
        this.providerCount = providerCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProviderDetailsAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ProviderDetailsAddress> addresses) {
        this.addresses = addresses;
    }

    public Boolean getIsPDC() {
        return isPDC;
    }

    public void setIsPDC(Boolean isPDC) {
        this.isPDC = isPDC;
    }

    public Integer getYearsExist() {
        return yearsExist;
    }

    public void setYearsExist(Integer yearsExist) {
        this.yearsExist = yearsExist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guid);
        dest.writeString(this.id);
        dest.writeString(this.legacyId);
        dest.writeValue(this.providerCount);
        dest.writeString(this.name);
        dest.writeList(this.addresses);
        dest.writeValue(this.isPDC);
        dest.writeValue(this.yearsExist);
    }

    public ProviderDetailsOffice() {
    }

    protected ProviderDetailsOffice(Parcel in) {
        this.guid = in.readString();
        this.id = in.readString();
        this.legacyId = in.readString();
        this.providerCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.addresses = new ArrayList<ProviderDetailsAddress>();
        in.readList(this.addresses, ProviderDetailsAddress.class.getClassLoader());
        this.isPDC = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.yearsExist = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProviderDetailsOffice> CREATOR = new Parcelable.Creator<ProviderDetailsOffice>() {
        @Override
        public ProviderDetailsOffice createFromParcel(Parcel source) {
            return new ProviderDetailsOffice(source);
        }

        @Override
        public ProviderDetailsOffice[] newArray(int size) {
            return new ProviderDetailsOffice[size];
        }
    };
}