/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.americanwell.sdk.entity.health.Condition;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to show and select medical history
 */
@RequiresPresenter(MedicalHistoryPresenter.class)
public class MedicalHistoryActivity extends BaseHealthItemIntakeActivity<MedicalHistoryPresenter, Condition> {

    public static Intent makeIntent(final Context context) {
        return new Intent(context, MedicalHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_medical_history);
    }

    @OnClick(R.id.fab)
    public void submitConditions() {
        getPresenter().updateConditions();
    }

    public void setConditionsSubmitted() {
        Toast.makeText(this, R.string.medical_history_updated, Toast.LENGTH_SHORT).show();
        finish();
    }
}
