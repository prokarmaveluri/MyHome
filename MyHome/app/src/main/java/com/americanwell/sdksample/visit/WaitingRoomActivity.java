/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.TriageQuestionsActivity;
import com.americanwell.sdksample.services.ProviderView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for Visit waiting room
 */
@RequiresPresenter(WaitingRoomPresenter.class)
public class WaitingRoomActivity extends BaseSampleNucleusActivity<WaitingRoomPresenter> {

    public static final String EXTRA_VISIT = "visitReport";
    public static final int ONGOING_NOTIFICATION_ID = 12345;

    @BindView(R.id.waiting_room_count)
    TextView waitingRoomCount;
    @BindView(R.id.waiting_room_progress_bar)
    ProgressBar waitingRoomProgressBar; // shown when chat is enabled to provide a smaller indeterminate
    @BindView(R.id.spinner_layout)
    View spinnerLayout;
    @BindView(R.id.chat_layout)
    View chatLayout;

    @BindString(R.string.waiting_room_ended_title)
    String endedTitle;
    @BindString(R.string.waiting_room_canceled)
    String canceled;
    @BindString(R.string.waiting_room_failed_to_cancel)
    String failedToCancel;

    private WaitingRoomChatFragment chatFragment;
    private static final String CHAT_FRAGMENT_TAG = "ChatFragmentTag";
    private TransferDialogFragment transferDialogFragment;
    private static final String TRANSFER_DIALOG_FRAGMENT_TAG = "TransferDialogFragmentTag";

    private AlertDialog cancelDialog;
    private AlertDialog transferFailedDialog;

    private NotificationManager notificationManager;

