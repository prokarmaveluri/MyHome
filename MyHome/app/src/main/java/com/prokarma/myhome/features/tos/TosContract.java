package com.prokarma.myhome.features.tos;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosContract {
    interface View {
        void onRegisterUserSuccess(Response<Void> response);

        void onRegisterUserFailed(Response<Void> response);

        void onRegisterUserFailed(Throwable throwable);

        void onTosInfoSuccess(Response<Tos> response);

        void onTosInfoFailed(Response<Tos> response);

        void onTosInfoFailed(Throwable throwable);
    }

    interface Interactor {
        void registerUser(EnrollmentRequest request);

        void getUsersTosInfo();
    }

    interface InteractorOutput {
        void registerUserSuccess(Response<Void> response);

        void registerUserFailed(Response<Void> response);

        void registerUserFailed(Throwable throwable);

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

        void startLoginPage(String userName, String password);
    }
}
