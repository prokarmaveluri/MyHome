package com.prokarma.myhome.features.tos;

import com.prokarma.myhome.entities.Tos;
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
    public void acceptToS() {
//        progress.setVisibility(View.VISIBLE);
//        NetworkManager.getInstance().acceptTos(AuthManager.getInstance().getBearerToken()).enqueue(new Callback<Tos>() {
//            @Override
//            public void onResponse(Call<Tos> call, Response<Tos> response) {
//                progress.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body().isValid) {
//                    //TODO TOS accepted... notify the user.
//                    tosView.findViewById(R.id.tc_button_bar).setVisibility(View.GONE);
//                } else {
//                    Timber.e("acceptTerms. Response, but not successful?\n" + response);
//                    ApiErrorUtil.getInstance().getTosError(getContext(), tosView, response);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Tos> call, Throwable t) {
//                progress.setVisibility(View.GONE);
//                Timber.e("acceptTerms. Something failed! :/");
//                Timber.e("Throwable = " + t);
//                ApiErrorUtil.getInstance().getTosFailed(getContext(), tosView, t);
//            }
//        });
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
