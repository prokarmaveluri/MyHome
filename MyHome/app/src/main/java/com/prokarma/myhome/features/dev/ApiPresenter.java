package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.SessionUtil;

import java.util.ArrayList;

/**
 * Created by kwelsh on 1/31/18.
 */

public class ApiPresenter implements ApiContract.Presenter, ApiContract.InteractorOutput {
    final ApiContract.View view;
    final ApiContract.Router router;
    final ApiContract.Interactor interactor;

    public ApiPresenter(ApiContract.View view) {
        this.view = view;
        this.router = new ApiRouter((BaseFragment) view);
        this.interactor = new ApiInteractor(this);
    }


    @Override
    public void onSignoutButtonPressed(BaseFragment fragment) {
        SessionUtil.logout(fragment.getActivity(), null);

        if (fragment.getActivity() != null) {
            router.exitApp();
        }
    }

    @Override
    public void requestingApiOptions() {
        interactor.getApiOptions();
    }

    @Override
    public void receivedApiOptions(ArrayList<ApiOption> apiOptions) {
        view.showApiOptions(apiOptions);
    }
}
