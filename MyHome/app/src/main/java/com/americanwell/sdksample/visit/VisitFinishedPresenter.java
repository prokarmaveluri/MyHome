/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit;

import android.os.Bundle;

import com.americanwell.sdk.activity.VideoVisitConstants;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;

import icepick.State;

import static com.americanwell.sdksample.home.HomePresenter.LOG_TAG;

/**
 * Presenter for VisitFinishedActivity
 *
 * @since AWSDK 3.1
 */
public class VisitFinishedPresenter extends BaseSampleNucleusRxPresenter<VisitFinishedActivity> {

    @State
    Integer resultCode;
    @State
    Visit visit;
    @State
    Boolean appServerDisconnected;
    @State
    Boolean videoDisconnected;
    @State
    Boolean providerConnected;

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    void setResultCode(final int resultCode) {
        this.resultCode = resultCode;
    }

    public void setVisit(final Visit visit) {
        this.visit = visit;
        if (stateUtils.getLoginConsumer() == null) {
            // in the use case where the app was killed and restarted during the visit console, we may have lost our
            // in-memory saved state.  this will help restore it.
            stateUtils.setLoginConsumer(visit.getAuthenticatedConsumer());
        }
    }

    void setAppServerDisconnected(boolean appServerDisconnected) {
        this.appServerDisconnected = appServerDisconnected;
    }

    void setVideoDisconnected(boolean videoDisconnected) {
        this.videoDisconnected = videoDisconnected;
    }

    void setProviderConnected(boolean providerConnected) {
        this.providerConnected = providerConnected;
    }

    @Override
    public void onTakeView(VisitFinishedActivity view) {
        super.onTakeView(view);

        switch (resultCode) {
            case VideoVisitConstants.VISIT_RESULT_READY_FOR_SUMMARY:
                DefaultLogger.d(LOG_TAG, "handling result - ready for summary");
                view.setReadyForSummary(visit);
                break;
            case VideoVisitConstants.VISIT_RESULT_PROVIDER_GONE:
                DefaultLogger.d(LOG_TAG, "handling result - provider gone");
                view.setProviderGone();
                break;
            case VideoVisitConstants.VISIT_RESULT_CONSUMER_CANCEL:
                DefaultLogger.d(LOG_TAG, "handling result - consumer cancel");
                view.setConsumerCancel();
                break;
            case VideoVisitConstants.VISIT_RESULT_TIMED_OUT:
                DefaultLogger.d(LOG_TAG, "handling result - timed out");
                view.setTimedOut();
                break;
            case VideoVisitConstants.VISIT_RESULT_FAILED:
                DefaultLogger.d(LOG_TAG, "handling result - failed");
                view.setFailed();
                break;
            case VideoVisitConstants.VISIT_RESULT_DECLINED:
                DefaultLogger.d(LOG_TAG, "handling result - declined");
                view.setDeclined();
                break;
            case VideoVisitConstants.VISIT_RESULT_PERMISSIONS_NOT_GRANTED:
                DefaultLogger.d(LOG_TAG, "handling result - permissions not granted");
                view.setPermissionsNotGranted();
                break;
            case VideoVisitConstants.VISIT_RESULT_FAILED_TO_END:
                DefaultLogger.d(LOG_TAG, "handling result - failed to end");
                view.setFailedToEnd();
                break;
            case VideoVisitConstants.VISIT_RESULT_IVR_REQUESTED:
                DefaultLogger.d(LOG_TAG, "handling result - ivr requested");
                view.startIVRActivity(visit);
                break;
            case VideoVisitConstants.VISIT_RESULT_NETWORK_FAILURE:
                // The following two booleans default to false, as it is required to provide a default value.
                // However, in reality, it could be considered an error condition if we receive VISIT_RESULT_NETWORK_FAILURE
                // and these Booleans are not found in the resultData extras.
                // The SDK guarantees these Boolean extras will be populated correctly and that you can trust that they will
                // exist and be correct.
                DefaultLogger.d(LOG_TAG, "handling result - network failure - video disconnected = " +
                        videoDisconnected + ". provider connected = " + providerConnected);
                view.setNetworkFailure(videoDisconnected, providerConnected);
                break;
            default:
                DefaultLogger.d(LOG_TAG, "handling result - default case - unexpected result or no result");
                view.setFailed();
                break;
        }
    }

    void abandon() {
        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        awsdk.getVisitManager().abandonCurrentVisit();
    }

    void setAwsdkState(final Bundle bundle) {
        if (!awsdk.isInitialized()) {
            // in the case where the app has been destroyed and recreated during the visit console, we will have lost
            // our saved state, this will help restore it
            awsdk.restoreInstanceState(bundle);
        }
    }
}
