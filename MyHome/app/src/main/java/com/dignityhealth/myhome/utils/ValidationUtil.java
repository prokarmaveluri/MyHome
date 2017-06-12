package com.dignityhealth.myhome.utils;

import android.support.annotation.Nullable;

import com.dignityhealth.myhome.features.fad.details.booking.req.RegIncluded;
import com.dignityhealth.myhome.features.fad.details.booking.req.RegValidationResponse;

import java.util.List;

/**
 * Created by cmajji on 4/26/17.
 */

public class ValidationUtil {
    public static final String TYPE_VALIDATIONS = "validations";
    public static final String TYPE_ENABLED_FIELDS = "enabled-fields";
    public static final String TYPE_INSURANCES = "insurances";

    public static final String FIELD_BIRTHDATE = "birthdate";
    public static final String FIELD_CAREGIVER_NAME = "caregiver-name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_FIRST_NAME = "first-name";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_INSURANCE_PLAN_NAME = "insurance-plan-name";
    public static final String FIELD_INSURANCE_PLAN_PERMALINK = "insurance-plan-permalink";
    public static final String FIELD_LAST_NAME = "last-name";
    public static final String FIELD_MIDDLE_INITIAL = "middle-initial";
    public static final String FIELD_NEW_PATIENT = "new-patient";
    public static final String FIELD_REASON_FOR_VISIT = "patient-complaint";
    public static final String FIELD_PHONE_NUMBER = "phone-number";
    public static final String FIELD_PREGNANT = "pregnant";
    public static final String FIELD_TERMS_OF_SERVICE = "terms-tos";
    public static final String FIELD_WEEKS_PREGNANT = "weeks-pregnant";
    public static final String FIELD_ZIP = "zip";

    @Nullable
    public static List<String> getEnabledFields(RegValidationResponse regValidationResponse){
        for (RegIncluded include : regValidationResponse.getValue().getIncluded()) {
            if(include.getType().equalsIgnoreCase(ValidationUtil.TYPE_ENABLED_FIELDS)){
                return include.getAttributes().getFields();
            }
        }

        return null;
    }
}
