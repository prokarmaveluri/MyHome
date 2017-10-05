package com.prokarma.myhome.features.login;

import com.prokarma.myhome.app.mvp.BasePresenter;
import com.prokarma.myhome.app.mvp.BaseView;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;

/**
 * Created by cmajji on 4/27/17.
 */

public interface LoginInteractor {

    interface View extends BaseView<Presenter> {

        void showProgress(boolean show);

        void showView(boolean show);

        android.view.View getRootView();

        void showEnrollmentStatus(String status);

        void SignInSuccess();

        void SignInSuccessBut30days();

        void acceptTermsOfService(boolean isTermsOfServiceAccepted);
    }

    interface Presenter extends BasePresenter {

        void openSignUpPage();

        void signIn(SignInRequest request);

    }

}
