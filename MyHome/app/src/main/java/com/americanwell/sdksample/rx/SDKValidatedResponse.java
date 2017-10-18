/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.manager.ValidationReason;

import java.util.Map;

/**
 * This is a variation of SDKResponse that adds support for validations
 */
public class SDKValidatedResponse<T, E extends SDKError> extends SDKResponse<T, E> {
    protected Map<String, ValidationReason> validationReasons;

    public SDKValidatedResponse(T result, E error, Map<String, ValidationReason> validationReasons) {
        super(result, error);
        this.validationReasons = validationReasons;
    }

    public Map<String, ValidationReason> getValidationReasons() {
        return validationReasons;
    }

}
