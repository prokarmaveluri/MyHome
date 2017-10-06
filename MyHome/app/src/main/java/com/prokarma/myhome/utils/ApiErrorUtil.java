package com.prokarma.myhome.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;

import retrofit2.Response;

/**
 * Created by kwelsh on 9/26/17.
 */

public class ApiErrorUtil {
    private static ApiErrorUtil mInstance = null;
    private Snackbar snackbar;

    public static ApiErrorUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ApiErrorUtil();
        }
        return mInstance;
    }

    private void genericError(final Context context, final View view, boolean isDismissable) {
        snackbar = Snackbar.make(view, context.getString(R.string.api_error_message), Snackbar.LENGTH_INDEFINITE);

        if (isDismissable) {
            View snackbarView = snackbar.getView();
            snackbarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearErrorMessage();
                }
            });
        }

        snackbar.show();
    }

    private void invalidPassword(final Context context, final View view) {
        snackbar = Snackbar.make(view, context.getString(R.string.api_error_incorrect_password), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //Profile
    public <T> void updateProfileError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void updateProfileFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void getProfileError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void getProfileFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    //Appointments & Booking
    public <T> void getMyAppointmentsError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void getMyAppointmentsFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    //TODO - Ask designers what they want to do for when Booking Done Fails (send to reg forms currently)
    public <T> void createAppointmentError(final Context context, final View view, final Response<T> response) {
        //genericError(context, view);
    }

    //TODO - Ask designers what they want to do for when Booking Done Fails (send to reg forms currently)
    public void createAppointmentFailed(final Context context, final View view, final Throwable throwable) {
        //genericError(context, view);
    }

    public <T> void getValidationRulesError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void getValidationRulesFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    //ToS
    public <T> void getTosError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void getTosFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    //Provider Details
    public <T> void getProviderDetailsError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void getProviderDetailsFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    //Login
    public <T> void signInError(final Context context, final View view, final Response<SignInResponse> response) {
        if (response != null && response.body() != null && !response.body().getValid()) {
            invalidPassword(context, view);
        } else {
            genericError(context, view, true);
        }
    }

    public void signInFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void signInRefreshError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void signInRefreshFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void signOutError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void signOutFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void registerError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void registerFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void changePasswordError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void changePasswordFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public <T> void changeSecurityQuestionError(final Context context, final View view, final Response<T> response) {
        genericError(context, view, true);
    }

    public void changeSecurityQuestionFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view, true);
    }

    public void clearErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }
}
