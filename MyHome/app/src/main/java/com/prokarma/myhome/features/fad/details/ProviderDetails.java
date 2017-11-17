package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetails {

    @SerializedName("Result")
    @Expose
    private List<ProviderDetailsResult> result = null;
    @SerializedName("IsValid")
    @Expose
    private Boolean isValid;
    @SerializedName("Errors")
    @Expose
    private List<String> errors = null;
    @SerializedName("Warnings")
    @Expose
    private List<String> warnings = null;

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

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

}