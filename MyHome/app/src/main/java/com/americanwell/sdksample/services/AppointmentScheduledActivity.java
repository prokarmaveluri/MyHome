/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * This activity is used when a consumer schedules an appointment with a provider to confirm that it was
 * scheduled.
 *
 * @since AWSDK 3.0
 */
@RequiresPresenter(AppointmentScheduledPresenter.class)
public class AppointmentScheduledActivity extends BaseSampleNucleusActivity<AppointmentScheduledPresenter> {

    private static final String EXTRA_PROVIDER_INFO = "providerInfo";
    private static final String EXTRA_APPOINTMENT_DATE = "appointmentDate";

    @BindView(R.id.appointment_scheduled_details)
    TextView appointmentScheduledDetails;
    @BindView(R.id.appointment_details_provider_image)
    ImageView providerImage;
    @BindView(R.id.appointment_details_with)
    TextView withText;
    @BindView(R.id.appointment_details_with_specialty)
    TextView withSpecialtyText;
    @BindView(R.id.appointment_datetime)
    TextView appointmentDatetime;

    public static Intent makeIntent(final Context context, final ProviderInfo providerInfo, final Date appointmentDate) {
        final Intent intent = new Intent(context, AppointmentScheduledActivity.class);
        intent.putExtra(EXTRA_PROVIDER_INFO, providerInfo);
        intent.putExtra(EXTRA_APPOINTMENT_DATE, appointmentDate.getTime());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_appointment_scheduled);

        if (savedInstanceState == null) {
            final ProviderInfo providerInfo = getIntent().getParcelableExtra(EXTRA_PROVIDER_INFO);
            getPresenter().setProviderInfo(providerInfo);
            final Date appointmentDate = new Date(getIntent().getLongExtra(EXTRA_APPOINTMENT_DATE, -1));
            getPresenter().setAppointmentDate(appointmentDate);
        }
    }

    public void setConfirmationEmailAddress(final String emailAddress) {
        final String prompt = getString(R.string.appointment_scheduled_details, emailAddress);
        appointmentScheduledDetails.setText(prompt);
    }

    // the presenter will call this to load the provider image
    public void setProviderImage(final ProviderInfo provider) {
        getPresenter().loadProviderImage(
                provider,
                providerImage,
                getResources().getDrawable(R.drawable.img_provider_photo_placeholder)
        ); // this *does* bounce back and forth between presenter and activity, but we still want the activity to be involved here
    }

    public void setWith(final String with) {
        if (TextUtils.isEmpty(with)) {
            withText.setVisibility(View.GONE);
        }
        else {
            withText.setText(with);
            withText.setVisibility(View.VISIBLE);
        }
    }

    public void setSpecialty(final String specialty) {
        if (TextUtils.isEmpty(specialty)) {
            withSpecialtyText.setVisibility(View.GONE);
        }
        else {
            withSpecialtyText.setText(specialty);
            withSpecialtyText.setVisibility(View.VISIBLE);
        }
    }

    public void setAppointmentDate(final Date appointmentDate) {
        final String date = localeUtils.formatAppointmentDate(appointmentDate);
        final String time = localeUtils.formatAppointmentTime(appointmentDate);
        appointmentDatetime.setText(getString(R.string.schedule_appointment_date, date, time));
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        finish();
    }
}
