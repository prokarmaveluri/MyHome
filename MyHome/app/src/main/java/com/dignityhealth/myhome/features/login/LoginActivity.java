package com.dignityhealth.myhome.features.login;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.dignityhealth.myhome.BuildConfig;
import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityLoginBinding;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.features.fad.LocationResponse;
import com.dignityhealth.myhome.features.login.dialog.EnrollmentSuccessDialog;
import com.dignityhealth.myhome.features.update.UpdateActivity;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.AppPreferences;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.TealiumUtil;
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

import timber.log.Timber;


/*
 * Activity to login.
 *
 * Created by cmajji on 4/26/17.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private ActivityLoginBinding binding;

    public static String FAILURE_COUNT = "FAILURE_COUNT";
    public static String FAILURE_TIME_STAMP = "FAILURE_TIME_STAMP";


    //Location
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 200;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 100;
    private FusedLocationProviderClient mFusedLocationClient;

    /*
     * Get an intent for login activity.
     */
    public static Intent getLoginIntent(Context context) {

        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        initApiClient();
        LoginFragment fragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.loginFrame.getId(), fragment).commit();

        boolean enrollmentSuccess = getIntent().getBooleanExtra("ENROLL_SUCCESS", false);
        if (enrollmentSuccess) {
            EnrollmentSuccessDialog dialog = EnrollmentSuccessDialog.newInstance();
            dialog.show(getSupportFragmentManager(), "EnrollmentSuccessDialog");
        }
//        startUpdateActivity(false);
//        buildUpdateAlert(getString(R.string.app_suggest_update_message));
        new LoginPresenter(fragment, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TealiumUtil.trackEvent(Constants.APP_CLOSE_EVENT, null);
        AppPreferences.getInstance().setPreference(FAILURE_COUNT,
                AuthManager.getInstance().getCount());
        AppPreferences.getInstance().setLongPreference(FAILURE_TIME_STAMP,
                AuthManager.getInstance().getPrevTimestamp());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TealiumUtil.trackEvent(Constants.APP_OPEN_EVENT, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        startLocationFetch();
    }

    private void startLocationFetch() {
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
        ActivityCompat.requestPermissions(LoginActivity.this,
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
                            status.startResolutionForResult(LoginActivity.this,
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
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startSettings();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        NetworkManager.getInstance().getUserLocation();
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

    private void buildUpdateAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.new_update_available)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        updateApplication();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateApplication() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException ex) {
        }
    }
}
