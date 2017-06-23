package com.prokarma.myhome.features.enrollment;

import com.prokarma.myhome.app.mvp.BasePresenter;
import com.prokarma.myhome.app.mvp.BaseView;

/**
 * Created by cmajji on 4/26/17.
 */

public interface EnrollmentInteractor {

    interface View extends BaseView<Presenter> {

        void showProgress(boolean show);

        void showView(boolean show);

        void showEnrollmentStatus(String status);
    }

    interface Presenter extends BasePresenter{

        void openLoginPage();

        void enrollUser(EnrollmentRequest request);
    }
}
