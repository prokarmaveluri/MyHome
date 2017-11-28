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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.logging.AWSDKLogger;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindString;
import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_IVR;

/**
 * Activity for the IVR call. When an IVR call is requested during the visit the consumer is
 * rerouted here to manage the process and display the status of the call. Once the call is
 * connected (or fails) the user is sent to the {@link VisitSummaryActivity}.
 */
@RequiresPresenter(IVRPresenter.class)
public class IVRActivity extends BaseSampleNucleusActivity<IVRPresenter> {

    private static final String LOG_TAG = IVRActivity.class.getName();

    @BindString(R.string.ivr_sorry)
    String ivrSorry;
    @BindString(R.string.ivr_cancel_message)
    String ivrCancelMessage;
    @BindView(R.id.ivr_calling_num)
    TextView ivrNumber;

    public static final String EXTRA_VISIT = "visit";

    private AlertDialog retryPrompt;
    private AlertDialog cancelPrompt;

    public static Intent makeIntent(final Context context, final Visit visit) {
        Intent intent = new Intent(context, IVRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_VISIT, visit);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.content_ivr);

        if (savedInstanceState == null) {
            getPresenter().setVisit((Visit) getIntent().getParcelableExtra(EXTRA_VISIT));
        }
    }

    @Override
    public void onBackPressed() {
        //Disabling the ability to go back for this screen
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ivr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel_ivr:
                showPromptForCancel();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void showBusy(boolean show) {
        // do nothing - page has it's own spinner
    }

    public void showPromptForRetry(String displayNumber) {
        if (!isRetryShowing()) {
            String message = getString(R.string.ivr_unable_to_reach,
                    PhoneNumberUtils.formatNumber(displayNumber));

            final AlertDialog.Builder builder = new AlertDialog.Builder(IVRActivity.this)
                    .setTitle(ivrSorry)
                    .setMessage(message)
                    .setPositiveButton(R.string.ivr_try_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DefaultLogger.d(AWSDKLogger.LOG_CATEGORY_DEFAULT, LOG_TAG,
                                    "IVR Retry Accepted");
                            dialog.dismiss();
                            retryPrompt = null;
                            getPresenter().retryIVR();
                        }
                    })
                    .setNegativeButton(R.string.ivr_end_visit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DefaultLogger.d(AWSDKLogger.LOG_CATEGORY_DEFAULT, LOG_TAG,
                                    "IVR Retry Rejected");
                            dialog.dismiss();
                            retryPrompt = null;
                            getPresenter().cancelIVR();
                        }
                    })
                    .setCancelable(false);
            retryPrompt = builder.create();
            retryPrompt.show();
        }
    }

    public void showPromptForCancel() {
        if (!isCancelShowing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(IVRActivity.this)
                    .setMessage(ivrCancelMessage)
                    .setPositiveButton(R.string.app_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DefaultLogger.d(AWSDKLogger.LOG_CATEGORY_IVR, LOG_TAG,
                                    "IVR Cancel Accepted");
                            dialog.dismiss();
                            cancelPrompt = null;
                            getPresenter().cancelIVR();
                        }
                    })
                    .setNegativeButton(R.string.app_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DefaultLogger.d(AWSDKLogger.LOG_CATEGORY_IVR, LOG_TAG, "IVR Cancel Rejected");
                            dialog.dismiss();
                            cancelPrompt = null;
                        }
                    })
                    .setCancelable(true);
            cancelPrompt = builder.create();
            cancelPrompt.show();
        }
    }

    private boolean isRetryShowing() {
        return retryPrompt != null && retryPrompt.isShowing();
    }

    private boolean isCancelShowing() {
        return cancelPrompt != null && cancelPrompt.isShowing();
    }

    public void startSummaryActivity() {
        DefaultLogger.d(LOG_CATEGORY_IVR, LOG_TAG, "Ending IVR. Starting Summary Activity");
        startActivity(VisitSummaryActivity.makeIntent(this, getPresenter().getVisit()));
        finish();
    }

    public void setIvrNumber(String phoneNumber) {
        String numberText = getString(R.string.ivr_calling_at,
                PhoneNumberUtils.formatNumber(phoneNumber));
        ivrNumber.setText(numberText);
    }
}
