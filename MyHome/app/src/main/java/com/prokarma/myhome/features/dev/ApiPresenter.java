package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.view.View;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.entities.ApiOption;
import com.prokarma.myhome.utils.SessionUtil;

import java.util.ArrayList;

/**
 * Created by kwelsh on 1/31/18.
 */

public class ApiPresenter implements ApiContract.Presenter, ApiContract.InteractorOutput {
    BaseFragment fragment;
    ApiContract.View view;
    ApiContract.Router router;
    ApiContract.Interactor interactor;

    public ApiPresenter(final Context context, final BaseFragment fragment, final View view) {
        this.fragment = fragment;
        this.view = new ApiView(context, view, this);
        this.router = new ApiRouter(fragment);
        this.interactor = new ApiInteractor(this);
    }

    @Override
    public void onCreate() {
        interactor.getApiOptions();
    }

    @Override
    public void onDestroy() {
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onSignoutButtonPressed() {
        SessionUtil.logout(fragment.getActivity(), null);

        if (fragment != null) {
            router.exitApp();
        }
    }

    @Override
    public void receivedApiOptions(ArrayList<ApiOption> apiOptions) {
        view.showApiOptions(apiOptions);
    }
}
