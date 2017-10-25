package com.prokarma.myhome.features.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.features.enrollment.EnrollmentActivity;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.DateUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 4/27/17.
 */

public class LoginPresenter implements LoginInteractor.Presenter {

    private LoginInteractor.View mView;
    private Activity mContext;

    public LoginPresenter(LoginInteractor.View view, Activity context) {
        mView = view;
        mContext = context;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showView(true);
    }


    @Override
    public void signIn(SignInRequest request) {
        CommonUtil.hideSoftKeyboard(mContext, mView.getRootView());
        mView.showView(true);
        mView.showProgress(true);

        if (!ConnectionUtil.isConnected(mContext)) {
            Toast.makeText(mContext, R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }

        login(request);
    }

    @Override
    public void openSignUpPage() {
        mView.showView(false);
        if (null != mContext) {
            Intent intent = EnrollmentActivity.getEnrollmentIntent(mContext);
            mContext.startActivity(intent);
        }
    }

    private void login(final SignInRequest request) {
        NetworkManager.getInstance().signIn(request).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                try {
                    if (response.isSuccessful() && response.body().getValid()) {
                        // get id_token & session id
                        AuthManager.getInstance().setCount(0);
                        ProfileManager.clearSessionData();
                        AppPreferences.getInstance().setLongPreference("IDLE_TIME", 0);
                        AuthManager.getInstance().setSessionId(response.body().getResult().getSessionId());
                        AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                        AuthManager.getInstance().setRefreshToken(response.body().getResult().getRefreshToken());

                        ProfileManager.setProfile(response.body().getResult().getUserProfile());
                        CryptoManager.getInstance().saveToken();
                        if (null != response.body().getResult().getUserProfile() &&
                                !response.body().getResult().getUserProfile().isVerified &&
                                DateUtil.isMoreThan30days(response.body().getResult().getUserProfile().createdDate)) {

                            mView.SignInSuccessBut30days();
                        } else if (null != response.body().getResult().getUserProfile() &&
                                !response.body().getResult().getUserProfile().isTermsAccepted) {
                            mView.acceptTermsOfService(false);

                        } else {
                            mView.SignInSuccess();
                        }
                    } else {
                        AuthManager.getInstance().setFailureAttempt();

                        if (response != null && response.body() != null && !response.body().getValid()) {
                            mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                        } else {
                            ApiErrorUtil.getInstance().signInError(mContext, mView.getRootView(), response);
                        }

                        mView.showProgress(false);
                        Timber.e("Response, but not successful?\n" + response);
                        mView.showView(true);
                    }
                } catch (NullPointerException ex) {
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                ApiErrorUtil.getInstance().signInFailed(mContext, mView.getRootView(), t);
                //mView.showEnrollmentStatus(mContext.getString(R.string.failure_msg));
                mView.showView(true);
                mView.showProgress(false);
                Timber.e("Login failure");
                Timber.e("Throwable = " + t);
            }
        });
    }
}
