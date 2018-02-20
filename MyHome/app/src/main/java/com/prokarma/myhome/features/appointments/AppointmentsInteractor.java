package com.prokarma.myhome.features.appointments;

import com.prokarma.myhome.entities.MyAppointmentsRequest;
import com.prokarma.myhome.entities.MyAppointmentsResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsInteractor implements AppointmentsContract.Interactor {
    private final AppointmentsContract.InteractorOutput output;

    public AppointmentsInteractor(AppointmentsContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void getAppointments() {
        NetworkManager.getInstance().getMyAppointments(AuthManager.getInstance().getBearerToken(),
                new MyAppointmentsRequest()).enqueue(new Callback<MyAppointmentsResponse>() {
            @Override
            public void onResponse(Call<MyAppointmentsResponse> call, Response<MyAppointmentsResponse> response) {
                if (response != null && response.isSuccessful()) {
                    output.receivedAppointmentsSuccess(response);
                } else {
                    output.receivedAppointmentsFailed(response);
                }
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable throwable) {
                output.receivedAppointmentsFailed(throwable);
            }
        });
    }
}
