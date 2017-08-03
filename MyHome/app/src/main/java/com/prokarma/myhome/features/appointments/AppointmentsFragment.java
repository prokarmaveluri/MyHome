package com.prokarma.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsFragment extends BaseFragment {
    public static final String APPOINTMENTS_TAG = "appointment_tag";
    protected static final String APPOINTMENT_KEY = "appointment_key";

    private View appointmentsView;
    private ProgressBar progressBar;
    private RecyclerView appointmentsList;
    private AppointmentsRecyclerViewAdapter appointmentsAdapter;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointmentsView = inflater.inflate(R.layout.appointments, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.appointments));

        progressBar = (ProgressBar) appointmentsView.findViewById(R.id.appointments_progress);

        Button book = (Button) appointmentsView.findViewById(R.id.book_appointment);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
            }
        });

        appointmentsAdapter = new AppointmentsRecyclerViewAdapter(getActivity(), null, new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                Appointment appointment = (Appointment) model;
                Bundle bundle = new Bundle();
                bundle.putParcelable(APPOINTMENT_KEY, appointment);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.APPOINTMENTS_DETAILS, bundle);
            }

            @Override
            public void onPinClick(Object model, int position) {
                Appointment appointment = (Appointment) model;
                CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
            }
        });

        appointmentsList = (RecyclerView) appointmentsView.findViewById(R.id.list_appointments);
        appointmentsList.setAdapter(appointmentsAdapter);
        appointmentsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        appointmentsList.addItemDecoration(itemDecoration);

        //getAppointmentInfo(AuthManager.getInstance().getBearerToken());
        // TODO:Testing
        getMyAppointments();
        return appointmentsView;
    }

    private void getAppointmentInfo(String bearer) {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showLoading();

        Timber.i("Session bearer " + bearer);
        NetworkManager.getInstance().getAppointments(bearer).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (isAdded()) {
                    showScreen();
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        AppointmentResponse result = response.body();

                        try {
                            ArrayList<Appointment> appointments = result.result.appointments;
                            //Attempt to sort the appointments by startTime
                            Collections.sort(appointments);
                            appointmentsAdapter.setAppointments(appointments);
                            ProfileManager.setAppointments(appointments);
                        } catch (Exception e) {
                            appointmentsAdapter.setAppointments(null);
                        }
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                    }
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                if (isAdded()) {
                    showScreen();
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                }
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        appointmentsList.setVisibility(View.GONE);
    }

    private void showScreen() {
        progressBar.setVisibility(View.GONE);
        appointmentsList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.APPOINTMENTS_SCREEN, null);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }

    private void getMyAppointments() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showLoading();

        NetworkManager.getInstance().getMyAppointments(AuthManager.getInstance().getBearerToken(),
                new MyAppointmentsRequest()).enqueue(new Callback<MyAppointmentsResponse>() {
            @Override
            public void onResponse(Call<MyAppointmentsResponse> call, Response<MyAppointmentsResponse> response) {

                if (isAdded()) {
                    showScreen();
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        MyAppointmentsResponse myAppointmentsResponse = response.body();
                        Timber.d("My Appointments Response: " + myAppointmentsResponse);

                        if (myAppointmentsResponse.getData() != null && myAppointmentsResponse.getData().getUser() != null) {
                            ArrayList<Appointment> appointments = (ArrayList<Appointment>) myAppointmentsResponse.getData().getUser().getAppointments();
                            Timber.i("Appointments: " + Arrays.deepToString(appointments.toArray()));

                            try {
                                //Attempt to sort the appointments by startTime
                                Collections.sort(appointments);
                                appointmentsAdapter.setAppointments(appointments);
                                ProfileManager.setAppointments(appointments);
                            } catch (Exception e) {
                                appointmentsAdapter.setAppointments(null);
                            }
                        }

                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable t) {
                if (isAdded()) {
                    showScreen();
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                }
            }
        });
    }
}
