package com.prokarma.myhome.features.login.endpoint;

import com.prokarma.myhome.features.profile.Profile;

import java.util.List;

/**
 * Created by cmajji on 9/22/17.
 */

public class SignInResponse {

    public Result result;
    public boolean isValid;
    public List<Object> errors = null;
    public List<Object> warnings = null;

    public Result getResult() {
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

    public class Address {

        public String line1;
        public String line2;
        public String city;
        public String stateOrProvince;
        public String zipCode;
        public String countryCode;

        public String getLine1() {
            return line1;
        }

        public String getLine2() {
            return line2;
        }

        public String getCity() {
            return city;
        }

        public String getStateOrProvince() {
            return stateOrProvince;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    public class InsuranceProvider {

        public String providerName;
        public String insurancePlan;
        public String groupNumber;
        public String memberNumber;

        public String getProviderName() {
            return providerName;
        }

        public String getInsurancePlan() {
            return insurancePlan;
        }

        public String getGroupNumber() {
            return groupNumber;
        }

        public String getMemberNumber() {
            return memberNumber;
        }
    }

    public class Result {

        public String sessionId;
        public String accessToken;
        public String idToken;
        public String refreshToken;
        public Profile userProfile;

        public String getSessionId() {
            return sessionId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getIdToken() {
            return idToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Profile getUserProfile() {
            return userProfile;
        }
    }
}

