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

import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;
import static com.americanwell.sdksample.visit.GuestWaitingRoomActivity.ONGOING_NOTIFICATION_ID;

/**
 * When the guest visit console is finished, this activity will launch and figure out what to do next
 * @since AWSDK 3.1
 */
@RequiresPresenter(GuestFinishedPresenter.class)
public class GuestFinishedActivity extends BaseSampleNucleusActivity<GuestFinishedPresenter> {

    @BindView(R.id.guest_finished_message)
    TextView guestFinishedMessage;

    @BindString(R.string.login_conference_canceled)
    String conferenceCanceled;
    @BindString(R.string.login_conference_ended)
    String conferenceEnded;
    @BindString(R.string.login_conference_exited)
    String conferenceExited;
    @BindString(R.string.video_conference_timed_out)
    String conferenceTimedOut;
    @BindString(R.string.waiting_room_permissions_not_granted)
    String permissionsNotGranted;
    @BindString(R.string.waiting_room_network_failure_app_server)
    String networkFailureAppServer;
    @BindString(R.string.waiting_room_network_failure_video_guest)
    String networkFailureVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_guest_finished);

        // cancel ongoing notification
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ONGOING_NOTIFICATION_ID);

        final Bundle visitExtras = getIntent().getBundleExtra(VISIT_FINISHED_EXTRAS);
        if (visitExtras != null) {
            getPresenter().setResultCode(visitExtras.getInt(VISIT_RESULT_CODE));
            getPresenter().setAppServerDisconnected(visitExtras.getBoolean(VISIT_STATUS_APP_SERVER_DISCONNECTED));
            getPresenter().setVideoDisconnected(visitExtras.getBoolean(VISIT_STATUS_VIDEO_DISCONNECTED));
        }
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().abandon();
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        finish();
    }

    public void setConferenceCanceled() {
        guestFinishedMessage.setText(conferenceCanceled);
    }

    public void setConferenceEnded() {
        guestFinishedMessage.setText(conferenceEnded);
    }

    public void setConferenceExited() {
        guestFinishedMessage.setText(conferenceExited);
    }

    public void setConferenceTimedOut() {
        guestFinishedMessage.setText(conferenceTimedOut);
    }

    public void setPermissionsNotGranted() {
        guestFinishedMessage.setText(permissionsNotGranted);
    }

    public void setNetworkFailure(final boolean isVideoDisconnected) {
        final String message = isVideoDisconnected ? networkFailureVideo : networkFailureAppServer;
        guestFinishedMessage.setText(message);
    }
}
