package com.prokarma.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;

import java.util.ArrayList;

/**
 * Created by kwelsh on 8/3/17.
 */

public class AppointmentsListFragment extends Fragment {
    protected static final String APPOINTMENT_KEY = "appointment_key";
    public static final String APPOINTMENTS_KEY = "appointments_key";
    public static final String PAST_APPOINTMENT_KEY = "past_appointment_key";

    private View appointmentsView;
    private RecyclerView appointmentsList;
    private AppointmentsRecyclerViewAdapter appointmentsAdapter;
    private boolean isPastAppointmentList;

    private ArrayList<Appointment> appointments;

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
        appointmentsView = inflater.inflate(R.layout.appointments_list, container, false);
        appointmentsList = (RecyclerView) appointmentsView.findViewById(R.id.list_appointments);

        if (getArguments() != null) {
            appointments = getArguments().getParcelableArrayList(APPOINTMENTS_KEY);
            isPastAppointmentList = getArguments().getBoolean(PAST_APPOINTMENT_KEY);
        }

        appointmentsAdapter = new AppointmentsRecyclerViewAdapter(getActivity(), appointments, isPastAppointmentList, new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                Appointment appointment = (Appointment) model;
                Bundle bundle = new Bundle();
                bundle.putParcelable(APPOINTMENT_KEY, appointment);
                bundle.putBoolean(PAST_APPOINTMENT_KEY, isPastAppointmentList);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.APPOINTMENTS_DETAILS, bundle);
            }

            @Override
            public void onPinClick(Object model, int position) {
                Appointment appointment = (Appointment) model;
                CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
            }
        });

        appointmentsList.setAdapter(appointmentsAdapter);
        appointmentsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        appointmentsList.addItemDecoration(itemDecoration);

        return appointmentsView;
    }


}
