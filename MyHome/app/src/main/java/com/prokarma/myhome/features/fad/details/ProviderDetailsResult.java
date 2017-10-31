package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsResult {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("npi")
    @Expose
    private String npi;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("displayLastName")
    @Expose
    private String displayLastName;
    @SerializedName("displayLastNamePlural")
    @Expose
    private String displayLastNamePlural;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("middleName")
    @Expose
    private String middleName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("suffix")
    @Expose
    private Object suffix;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("images")
    @Expose
    private List<ProviderDetailsImage> images = null;
    @SerializedName("philosophy")
    @Expose
    private Object philosophy;
    @SerializedName("offices")
    @Expose
    private List<ProviderDetailsOffice> offices = null;
    @SerializedName("primarySpecialities")
    @Expose
    private List<String> primarySpecialities = null;
    @SerializedName("supportsOnlineBooking")
    @Expose
    private Boolean supportsOnlineBooking;
    @SerializedName("providerDetailsUrl")
    @Expose
    private String providerDetailsUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayLastName() {
        return displayLastName;
    }

    public void setDisplayLastName(String displayLastName) {
        this.displayLastName = displayLastName;
    }

    public String getDisplayLastNamePlural() {
        return displayLastNamePlural;
    }

    public void setDisplayLastNamePlural(String displayLastNamePlural) {
        this.displayLastNamePlural = displayLastNamePlural;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Object getSuffix() {
        return suffix;
    }

    public void setSuffix(Object suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProviderDetailsImage> getImages() {
        return images;
    }

    public void setImages(List<ProviderDetailsImage> images) {
        this.images = images;
    }

    public Object getPhilosophy() {
        return philosophy;
    }

    public void setPhilosophy(Object philosophy) {
        this.philosophy = philosophy;
    }

    public List<ProviderDetailsOffice> getOffices() {
        return offices;
    }

    public void setOffices(List<ProviderDetailsOffice> offices) {
        this.offices = offices;
    }

    public List<String> getPrimarySpecialities() {
        return primarySpecialities;
    }

    public void setPrimarySpecialities(List<String> primarySpecialities) {
        this.primarySpecialities = primarySpecialities;
    }

    public Boolean getSupportsOnlineBooking() {
        return supportsOnlineBooking;
    }

    public void setSupportsOnlineBooking(Boolean supportsOnlineBooking) {
        this.supportsOnlineBooking = supportsOnlineBooking;
    }

    public String getProviderDetailsUrl() {
        return providerDetailsUrl;
    }

    public void setProviderDetailsUrl(String providerDetailsUrl) {
        this.providerDetailsUrl = providerDetailsUrl;
    }
}