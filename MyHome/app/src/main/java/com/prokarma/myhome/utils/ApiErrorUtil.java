package com.prokarma.myhome.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.prokarma.myhome.R;

import retrofit2.Response;

/**
 * Created by kwelsh on 9/26/17.
 */

public class ApiErrorUtil {
    private static ApiErrorUtil mInstance = null;
    private Snackbar snackbar;

    public static ApiErrorUtil getInstance(){
        if(mInstance == null){
            mInstance = new ApiErrorUtil();
        }
        return mInstance;
    }

    private void genericError(final Context context, final View view) {
        snackbar = Snackbar.make(view, context.getString(R.string.api_error_message), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    //Profile
    public <T> void updateProfileError(final Context context, final View view, final Response<T> response) {
        genericError(context, view);
    }

    public void updateProfileFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    public <T> void getProfileError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getProfileFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    //Appointments & Booking
    public <T> void getMyAppointmentsError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getMyAppointmentsFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    //TODO - Ask designers what they want to do for when Booking Done Fails (send to reg forms currently)
    public <T> void createAppointmentError(final Context context, final View view, final Response<T> response){
        //genericError(context, view);
    }

    //TODO - Ask designers what they want to do for when Booking Done Fails (send to reg forms currently)
    public void createAppointmentFailed(final Context context, final View view, final Throwable throwable) {
        //genericError(context, view);
    }

    public <T> void getValidationRulesError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getValidationRulesFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    //ToS
    public <T> void getTosError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getTosFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    //Provider Details
    public <T> void getProviderDetailsError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getProviderDetailsFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    //Login
    public <T> void signInError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void signInFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    public <T> void signInRefreshError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void signInRefreshFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }

    public <T> void signOutError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void signOutFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }


    public void clearErrorMessage(){
        if(snackbar != null){
            snackbar.dismiss();
        }
    }
}
