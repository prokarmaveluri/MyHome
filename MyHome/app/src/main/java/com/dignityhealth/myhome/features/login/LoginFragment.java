package com.dignityhealth.myhome.features.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.FragmentLoginBinding;
import com.dignityhealth.myhome.features.contact.ContactUsActivity;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordActivity;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.ValidateInputsOnFocusChange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binder.email.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.email,
                Constants.INPUT_TYPE.EMAIL));
//        binder.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binder.password,
//                Constants.INPUT_TYPE.PASSWORD));

        binder.email.addTextChangedListener(new LoginTextWatcher());
        binder.password.addTextChangedListener(new LoginTextWatcher());

        String text = getResources().getString(R.string.enroll_now);
        SpannableString content = new SpannableString(text + " Enroll Now");
        content.setSpan(new UnderlineSpan(), text.length() + 1, content.length(), 0);
        binder.enrollNow.setText(content);

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
    public void fetchIdToken(String sessionToken) {
        loadWebView(sessionToken);
    }

    public class LoginViewClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.enroll_now:
                    presenter.openSignUpPage();
                    break;
                case R.id.log_in_button:
                    if (ConnectionUtil.isConnected(getActivity())) {
                        showView(false);
                        LoginRequest request = getRequest();
                        if (null != request)
                            presenter.signIn(request);
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

    private LoginRequest getRequest() {
        LoginRequest request = null;

        if (!CommonUtil.isValidEmail(binder.email.getText().toString())) {
            binder.email.setError(getString(R.string.valid_email));
            return null;
        }

        if (!CommonUtil.isValidPassword(binder.password.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password), Toast.LENGTH_LONG).show();
            return null;
        }
        LoginRequest.Options options = new LoginRequest.Options(true, true);
        request = new LoginRequest(binder.email.getText().toString(),
                binder.password.getText().toString(), options);

        return request;
    }

    private void loadWebView(String sessionToken) {
        showView(true);
        this.sessionToken = sessionToken;
        binder.webViewRedirect.clearCache(true);
        binder.webViewRedirect.clearHistory();
        binder.webViewRedirect.clearSslPreferences();

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {

                }
            });
        } else {
            cookieManager.removeAllCookie();
        }

        binder.webViewRedirect.setWebViewClient(new RedirectClient());
        binder.webViewRedirect.getSettings().setJavaScriptEnabled(true);
        binder.webViewRedirect.loadUrl(Constants.auth2Url + sessionToken);
//        thread.start();
    }

    private class RedirectClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            showProgress(false);
            String cookies = CookieManager.getInstance().getCookie(url);
            Timber.i("Cookie "+cookies);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String token = parseIDToken(url);

            String cookies = CookieManager.getInstance().getCookie(url);

            Timber.i("Cookie "+cookies);
            if (null != token) {
                AuthManager.setBearerToken(token);
                mHandler.sendEmptyMessageDelayed(ACTION_FINISH, 100);
            } else {
                mHandler.sendEmptyMessageDelayed(TOKEN_ERROR, 100);
            }
            showProgress(false);
            showView(true);
            presenter.createSession(sessionToken);
            return false;
        }
    }

    private String parseIDToken(String url) {

        int index = url.indexOf("id_token=");
        if (-1 != index) {
            String token = url.substring(index + "id_token=".length(), url.indexOf("&"));
            Timber.i("Session id_token ** " + token);
            return token;
        }
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_FINISH:
                    //received token and stored it in AuthManager. start nav activity
                    Intent intentHome = new Intent(getActivity(), NavigationActivity.class);
                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left);
                    ActivityCompat.startActivity(getActivity(), intentHome, options.toBundle());
                    getActivity().finish();
                    break;
                case TOKEN_ERROR:
                    showProgress(false);
                    AuthManager.setBearerToken(null);
                    Toast.makeText(getActivity(), getString(R.string.sign_in_failure_msg),
                            Toast.LENGTH_LONG).show();
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

    private boolean isAllInputsValid() {
        if (CommonUtil.isValidEmail(binder.email.getText().toString()) &&
                CommonUtil.isValidPassword(binder.password.getText().toString())) {
            return true;
        }
        return false;
    }

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

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binder.logInButton.setBackgroundResource(R.drawable.button_enabled);
            binder.logInButton.setTextColor(Color.WHITE);
        } else {
            binder.logInButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binder.logInButton.setTextColor(Color.GRAY);
        }
    }

    private void drawableClickEvent() {
        binder.password.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_RIGHT = 2;

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if ((int) event.getRawX() >= (binder.password.getRight() -
                                    binder.password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

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
                        return false;
                    }
                });
    }

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

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            fetchIdTokenUrlConnection();
        }
    });

    private void fetchIdTokenUrlConnection() {

        try {
            URL url = new URL(Constants.auth2Url + sessionToken);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                urlConnection.getResponseCode();
                urlConnection.getRequestMethod();
                URL redirectUrl = urlConnection.getURL();

                String token = parseIDToken(redirectUrl.toString());
                Timber.i("Session, redirectUrl " + redirectUrl);
                Timber.i("Session, id token " + token);
                if (null != token) {
                    AuthManager.setBearerToken(token);
                    mHandler.sendEmptyMessage(ACTION_FINISH);
                } else {
                    mHandler.sendEmptyMessage(TOKEN_ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
