package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 11/4/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsFacility {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Phone")
    @Expose
    private Object phone;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Addresses")
    @Expose
    private List<ProviderDetailsAddress> addresses = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ProviderDetailsAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ProviderDetailsAddress> addresses) {
        this.addresses = addresses;
    }
}
