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

import com.americanwell.sdk.entity.health.Allergy;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to display a list of allergies for selection in consumer profile
 */
@RequiresPresenter(AllergiesPresenter.class)
public class AllergiesActivity extends BaseHealthItemIntakeActivity<AllergiesPresenter, Allergy> {

    public static Intent makeIntent(final Context context) {
        return new Intent(context, AllergiesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_allergies);
    }

    @OnClick(R.id.fab)
    public void submitAllergies() {
        getPresenter().updateAllergies();
    }

    public void setAllergiesSubmitted() {
        Toast.makeText(this, R.string.allergies_updated, Toast.LENGTH_SHORT).show();
        finish();
    }
}
