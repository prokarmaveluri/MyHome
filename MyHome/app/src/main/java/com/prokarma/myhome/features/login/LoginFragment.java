package com.prokarma.myhome.features.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.OptionsActivity;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.databinding.FragmentLoginBinding;
import com.prokarma.myhome.features.contact.ContactUsActivity;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.login.fingerprint.FingerprintDialogCallbackInterface;
import com.prokarma.myhome.features.login.fingerprint.FingerprintSignIn;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordActivity;
import com.prokarma.myhome.features.login.verify.EmailVerifyActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.utils.ValidateInputsOnFocusChange;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static com.prokarma.myhome.features.settings.TouchIDFragment.TOUCH_ID_KEY;

public class LoginFragment extends Fragment implements LoginInteractor.View, FingerprintDialogCallbackInterface {

    private LoginInteractor.Presenter presenter;
    private FragmentLoginBinding binder;

    public static final int VERIFY_EMAIL = 90;
    private static final int ACTION_FINISH = 100;
    private static final int TOKEN_ERROR = 200;
    private static boolean showPassword = false;

    public static String EMAIL_ID_KEY = "EMAIL_ID";

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        binder.email.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.email, binder.emailLayout,
                getActivity().getApplicationContext(), Constants.INPUT_TYPE.EMAIL_LOGIN));
