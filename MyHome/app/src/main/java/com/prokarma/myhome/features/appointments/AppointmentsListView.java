package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.entities.Appointment;

import java.util.ArrayList;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsListView implements AppointmentsListContract.View {

    private RecyclerView appointmentsList;
    private AppointmentsRecyclerViewAdapter appointmentsAdapter;

    public AppointmentsListView(final Context context, final View masterView, final AppointmentsListContract.Presentor presentor, final ArrayList<Appointment> appointments, final boolean isPastAppointmentList) {
        appointmentsList = (RecyclerView) masterView.findViewById(R.id.list_appointments);

        appointmentsAdapter = new AppointmentsRecyclerViewAdapter(context, appointments, isPastAppointmentList, new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                presentor.onAppointmentClicked((Appointment) model, isPastAppointmentList);
            }

            @Override
            public void onPinClick(Object model, int position) {
                presentor.onDirectionPinClicked((Appointment) model);
            }
        });

        appointmentsList.setAdapter(appointmentsAdapter);
        appointmentsList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        appointmentsList.addItemDecoration(itemDecoration);
    }
}
