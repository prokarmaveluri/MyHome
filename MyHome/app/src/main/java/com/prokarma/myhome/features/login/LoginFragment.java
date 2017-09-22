package com.prokarma.myhome.features.login;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.databinding.FragmentLoginBinding;
import com.prokarma.myhome.features.contact.ContactUsActivity;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.utils.ValidateInputsOnFocusChange;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import timber.log.Timber;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class LoginFragment extends Fragment implements LoginInteractor.View {

    private LoginInteractor.Presenter presenter;
    private FragmentLoginBinding binder;
    private String sessionToken;

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

        //Automatically populate developer builds with a test account
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("developer")) {
            // add your debugging credentials
        }

        binder.email.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.email, binder.emailLayout,
                getActivity().getApplicationContext(), Constants.INPUT_TYPE.EMAIL_LOGIN));
//        binder.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.password,
//                Constants.INPUT_TYPE.PASSWORD));

        binder.email.addTextChangedListener(new LoginTextWatcher());
        binder.password.addTextChangedListener(new LoginTextWatcher());

        String text = getResources().getString(R.string.enroll_now);
        SpannableString content = new SpannableString(text + " Enroll Now");
        content.setSpan(new UnderlineSpan(), text.length() + 1, content.length(), 0);
        binder.enrollNow.setText(content);

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
        if (null != presenter)
            presenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public void showEnrollmentStatus(String status) {

        checkNotNull(status);

        if (null != getActivity())
            Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void SignInSuccess() {
        mHandler.sendEmptyMessage(ACTION_FINISH);
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
                        Toast.makeText(getActivity(), R.string.no_network_msg,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.forgot_password:
                    if (ConnectionUtil.isConnected(getActivity())) {
                        startForgotPasswordActivity();
                    }
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
            return null;
        }

        if (!CommonUtil.isValidPassword(binder.password.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password), Toast.LENGTH_LONG).show();
            return null;
        }

        if (AuthManager.getInstance().isMaxFailureAttemptsReached() &&
                !AuthManager.getInstance().isTimeStampGreaterThan5Mins()) {
            Toast.makeText(getActivity(), getString(R.string.max_login_tries_reached),
                    Toast.LENGTH_LONG).show();
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
                        ProfileManager.getProfileInfo();
                        NetworkManager.getInstance().getMyAppointments();
                        AuthManager.getInstance().setCount(0);
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
                        Toast.makeText(getActivity(), getString(R.string.failure_msg),
                                Toast.LENGTH_LONG).show();
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


    // Generate Code Verifier and Code Challenge for new login

    private String codeVerifier;
    private String codeChallenge;

    /**
     * Base64 encoding settings used for generated code verifiers.
     */
    private static final int PKCE_BASE64_ENCODE_SETTINGS =
            Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE;

    /**
     * The default entropy (in bytes) used for the code verifier.
     */
    public static final int DEFAULT_CODE_VERIFIER_ENTROPY = 64;

    /**
     * The minimum permitted entropy (in bytes) for use with
     * {@link #generateRandomCodeVerifier(SecureRandom, int)}.
     */
    public static final int MIN_CODE_VERIFIER_ENTROPY = 32;

    /**
     * The maximum permitted entropy (in bytes) for use with
     * {@link #generateRandomCodeVerifier(SecureRandom, int)}.
     */
    public static final int MAX_CODE_VERIFIER_ENTROPY = 96;


    public static String deriveCodeVerifierChallenge(String codeVerifier) {
        try {
            MessageDigest sha256Digester = MessageDigest.getInstance("SHA-256");
            sha256Digester.update(codeVerifier.getBytes("ISO_8859_1"));
            byte[] digestBytes = sha256Digester.digest();
            return Base64.encodeToString(digestBytes, PKCE_BASE64_ENCODE_SETTINGS);
        } catch (NoSuchAlgorithmException e) {
            Log.w("CodeVerifierChallenge", "SHA-256 is not supported on this device! Using plain challenge", e);
            return codeVerifier;
        } catch (UnsupportedEncodingException e) {
            Log.e("CodeVerifierChallenge", "ISO-8859-1 encoding not supported on this device!", e);
            throw new IllegalStateException("ISO-8859-1 encoding not supported", e);
        }
    }

    /**
     * Generates a random code verifier string using {@link SecureRandom} as the source of
     * entropy, with the default entropy quantity as defined by
     * {@link #DEFAULT_CODE_VERIFIER_ENTROPY}.
     */
    public static String generateRandomCodeVerifier() {
//        return generateRandomCodeVerifier(new SecureRandom(), DEFAULT_CODE_VERIFIER_ENTROPY);
        return generateRandomCodeVerifier(new SecureRandom(), 32);
    }

    /**
     * Generates a random code verifier string using the provided entropy source and the specified
     * number of bytes of entropy.
     */
    public static String generateRandomCodeVerifier(SecureRandom entropySource, int entropyBytes) {
        byte[] randomBytes = new byte[entropyBytes];
        entropySource.nextBytes(randomBytes);
        return Base64.encodeToString(randomBytes, PKCE_BASE64_ENCODE_SETTINGS);
    }
}

