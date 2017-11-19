package com.prokarma.myhome.features.login.endpoint;

import java.util.List;

/**
 * Created by kwelsh on 11/18/17.
 */

public class AmWellResponse {
    public String result;
    public boolean isValid;
    public List<Object> errors = null;
    public List<Object> warnings = null;

    public String getResult() {
        return result;
    }

    public boolean getValid() {
        return isValid;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public List<Object> getWarnings() {
        return warnings;
    }
}
