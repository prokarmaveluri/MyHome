package com.prokarma.myhome.features.tos;

import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosInteractor implements TosContract.Interactor {
    final TosContract.InteractorOutput output;

    public TosInteractor(TosContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void registerUser(final EnrollmentRequest request) {
        NetworkManager.getInstance().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response != null && response.isSuccessful()) {
                    output.registerUserSuccess(response);
                } else {
                    output.registerUserFailed(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                output.registerUserFailed(throwable);
            }
        });
    }

    @Override
    public void getUsersTosInfo() {
        NetworkManager.getInstance().getTos(AuthManager.getInstance().getBearerToken()).enqueue(new Callback<Tos>() {
            @Override
            public void onResponse(Call<Tos> call, Response<Tos> response) {
                if (response.isSuccessful() && response.body().isValid && response.body().result != null) {
                    output.receivedUsersTosInfoSuccess(response);
                } else {
                    output.receivedUsersTosInfoFailed(response);
                }
            }

            @Override
            public void onFailure(Call<Tos> call, Throwable throwable) {
                output.receivedUsersTosInfoFailed(throwable);
            }
        });
    }
}
