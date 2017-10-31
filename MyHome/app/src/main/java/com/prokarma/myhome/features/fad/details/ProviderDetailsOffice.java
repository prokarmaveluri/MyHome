package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsOffice {

    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("legacyId")
    @Expose
    private Object legacyId;
    @SerializedName("providerCount")
    @Expose
    private Integer providerCount;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("addresses")
    @Expose
    private List<ProviderDetailsAddress> addresses = null;
    @SerializedName("isPdc")
    @Expose
    private Boolean isPdc;
    @SerializedName("yearsExist")
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

    public Object getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(Object legacyId) {
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

    public Boolean getIsPdc() {
        return isPdc;
    }

    public void setIsPdc(Boolean isPdc) {
        this.isPdc = isPdc;
    }

    public Integer getYearsExist() {
        return yearsExist;
    }

    public void setYearsExist(Integer yearsExist) {
        this.yearsExist = yearsExist;
    }
}