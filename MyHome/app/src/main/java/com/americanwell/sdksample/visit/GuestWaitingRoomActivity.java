/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindString;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for Guest Visit waiting room
 *
 * @since AWSDK 2.0
 */
@RequiresPresenter(GuestWaitingRoomPresenter.class)
public class GuestWaitingRoomActivity extends BaseSampleNucleusActivity<GuestWaitingRoomPresenter> {

    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_NAME = "EXTRA_NAME";

    public static final int ONGOING_NOTIFICATION_ID = 123456;


    @BindString(R.string.waiting_room_ended_title)
    String endedTitle;

    @BindString(R.string.login_conference_canceled)
    String conferenceCanceled;
    @BindString(R.string.login_conference_disabled)
    String conferenceDisabled;
    @BindString(R.string.login_conference_ended)
    String conferenceEnded;
    @BindString(R.string.login_conference_exited)
    String conferenceExited;
    @BindString(R.string.video_conference_timed_out)
    String conferenceTimedOut;
    @BindString(R.string.guest_console_ongoing_notification)
    String ongoingNotification;

    private AlertDialog cancelDialog;

    private NotificationManager notificationManager;

    private DialogInterface.OnClickListener leaveWaitingRoomListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            getPresenter().leaveVisit();
        }
    };

    public static Intent makeIntent(final Context context, String name, String email) {
        Intent intent = new Intent(context, GuestWaitingRoomActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_EMAIL, email);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_waiting_room);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String name = intent.getStringExtra(EXTRA_NAME);
            String email = intent.getStringExtra(EXTRA_EMAIL);
            getPresenter().setCredentials(name, email);

            // this Intent is passed into the VisitManager to specify which activity to start when the
            // visit console finishes
            final Intent guestFinishedIntent = new Intent(getApplicationContext(), GuestFinishedActivity.class);
            getPresenter().setVisitFinishedIntent(guestFinishedIntent);
        }
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(cancelDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_waiting_room, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cancel_visit);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                promptForCancel();
                return false;
            }
        });
        return true;
    }

    public void promptForCancel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GuestWaitingRoomActivity.this)
                .setMessage(R.string.waiting_room_confirm_cancel)
                .setPositiveButton(R.string.app_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().leaveVisit();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.app_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        cancelDialog = builder.create();
        cancelDialog.show();
    }

    @Override
    public void onBackPressed() {
        promptForCancel();
    }

    public void setConferenceReady(final Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_local_hospital_white_18dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(ongoingNotification)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());

        startActivity(intent);
        finish();
    }

    public void setConferenceCanceled() {
        sampleUtils.handleError(this, endedTitle, conferenceCanceled, leaveWaitingRoomListener);
    }

    public void setConferenceDisabled() {
        sampleUtils.handleError(this, endedTitle, conferenceDisabled, leaveWaitingRoomListener);
    }

    public void setConferenceEnded() {
        sampleUtils.handleError(this, endedTitle, conferenceEnded, leaveWaitingRoomListener);
    }

    @Override
    protected void showBusy(boolean show) {
        // do nothing - page has it's own spinner
    }

    @Override
    public void setError(@NonNull final SDKError error) {
        setNothingIsBusy();
        // in this case, we want to stop what we're doing if there's an error - it's "fatal"
        sampleUtils.handleError(this, error, leaveWaitingRoomListener);
    }

    public void onConferenceAbandoned() {
        finish();
    }
}