    public static Intent makeIntent(final Context context, final Visit visit) {
        Intent intent = new Intent(context, WaitingRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_VISIT, visit);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_waiting_room);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (savedInstanceState == null) {
            getPresenter().setVisit((Visit) getIntent().getParcelableExtra(EXTRA_VISIT));

            // this Intent is passed into the VisitManager to specify which activity to start when the
            // visit console finishes
            final Intent visitFinishedIntent = new Intent(getApplicationContext(), VisitFinishedActivity.class);
            getPresenter().setVisitFinishedIntent(visitFinishedIntent);
        }
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(transferDialogFragment);
        dismissDialog(cancelDialog);
        dismissDialog(transferFailedDialog);
    }

    // Because after a transfer we are re-launching the waiting room activity with the flag
    // CLEAR_TOP we expect the activity to have already been created and this call to re-initialize
    // the visit. See Intent#FLAG_ACTIVITY_CLEAR_TOP for more information
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().setVisit((Visit) getIntent().getParcelableExtra(EXTRA_VISIT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_waiting_room, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cancel_visit);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                promptForCancel();
                return true;
            }
        });
        return true;
    }

    public void promptForCancel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WaitingRoomActivity.this)
                .setMessage(R.string.waiting_room_confirm_cancel)
                .setPositiveButton(R.string.app_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().cancelVisit();
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

    public void setEndReason(final VisitEndReason visitEndReason) {
        sampleUtils.handleError(
                this,
                endedTitle,
                getString(R.string.waiting_room_ended_message, visitEndReason.toString()),
                goHomeClickListener());
    }

    public void setVisitIntent(final Intent intent) {
        // set up ongoing notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_local_hospital_white_18dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.video_console_ongoing_notification, getPresenter().getProviderName()))
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());

        // start activity
        startActivity(intent);
        finish();
    }

    public void setWaitingRoomCount(final int count, final String providerName) {
        waitingRoomCount.setVisibility(View.VISIBLE);
        final String prompt;
        //
        //      Because we don't want to display any string when the count is null or less than 1 we
        //      first check for these values
        if (count > 0) {
            //          Using the quantity string gives us more power as to how to handle the localization in
            //          the future
            prompt = getResources()
                    .getQuantityString(R.plurals.patients_ahead_plurals, count, count, providerName);
        }
        else {
            String rawString = getString(R.string.waiting_room_contacting_provider);
            prompt = String.format(rawString, providerName);
        }

        waitingRoomCount.setText(prompt);
    }

    public void hideWaitingRoomCount() {
        waitingRoomCount.setVisibility(View.GONE);
    }

    public void setFailedToCancel() {
        setNothingIsBusy();
        sampleUtils.handleError(this, endedTitle, failedToCancel, goHomeClickListener());
    }

    @Override
    protected void showBusy(boolean show) {
        // do nothing - page has it's own spinner
    }

    public void setConsumerCancel() {
        sampleUtils.handleError(this, endedTitle, canceled, goHomeClickListener());
    }

    @Override
    public void setError(@NonNull final SDKError error) {
        setNothingIsBusy();
        // in this case, we want to stop what we're doing if there's an error - it's "fatal"
        sampleUtils.handleError(this, error, goHomeClickListener());
    }

    public void setTransferVisit(final Visit transferVisit) {
        if (!isFinishing()) {
            if (transferDialogFragment != null) {
                transferDialogFragment.dismiss();
            }

            Provider transferProvider;
            boolean isSuggested;
            if (transferVisit.getDeclineAndTransfer() != null) {
                transferProvider = transferVisit.getDeclineAndTransfer().getProvider();
                isSuggested = false;
            }
            else {
                transferProvider = transferVisit.getSuggestedTransfer().getProvider();
                isSuggested = true;
            }

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            transferDialogFragment = TransferDialogFragment.getInstance(
                    transferVisit.getAssignedProvider(), transferProvider, isSuggested);
            ft.add(transferDialogFragment, TRANSFER_DIALOG_FRAGMENT_TAG);
            ft.commitAllowingStateLoss();
        }
    }

    public void displayTransferFailed(@NonNull String assignedProvider) {
        String message = String.format(getString(R.string.waiting_room_xfer_failed), assignedProvider);
        final AlertDialog.Builder builder = new AlertDialog.Builder(WaitingRoomActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        transferFailedDialog = builder.create();
        transferFailedDialog.show();
    }

    public void goToIntake(VisitContext visitContext) {
        final Intent intent = TriageQuestionsActivity.makeIntent(this, visitContext);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void startNewVisit(Visit visit) {
        waitingRoomCount.setVisibility(View.GONE);
        startActivity(WaitingRoomActivity.makeIntent(this, visit));
    }

    // Chat-related things below
    public void showChat() {
        // once we have a chat, we permanently get rid of the spinner and replace it with a horizontal progress bar
        // and the chat layout
        spinnerLayout.setVisibility(View.GONE);
        waitingRoomProgressBar.setVisibility(View.VISIBLE);
        chatLayout.setVisibility(View.VISIBLE);

        if (chatFragment == null) {
            final android.app.FragmentManager fm = getFragmentManager();
            final android.app.FragmentTransaction ft = fm.beginTransaction();
            chatFragment = WaitingRoomChatFragment.newInstance();
            ft.add(R.id.chat_layout, chatFragment, CHAT_FRAGMENT_TAG);
            ft.commit();
        }
    }

    public void setHasNewChatItems() {
        if (chatFragment != null) {
            chatFragment.setHasNewChatItems(); // notify the fragment
        }
    }

    /**
     * Dialog Fragment for showing provider info for a transfer
     * there are 2 modes - "suggested" or "manual"
     * they come at different points in the lifecycle.
     * see documentation for futher details.
     */
    public static class TransferDialogFragment extends DialogFragment {

        private static final String ARG_ASSIGNED_PROVIDER = "assignedProvider";
        private static final String ARG_TRANSFER_PROVIDER = "transferProvider";
        private static final String ARG_IS_SUGGESTED = "isSuggested";

        @BindView(R.id.transfer_prompt)
        TextView transferPrompt;
        @BindView(R.id.transfer_provider)
        ProviderView providerView;

        public static TransferDialogFragment getInstance(
                final ProviderInfo assignedProvider,
                final Provider transferProvider,
                final boolean isSuggested) {
            final TransferDialogFragment fragment = new TransferDialogFragment();
            final Bundle args = new Bundle();
            args.putParcelable(ARG_ASSIGNED_PROVIDER, assignedProvider);
            args.putParcelable(ARG_TRANSFER_PROVIDER, transferProvider);
            args.putBoolean(ARG_IS_SUGGESTED, isSuggested);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final boolean isSuggested = getArguments().getBoolean(ARG_IS_SUGGESTED);
            final ProviderInfo assignedProvider = getArguments().getParcelable(ARG_ASSIGNED_PROVIDER);
            final Provider transferProvider = getArguments().getParcelable(ARG_TRANSFER_PROVIDER);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_transfer, null);
            ButterKnife.bind(this, view);

            transferPrompt.setText(isSuggested
                    ? String.format(getString(R.string.waiting_room_suggested_xfer),
                    transferProvider.getFullName(),
                    assignedProvider.getFullName())
                    : String.format(getString(R.string.waiting_room_manual_xfer),
                    transferProvider.getFullName(),
                    transferProvider.getSpecialty().getName())
            );

            providerView.setShowGetStartedButton(false);
            providerView.setProvider(transferProvider);
            builder.setView(view)
                    .setCancelable(isSuggested)
                    .setPositiveButton(R.string.waiting_room_accept_xfer, null)
                    .setNegativeButton(isSuggested ? R.string.waiting_room_decline_xfer : R.string.waiting_room_end_visit, null);

            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isSuggested) {
                                ((WaitingRoomActivity) getActivity()).getPresenter().setAcceptSuggestedTransfer();
                            }
                            else {
                                ((WaitingRoomActivity) getActivity()).getPresenter().setAcceptDeclineAndTransfer();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (isSuggested) {
                                ((WaitingRoomActivity) getActivity()).getPresenter().setDeclineSuggestedTransfer();
                            }
                            else {
                                ((WaitingRoomActivity) getActivity()).goHome();
                            }
                        }
                    });
                }
            });
            return dialog;
        }
    }

}
