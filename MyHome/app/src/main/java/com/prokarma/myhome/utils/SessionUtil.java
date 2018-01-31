package com.prokarma.myhome.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.prokarma.myhome.R;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.features.fad.FadFragment;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.televisit.AwsManager;

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

        CommonUtil.showToast(activity, activity.getString(R.string.signed_out_successfully));
        clearData(true);
        if (null != progressBar)
            progressBar.setVisibility(View.GONE);
        if (null != activity) {
            Intent intent = LoginActivity.getLoginIntent(activity);
            activity.startActivity(intent);
            activity.finish();
        }

        TealiumUtil.trackEvent(Constants.SIGN_OUT_EVENT, null);
    }

    /**
     * Clear out all saved data in memory
     *
     * @param keepRefreshToken If true, we will not clear the refreshToken needed for auto-signin and FingerPrint login. If false, the refresh token will also be cleared
     */
    public static void clearData(boolean keepRefreshToken) {
        if (!keepRefreshToken) {
            CryptoManager.getInstance().clearToken();
        }

        AuthManager.getInstance().setSessionId(null);
        AuthManager.getInstance().setBearerToken(null);
        AuthManager.getInstance().setAmWellToken(null);
        AuthManager.getInstance().setSessionToken(null);
        ProfileManager.setProfile(null);
        ProfileManager.setFavoriteProviders(null);
        ProfileManager.setAppointments(null);
        AwsManager.getInstance().clearData();
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