package com.prokarma.myhome.features.tos;

import android.content.Context;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosPresenter implements TosContract.Presentor, TosContract.InteractorOutput {
    private Context context;
    private TosView view;
    private TosInteractor interactor;
    private TosRouter router;

    public TosPresenter(Context context, BaseFragment fragment, View masterView) {
        this.context = context;
        this.view = new TosView(context, masterView, this);
        this.interactor = new TosInteractor(this);
        this.router = new TosRouter(fragment);
    }

    @Override
    public void onCreate() {
        if (!ConnectionUtil.isConnected(context)) {
            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
        } else {
            interactor.getUsersTosInfo();
        }
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }


    @Override
    public void onAcceptClicked() {
        if (!ConnectionUtil.isConnected(context)) {
            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
        } else {
            interactor.acceptToS();
        }
    }

    @Override
    public void onCancelClicked() {
        router.finishTos();
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
