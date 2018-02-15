package com.prokarma.myhome.features.tos;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosPresenter implements TosContract.Presentor, TosContract.InteractorOutput {
    private Context context;
    private EnrollmentRequest request;
    private TosView view;
    private TosInteractor interactor;
    private TosRouter router;

    public TosPresenter(Context context, Activity activity, View masterView, final EnrollmentRequest request) {
        this.context = context;
        this.request = request;
        this.view = new TosView(context, masterView, this, request != null);
        this.interactor = new TosInteractor(this);
        this.router = new TosRouter(activity);
    }

    @Override
    public void onCreate() {
//        if (!ConnectionUtil.isConnected(context)) {
//            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
//        } else {
//            interactor.getUsersTosInfo();
//        }
    }

    @Override
    public void onDestroy() {
        context = null;
        request = null;
        view = null;
        interactor = null;
        router = null;
    }


    @Override
    public void onAcceptClicked() {
        if (!ConnectionUtil.isConnected(context)) {
            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
        } else if (request != null) {
            request.setHasAcceptedTerms(true);
            request.setSkipVerification(true); // need to discuss about the skip.
            interactor.registerUser(request);
        }
    }

    @Override
    public void onCancelClicked() {
        router.finishTos();
    }

    @Override
    public void registerUserSuccess(Response<Void> response) {
        view.onRegisterUserSuccess(response);
        router.startLoginPage(request.getEmail(), request.getPassword());
    }

    @Override
    public void registerUserFailed(Response<Void> response) {
        view.onRegisterUserFailed(response);
    }

    @Override
    public void registerUserFailed(Throwable throwable) {
        view.onRegisterUserFailed(throwable);
    }

    @Override
    public void receivedUsersTosInfoSuccess(Response<Tos> response) {
        view.onTosInfoSuccess(response);
    }

    @Override
    public void receivedUsersTosInfoFailed(Response<Tos> response) {
        view.onTosInfoFailed(response);
    }

    @Override
    public void receivedUsersTosInfoFailed(Throwable throwable) {
        view.onTosInfoFailed(throwable);
    }
}
