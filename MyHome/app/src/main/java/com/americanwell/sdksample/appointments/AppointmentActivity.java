/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.appointments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderType;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.TriageQuestionsActivity;
import com.americanwell.sdksample.util.LocaleUtils;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for a single Appointment.
 * Can be called from the Appointment list or if starting an appointment from an email link
 */
@RequiresPresenter(AppointmentPresenter.class)
public class AppointmentActivity extends BaseSampleNucleusActivity<AppointmentPresenter> {

    private static final String EXTRA_APPOINTMENT = "appointment";
    private static final String EXTRA_GOHOME = "gohome";

    @BindView(R.id.appointment_details_header)
    View headerView;
    @BindView(R.id.appointment_details_header_greeting)
    TextView greetingText;
    @BindView(R.id.appointment_details_header_text)
    TextView headerText;
    @BindView(R.id.appointment_details_provider_image)
    ImageView providerImage;
    @BindView(R.id.appointment_details_with)
    TextView withText;
    @BindView(R.id.appointment_details_with_specialty)
    TextView withSpecialtyText;
    @BindView(R.id.appointment_details_startDate)
    TextView startDateText;
    @BindView(R.id.appointment_details_startTime)
    TextView startTimeText;
    @BindView(R.id.appointment_details_forDependent)
    TextView forDependentText;
    @BindView(R.id.appointment_details_notes)
    TextView notesText;
    @BindView(R.id.appointment_details_early_text)
    TextView earlyText;
    @BindString(R.string.appointment_details_for)
    String forFormatString;

    @Inject
    protected LocaleUtils localeUtils;

    /**
     * use this to get an intent instance
     *
     * @param context     REQUIRED
     * @param appointment REQUIRED the Appointment
     * @return a new Intent for this activity
     */
    public static Intent makeIntent(@NonNull final Context context, @NonNull final Appointment appointment, final boolean gohome) {
        final Intent intent = new Intent(context, AppointmentActivity.class);
        intent.putExtra(EXTRA_APPOINTMENT, appointment);
        intent.putExtra(EXTRA_GOHOME, gohome); // set this to TRUE if the back button should always go to the home screen
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this); // dagger2 injection
        setContentView(R.layout.activity_appointment);
        if (savedInstanceState == null) {
            // first time in, set appointment on presenter
            getPresenter().setAppointment((Appointment) getIntent().getParcelableExtra(EXTRA_APPOINTMENT));
        }
    }

    @Override
    public void onBackPressed() {
        final boolean gohome = getIntent().getBooleanExtra(EXTRA_GOHOME, false);
        if (gohome) { // if we said to always go home, go home
            goHome();
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * onclick handler for the floating action button, which will start the appointment
     */
    @OnClick(R.id.fab)
    public void startAppointment() {
        // request a new visit context
        getPresenter().getVisitContext();
    }

    // the presenter will call this when a visit context has been created
    public void setVisitContext(final VisitContext visitContext) {
        final Intent intent = TriageQuestionsActivity.makeIntent(this, visitContext);
        startActivity(intent);
    }

    // the presenter will call this to load the provider image
    public void setProviderImage(final Provider provider) {
        getPresenter().loadProviderImage(
                provider,
                providerImage,
                getResources().getDrawable(R.drawable.img_provider_photo_placeholder)
        ); // this *does* bounce back and forth between presenter and activity, but we still want the activity to be involved here
    }

    public void setNoProviderImage() {
        Picasso.with(this).load(R.drawable.img_providers_first_available).into(providerImage);
    }

    public void setGreeting(final String firstName) {
        greetingText.setText(getString(R.string.appointment_details_header_greeting, firstName));
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

    public void setStart(final Date start) {
        if (start != null) {
            startDateText.setText(localeUtils.formatAppointmentDate(start));
            startDateText.setVisibility(View.VISIBLE);
            startTimeText.setText(localeUtils.formatAppointmentTime(start));
            startTimeText.setVisibility(View.VISIBLE);
        }
        else {
            startDateText.setVisibility(View.GONE);
            startTimeText.setVisibility(View.GONE);
        }
    }

    public void setDependent(final String dependent) {
        if (TextUtils.isEmpty(dependent)) {
            forDependentText.setVisibility(View.GONE);
        }
        else {
            final String forText = MessageFormat.format(forFormatString, dependent);
            forDependentText.setText(forText);
        }
    }

    public void setNotes(final String notes) {
        if (TextUtils.isEmpty(notes)) {
            notesText.setVisibility(View.GONE);
        }
        else {
            final String forText = MessageFormat.format(forFormatString, notes);
            notesText.setText(forText);
        }
    }

    private void setHeaderText(@StringRes final int resId) {
        setHeaderText(getString(resId));
    }

    private void setHeaderText(@NonNull String header) {
        headerText.setText(header);
        headerText.setVisibility(View.VISIBLE);
    }

    public void setCanceled() {
        headerView.setBackgroundResource(R.color.color_appointment_canceled);
        setHeaderText(R.string.appointment_details_header_cancelled);
    }

    public void setResolved() {
        headerView.setBackgroundResource(R.color.color_appointment_ontime);
        setHeaderText(R.string.appointment_details_header_complete_text);
    }

    public void setInProgress() {
        setHeaderText(R.string.appointment_details_header_in_progress_text);
    }

    public void setLate() {
        setHeaderText(R.string.appointment_details_header_late_text);
    }

    public void setNoProviders(ProviderType provider) {
        String noProvider = getString(
                R.string.appointment_details_header_no_providers,
                provider.getName()
        );
        setHeaderText(noProvider);
    }

    public void setEarly(int marginMins) {
        setHeaderText(R.string.appointment_details_header_early_text);
        final String prompt = getString(R.string.appointment_details_early_text, marginMins);
        earlyText.setText(prompt);
        earlyText.setVisibility(View.VISIBLE);
    }

    public void setOnTime() {
        headerView.setBackgroundResource(R.drawable.bg_gradient_appt_ontime);
        setHeaderText(R.string.appointment_details_header_begin_text);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE); // show the start visit button
        }
    }

}
