package com.televisit.interfaces;

import com.americanwell.sdk.entity.health.Allergy;

import java.util.List;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsGetAllergies {
    void getAllergiesComplete(List<Allergy> allergy);

    void getAllergiesFailed(String errorMessage);
}
