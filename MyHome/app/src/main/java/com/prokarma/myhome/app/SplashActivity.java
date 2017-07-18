package com.prokarma.myhome.app;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.login.RefreshAccessTokenResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.update.UpdateActivity;
import com.prokarma.myhome.features.update.UpdateResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.RESTConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * MyHome splash activity
 */
public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ProgressBar progress;
    private TextView clickToRefresh;
    private boolean isGPSVerified = false;
    private boolean isVersionVerified = false;

    //Location
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 200;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 100;
    private FusedLocationProviderClient mFusedLocationClient;

    /*
     * Get an intent for splash activity.
     */
    public static Intent getSplashIntent(Context context) {

        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_splash);

        initApiClient();
        progress = (ProgressBar) findViewById(R.id.splash_progress);
        clickToRefresh = (TextView) findViewById(R.id.splashRefresh);

        clickToRefresh.setVisibility(View.GONE);
        if (!ConnectionUtil.isConnected(this)) {
            clickToRefresh.setVisibility(View.VISIBLE);
        }

        clickToRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionCheck(true);
            }
        });
        versionCheck(false);
    }

    private void getAccessTokenFromRefresh() {
        if (AuthManager.getInstance().getRefreshToken() != null) {
            refreshAccessToken(AuthManager.getInstance().getRefreshToken());
        } else if (CryptoManager.getInstance().getToken() != null) {
            refreshAccessToken(CryptoManager.getInstance().getToken());
        } else {
            progress.setVisibility(View.GONE);
            onRefreshFailed();
        }
    }

    private void refreshAccessToken(String refreshToken) {
        if (!ConnectionUtil.isConnected(this)) {
            Toast.makeText(this, R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
            return;
        }
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().refreshAccessToken(RESTConstants.GRANT_TYPE_REFRESH,
                refreshToken,
                RESTConstants.CLIENT_ID,
                RESTConstants.AUTH_REDIRECT_URI).enqueue(new Callback<RefreshAccessTokenResponse>() {
            @Override
            public void onResponse(Call<RefreshAccessTokenResponse> call, Response<RefreshAccessTokenResponse> response) {
                if (response.isSuccessful()) {
                    AuthManager.getInstance().setBearerToken(response.body().getAccessToken());
                    AuthManager.getInstance().setRefreshToken(response.body().getRefreshToken());
                    CryptoManager.getInstance().saveToken();
                    onRefreshSuccess();
                } else {
                    onRefreshFailed();
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RefreshAccessTokenResponse> call, Throwable t) {
                Timber.i("onFailure : ");
                progress.setVisibility(View.GONE);
                onRefreshFailed();
            }
        });
    }


    private void onRefreshSuccess() {
        //  Pre- load profile and appointment
        ProfileManager.getProfileInfo();
        ProfileManager.getAppointmentInfo();
        AuthManager.getInstance().setCount(0);
        Intent intentHome = new Intent(this, NavigationActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
        ActivityCompat.startActivity(this, intentHome, options.toBundle());
        finish();
    }

    private void onRefreshFailed() {
        //  Pre- load profile and appointment
        Intent intentHome = new Intent(this, LoginActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
        ActivityCompat.startActivity(this, intentHome, options.toBundle());
        finish();
    }

    //Location permission


    @Override
    public void onStart() {
        super.onStart();
        startLocationFetch();
    }


    private void startLocationFetch() {
        isGPSVerified = false;
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }
        if (!enableGPS()) {
            buildGpsAlert();
            return;
        }
        getLastLocation();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SplashActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {

        Timber.i("Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        startLocationPermissionRequest();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Timber.i("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                startLocationFetch();
            } else {
                // Permission denied.
                NetworkManager.getInstance().getUserLocation();
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            Timber.w("getLastLocation " + location.toString());
                            if (null != location) {
                                LocationResponse locObj = new LocationResponse();
                                locObj.setLat(String.valueOf(location.getLatitude()));
                                locObj.setLon(String.valueOf(location.getLongitude()));
                                locObj.setDisplayName("User Location");
                                locObj.setZipCode("");
                                FadManager.getInstance().setCurrentLocation(locObj);
                                FadManager.getInstance().setLocation(locObj);
                            } else {
                                NetworkManager.getInstance().getUserLocation();
                            }
                        } else {
                            if (null != task)
                                Timber.w("getLastLocation:exception", task.getException());
                            NetworkManager.getInstance().getUserLocation();
                        }
                        isGPSVerified = true;
                        if (isVersionVerified) {
                            getAccessTokenFromRefresh();
                        } else {
                            versionCheck(true);
                        }
                    }
                });
    }

    private boolean enableGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Change Location setting in app access
     * init mGoogleApiClient and connect before using this method
     */
    private void changeSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Timber.w("getLastLocation:SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Timber.w("getLastLocation:SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    private void startSettings() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                REQUEST_CHECK_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    startLocationFetch();
                }
                break;
        }
    }

    private void initApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void buildGpsAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_dialog_message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startSettings();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        NetworkManager.getInstance().getUserLocation();
                        versionCheck(true);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void startUpdateActivity(boolean isForceUpdate) {
        Intent intent = UpdateActivity.getLoginIntent(this);
        intent.putExtra("IS_FORCE_UPDATE", isForceUpdate);
        startActivity(intent);

        if (isForceUpdate)
            finish();
    }

    private boolean isForceUpdate() {
        if (BuildConfig.VERSION_CODE == Constants.DEV_UPDATE_VERSION)
            return true;
        return false;
    }

    private boolean isSoftUpdate() {
        if (BuildConfig.VERSION_CODE == Constants.DEV_UPDATE_VERSION)
            return true;
        return false;
    }

    private void buildUpdateAlert(String message, boolean isForceupdate) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!isForceupdate) {
            builder.setMessage(message)
                    .setTitle(R.string.new_update_available)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            updateApplication();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
        } else {
            builder.setMessage(message)
                    .setTitle(R.string.new_update_available)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            updateApplication();
                            finish();
                        }
                    });
        }
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateApplication() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException ex) {
        }
    }

    private void versionCheck(final boolean isGPSVerified) {
        isVersionVerified = false;
        if (!ConnectionUtil.isConnected(this)) {
            Toast.makeText(this, R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
            return;
        }
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().versionCheck().enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        String fourceUpdate = response.body().getServices().getCiam().getClients()
                                .getMyhomemobileAndroid().getForceUpdate();
                        String suggestUpdate = response.body().getServices().getCiam().getClients()
                                .getMyhomemobileAndroid().getSuggestUpdate();

                        Double doubleFourceUpdate = Double.parseDouble(fourceUpdate);
                        Double doubleSuggestUpdate = Double.parseDouble(suggestUpdate);

                        int intFourceUpdate = doubleFourceUpdate.intValue();
                        int intSuggestUpdate = doubleSuggestUpdate.intValue();

                        Timber.i("intFourceUpdate val " + intFourceUpdate);
                        Timber.i("intSuggestUpdate val " + intSuggestUpdate);

                        if (!appUpdate(intFourceUpdate, intSuggestUpdate)) {
                            isVersionVerified = true;
                            if (isGPSVerified)
                                getAccessTokenFromRefresh();
                        }

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    private boolean appUpdate(int forceUpdate, int suggestUpdate) {
        if (forceUpdate > BuildConfig.VERSION_CODE) {
            buildUpdateAlert(getString(R.string.app_force_update_message), true);
            return true;
        }
        if (suggestUpdate > BuildConfig.VERSION_CODE) {
            buildUpdateAlert(getString(R.string.app_suggest_update_message), false);
            return true;
        }
        return false;
    }
}