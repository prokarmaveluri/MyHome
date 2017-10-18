/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_PROVIDER_CONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;
import static com.americanwell.sdksample.visit.WaitingRoomActivity.ONGOING_NOTIFICATION_ID;
import static com.americanwell.sdksample.visit.WaitingRoomPresenter.EXTRA_AWSDK_STATE;

/**
 * When the video visit console is finished, this activity will launch and figure out what to do next
 * @since AWSDK 3.1
 */
@RequiresPresenter(VisitFinishedPresenter.class)
public class VisitFinishedActivity extends BaseSampleNucleusActivity<VisitFinishedPresenter> {

    @BindView(R.id.visit_finished_message)
    TextView visitFinishedMessage;

    @BindString(R.string.waiting_room_provider_gone)
    String providerGone;
    @BindString(R.string.waiting_room_canceled)
    String canceled;
    @BindString(R.string.waiting_room_timed_out)
    String timedOut;
    @BindString(R.string.waiting_room_failed)
    String failed;
    @BindString(R.string.waiting_room_declined)
    String declined;
    @BindString(R.string.waiting_room_failed_to_end)
    String failedToEnd;
    @BindString(R.string.waiting_room_permissions_not_granted)
    String permissionsNotGranted;
    @BindString(R.string.waiting_room_network_failure_app_server)
    String networkFailureAppServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_visit_finished);

        // cancel ongoing notification
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ONGOING_NOTIFICATION_ID);

        // pull extras out of the bundle provided by the visit console
        final Bundle visitExtras = getIntent().getBundleExtra(VISIT_FINISHED_EXTRAS);
        if (visitExtras != null) {
            getPresenter().setAwsdkState(getIntent().getBundleExtra(EXTRA_AWSDK_STATE));
            getPresenter().setResultCode(visitExtras.getInt(VISIT_RESULT_CODE));
            getPresenter().setVisit((Visit)visitExtras.getParcelable(VISIT));
            getPresenter().setAppServerDisconnected(visitExtras.getBoolean(VISIT_STATUS_APP_SERVER_DISCONNECTED));
            getPresenter().setVideoDisconnected(visitExtras.getBoolean(VISIT_STATUS_VIDEO_DISCONNECTED));
            getPresenter().setProviderConnected(visitExtras.getBoolean(VISIT_STATUS_PROVIDER_CONNECTED));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().abandon();
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        goHome();
    }

    public void setReadyForSummary(final Visit visit) {
        startActivity(VisitSummaryActivity.makeIntent(this, visit));
        finish();
    }

    public void setProviderGone() {
        visitFinishedMessage.setText(providerGone);
    }

    public void setConsumerCancel() {
        visitFinishedMessage.setText(canceled);
    }

    public void setTimedOut() {
        visitFinishedMessage.setText(timedOut);
    }

    public void setFailed() {
        visitFinishedMessage.setText(failed);
    }

    public void setDeclined() {
        visitFinishedMessage.setText(declined);
    }

    public void setFailedToEnd() {
        visitFinishedMessage.setText(failedToEnd);
    }

    public void setPermissionsNotGranted() {
        visitFinishedMessage.setText(permissionsNotGranted);
    }

    public void startIVRActivity(Visit visit) {
        startActivity(IVRActivity.makeIntent(this, visit));
        finish();
    }

    public void setNetworkFailure(final boolean isVideoDisconnected, final boolean providerConnected) {
        final String message = isVideoDisconnected ? getString(R.string.waiting_room_network_failure_video, providerConnected) : networkFailureAppServer;
        visitFinishedMessage.setText(message);
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }
}
