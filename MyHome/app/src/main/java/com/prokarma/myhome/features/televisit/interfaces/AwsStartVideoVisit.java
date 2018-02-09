package com.prokarma.myhome.features.televisit.interfaces;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.ChatReport;

import java.util.Map;

/**
 * Created by kwelsh on 11/30/17.
 * Should mirror StartVisitCallback from AmWell
 */

public interface AwsStartVideoVisit {
    void onValidationFailure(@NonNull Map<String, String> map);

    void onProviderEntered(@NonNull Intent intent);

    void onStartVisitEnded(@NonNull String s);

    void onPatientsAheadOfYouCountChanged(int i);

    void onSuggestedTransfer();

    void onChat(@NonNull ChatReport chatReport);

    void onPollFailure(@NonNull Throwable throwable);

    void onResponse(Void aVoid, SDKError sdkError);

    void onFailure(Throwable throwable);
}
