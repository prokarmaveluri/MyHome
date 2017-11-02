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

public class ProviderDetails implements Parcelable {

    @SerializedName("result")
    @Expose
    private List<ProviderDetailsResult> result = null;
    @SerializedName("isValid")
    @Expose
    private Boolean isValid;
    @SerializedName("errors")
    @Expose
    private List<Object> errors = null;
    @SerializedName("warnings")
    @Expose
    private List<Object> warnings = null;

    public List<ProviderDetailsResult> getResult() {
        return result;
    }

    public void setResult(List<ProviderDetailsResult> result) {
        this.result = result;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.result);
        dest.writeValue(this.isValid);
        dest.writeList(this.errors);
        dest.writeList(this.warnings);
    }

    public ProviderDetails() {
    }

    protected ProviderDetails(Parcel in) {
        this.result = new ArrayList<ProviderDetailsResult>();
        in.readList(this.result, ProviderDetailsResult.class.getClassLoader());
        this.isValid = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.errors = new ArrayList<Object>();
        in.readList(this.errors, Object.class.getClassLoader());
        this.warnings = new ArrayList<Object>();
        in.readList(this.warnings, Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProviderDetails> CREATOR = new Parcelable.Creator<ProviderDetails>() {
        @Override
        public ProviderDetails createFromParcel(Parcel source) {
            return new ProviderDetails(source);
        }

        @Override
        public ProviderDetails[] newArray(int size) {
            return new ProviderDetails[size];
        }
    };
}