package com.dignityhealth.myhome.features.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.enrollment.EnrollmentActivity;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.ConnectionUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            mContext.finish();
        }
    }

    private void login(final LoginRequest request) {
        NetworkManager.getInstance().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // notification for testing.
                    mView.showEnrollmentStatus("Received Session token.");
                    // get id_token
                    mView.fetchIdToken(response.body().getSessionToken());
                } else {
                    mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                    mView.showProgress(false);
                }
                mView.showView(true);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                mView.showView(true);
                mView.showProgress(false);
            }
        });
    }
}