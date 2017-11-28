/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit;

import com.americanwell.sdk.activity.VideoVisitConstants;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;

import icepick.State;

/**
 * Presenter for GuestFinishedActivity
 *
 * @since AWSDK 3.1
 */
public class GuestFinishedPresenter extends BaseSampleNucleusRxPresenter<GuestFinishedActivity> {

    private static final String LOG_TAG = GuestFinishedPresenter.class.getName();

    @State
    Integer resultCode;
    @State
    Boolean appServerDisconnected;
    @State
    Boolean videoDisconnected;

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    public void setResultCode(final int resultCode) {
        this.resultCode = resultCode;
    }

    public void setAppServerDisconnected(boolean appServerDisconnected) {
        this.appServerDisconnected = appServerDisconnected;
    }

    public void setVideoDisconnected(boolean videoDisconnected) {
        this.videoDisconnected = videoDisconnected;
    }

    @Override
    public void onTakeView(GuestFinishedActivity view) {
        super.onTakeView(view);

        switch(resultCode) {
            case VideoVisitConstants.GUEST_CONFERENCE_RESULT_CANCELED:
                DefaultLogger.d(LOG_TAG, "handling result - canceled");
                view.setConferenceCanceled();
                break;
            case VideoVisitConstants.GUEST_CONFERENCE_RESULT_ENDED:
                DefaultLogger.d(LOG_TAG, "handling result - ended");
                view.setConferenceEnded();
                break;
            case VideoVisitConstants.GUEST_CONFERENCE_RESULT_EXITED:
                DefaultLogger.d(LOG_TAG, "handling result - exited");
                view.setConferenceExited();
                break;
            case VideoVisitConstants.VISIT_RESULT_TIMED_OUT:
                DefaultLogger.d(LOG_TAG, "handling result - timed out");
                view.setConferenceTimedOut();
                break;
            case VideoVisitConstants.VISIT_RESULT_PERMISSIONS_NOT_GRANTED:
                DefaultLogger.d(LOG_TAG, "handling result - permissions not granted");
                view.setPermissionsNotGranted();
                break;
            case VideoVisitConstants.VISIT_RESULT_NETWORK_FAILURE:
                DefaultLogger.d(LOG_TAG, "handling result - network failure");
                view.setNetworkFailure(videoDisconnected);
                break;
        }
    }

    public void abandon() {
        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        awsdk.getVisitManager().abandonGuestConference();
    }
}
