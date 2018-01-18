package com.prokarma.myhome.features.login.fingerprint;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.prokarma.myhome.R;

/**
 * Helps in implementing the FingerprintAuthenticationDialogFragment result.
 */
@TargetApi(Build.VERSION_CODES.M)
public interface FingerprintDialogCallbackInterface {

    void onFingerprintAuthentication();

    void onFingerprintAuthenticationCancel();

    void onFingerprintAuthenticationUsePassword();
}
