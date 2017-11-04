package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsAddress {

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
}