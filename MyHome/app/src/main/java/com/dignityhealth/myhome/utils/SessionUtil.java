package com.dignityhealth.myhome.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.dignityhealth.myhome.features.login.LoginActivity;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/10/17.
 * Utility class to help handle the sessions
 */
public class SessionUtil {

    /**
     * Signs the current user out and kicks him to the Login Activity
     *
     * @param activity the activity context
     */
    public static void logout(final Activity activity, final ProgressBar progressBar) {
        if (null != progressBar)
            progressBar.setVisibility(View.VISIBLE);
        if (null == AuthManager.getInstance().getSid()) {
            Timber.i("AuthManager didn't have an Id Token for Sign Out.\nSending User to log in again...");
            Toast.makeText(activity, activity.getString(R.string.no_valid_session),
                    Toast.LENGTH_SHORT).show();

            clearData();
            if (null != progressBar)
                progressBar.setVisibility(View.GONE);
            if (null != activity) {
                Intent intent = LoginActivity.getLoginIntent(activity);
                activity.startActivity(intent);
                activity.finish();
            }
            return;
        }
        TealiumUtil.trackEvent(Constants.SIGN_OUT_EVENT, null);
        NetworkManager.getInstance().logout(
                AuthManager.getInstance().getSid()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (null != progressBar)
                    progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Timber.i("Response successful: " + response);

                    activity.finishAffinity();
                    Toast.makeText(activity, activity.getString(R.string.signed_out_successfully),
                            Toast.LENGTH_SHORT).show();

                    clearData();
                    Intent intent = LoginActivity.getLoginIntent(activity);
                    activity.startActivity(intent);
                    activity.finish();
                    return;
                }

                Timber.i("Response not successful: " + response);
                Toast.makeText(activity, activity.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.i("Logout failed");
                Toast.makeText(activity, activity.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();
                if (null != progressBar)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Clear out all saved data in memory
     */
    public static void clearData() {
        Timber.v("Clearing Memory: " +
                "Bearer Token = " + AuthManager.getInstance().getBearerToken() +
                "\nSession Token = " + AuthManager.getInstance().getSessionToken() +
                "\nProfile = " + ProfileManager.getProfile());

        AuthManager.getInstance().setSessionId(null);
        AuthManager.getInstance().setSid(null);
        AuthManager.getInstance().setBearerToken(null);
        AuthManager.getInstance().setSessionToken(null);
        ProfileManager.setProfile(null);
        RecentlyViewedDataSourceDB.getInstance().deleteTable();
    }

    public static void signOutAlert(final Activity activity, final ProgressBar progressBar) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.signout_alert_message)
                .setTitle(null)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        logout(activity, progressBar);
                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.i("Sign out alert, No option");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}