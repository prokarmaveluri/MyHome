package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.entities.ApiOption;

import java.util.ArrayList;

/**
 * Created by kwelsh on 2/01/18.
 */

public class ApiContract {
    interface View {
        void showApiOptions(ArrayList<ApiOption> apiOptions);
    }

    interface Presenter extends BasePresenter {
        void onSignoutButtonPressed();
    }

    interface Interactor {
        void getApiOptions();
    }

    interface InteractorOutput {
        void receivedApiOptions(ArrayList<ApiOption> apiOptions);
    }

    interface Router {
        void exitApp();
    }
}
