package com.prokarma.myhome.features.enrollment;

import android.app.Activity;
import android.content.Intent;

import com.prokarma.myhome.features.enrollment.sq.SQListActivity;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.utils.Constants;

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

        mView.showView(true);
        mView.showProgress(false);

        Intent intent = SQListActivity.getSQListActivityIntent(mContext);
        intent.putExtra(Constants.ENROLLMENT_REQUEST, request);
        mContext.startActivity(intent);
    }

    @Override
    public void openLoginPage() {
        mView.showView(true);
        if (null != mContext) {
            Intent intent = LoginActivity.getLoginIntent(mContext);
            mContext.startActivity(intent);
            mContext.finish();
        }
    }
}
