package com.dignityhealth.myhome.features.login;

import com.dignityhealth.myhome.app.mvp.BasePresenter;
import com.dignityhealth.myhome.app.mvp.BaseView;

/**
 * Created by cmajji on 4/27/17.
 */

public interface LoginInteractor {

    interface View extends BaseView<Presenter>{

        void showProgress(boolean show);

        void showView(boolean show);

        void showEnrollmentStatus(String status);

        void fetchIdToken(String sessionToken);

    }

    interface Presenter extends BasePresenter{

        void openSignUpPage();

        void signIn(LoginRequest request);

        void createSession(String sessionToken);

    }

}
