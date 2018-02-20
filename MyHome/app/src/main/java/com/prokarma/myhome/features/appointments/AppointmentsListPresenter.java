package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.prokarma.myhome.entities.Appointment;

import java.util.ArrayList;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsListPresenter implements AppointmentsListContract.Presentor {
    private Context context;
    private AppointmentsListView view;
    private AppointmentsListRouter router;

    public AppointmentsListPresenter(final Context context, final Fragment fragment, final View masterView, final ArrayList<Appointment> appointments, final boolean isPastAppointmentList) {
        this.context = context;
        this.view = new AppointmentsListView(context, masterView, this, appointments, isPastAppointmentList);
        this.router = new AppointmentsListRouter(context);
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        router = null;
    }

    @Override
    public void onAppointmentClicked(Appointment appointment, boolean isPastAppointment) {
        router.goToAppointmentDetails(appointment, isPastAppointment);
    }

    @Override
    public void onDirectionPinClicked(Appointment appointment) {
        router.goToAppointmentDirections(appointment);
    }
}
