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

    //Appointments
    public <T> void getMyAppointmentsError(final Context context, final View view, final Response<T> response){
        genericError(context, view);
    }

    public void getMyAppointmentsFailed(final Context context, final View view, final Throwable throwable) {
        genericError(context, view);
    }



    public void clearErrorMessage(){
        if(snackbar != null){
            snackbar.dismiss();
        }
    }
}
