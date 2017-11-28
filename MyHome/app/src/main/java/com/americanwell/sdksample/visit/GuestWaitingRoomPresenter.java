/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.content.Intent;
import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.manager.VisitManager;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKStartConferenceResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for {@link GuestWaitingRoomActivity}
 *
 * @since AWSDK 2.0
 */
public class GuestWaitingRoomPresenter extends BaseSampleNucleusRxPresenter<GuestWaitingRoomActivity> {

    private static final int START_GUEST_CONFERENCE = 618;

    @State
    String name;
    @State
    String email;
    @State
    boolean started;
    @State
    Intent visitFinishedIntent;

    @Inject
    VisitManager visitManager;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(START_GUEST_CONFERENCE,
                new SampleRequestFunc0<SDKStartConferenceResponse<Void, SDKError>>(START_GUEST_CONFERENCE) {
                    @Override
                    public Observable<SDKStartConferenceResponse<Void, SDKError>> go() {
                        return observableService.startGuestConference(
                                awsdk.getLaunchParams(),
                                email,
                                name,
                                visitFinishedIntent
                        );
                    }
                },
                new SampleStartConferenceResponseAction2<Void, SDKError, SDKStartConferenceResponse<Void, SDKError>>(START_GUEST_CONFERENCE) {
                    @Override
                    public void onConferenceReady(GuestWaitingRoomActivity activity, Intent intent) {
                        stop(START_GUEST_CONFERENCE);
                        view.setConferenceReady(intent);
                    }

                    @Override
                    public void onConferenceCanceled(GuestWaitingRoomActivity activity) {
                        view.setConferenceCanceled();
                    }

                    @Override
                    public void onConferenceDisabled(GuestWaitingRoomActivity activity) {
                        view.setConferenceDisabled();
                    }

                    @Override
                    public void onConferenceEnded(GuestWaitingRoomActivity activity) {
                        view.setConferenceEnded();
                    }
                },
                new SampleFailureAction2(START_GUEST_CONFERENCE)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(GuestWaitingRoomActivity view) {
        super.onTakeView(view);
        if (!started) {
            started = true;
            start(START_GUEST_CONFERENCE);
        }
    }

    public void setCredentials(String name, String email) {
        this.name = name;
        this.email = email;
    }

    protected void leaveVisit() {
        visitManager.abandonGuestConference();
        view.onConferenceAbandoned();
    }

    @Override
    protected boolean shouldHaveConsumer() {
        return false;
    }

    public void setVisitFinishedIntent(final Intent intent) {
        this.visitFinishedIntent = intent;
    }
}
