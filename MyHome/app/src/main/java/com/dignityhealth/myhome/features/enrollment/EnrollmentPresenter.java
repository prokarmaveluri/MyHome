package com.dignityhealth.myhome.features.enrollment;

import android.app.Activity;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.networking.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cmajji on 4/26/17.
 */

public class EnrollmentPresenter implements EnrollmentInteractor.Presenter {

    private EnrollmentInteractor.View mView;
    private Activity mContext;

    public EnrollmentPresenter(EnrollmentInteractor.View view, Activity context) {
        mView = view;
        mContext = context;
        mView.setPresenter(this);
    }


    @Override
    public void start() {
        mView.showView(true);
    }

    @Override
    public void enrollUser(EnrollmentRequest request) {

        mView.showView(false);
        mView.showProgress(true);

        registerUser(request);
    }

    @Override
    public void openLoginPage() {
        mView.showView(false);
    }
    private void registerUser(EnrollmentRequest request) {
        NetworkManager.getInstance().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mView.showEnrollmentStatus(mContext.getString(R.string.registered_successfully));
                    openLoginPage();
                } else {
                    mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                }
                mView.showView(true);
                mView.showProgress(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mView.showEnrollmentStatus(mContext.getString(R.string.something_went_wrong));
                mView.showView(true);
                mView.showProgress(false);
            }
        });
    }
}
