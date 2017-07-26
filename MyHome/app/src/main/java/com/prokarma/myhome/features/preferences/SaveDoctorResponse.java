package com.prokarma.myhome.features.preferences;

import java.util.List;

/**
 * Created by cmajji on 7/24/17.
 */

public class SaveDoctorResponse {

    private Object result;
    private Boolean isValid;
    private List<Object> errors = null;
    private List<Object> warnings = null;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
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

}
