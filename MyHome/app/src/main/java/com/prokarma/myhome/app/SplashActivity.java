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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.prokarma.myhome.features.dev.EnvironmentSelectorFragment;
import com.prokarma.myhome.features.dev.EnvironmentSelectorInterface;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.login.dialog.EnrollmentSuccessDialog;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.login.fingerprint.FingerprintDialogCallbackInterface;
import com.prokarma.myhome.features.login.fingerprint.FingerprintSignIn;
import com.prokarma.myhome.features.login.verify.EmailVerifyActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.settings.TouchIDFragment;
import com.prokarma.myhome.features.update.UpdateResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.EnviHandler;
import com.prokarma.myhome.utils.TealiumUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.mock.NetworkBehavior;
import timber.log.Timber;

/**
 * MyHome splash activity
 */
public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        EnrollmentSuccessDialog.EnrollDialogAction,
        EnvironmentSelectorInterface,
        FingerprintDialogCallbackInterface {

    private ProgressBar progress;
    private TextView clickToRefresh;
    private boolean isGPSVerified = false;
    private boolean isVersionVerified = false;
    private static EnviHandler.AmWellEnvType currentAmwellEnv = EnviHandler.AmWellEnvType.NONE;
    private static EnviHandler.EnvType currentEnv = EnviHandler.EnvType.NONE;

    //Location
    private GoogleApiClient mGoogleApiClient;
    public static final int VERIFY_EMAIL = 90;
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

        Log.i("releaseTest", "onCreate");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_splash);

        CryptoManager.getInstance().setContext(getApplicationContext());
        progress = (ProgressBar) findViewById(R.id.splash_progress);
        clickToRefresh = (TextView) findViewById(R.id.splashRefresh);

        progress.setVisibility(View.GONE);
        if (getIntent() != null && getIntent().getBooleanExtra("ENROLL_SUCCESS", false)) {

            TealiumUtil.trackEvent(Constants.ENROLLMENT_SUCCESS_EVENT, null);
            EnrollmentSuccessDialog dialog = EnrollmentSuccessDialog.newInstance(this);
            dialog.show(getSupportFragmentManager(), "EnrollmentSuccessDialog");
        } else {
            buildEnvAlert();
        }
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
    }

    private void buildEnvAlert() {

        if (!BuildConfig.BUILD_TYPE.contains("release")) {
            if (currentEnv != EnviHandler.EnvType.NONE) {
                if (currentEnv == EnviHandler.EnvType.DEV) {
                    EnviHandler.initEnv(EnviHandler.EnvType.DEV);
                } else if (currentEnv == EnviHandler.EnvType.SLOT1) {
                    EnviHandler.initEnv(EnviHandler.EnvType.SLOT1);
                } else if (currentEnv == EnviHandler.EnvType.STAGE) {
                    EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
                } else if (currentEnv == EnviHandler.EnvType.PROD) {
                    EnviHandler.initEnv(EnviHandler.EnvType.PROD);
                }
            }
        } else {
            EnviHandler.initEnv(EnviHandler.EnvType.PROD);
        }

        if (!BuildConfig.BUILD_TYPE.contains("release") && currentEnv == EnviHandler.EnvType.NONE) {
            EnvironmentSelectorFragment selectorDialog = new EnvironmentSelectorFragment();
            selectorDialog.setEnvironmentSelectorInterface(this);
            selectorDialog.setCancelable(false);
            selectorDialog.show(getSupportFragmentManager(), EnvironmentSelectorFragment.ENVIRONMENT_SELECTOR_TAG);
        } else {
            initApiClient();

            //init retrofit service
            if(EnviHandler.EnvType.DEMO.equals(currentEnv)){
                NetworkManager.getInstance().initMockService(NetworkBehavior.create());
            } else {
                NetworkManager.getInstance().initService();
            }

            startLocationFetch();
        }
    }

    private void getAccessTokenFromRefresh() {

        // 31614: Android: Fingerprint Authentication upon sign-out.
        // we are going to show this on Login screen upon signout..
        // and hence we donot need this logic on splash screen, which makes the fingerprint dialog to come up only after killing the app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                AppPreferences.getInstance().getBooleanPreference(TouchIDFragment.TOUCH_ID_KEY) &&
                CryptoManager.getInstance().getToken() != null) {

            FingerprintSignIn fingerprint = new FingerprintSignIn(this, FingerprintSignIn.DEFAULT_KEY_NAME);
            if (fingerprint.isSupportFingerprint() && fingerprint.isDeviceConfiguredFingerprint()) {
                loadLogin();
                return;
            }
        }
        refreshToken();
    }

    @Override
    public void onFingerprintAuthentication() {
        refreshToken();
    }

    @Override
    public void onFingerprintAuthenticationCancel() {
        finish();
    }

    @Override
    public void onFingerprintAuthenticationUsePassword() {
        onRefreshFailed();
    }

    private void refreshToken() {
        if (CryptoManager.getInstance().getToken() != null) {
            refreshAccessToken(CryptoManager.getInstance().getToken());
        } else {
            progress.setVisibility(View.GONE);
            onRefreshFailed();
        }
    }

    /**
     * Refresh Auth tokens
     *
     * @param refreshToken
     */
    private void refreshAccessToken(final String refreshToken) {
        if (!ConnectionUtil.isConnected(this)) {
            CommonUtil.showToast(this, this.getString(R.string.no_network_msg));
            progress.setVisibility(View.GONE);
            return;
        }
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().signInRefresh(new RefreshRequest(refreshToken))
                .enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                        progress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body().getValid()) {
                            try {

                                AppPreferences.getInstance().setLongPreference("FETCH_TIME", System.currentTimeMillis());
                                AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                                AuthManager.getInstance().getUsersAmWellToken();
                                NetworkManager.getInstance().getSavedDoctors(getApplicationContext(), progress);
                                CryptoManager.getInstance().saveToken(response.body().getResult().getRefreshToken());

                                ProfileManager.setProfile(response.body().getResult().getUserProfile());
                                NetworkManager.getInstance().getSavedDoctors(getApplicationContext(), progress);

                                if (null != response.body().getResult().getUserProfile() &&
                                        !response.body().getResult().getUserProfile().isVerified &&
                                        DateUtil.isMoreThan30days(response.body().getResult().getUserProfile().createdDate)) {

                                    SignInSuccessBut30days();
                                } else if (null != response.body().getResult().getUserProfile() &&
                                        !response.body().getResult().getUserProfile().isTermsAccepted) {
                                    acceptTermsOfService(false);

                                } else {
                                    onRefreshSuccess();
                                }
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            onRefreshFailed();
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResponse> call, Throwable t) {
                        Timber.i("onFailure : ");
                        progress.setVisibility(View.GONE);
                        onRefreshFailed();
                    }
                });
    }


    private void onRefreshSuccess() {
        try {
            //Pre- load profile and appointment
            //ProfileManager.getProfileInfo();
            NetworkManager.getInstance().getMyAppointments();
            AuthManager.getInstance().setCount(0);

            Intent intentHome = new Intent(this, NavigationActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
            ActivityCompat.startActivity(this, intentHome, options.toBundle());
            finish();

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    private void onRefreshFailed() {
        //  Pre- load profile and appointment
        loadLogin();
    }

    private void loadLogin() {
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

    }


    private void startLocationFetch() {
        isGPSVerified = false;

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!hasPermissions(this, permissions)) {
            requestPermissions();
            return;
        }
        if (!CommonUtil.isGPSEnabled(this)) {
            buildGpsAlert();
            return;
        }
        getLastLocation();
    }

    private void requestPermissions() {

        Timber.i("Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        ActivityCompat.requestPermissions(SplashActivity.this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
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
                isGPSVerified = true;
                if (isGPSVerified) {
                    getAccessTokenFromRefresh();
                } else {
                    versionCheck(isGPSVerified);
                }
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

                            if (null != location) {

                                LocationResponse locObj = new LocationResponse();
                                locObj.setLat(String.valueOf(location.getLatitude()));
                                locObj.setLon(String.valueOf(location.getLongitude()));
                                locObj.setDisplayName("Current Location");
                                locObj.setZipCode("");
                                FadManager.getInstance().setCurrentLocation(locObj);
                                FadManager.getInstance().setLocation(locObj);
                            } else {
                                NetworkManager.getInstance().getUserLocation();
                            }
                        } else {
                            if (null != task)
                                Timber.w(task.getException());
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
            case VERIFY_EMAIL:
                if (resultCode == Activity.RESULT_OK) {
                    finish();
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
                        isGPSVerified = true;
                        startSettings();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        NetworkManager.getInstance().getUserLocation();
                        isGPSVerified = true;
                        versionCheck(true);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildUpdateAlert(String message, final boolean isForceupdate) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!isForceupdate) {
            builder.setMessage(message)
                    .setTitle(R.string.new_update_available)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            isVersionVerified = true;
                            updateApplication();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            isVersionVerified = true;
                            getAccessTokenFromRefresh();
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
                            isVersionVerified = true;
                            finish();
                        }
                    });
        }
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateApplication() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
        } catch (ActivityNotFoundException ex) {
            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        }
    }

    /**
     * Application version check
     *
     * @param isGPSVerified
     */
    private void versionCheck(final boolean isGPSVerified) {
        isVersionVerified = false;
        if (!ConnectionUtil.isConnected(this)) {
            CommonUtil.showToast(this, getString(R.string.no_network_msg));
            progress.setVisibility(View.GONE);
            return;
        }
        if (isVersionVerified)
            return;
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
                            if (isGPSVerified) {
                                getAccessTokenFromRefresh();
                            }
                        }

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ApiErrorUtil.getInstance().versionCheckError(getApplicationContext(), progress, response);
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                ApiErrorUtil.getInstance().versionCheckFailed(getApplicationContext(), progress, t);
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

    /**
     * Auto Login from enrollment
     *
     * @param request
     */
    private void login(final SignInRequest request) {
        if (!ConnectionUtil.isConnected(this)) {
            CommonUtil.showToast(this, getString(R.string.no_network_msg));
            progress.setVisibility(View.GONE);
            return;
        }
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().signIn(request).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body().getValid()) {
                    try {
                        // get id_token & session id
                        AuthManager.getInstance().setCount(0);
                        ProfileManager.clearSessionData();
                        AppPreferences.getInstance().setLongPreference("IDLE_TIME", 0);

                        AuthManager.getInstance().setSessionId(response.body().getResult().getSessionId());
                        AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                        AuthManager.getInstance().getUsersAmWellToken();

                        ProfileManager.setProfile(response.body().getResult().getUserProfile());
                        NetworkManager.getInstance().getSavedDoctors(getApplicationContext(), progress);
                        CryptoManager.getInstance().saveToken(response.body().getResult().getRefreshToken());
                        if (null != response.body().getResult().getUserProfile() &&
                                !response.body().getResult().getUserProfile().isVerified &&
                                DateUtil.isMoreThan30days(response.body().getResult().getUserProfile().createdDate)) {

                            SignInSuccessBut30days();
                        } else if (null != response.body().getResult().getUserProfile() &&
                                !response.body().getResult().getUserProfile().isTermsAccepted) {
                            acceptTermsOfService(false);

                        } else {
                            onRefreshSuccess();
                        }
                    } catch (NullPointerException ex) {
                        Timber.w(ex);
                    }
                } else {
                    onRefreshFailed();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                onRefreshFailed();
                Timber.e("Login failure");
                Timber.e("Throwable = " + t);
            }
        });
    }

    @Override
    public void onEnrollDialogUserAction() {
        String userName = getIntent().getStringExtra("USER_NAME");
        String password = getIntent().getStringExtra("PASSWORD");
        SignInRequest request = new SignInRequest(userName, password);
        login(request);
    }

    public void SignInSuccessBut30days() {
        startVerify();
    }

    public void acceptTermsOfService(boolean isTermsOfServiceAccepted) {
        startTermsOfServiceActivity();
        finish();
    }

    private void startTermsOfServiceActivity() {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.slide_in_right, R.anim.slide_out_left);

        NavigationActivity.setActivityTag(Constants.ActivityTag.TERMS_OF_SERVICE);
        Intent intentTos = new Intent(this, OptionsActivity.class);
        ActivityCompat.startActivity(this, intentTos, options.toBundle());
    }

    private void startVerify() {
        Intent intentVerify = EmailVerifyActivity.getEmailVerifyIntent(this);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.slide_in_right, R.anim.slide_out_left);

        ActivityCompat.startActivityForResult(this, intentVerify, VERIFY_EMAIL, options.toBundle());
        finish();
    }

    @Override
    public void envAmWellSelected(EnviHandler.AmWellEnvType amWellType) {
        Timber.i("Environment: Amwell " + amWellType + " selected");

        switch (amWellType) {
            case DEV:
                currentAmwellEnv = EnviHandler.AmWellEnvType.DEV;
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.DEV);
                break;
            case STAGE:
                currentAmwellEnv = EnviHandler.AmWellEnvType.STAGE;
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.STAGE);
                break;
            case IOT:
                currentAmwellEnv = EnviHandler.AmWellEnvType.IOT;
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.IOT);
                break;
            case PROD:
                currentAmwellEnv = EnviHandler.AmWellEnvType.PROD;
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.PROD);
                break;
        }
        AppPreferences.getInstance().setPreference(Constants.ENV_AMWELL, currentAmwellEnv.toString());
    }

    @Override
    public void envMyHomeSelected(EnviHandler.EnvType envType) {
        Timber.i("Environment: " + envType + " selected");

        switch (envType) {
            case DEMO:
                currentEnv = EnviHandler.EnvType.DEMO;
                EnviHandler.initEnv(EnviHandler.EnvType.DEMO);
                break;
            case DEV:
                currentEnv = EnviHandler.EnvType.DEV;
                EnviHandler.initEnv(EnviHandler.EnvType.DEV);
                break;
            case TEST:
                currentEnv = EnviHandler.EnvType.TEST;
                EnviHandler.initEnv(EnviHandler.EnvType.TEST);
                break;
            case SLOT1:
                currentEnv = EnviHandler.EnvType.SLOT1;
                EnviHandler.initEnv(EnviHandler.EnvType.SLOT1);
                break;
            case STAGE:
                currentEnv = EnviHandler.EnvType.STAGE;
                EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
                break;
            case PROD:
                currentEnv = EnviHandler.EnvType.PROD;
                EnviHandler.initEnv(EnviHandler.EnvType.PROD);
                break;
        }
        AppPreferences.getInstance().setPreference(Constants.ENV_MYHOME, currentEnv.toString());

        progress.setVisibility(View.VISIBLE);
        initApiClient();

        //init retrofit service
        if(EnviHandler.EnvType.DEMO.equals(currentEnv)){
            NetworkManager.getInstance().initMockService(NetworkBehavior.create());
        } else {
            NetworkManager.getInstance().initService();
        }

        startLocationFetch();
    }

    @Override
    public void attemptMutualAuth(boolean attemptMutualAuth) {
        Timber.i("Environment: attemptMutualAuth = " + attemptMutualAuth);
        EnviHandler.setAttemptMutualAuth(attemptMutualAuth);
        AppPreferences.getInstance().setBooleanPreference(Constants.ENV_MUTUAL_AUTH, attemptMutualAuth);
    }

    @Override
    public void hardcodedUser(String user, String password) {
        Timber.i("Environment: hardcodedUser. username = " + user);
        EnviHandler.setAmwellUsername(user);
        EnviHandler.setAmwellPassword(password);
        AppPreferences.getInstance().setPreference(Constants.ENV_AMWELL_USERNAME, user);
        AppPreferences.getInstance().setPreference(Constants.ENV_AMWELL_PASSWORD, password);
    }
}
