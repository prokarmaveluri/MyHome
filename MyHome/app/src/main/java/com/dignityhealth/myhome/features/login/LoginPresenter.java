package com.dignityhealth.myhome.features.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.enrollment.EnrollmentActivity;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionRequest;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionResponse;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.ConnectionUtil;

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
    public void signIn(LoginRequest request) {

        mView.showView(false);
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

    @Override
    public void createSession(String sessionToken) {
        mView.showView(false);
        getSessionId(sessionToken);
    }

    private void login(final LoginRequest request) {
        NetworkManager.getInstance().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // notification for testing.
                    mView.showEnrollmentStatus("Received Session token.");
                    // get id_token
                    AuthManager.setSessionToken(response.body().getSessionToken());
                    Timber.i("Session token : " + response.body().getSessionToken());
                    mView.fetchIdToken(response.body().getSessionToken());
//                    getSessionId(response.body().getSessionToken());
                } else {
                    mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                    mView.showProgress(false);
                    Timber.i("Response not successful");
                }
                mView.showView(true);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                mView.showView(true);
                mView.showProgress(false);
                Timber.i("Login failure");
            }
        });
    }

    public void getSessionId(String sessionToken) {
        CreateSessionRequest request = new CreateSessionRequest(sessionToken);
        NetworkManager.getInstance().createSession(request).enqueue(new Callback<CreateSessionResponse>() {
            @Override
            public void onResponse(Call<CreateSessionResponse> call, Response<CreateSessionResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("Session Id: " + response.body().getId());
                    AuthManager.setIdTokenForSignOut(response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<CreateSessionResponse> call, Throwable t) {
                Timber.i("Session Id: Failed" );
                Timber.i(mContext.getString(R.string.something_went_wrong));
                AuthManager.setIdTokenForSignOut(null);
            }
        });
    }
}
