package com.dignityhealth.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/27/17.
 */

public class AppointmentsFragment extends BaseFragment {

    public static final String APPOINTMENTS_TAG = "appointment_tag";
    View appointmentsView;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointmentsView = inflater.inflate(R.layout.appointments, container, false);
        getAppointmentInfo("Bearer " + AuthManager.getInstance().getBearerToken());
        return appointmentsView;
    }

    private void getAppointmentInfo(String bearer) {
        Timber.i("Session bearer " + bearer);
        NetworkManager.getInstance().getAppointments(bearer).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    AppointmentResponse result = response.body();
                    ArrayList<Appointment> appointments = result.appointments;
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
