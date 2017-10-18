/**
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.americanwell.sdk.entity.provider.AvailableProvider;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.TriageQuestionsActivity;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for showing Provider's details and allowing the start of a visit
 */
@RequiresPresenter(ProviderPresenter.class)
public class ProviderActivity extends BaseSampleNucleusActivity<ProviderPresenter> implements ProviderView.ProviderViewListener {

    private static final String EXTRA_PROVIDER_INFO = "providerInfo";
    private static final String EXTRA_AVAILABLE_PROVIDER = "availableProvider";
    private static final String EXTRA_APPOINTMENT_DATE = "appointmentDate";

    @BindView(R.id.provider_layout)
    ProviderView providerLayout;

    /**
     * use this makeIntent() to show a standard providerinfo without any ui for
     * appointment scheduling
     *
     * @param context
     * @param providerInfo
     * @return
     */
    public static Intent makeIntent(final Context context, final ProviderInfo providerInfo) {
        final Intent intent = new Intent(context, ProviderActivity.class);
        intent.putExtra(EXTRA_PROVIDER_INFO, providerInfo);
        return intent;
    }

    /**
     * use this makeIntent() if you want the provider details ui to also allow scheduling
     * an appointment
     *
     * @param context
     * @param availableProvider
     * @param appointmentDate
     * @return
     */
    public static Intent makeIntent(@NonNull final Context context,
                                    @NonNull final AvailableProvider availableProvider,
                                    @Nullable final Date appointmentDate) {
        final Intent intent = new Intent(context, ProviderActivity.class);
        intent.putExtra(EXTRA_AVAILABLE_PROVIDER, availableProvider);
        intent.putExtra(EXTRA_APPOINTMENT_DATE, appointmentDate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_provider);

        if (savedInstanceState == null) {
            ProviderInfo providerInfo = null;
            List<Date> availableAppointments = null;
            Date appointmentDate = null;

            // if we're allowing scheduling, there will be an availableProvider
            if (getIntent().hasExtra(EXTRA_AVAILABLE_PROVIDER)) {
                providerInfo = ((AvailableProvider) getIntent().getParcelableExtra(EXTRA_AVAILABLE_PROVIDER)).getProviderInfo();
                availableAppointments = ((AvailableProvider) getIntent().getParcelableExtra(EXTRA_AVAILABLE_PROVIDER)).getAvailableAppointmentTimeSlots();
                appointmentDate = (Date) getIntent().getSerializableExtra(EXTRA_APPOINTMENT_DATE);
            }
            else {
                providerInfo = getIntent().getParcelableExtra(EXTRA_PROVIDER_INFO);
            }

            getPresenter().setProviderInfo(providerInfo);
            getPresenter().setAvailableAppointments(availableAppointments);
            getPresenter().setAppointmentDate(appointmentDate);
        }

        providerLayout.setGetStartedOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().getVisitContext();
            }
        });

        providerLayout.setListener(this);
    }

    public void setProvider(final Provider provider) {
        providerLayout.setProvider(provider);
    }

    // the presenter will call this when a visit context has been created
    public void setVisitContext(final VisitContext visitContext) {
        final Intent intent = TriageQuestionsActivity.makeIntent(this, visitContext);
        startActivity(intent);
    }

    public void setAvailableAppointments(final List<Date> availableAppointments) {
        if (availableAppointments != null) {
            providerLayout.setShowAppointmentsLayout(true);
            providerLayout.setAvailableAppointments(availableAppointments);
        }
    }

    public void setAppointmentDate(Date appointmentDate) {
        if (appointmentDate == null) {
            appointmentDate = new Date();
        }
        providerLayout.setAppointmentDate(appointmentDate);
    }

    @Override
    public void onScheduleAppointment(final Date date) {
        final Intent intent = ScheduleAppointmentActivity.makeIntent(this, getPresenter().getProvider(), date);
        startActivity(intent);
        finish();
    }

}
