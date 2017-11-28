/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.cost.VisitCostActivity;
import com.americanwell.sdksample.widget.SDKSpinner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for starting the "first available" provider search
 */
@RequiresPresenter(FirstAvailablePresenter.class)
public class FirstAvailableActivity extends BaseIntakeActivity<FirstAvailablePresenter> {

    @BindView(R.id.first_available_status)
    TextView statusView;
    @BindView(R.id.first_available_progress)
    SDKSpinner progressBar;
    @BindView(R.id.first_available_privacy)
    View privacySection;
    @BindView(R.id.privacy_policy_check_box)
    CheckBox privacyPolicyCheckBox;
    @BindView(R.id.privacy_policy_link)
    View privacyPolicyLink;

    @BindString(R.string.app_warning)
    String appWarning;
    @BindString(R.string.patient_selection_privacy_policy_warning)
    String privacyPolicyWarning;
    @BindString(R.string.first_available_error_title)
    String firstAvailableErrorTitle;
    @BindString(R.string.first_available_error_provider_list_exhausted)
    String providerListExhausted;
    @BindString(R.string.first_available_error_request_gone)
    String requestGone;

    private AlertDialog cancelDialog;
    private AlertDialog privacyPolicyDialog;

    public static Intent makeIntent(final Context context, final VisitContext visitContext) {
        final Intent intent = new Intent(context, FirstAvailableActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_VISIT_CONTEXT, visitContext);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_available);
        SampleApplication.getActivityComponent().inject(this);
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(cancelDialog);
        dismissDialog(privacyPolicyDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_first_available, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean flag = true;
        switch (item.getItemId()) {
            case R.id.action_cancel_matchmaker:
                promptForCancel();
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }

    public void promptForCancel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.first_available_confirm_cancel)
                .setPositiveButton(R.string.app_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().cancelMatchmaking();
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

    // the presenter will call this when matchmaking is canceled
    public void setMatchmakingCanceled(boolean value) {
        if (value) {
            goHome();
        }
    }

    @Override
    public void onBackPressed() {
        promptForCancel();
    }

    @OnCheckedChanged(R.id.privacy_policy_check_box)
    public void setPrivacyPolicyAccepted(boolean checked) {
        getPresenter().setLegalTextsAccepted(checked);
    }

    public void setPrivacyPolicyError() {
        setError(appWarning, privacyPolicyWarning);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        getPresenter().validate();
    }

    public void setFieldsValid() {
        startActivity(VisitCostActivity.makeIntent(this, getPresenter().getVisitContext()));
        finish();
    }

    @OnClick(R.id.privacy_policy_link)
    public void showPrivacyPolicy() {
        @SuppressLint("InflateParams")
        final WebView disclaimerWebView = (WebView) getLayoutInflater().inflate(R.layout.dialog_disclaimer, null);
        disclaimerWebView.loadData(getPresenter().getPrivacyPolicy(), "text/html;charset=UTF-8", "UTF-8");
        privacyPolicyDialog = new AlertDialog.Builder(this)
                .setView(disclaimerWebView)
                .show();
    }

    public void setProvider(final Provider provider) {
        if (provider != null) {
            statusView.setText(getString(R.string.first_available_matched, provider.getFullName()));
            if (fab != null) {
                fab.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showPrivacySection(boolean show) {
        privacySection.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setProviderListExhausted(boolean value) {
        if (value) {
            sampleUtils.handleError(this, firstAvailableErrorTitle, providerListExhausted, goHomeClickListener());
        }
    }

    public void setRequestGone(boolean value) {
        if (value) {
            sampleUtils.handleError(this, firstAvailableErrorTitle, requestGone, goHomeClickListener());
        }
    }

    // this screen has it's own progress bar
    @Override
    protected void showBusy(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        else {
            super.showBusy(show);
        }
    }
}
