/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.List;

import rx.Observable;

/**
 * Presenter for MedicalHistoryActivity
 */
public class MedicalHistoryPresenter extends BaseHealthItemIntakePresenter<MedicalHistoryActivity, Condition> {

    private static int GET_CONDITIONS = 320;
    private static int UPDATE_CONDITIONS = 321;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_CONDITIONS,
                new SampleRequestFunc0<SDKResponse<List<Condition>, SDKError>>(GET_CONDITIONS) {
                    @Override
                    public Observable<SDKResponse<List<Condition>, SDKError>> go() {
                        return observableService.getConditions(consumer);
                    }
                },
                new SampleResponseAction2<List<Condition>, SDKError, SDKResponse<List<Condition>, SDKError>>(GET_CONDITIONS) {
                    @Override
                    public void onSuccess(MedicalHistoryActivity activity, List<Condition> conditions) {
                        setItems(conditions);
                    }
                },
                new SampleFailureAction2(GET_CONDITIONS)
        );

        restartableLatestCache(UPDATE_CONDITIONS,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(UPDATE_CONDITIONS) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        // pass in consumerHealthItems (conditions)
                        // the manager will figure out which ones are "current"
                        return observableService.updateConditions(
                                consumer,
                                consumerHealthItems
                        );
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(UPDATE_CONDITIONS) {
                    @Override
                    public void onSuccess(MedicalHistoryActivity activity, Void aVoid) {
                        view.setConditionsSubmitted();
                    }
                },
                new SampleFailureAction2(UPDATE_CONDITIONS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    public void startGetItems() {
        start(GET_CONDITIONS);
    }

    public void updateConditions() {
        start(UPDATE_CONDITIONS);
    }
}
