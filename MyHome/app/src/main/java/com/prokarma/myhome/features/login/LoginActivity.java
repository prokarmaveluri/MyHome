package com.prokarma.myhome.features.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.databinding.ActivityLoginBinding;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;


/*
 * Activity to login.
 *
 * Created by cmajji on 4/26/17.
 */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    public static String FAILURE_COUNT = "FAILURE_COUNT";
    public static String FAILURE_TIME_STAMP = "FAILURE_TIME_STAMP";

    /*
     * Get an intent for login activity.
     */
    public static Intent getLoginIntent(Context context) {

        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        LoginFragment fragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.loginFrame.getId(), fragment).commit();

//        boolean enrollmentSuccess = getIntent().getBooleanExtra("ENROLL_SUCCESS", false);
//        if (enrollmentSuccess) {
//            TealiumUtil.trackEvent(Constants.ENROLLMENT_SUCCESS_EVENT, null);
//
//            EnrollmentSuccessDialog dialog = EnrollmentSuccessDialog.newInstance();
//            dialog.show(getSupportFragmentManager(), "EnrollmentSuccessDialog");
//        }

        NetworkManager.getInstance().setExpiryListener(null);
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
}
