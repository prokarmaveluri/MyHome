/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.readiness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.logging.AWSDKLogger;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for demonstrating the Appointment Readiness Technical Check feature.
 */
@RequiresPresenter(TechCheckPresenter.class)
public class TechCheckActivity extends BaseSampleNucleusActivity<TechCheckPresenter> {

    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.submit)
    Button submit;

    private static final String LOG_TAG = TechCheckActivity.class.getName();

    public static Intent makeIntent(@NonNull final Context context) {
        return new Intent(context, TechCheckActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_tech_check);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DefaultLogger.d(AWSDKLogger.LOG_CATEGORY_DEFAULT, LOG_TAG, "onResume");
        getPresenter().updateData();
    }

    @OnClick(R.id.submit)
    public void onDoneClick() {
        getPresenter().setWrapUp();
    }

    public void setHasPermissions() {
        icon.setImageResource(R.drawable.img_success);
        title.setText(R.string.all_set);
        description.setText(R.string.ready_for_appointment);
        submit.setText(R.string.done);
    }

    public void setMissingPermission() {
        icon.setImageResource(R.drawable.img_lock);
        title.setText(R.string.access_required);
        String appName = getString(R.string.app_name);
        description.setText(getString(R.string.needs_permission, appName));
        submit.setText(android.R.string.ok);
    }

    public void finish(int resultCode) {
        setResult(resultCode);
        finish();
    }
}
