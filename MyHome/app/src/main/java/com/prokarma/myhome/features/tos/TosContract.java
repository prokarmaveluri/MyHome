package com.prokarma.myhome.features.tos;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.entities.Tos;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosContract {
    interface View {
        void onTosInfoSuccess(Response<Tos> response);

        void onTosInfoFailed(Response<Tos> response);

        void onTosInfoFailed(Throwable throwable);
    }

    interface Interactor {
        void acceptToS();

        void getUsersTosInfo();
    }

    interface InteractorOutput {
        void receivedUsersTosInfoSuccess(Response<Tos> response);

        void receivedUsersTosInfoFailed(Response<Tos> response);

        void receivedUsersTosInfoFailed(Throwable throwable);
    }

    interface Presentor extends BasePresenter {
        void onAcceptClicked();

        void onCancelClicked();
    }

    interface Router {
        void finishTos();
    }
}
