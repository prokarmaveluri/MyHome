package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.profile.Profile;

import retrofit2.Response;

/**
 * Created by kwelsh on 5/28/17.
 */

public interface BookingDialogInterface<T> {
    void onBookingDialogFinished(Profile bookingProfile);

    void onValidationRulesError(Response<T> response);

    void onValidationRulesFailed(Throwable throwable);
}
