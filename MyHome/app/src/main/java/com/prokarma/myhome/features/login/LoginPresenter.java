package com.prokarma.myhome.features.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.enrollment.EnrollmentActivity;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.ConnectionUtil;

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
                if (response.isSuccessful() && response.body().getValid()) {
                    // get id_token & session id
                    AuthManager.getInstance().setCount(0);
                    ProfileManager.clearSessionData();
                    AppPreferences.getInstance().setLongPreference("IDLE_TIME", 0);
//                    AuthManager.getInstance().setExpiresAt(response.body().getExpiresAt());
                    AuthManager.getInstance().setSessionId(response.body().getResult().getSessionId());
                    AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                    AuthManager.getInstance().setRefreshToken(response.body().getResult().getRefreshToken());
                    mView.SignInSuccess();
                } else {
                    AuthManager.getInstance().setFailureAttempt();
                    mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                    mView.showProgress(false);
                    Timber.e("Response, but not successful?\n" + response);
                    mView.showView(true);
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                mView.showEnrollmentStatus(mContext.getString(R.string.failure_msg));
                mView.showView(true);
                mView.showProgress(false);
                Timber.e("Login failure");
                Timber.e("Throwable = " + t);
            }
        });
    }
}
