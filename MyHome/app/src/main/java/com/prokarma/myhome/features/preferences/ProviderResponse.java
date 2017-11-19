package com.prokarma.myhome.features.preferences;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cmajji on 8/2/17.
 */

public class ProviderResponse implements Parcelable {

    private String firstName;
    private String lastName;
    private String npi;
    private String displayName;
    private String displayLastName;
    private String displayLastNamePlural;
    private String middleName;
    private String suffix;
    private String title;
    private String philosophy;
    private Boolean supportsOnlineBooking;
    private String providerDetailsUrl;
    private List<String> primarySpecialities = null;
    private List<ImagesResponse> images = null;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Object getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getPhilosophy() {
        return philosophy;
    }

    public void setPhilosophy(String philosophy) {
        this.philosophy = philosophy;
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

    public List<String> getPrimarySpecialities() {
        return primarySpecialities;
    }

    public void setPrimarySpecialities(List<String> primarySpecialities) {
        this.primarySpecialities = primarySpecialities;
    }

    public List<ImagesResponse> getImages() {
        return images;
    }

    public void setImages(List<ImagesResponse> images) {
        this.images = images;
    }

    public ProviderResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.npi);
        dest.writeString(this.displayName);
        dest.writeString(this.displayLastName);
        dest.writeString(this.displayLastNamePlural);
        dest.writeString(this.middleName);
        dest.writeString(this.suffix);
        dest.writeString(this.title);
        dest.writeString(this.philosophy);
        dest.writeValue(this.supportsOnlineBooking);
        dest.writeString(this.providerDetailsUrl);
        dest.writeStringList(this.primarySpecialities);
        dest.writeTypedList(this.images);
    }

    protected ProviderResponse(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.npi = in.readString();
        this.displayName = in.readString();
        this.displayLastName = in.readString();
        this.displayLastNamePlural = in.readString();
        this.middleName = in.readString();
        this.suffix = in.readString();
        this.title = in.readString();
        this.philosophy = in.readString();
        this.supportsOnlineBooking = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.providerDetailsUrl = in.readString();
        this.primarySpecialities = in.createStringArrayList();
        this.images = in.createTypedArrayList(ImagesResponse.CREATOR);
    }

    public static final Creator<ProviderResponse> CREATOR = new Creator<ProviderResponse>() {
        @Override
        public ProviderResponse createFromParcel(Parcel source) {
            return new ProviderResponse(source);
        }

        @Override
        public ProviderResponse[] newArray(int size) {
            return new ProviderResponse[size];
        }
    };
}
