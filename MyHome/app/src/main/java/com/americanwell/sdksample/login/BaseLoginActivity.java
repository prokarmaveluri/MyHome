/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.login;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.americanwell.sdksample.appointments.AppointmentActivity;

/**
 * The base activity for login and enrollment.
 */
public class BaseLoginActivity<P extends BaseLoginPresenter>
        extends BaseSampleNucleusActivity<P> {


    @Override
    public void onBackPressed() {
        getPresenter().logout(); // otherwise logout
        super.onBackPressed();
    }

    /**
     * presenter will call this when a consumer is fetched
     */
    public void setConsumerRetrieved() {
        goHome(); // ok, we're ready to start!
    }

    // if we're launching from a telehealth now URL, we fetch the appointment
    // once it's fetched, we set it here to move on.
    public void setAppointment(final Appointment appointment) {
        startActivity(AppointmentActivity.makeIntent(this, appointment, true));
        finish();
    }

    public void setNeedsToAcceptOustandingDisclaimer(final Authentication authentication) {
        startActivity(OutstandingDisclaimerActivity.makeIntent(this, authentication));
    }

    // presenter will call this if auth was successful, but needs to complete enrollment
    public void setNeedsToCompleteEnrollment(final Authentication authentication,
                                             final String completeEnrollmentEmail,
                                             final State completeEnrollmentLocation) {
        startActivity(CompleteEnrollmentActivity.makeIntent(this, authentication,
                completeEnrollmentEmail, completeEnrollmentLocation));
    }
}