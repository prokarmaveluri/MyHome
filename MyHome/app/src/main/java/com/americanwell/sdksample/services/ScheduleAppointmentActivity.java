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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.americanwell.sdk.entity.consumer.RemindOptions;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * This activity demonstrates a confirmation page for a scheduled appointment
 *
 * @since AWSDK 3.0
 */
@RequiresPresenter(ScheduleAppointmentPresenter.class)
public class ScheduleAppointmentActivity extends BaseSampleNucleusActivity<ScheduleAppointmentPresenter> {

    @BindView(R.id.appointment_datetime)
    TextView appointmentDatetime;
    @BindView(R.id.appointment_provider)
    TextView appointmentProvider;
    @BindView(R.id.callback_number_layout)
    View callbackNumberLayout;
    @BindView(R.id.callback_number)
    EditText callbackNumber;
    @BindView(R.id.reminder_spinner)
    Spinner reminderSpinner;

    private static final String EXTRA_PROVIDER_INFO = "providerInfo";
    private static final String EXTRA_APPOINTMENT_DATE = "appointmentDate";

    private static final String PREFIX_REMIND_OPTIONS = "remind_options_";

    private List<AppointmentRemindOption> appointmentRemindOptions = new ArrayList<>();
    private ArrayAdapter<AppointmentRemindOption> appointmentRemindOptionArrayAdapter;

    public static Intent makeIntent(final Context context, final ProviderInfo providerInfo, final Date appointmentDate) {
        final Intent intent = new Intent(context, ScheduleAppointmentActivity.class);
        intent.putExtra(EXTRA_PROVIDER_INFO, providerInfo);
        intent.putExtra(EXTRA_APPOINTMENT_DATE, appointmentDate.getTime());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_schedule_appointment);

        if (savedInstanceState == null) {
            final ProviderInfo providerInfo = getIntent().getParcelableExtra(EXTRA_PROVIDER_INFO);
            getPresenter().setProviderInfo(providerInfo);
            final Date appointmentDate = new Date(getIntent().getLongExtra(EXTRA_APPOINTMENT_DATE, -1));
            getPresenter().setAppointmentDate(appointmentDate);
        }

        populateAppointmentRemindOptions();
        appointmentRemindOptionArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        appointmentRemindOptionArrayAdapter.addAll(appointmentRemindOptions);
        reminderSpinner.setAdapter(appointmentRemindOptionArrayAdapter);

        callbackNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @OnClick(R.id.fab)
    public void onConfirmScheduleAppointment() {
        getPresenter().scheduleAppointment();
    }

    public void setAppointmentDate(@NonNull final Date appointmentDate) {
        final String date = localeUtils.formatAppointmentDate(appointmentDate);
        final String time = localeUtils.formatAppointmentTime(appointmentDate);
        appointmentDatetime.setText(getString(R.string.schedule_appointment_date, date, time));
    }

    public void setProviderInfo(@NonNull final ProviderInfo providerInfo) {
        appointmentProvider.setText(getString(R.string.schedule_appointment_provider, providerInfo.getFullName(), providerInfo.getSpecialty().getName()));
    }

    public void setCallbackNumber(@NonNull final String callbackNumber) {
        this.callbackNumber.setText(callbackNumber);
        this.callbackNumber.setSelection(this.callbackNumber.getText().length());
    }

    @OnTextChanged(value = R.id.callback_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCallbackNumberChanged(final CharSequence value) {
        getPresenter().setCallbackNumber(sampleUtils.stripNonDigitCharacters(value.toString().trim()));
    }

    public void setRemindOptions(@NonNull final RemindOptions reminderOptions) {
        final AppointmentRemindOption findMe = new AppointmentRemindOption(reminderOptions);
        reminderSpinner.setSelection(appointmentRemindOptionArrayAdapter.getPosition(findMe));
    }

    @OnItemSelected(R.id.reminder_spinner)
    public void onReminderSpinnerItemSelected(final int position) {
        getPresenter().setRemindOptions(((AppointmentRemindOption) reminderSpinner.getItemAtPosition(position)).remindOptions);
    }

    public void setAppointmentScheduled(@NonNull final ProviderInfo provider, @NonNull final Date appointmentDate) {
        final Intent intent = AppointmentScheduledActivity.makeIntent(this, provider, appointmentDate);
        startActivity(intent);
        finish();
    }

    // create helper objects for each enum value
    private void populateAppointmentRemindOptions() {
        for (RemindOptions remindOptions : RemindOptions.values()) {
            final AppointmentRemindOption appointmentRemindOption = new AppointmentRemindOption(remindOptions);
            // filter out unavailable reminder options.  note - this data is not compared dynamically when the dropdown
            // is opened, but just done when the onCreate() is called so if the screen is open for a long time without
            // being touched, it's theoretically possible for an option to become invalid.  it's an edge case which
            // this sample app is not implementing
            if (appointmentRemindOption.isAvailable(getPresenter().getAppointmentDate())) {
                appointmentRemindOptions.add(new AppointmentRemindOption(remindOptions));
            }
        }
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_PHONE, callbackNumberLayout);
        return views;
    }

    public void setValidationReasons(@Nullable final Map<String, ValidationReason> validationReasons) {
        sampleUtils.handleValidationFailures(this, getValidationViews(), validationReasons, finishClickListener());
    }

    // helper class to wrap RemindOptions enum and an associated display string
    class AppointmentRemindOption {
        RemindOptions remindOptions;
        String displayString;

        AppointmentRemindOption(RemindOptions remindOptions) {
            this.remindOptions = remindOptions;
            this.displayString = sampleUtils.getOverrideString(getApplicationContext(), PREFIX_REMIND_OPTIONS, remindOptions.toString().toLowerCase());
        }

        @Override
        public String toString() {
            return displayString;
        }

        @Override
        public boolean equals(Object obj) {
            return remindOptions.equals(((AppointmentRemindOption) obj).remindOptions);
        }

        boolean isAvailable(@NonNull final Date date) {
            boolean available = true;
            final int minutes = sampleUtils.getOverrideInt(getApplicationContext(), PREFIX_REMIND_OPTIONS, remindOptions.toString().toLowerCase());
            if (minutes > -1) { // filter out the "None" option
                final Calendar reminder = Calendar.getInstance();
                reminder.setTime(date);
                reminder.add(Calendar.MINUTE, -minutes); // subtract the reminder minutes from the appointment date
                final Calendar now = Calendar.getInstance();
                available = reminder.after(now); // make sure the reminder is after "now"
            }
            return available;
        }
    }

}
