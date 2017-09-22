package com.prokarma.myhome.features.login.endpoint;

import java.util.List;

/**
 * Created by cmajji on 9/22/17.
 */

public class RefreshResponse {

    public Result result;
    public Boolean isValid;
    public List<Object> errors = null;
    public List<Object> warnings = null;

    public Result getResult() {
        return result;
    }

    public Boolean getValid() {
        return isValid;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public class Result {

        public String accessToken;
        public String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