//        binder.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.password,
//                Constants.INPUT_TYPE.PASSWORD));
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            ViewCompat.setAccessibilityDelegate(binder.email, new EmailAccessibilityDelegate());
        }

        binder.email.addTextChangedListener(new LoginTextWatcher());
        binder.password.addTextChangedListener(new LoginTextWatcher());

        String text = getResources().getString(R.string.enroll_now);
        SpannableString content = new SpannableString(text + " Enroll Now");
        content.setSpan(new UnderlineSpan(), text.length() + 1, content.length(), 0);
        binder.enrollNow.setText(content);

        SpannableString fpContent = new SpannableString(getResources().getString(R.string.forgot_password));
        fpContent.setSpan(new UnderlineSpan(), 0, fpContent.length(), 0);
        binder.forgotPassword.setText(fpContent);

        SpannableString helpContent = new SpannableString(getResources().getString(R.string.login_help));
        helpContent.setSpan(new UnderlineSpan(), 0, helpContent.length(), 0);
        binder.loginHelp.setText(helpContent);


        String email = AppPreferences.getInstance().getPreference("EMAIL_PREF");
        if (email != null && !email.trim().isEmpty())
            binder.email.setText(email);
        drawableClickEvent();
        binder.setHandlers(new LoginViewClickEvent());
        return binder.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != presenter) {
            presenter.start();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AppPreferences.getInstance().getBooleanPreference(TOUCH_ID_KEY)) {
            FingerprintSignIn fingerprint = new FingerprintSignIn(getActivity(), FingerprintSignIn.DEFAULT_KEY_NAME);
            fingerprint.initiateFingerprint();
            if (fingerprint.isSupportFingerprint() && fingerprint.isDeviceConfiguredFingerprint()) {
                return;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setPresenter(LoginInteractor.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            binder.loginProgress.setVisibility(View.VISIBLE);
        } else {
            binder.loginProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showView(boolean show) {
        if (show) {
            binder.logInButton.setEnabled(true);
            binder.enrollNow.setEnabled(true);
        } else {
            binder.logInButton.setEnabled(false);
            binder.enrollNow.setEnabled(false);
        }
    }

    @Override
    public View getRootView() {
        return binder.getRoot();
    }

    @Override
    public void showEnrollmentStatus(String status) {

        checkNotNull(status);

        if (null != getActivity())
            CommonUtil.showToast(getActivity(), status);
    }

    @Override
    public void SignInSuccess() {
        getHandler().sendEmptyMessage(ACTION_FINISH);
    }

    @Override
    public void SignInSuccessBut30days() {
        binder.loginProgress.setVisibility(View.GONE);
        startVerify();
    }

    @Override
    public void acceptTermsOfService(boolean isTermsOfServiceAccepted) {
        startTermsOfServiceActivity();
    }

    private void startTermsOfServiceActivity() {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                R.anim.slide_in_right, R.anim.slide_out_left);

        NavigationActivity.setActivityTag(Constants.ActivityTag.TERMS_OF_SERVICE);
        Intent intentTos = new Intent(getActivity(), OptionsActivity.class);
        ActivityCompat.startActivity(getActivity(), intentTos, options.toBundle());
    }

    private void startVerify() {
        Intent intentVerify = EmailVerifyActivity.getEmailVerifyIntent(getActivity());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                R.anim.slide_in_right, R.anim.slide_out_left);
        ActivityCompat.startActivityForResult(getActivity(), intentVerify, VERIFY_EMAIL, options.toBundle());
        getActivity().finish();
    }

    public class LoginViewClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.enroll_now:
                    presenter.openSignUpPage();
                    break;
                case R.id.log_in_button:
                    if (ConnectionUtil.isConnected(getActivity())) {
                        SignInRequest request = getRequest();
                        if (null != request) {
                            AppPreferences.getInstance().setPreference("EMAIL_PREF", binder.email.getText().toString());
                            showView(false);
                            TealiumUtil.trackEvent(Constants.SIGN_IN_EVENT, null);
                            presenter.signIn(request);

                        }
                    } else {
                        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
                    }
                    break;
                case R.id.forgot_password:
                    startForgotPasswordActivity();
                    break;
                case R.id.login_help:
                    startContactUsActivity();
                    break;
            }
        }
    }

    @Nullable
    private SignInRequest getRequest() {
        SignInRequest request = null;

        if (!CommonUtil.isValidEmail(binder.email.getText().toString())) {
            binder.emailLayout.setError(getString(R.string.valid_email));
            binder.email.requestFocus();
            return null;
        }

        if (!CommonUtil.isValidPassword(binder.password.getText().toString())) {
            CommonUtil.showToast(getActivity(), getString(R.string.valid_password));
            binder.password.requestFocus();
            return null;
        }

        if (AuthManager.getInstance().isMaxFailureAttemptsReached() &&
                !AuthManager.getInstance().isTimeStampGreaterThan5Mins()) {
            CommonUtil.showToast(getActivity(), getString(R.string.max_login_tries_reached));
            return null;
        }
        LoginRequest.Options options = new LoginRequest.Options(true, true);
        request = new SignInRequest(binder.email.getText().toString(),
                binder.password.getText().toString());

        return request;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_FINISH:
                    //received token and stored it in AuthManager. start nav activity
                    if (isAdded()) {
                        //  Pre- load profile and appointment
                        //ProfileManager.getProfileInfo();
                        NetworkManager.getInstance().getMyAppointments();
                        AuthManager.getInstance().setCount(0);

                        AppPreferences.getInstance().setBooleanPreference("auto_signin", true);
                        Intent intentHome = new Intent(getActivity(), NavigationActivity.class);
                        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left);
                        ActivityCompat.startActivity(getActivity(), intentHome, options.toBundle());
                        getActivity().finish();
                    }

                    break;
                case TOKEN_ERROR:
                    if (isAdded()) {
                        showProgress(false);
                        AuthManager.getInstance().setBearerToken(null);
                        CommonUtil.showToast(getActivity(), getString(R.string.failure_msg));
                    }
                    break;
            }
        }
    };

    private void startForgotPasswordActivity() {
        Intent intent = ForgotPasswordActivity.getForgotPasswordIntent(getActivity());
        String email = binder.email.getText().toString();
        if (CommonUtil.isValidEmail(email))
            intent.putExtra(EMAIL_ID_KEY, email);
        startActivity(intent);
    }

    private void startContactUsActivity() {
        Intent intent = ContactUsActivity.getContactUsIntent(getActivity());
        startActivity(intent);
    }

    /**
     * validate inputs for login
     *
     * @return
     */
    private boolean isAllInputsValid() {
        if (CommonUtil.isValidEmail(binder.email.getText().toString()) && !binder.password.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * text watcher for input fields
     */
    private class LoginTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isAllInputsValid()) {
                updateButtonState(true);
            } else {
                updateButtonState(false);
            }
        }
    }

    /**
     * update login button state
     *
     * @param isEnabled
     */
    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binder.logInButton.setBackgroundResource(R.drawable.button_enabled);
            binder.logInButton.setTextColor(Color.WHITE);
        } else {
            binder.logInButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binder.logInButton.setTextColor(Color.GRAY);
        }
    }

    /**
     * register password drawable click event
     */
    private void drawableClickEvent() {
        binder.password.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        try {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int[] locationOnScreen = new int[2];
                                binder.password.getLocationOnScreen(locationOnScreen);
                                int touchXCoordinate = (int) event.getRawX() - locationOnScreen[0];
                                if (touchXCoordinate >= (binder.password.getRight() - binder.password.getTotalPaddingRight()) &&
                                        touchXCoordinate <= (binder.password.getRight() - binder.password.getPaddingRight())) {

                                    showPassword = !showPassword;

                                    if (showPassword) {
                                        binder.password.setTransformationMethod(null);
                                        updateDrawable(R.mipmap.hide_password);
                                    } else {
                                        binder.password.setTransformationMethod(new PasswordTransformationMethod());
                                        updateDrawable(R.mipmap.show_password);
                                    }
                                    return true;
                                }
                            }
                        } catch (NullPointerException ex) {
                            Timber.i("NullPointerException in drawableClickEvent ");
                            return false;
                        }
                        return false;
                    }
                });
    }

    /**
     * update password drawable
     *
     * @param res
     */
    @SuppressWarnings("deprecation")
    private void updateDrawable(int res) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(res, getActivity().getTheme());
        } else {
            drawable = getResources().getDrawable(res);
        }
        if (null != drawable) {
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, w, h);
        }
        binder.password.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_EMAIL) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
    }

    private static class LoginHandler extends Handler {
        private final WeakReference<LoginFragment> mLoginFragment;

        private LoginHandler(LoginFragment loginFragment) {
            mLoginFragment = new WeakReference<LoginFragment>(loginFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginFragment loginFragment = mLoginFragment.get();
            if (loginFragment != null) {
                switch (msg.what) {
                    case ACTION_FINISH:
                        //received token and stored it in AuthManager. start nav activity
                        if (loginFragment.isAdded()) {
                            //  Pre- load profile and appointment
                            //ProfileManager.getProfileInfo();
                            NetworkManager.getInstance().getMyAppointments();
                            AuthManager.getInstance().setCount(0);

                            AppPreferences.getInstance().setBooleanPreference("auto_signin", true);
                            Intent intentHome = new Intent(loginFragment.getActivity(), NavigationActivity.class);
                            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(loginFragment.getActivity(), R.anim.slide_in_right, R.anim.slide_out_left);
                            ActivityCompat.startActivity(loginFragment.getActivity(), intentHome, options.toBundle());
                            loginFragment.getActivity().finish();
                        }

                        break;
                    case TOKEN_ERROR:
                        if (loginFragment.isAdded()) {
                            loginFragment.showProgress(false);
                            AuthManager.getInstance().setBearerToken(null);
                            AuthManager.getInstance().setAmWellToken(null);
                            CommonUtil.showToast(loginFragment.getActivity(), loginFragment.getString(R.string.failure_msg));
                        }
                        break;
                }
            }
        }
    }

    private Handler getHandler() {
        return new LoginHandler(this);
    }

    @Override
    public void onFingerprintAuthentication() {
        Timber.d("login. onFingerprintAuthentication ");
        refreshToken();
    }

    @Override
    public void onFingerprintAuthenticationCancel() {
        Timber.d("login. onFingerprintAuthenticationCancel ");
        getActivity().finish();
    }

    @Override
    public void onFingerprintAuthenticationUsePassword() {
        Timber.d("login. onFingerprintAuthenticationUsePassword ");
    }

    private void refreshToken() {
        if (CryptoManager.getInstance().getToken() != null) {
            Timber.d("login. Token not null ");
            refreshAccessToken(CryptoManager.getInstance().getToken());
        } else {
            Timber.d("login. refresh failed ");
            binder.loginProgress.setVisibility(View.GONE);
            onRefreshFailed();
        }
    }

    /**
     * Refresh Auth tokens
     *
     * @param refreshToken
     */
    private void refreshAccessToken(final String refreshToken) {
        if (!ConnectionUtil.isConnected(getContext())) {
            CommonUtil.showToast(getContext(), this.getString(R.string.no_network_msg));
            binder.loginProgress.setVisibility(View.GONE);
            return;
        }
        binder.loginProgress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().signInRefresh(new RefreshRequest(refreshToken))
                .enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                        binder.loginProgress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body().getValid()) {
                            try {

                                AppPreferences.getInstance().setLongPreference("FETCH_TIME", System.currentTimeMillis());
                                AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                                AuthManager.getInstance().getUsersAmWellToken();
                                NetworkManager.getInstance().getSavedDoctors(getActivity().getApplicationContext(), binder.loginProgress);
                                CryptoManager.getInstance().saveToken(response.body().getResult().getRefreshToken());

                                ProfileManager.setProfile(response.body().getResult().getUserProfile());
                                NetworkManager.getInstance().getSavedDoctors(getActivity().getApplicationContext(), binder.loginProgress);

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
                        binder.loginProgress.setVisibility(View.GONE);
                        onRefreshFailed();
                    }
                });
    }


    private void onRefreshSuccess() {
        Timber.d("login. onRefreshSuccess ");
        try {
            //Pre- load profile and appointment
            //ProfileManager.getProfileInfo();
            NetworkManager.getInstance().getMyAppointments();
            AuthManager.getInstance().setCount(0);

            AppPreferences.getInstance().setBooleanPreference("auto_signin", true);
            Intent intentHome = new Intent(getContext(), NavigationActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            ActivityCompat.startActivity(getContext(), intentHome, options.toBundle());
            getActivity().finish();

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    private void onRefreshFailed() {
        Timber.d("login. onRefreshFailed ");
    }

    private static class EmailAccessibilityDelegate extends AccessibilityDelegateCompat {

        public EmailAccessibilityDelegate() {
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host,
                                                      AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            try {
                ((EditText)host).setSelection(info.getText().length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

