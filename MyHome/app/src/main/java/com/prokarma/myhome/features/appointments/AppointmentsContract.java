package com.prokarma.myhome.features.appointments;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.entities.MyAppointmentsResponse;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsContract {
    interface View {
        void onAppointmentsSuccess(Response<MyAppointmentsResponse> response);

        void onAppointmentsFailed(Response<MyAppointmentsResponse> response);

        void onAppointmentsFailed(Throwable throwable);
    }

    interface Interactor {
        void getAppointments();
    }

    interface InteractorOutput {
        void receivedAppointmentsSuccess(Response<MyAppointmentsResponse> response);

        void receivedAppointmentsFailed(Response<MyAppointmentsResponse> response);

        void receivedAppointmentsFailed(Throwable throwable);
    }

    interface Presentor extends BasePresenter {

    }

    interface Router {

    }
}
