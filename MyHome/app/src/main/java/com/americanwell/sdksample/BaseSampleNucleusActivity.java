/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.NamedSDKEntity;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.home.HomeActivity;
import com.americanwell.sdksample.login.LoginActivity;
import com.americanwell.sdksample.util.FileUtils;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.SampleNamedNothingSelectedSpinnerAdapter;
import com.americanwell.sdksample.util.SampleUtils;
import com.livefront.bridge.Bridge;
import com.prokarma.myhome.R;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Base class for all activities in this application provides some common implementation and
 * extends the use of the Nucleus MVP library (https://github.com/konmik/nucleus)
 */
public abstract class BaseSampleNucleusActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

    private static final String LOG_TAG = BaseSampleNucleusActivity.class.getName();

    protected static final int PROGRESS_DIALOG_DELAY_MS = 500;

    @Inject
    protected SampleUtils sampleUtils; // provides this to all child classes
    @Inject
    FileUtils fileUtils; // this one's just used here.  other activities can inject independently
    @Inject
    protected LocaleUtils localeUtils; // provides this to all child classes

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar; // common toolbar storage
    @Nullable
    @BindView(R.id.fab)
    protected FloatingActionButton fab; // common fab storage

    @BindString(R.string.app_default_indeterminate)
    String indeterminate;

    // This count is to keep track of how many "things" (aka asynchronous requests) are currently
    // being waited for.  typically controlled by the presenter, a call to setSomethingIsBusy(true) will increment
    // the counter, while a call with the parameter as "false" will decrement.
    // this count is used to show and hide the progress bar.  (the progress bar paradigm can be overridden in the
    // activity if there's a different way to show the "waiting" mode)
    @State
    int busyCount = 0;

    // default standard progress dialog, will say "Please wait..."
    private ProgressDialog progressDialog;
    protected Dialog datePickerDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean forcePortrait = getResources().getBoolean(R.bool.sdksample_force_portrait);
        if (forcePortrait) {
            DefaultLogger.d(LOG_TAG, "forced video to portrait orientation as per config");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Bridge.restoreInstanceState(this, savedInstanceState); // use Icepick to save/restore state
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new ProgressDialog(this); // instantiate the progress bar
        progressDialog.setMessage(indeterminate);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        showBusy(busyCount > 0); // this will put the progress bar back up on rotate, if necessary
    }

    @Override
    protected void onPause() {
        super.onPause();
        showBusy(false); // hide the progressbar to avoid hanging on to resources, etc
        dismissDialogs();
    }

    protected void dismissDialogs() {
        dismissDialog(progressDialog);
        dismissDialog(datePickerDialog);
    }

    protected void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void dismissDialog(DialogFragment dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this); // use Butterknife to bind the activity views
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // generic back-arrow handling - just make it do what the back button does
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bridge.saveInstanceState(this, outState); // always use icepick to save state
    }

    // Validation and Error Handling methods

    /**
     * call this if validation fails
     *
     * @param validationReasons
     */
    public void setValidationReasons(@Nullable final Map<String, ValidationReason> validationReasons) {
        sampleUtils.handleValidationFailures(this, getValidationViews(), validationReasons);
    }

    /**
     * When dealing with a SDKValidatedCallback, you may get an array of key/value pairs with
     * validation failures.  There is standard handling for this in sampleUtils.  If you return
     * a Map of the expected keys to views in your UI, the standard handling will call 'setError' on
     * the views.  otherwise the default handling is a pop-up dialog.
     *
     * @return
     */
    protected Map<String, View> getValidationViews() {
        return null; // override if you have any
    }

    public void setError(@NonNull final SDKError error) {
        setNothingIsBusy();
        sampleUtils.handleError(this, error);
    }

    public void setPasswordError(@NonNull final SDKPasswordError passwordError) {
        setNothingIsBusy();
        sampleUtils.handleError(this, passwordError);
    }

    public void setError(@NonNull final Throwable throwable) {
        setNothingIsBusy();
        sampleUtils.handleError(this, throwable);
    }

    public void setError(@NonNull final String title,
                         @NonNull final String message) {
        setNothingIsBusy();
        sampleUtils.handleError(this, title, message, null);
    }

    /**
     * Whether or not to show a progress bar with a cyclic animation without an indication of
     * progress.
     */
    public void setSomethingIsBusy(final boolean busy) {
        if (busy) {
            busyCount++;
        }
        else if (busyCount > 0) {
            busyCount--;
        }
        DefaultLogger.d(LOG_TAG, "setting busy to " + busy + ". count = " + busyCount);
        showBusy(busyCount > 0);
    }

    // Runnable for displaying the progress dialog
    final Runnable busyTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (busyCount > 0) {
                progressDialog.show();
            }
        }
    };
    final Handler busyTimer = new Handler(Looper.getMainLooper());

    /**
     * default behavior for showBusy(), which is to put up the progress dialog
     * override this if your activity has different requirements
     *
     * @param show
     */
    protected void showBusy(final boolean show) {
        if (show) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                // set up a timer to show the progress dialog
                busyTimer.postDelayed(busyTimerRunnable, PROGRESS_DIALOG_DELAY_MS);
            }
        }
        else {
            busyTimer.removeCallbacks(busyTimerRunnable);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    /**
     * clear out the busy count and hide the busy business
     */
    public void setNothingIsBusy() {
        busyCount = 0;
        showBusy(false);
    }

    /**
     * presenter will call this if there's an error
     *
     * @param throwable
     */
    public void onFailure(@NonNull Throwable throwable) {
        setNothingIsBusy();

        DialogInterface.OnClickListener listener = null;
        // This demonstrates on possible (rudimentary) way to handle the case when the server is
        // unavailable.  If we are thrown a ConnectException, rather than having a series of
        // errors, or a crash downstream, we redirect back to the login page... which will also
        // error with a ConnectException until the server is available.  There are certainly more
        // elegant ways to deal with the ConnectException - but this will at least avoid crashes.
        if (throwable instanceof ConnectException) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Intent intent = new Intent(getApplicationContext(), LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            };
        }

        sampleUtils.handleError(this, throwable, listener);
    }

    protected void setTextViewError(final boolean bIsError,
                                    @NonNull final TextInputLayout view,
                                    @StringRes final int resId) {
        if (bIsError) {
            setSomethingIsBusy(false);
            view.setError(getString(resId));
            view.requestFocus();
        }
        else {
            view.setError(null);
        }
    }

    // convenience method to return to services list
    protected void goHome() {
        setNothingIsBusy();
        final Intent intent = HomeActivity.makeIntent(this);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // canned listener which will call goHome()
    protected DialogInterface.OnClickListener goHomeClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goHome();
            }
        };
    }

    // canned listener which will finish current activity
    protected DialogInterface.OnClickListener finishClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        };
    }

    // common handling to open a given file attachment
    public void setAttachment(@NonNull final FileAttachment fileAttachment) {
        fileUtils.openAttachment(this, fileAttachment);
    }

    // show an error and then go home
    public void handleFatalError(final SDKError sdkError) {
        setNothingIsBusy();
        sampleUtils.handleError(this, sdkError, goHomeClickListener());
    }

    // show an error and then go home
    public void handleFatalError(final Throwable throwable) {
        setNothingIsBusy();
        sampleUtils.handleError(this, throwable, goHomeClickListener());
    }

    // show an error and finish
    public void handleErrorAndFinish(final SDKError sdkError) {
        setNothingIsBusy();
        sampleUtils.handleError(this, sdkError, finishClickListener());
    }

    protected SampleNamedNothingSelectedSpinnerAdapter populateAdapter(List<? extends NamedSDKEntity> list,
                                                                       @LayoutRes final int resLayout) {
        return new SampleNamedNothingSelectedSpinnerAdapter(this, list, resLayout);
    }

    protected SampleNamedNothingSelectedSpinnerAdapter populateAdapter(List<? extends NamedSDKEntity> list,
                                                                       @LayoutRes final int resLayout,
                                                                       boolean displayNothingSelectedOnEmpty) {
        return new SampleNamedNothingSelectedSpinnerAdapter(this, list, resLayout, displayNothingSelectedOnEmpty);
    }

    protected boolean showBackButton() {
        return true;
    }
}
