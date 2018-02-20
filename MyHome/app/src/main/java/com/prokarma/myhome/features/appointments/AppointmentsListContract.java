package com.prokarma.myhome.features.appointments;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.entities.Appointment;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsListContract {
    interface View {

    }

    interface Interactor {

    }

    interface InteractorOutput {

    }

    interface Presentor extends BasePresenter {
        void onAppointmentClicked(Appointment appointment, boolean isPastAppointment);

        void onDirectionPinClicked(Appointment appointment);
    }

    interface Router {
        void goToAppointmentDetails(Appointment appointment, boolean isPastAppointment);

        void goToAppointmentDirections(Appointment appointment);
    }
}
