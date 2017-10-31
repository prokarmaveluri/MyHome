package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsAddress {

    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("isPrimaryOffice")
    @Expose
    private Boolean isPrimaryOffice;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("addressType")
    @Expose
    private String addressType;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("phones")
    @Expose
    private List<String> phones = null;
    @SerializedName("faxes")
    @Expose
    private List<String> faxes = null;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("practiceUrl")
    @Expose
    private String practiceUrl;
    @SerializedName("legacyId")
    @Expose
    private Object legacyId;

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

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public String getPracticeUrl() {
        return practiceUrl;
    }

    public void setPracticeUrl(String practiceUrl) {
        this.practiceUrl = practiceUrl;
    }

    public Object getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(Object legacyId) {
        this.legacyId = legacyId;
    }
}