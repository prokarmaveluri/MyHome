package com.prokarma.myhome.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.prokarma.myhome.features.fad.details.booking.req.validation.RegIncluded;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;

import java.util.ArrayList;
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

    /**
     * Grabs the enabled fields from a RegValidationResponse
     *
     * @param regValidationResponse
     * @return an array of all the fields enabled
     */
    @Nullable
    public static List<String> getEnabledFields(RegValidationResponse regValidationResponse) {
        for (RegIncluded include : regValidationResponse.getValue().getIncluded()) {
            if (include.getType().equalsIgnoreCase(ValidationUtil.TYPE_ENABLED_FIELDS)) {
                return include.getAttributes().getFields();
            }
        }

        return null;
    }

    /**
     * Grabs the validation rules from a RegValidationResponse
     *
     * @param regValidationResponse
     * @return an array of all the validations
     */
    public static ArrayList<RegIncluded> getValidations(final RegValidationResponse regValidationResponse) {
        ArrayList<RegIncluded> validations = new ArrayList<>();

        for (RegIncluded include : regValidationResponse.getValue().getIncluded()) {
            if (include.getType().equalsIgnoreCase(ValidationUtil.TYPE_VALIDATIONS)) {
                validations.add(include);
            }
        }

        return validations;
    }

    /**
     * Grabs all the required fields from a RegValidationResponse
     *
     * @param regValidationResponse
     * @return an array of the fields that don't allow blanks or nulls
     */
    public static ArrayList<String> getRequired(@NonNull final RegValidationResponse regValidationResponse) {
        ArrayList<String> requiredFields = new ArrayList<>();
        ArrayList<RegIncluded> validations = getValidations(regValidationResponse);

        for (RegIncluded validation : validations) {
            if (!validation.getAttributes().getAllowBlank() || !validation.getAttributes().getAllowNil()) {
                requiredFields.add(validation.getAttributes().getName()); //Is this supposed to be name? Be wary....
            }
        }

        return requiredFields;
    }

    /**
     * Grabs the insurances from a RegValidationResponse
     *
     * @param regValidationResponse
     * @return an array of all the insurances
     */
    public static ArrayList<RegIncluded> getInsurances(@NonNull final RegValidationResponse regValidationResponse) {
        ArrayList<RegIncluded> insurances = new ArrayList<>();

        for (RegIncluded include : regValidationResponse.getValue().getIncluded()) {
            if (include.getType().equalsIgnoreCase(ValidationUtil.TYPE_INSURANCES)) {
                insurances.add(include);
            }
        }

        return insurances;
    }
}
