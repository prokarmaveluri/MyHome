package com.dignityhealth.myhome.features.fad;

import android.content.Context;

import com.dignityhealth.myhome.networking.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Presenter to to communicate between fragment and data model or network APIs.
 */

public class FadPresenter implements FadInteractor.Presenter {

    private FadInteractor.View mView;
    private Context mContext;

    public FadPresenter(FadInteractor.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Override
    public void start() {
        mView.showViews(true);
    }

    /**
     * get location suggestions
     */
    @Override
    public void getLocationSuggestions() {

    }

    /**
     * fetch provider list
     */
    @Override
    public void getProviderList(String queryString, String lat, String lon, String displayName,
                                String zipCode) {
        NetworkManager.getInstance().getProviders(queryString, lat, lon, displayName, zipCode)
                .enqueue(new Callback<ProvidersResponse>() {
                    @Override
                    public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                        if (response.isSuccessful() && response.body().getProviders().size() > 0) {
                            mView.updateProviderList(response.body().getProviders());
                        } else {
                            mView.showErrorMessage("There are no providers for the search");
                        }
                        mView.showProgress(false);
                    }

                    @Override
                    public void onFailure(Call<ProvidersResponse> call, Throwable t) {
                        mView.showProgress(false);
                        mView.showErrorMessage("Something went wrong!");
                    }
                });

    }

    /**
     * go to provider details
     */
    @Override
    public void providerDetails() {

    }
}
