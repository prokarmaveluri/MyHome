package com.televisit.interfaces;

import com.americanwell.sdk.entity.health.Condition;

import java.util.List;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsGetConditions {
    void getConditionsComplete(List<Condition> conditions);

    void getConditionsFailed(String errorMessage);
}
