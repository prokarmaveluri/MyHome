package com.dignityhealth.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.app.RecyclerViewListener;
import com.dignityhealth.myhome.features.profile.Address;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.Random;

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
                //createAppointment("Bearer " + AuthManager.getInstance().getBearerToken());
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
        appointmentsList.setClickable(true);
        appointmentsList.setAdapter(appointmentsAdapter);
        appointmentsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getAppointmentInfo("Bearer " + AuthManager.getInstance().getBearerToken());
        return appointmentsView;
    }

    private void getAppointmentInfo(String bearer) {
        showLoading();

        Timber.i("Session bearer " + bearer);
        NetworkManager.getInstance().getAppointments(bearer).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                showScreen();
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    AppointmentResponse result = response.body();
                    ArrayList<Appointment> appointments = result.result.appointments;
                    appointmentsAdapter.setAppointments(appointments);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                showScreen();
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    private void createAppointment(String bearer) {
        Timber.i("Session bearer " + bearer);

        Appointment dummyAppointment =
                new Appointment(new Random().nextInt(100), false, "jjonnalagadda@gmail.com", "2017-07-11T17:02:04.43Z", "Dermatology", false, "care giver name here", "This is a dummy appointment for testing", "Make sure my skin is nice and silky smooth", "Dr.Seuss", "Facility name here...", "6168675309", new Address("1301 Shoreway Road", null, "Belmont", "CA", "94002", "US"));

        NetworkManager.getInstance().createAppointment(bearer, dummyAppointment).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        appointmentsList.setVisibility(View.GONE);
    }

    private void showScreen(){
        progressBar.setVisibility(View.GONE);
        appointmentsList.setVisibility(View.VISIBLE);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
