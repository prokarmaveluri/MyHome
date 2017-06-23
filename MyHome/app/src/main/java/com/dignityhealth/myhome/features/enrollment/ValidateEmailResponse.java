package com.dignityhealth.myhome.features.enrollment;

import java.util.List;

/**
 * Created by cmajji on 6/23/17.
 */

public class ValidateEmailResponse {

    private Boolean result;
    private Boolean isValid;
    private List<Object> errors = null;
    private List<Object> warnings = null;

    public Boolean getResult() {
        return result;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public List<Object> getWarnings() {
        return warnings;
    }
}
