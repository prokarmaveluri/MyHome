package com.prokarma.myhome.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.SplashActivity;
import com.prokarma.myhome.features.fad.FadFragment;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.login.endpoint.SignOutRequest;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;

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
        NetworkManager.getInstance().SignOut(
                new SignOutRequest(AuthManager.getInstance().getSessionId(),
                        AuthManager.getInstance().getBearerToken(),
                        AuthManager.getInstance().getRefreshToken()),
                AuthManager.getInstance().getBearerToken()).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (null != progressBar)
                    progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body().getIsValid()) {
                    Timber.i("Response successful: " + response);
                    Toast.makeText(activity, activity.getString(R.string.signed_out_successfully),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Timber.i("Response not successful: " + response);
                    Toast.makeText(activity, activity.getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG).show();
                }

                activity.finishAffinity();
                clearData();
                Intent intent = SplashActivity.getSplashIntent(activity);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Timber.i("Logout failed");
                Toast.makeText(activity, activity.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();

                if (null != progressBar)
                    progressBar.setVisibility(View.GONE);

                activity.finishAffinity();
                clearData();
                Intent intent = SplashActivity.getSplashIntent(activity);
                activity.startActivity(intent);
                activity.finish();
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

        AppPreferences.getInstance().setPreference("auth_token", null);
        AppPreferences.getInstance().setPreference("auth_token_iv", null);

        AuthManager.getInstance().setSessionId(null);
        AuthManager.getInstance().setSid(null);
        AuthManager.getInstance().setBearerToken(null);
        AuthManager.getInstance().setRefreshToken(null);
        AuthManager.getInstance().setSessionToken(null);
        ProfileManager.setProfile(null);
        ProfileManager.setFavoriteProviders(null);
        ProfileManager.setAppointments(null);
        FadFragment.providerList.clear();
        FadFragment.currentSearchQuery = "";
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