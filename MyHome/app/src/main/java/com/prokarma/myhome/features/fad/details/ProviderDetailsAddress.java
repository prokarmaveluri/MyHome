package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsAddress implements Parcelable {

    @SerializedName("Guid")
    @Expose
    private String guid;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("IsPrimaryOffice")
    @Expose
    private Boolean isPrimaryOffice;
    @SerializedName("Rank")
    @Expose
    private Integer rank;
    @SerializedName("AddressType")
    @Expose
    private String addressType;
    @SerializedName("Phones")
    @Expose
    private List<String> phones = null;
    @SerializedName("Faxes")
    @Expose
    private List<String> faxes = null;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("PracticeUrl")
    @Expose
    private String practiceUrl;
    @SerializedName("LegacyId")
    @Expose
    private String legacyId;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Zip")
    @Expose
    private String zip;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("AddressHash")
    @Expose
    private String addressHash;
    @SerializedName("Hash")
    @Expose
    private String hash;
    @SerializedName("LatLongHash")
    @Expose
    private String latLongHash;

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

    public Boolean getIsPrimaryOffice() {
        return isPrimaryOffice;
    }

    public void setIsPrimaryOffice(Boolean isPrimaryOffice) {
        this.isPrimaryOffice = isPrimaryOffice;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getFaxes() {
        return faxes;
    }

    public void setFaxes(List<String> faxes) {
        this.faxes = faxes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPracticeUrl() {
        return practiceUrl;
    }

    public void setPracticeUrl(String practiceUrl) {
        this.practiceUrl = practiceUrl;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public String getAddressHash() {
        return addressHash;
    }

    public void setAddressHash(String addressHash) {
        this.addressHash = addressHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLatLongHash() {
        return latLongHash;
    }

    public void setLatLongHash(String latLongHash) {
        this.latLongHash = latLongHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guid);
        dest.writeString(this.id);
        dest.writeValue(this.isPrimaryOffice);
        dest.writeValue(this.rank);
        dest.writeString(this.addressType);
        dest.writeStringList(this.phones);
        dest.writeStringList(this.faxes);
        dest.writeString(this.name);
        dest.writeString(this.practiceUrl);
        dest.writeString(this.legacyId);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.zip);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeString(this.addressHash);
        dest.writeString(this.hash);
        dest.writeString(this.latLongHash);
    }

    public ProviderDetailsAddress() {
    }

    protected ProviderDetailsAddress(Parcel in) {
        this.guid = in.readString();
        this.id = in.readString();
        this.isPrimaryOffice = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.rank = (Integer) in.readValue(Integer.class.getClassLoader());
        this.addressType = in.readString();
        this.phones = in.createStringArrayList();
        this.faxes = in.createStringArrayList();
        this.name = in.readString();
        this.practiceUrl = in.readString();
        this.legacyId = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.zip = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.addressHash = in.readString();
        this.hash = in.readString();
        this.latLongHash = in.readString();
    }

    public static final Parcelable.Creator<ProviderDetailsAddress> CREATOR = new Parcelable.Creator<ProviderDetailsAddress>() {
        @Override
        public ProviderDetailsAddress createFromParcel(Parcel source) {
            return new ProviderDetailsAddress(source);
        }

        @Override
        public ProviderDetailsAddress[] newArray(int size) {
            return new ProviderDetailsAddress[size];
        }
    };
}