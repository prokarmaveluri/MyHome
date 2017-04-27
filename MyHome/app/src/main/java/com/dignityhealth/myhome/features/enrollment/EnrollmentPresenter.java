package com.dignityhealth.myhome.features.enrollment;

/**
 * Created by cmajji on 4/26/17.
 */

public class EnrollmentPresenter implements EnrollmentInteractor.Presenter {

    private EnrollmentInteractor.View mView;

    public EnrollmentPresenter(EnrollmentInteractor.View view) {
        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void start() {
        mView.showView(true);
    }

    @Override
    public void openLoginPage() {
        System.out.println("openLoginPage");
    }

    @Override
    public void enrollUser() {
        System.out.println("enrollUser");
    }
}
