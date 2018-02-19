package com.prokarma.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Appointment;

import java.util.ArrayList;

/**
 * Created by kwelsh on 8/3/17.
 */

public class AppointmentsListFragment extends Fragment {

    public static final String APPOINTMENT_DETAILS_INTENT = "appointment_details_intent";
    public static final String APPOINTMENT_KEY = "appointment_key";
    public static final String APPOINTMENTS_KEY = "appointments_key";
    public static final String PAST_APPOINTMENT_KEY = "past_appointment_key";

    AppointmentsListPresenter presentor;

    public static AppointmentsListFragment newInstance() {
        return new AppointmentsListFragment();
    }

    public static AppointmentsListFragment newInstance(ArrayList<Appointment> appointments, boolean isPastAppointmentList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(APPOINTMENTS_KEY, appointments);
        bundle.putBoolean(PAST_APPOINTMENT_KEY, isPastAppointmentList);
        AppointmentsListFragment appointmentsListFragment = new AppointmentsListFragment();
        appointmentsListFragment.setArguments(bundle);
        return appointmentsListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View appointmentsView = inflater.inflate(R.layout.appointments_list, container, false);

        if (getArguments() != null) {
            ArrayList<Appointment> appointments = getArguments().getParcelableArrayList(APPOINTMENTS_KEY);
            boolean isPastAppointmentList = getArguments().getBoolean(PAST_APPOINTMENT_KEY);

            presentor = new AppointmentsListPresenter(getContext(), this, appointmentsView, appointments, isPastAppointmentList);
            presentor.onCreate();
        }

        return appointmentsView;
    }

    @Override
    public void onDestroy() {
        if (presentor != null) {
            presentor.onDestroy();
            presentor = null;
        }

        super.onDestroy();
    }
}
