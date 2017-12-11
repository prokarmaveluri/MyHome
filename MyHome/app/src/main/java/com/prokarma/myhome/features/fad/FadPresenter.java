package com.prokarma.myhome.features.fad;

import android.content.Context;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.networking.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
                                String zipCode,
                                String page,
                                String pageSize,
                                String distance,
                                String sortBy,
                                String gender,
                                String languages,
                                String specialties,
                                String facilities,
                                String practices,
                                String patients) {
        NetworkManager.getInstance().getProviders(queryString, lat, lon, displayName, zipCode,
                page,
                pageSize,
                distance,
                sortBy,
                gender,
                languages,
                specialties,
                facilities,
                practices,
                patients)
                .enqueue(new Callback<ProvidersResponse>() {
                    @Override
                    public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                        if (response.isSuccessful() && response.body().getProviders().size() > 0) {
                            Timber.d("getProviders. Successful Response\n" + response);
                            mView.updateProviderList(response.body());
                            FadFragment.maxCount = response.body().getNumResults();
                        } else if(response.isSuccessful() && response.body().getProviders().size() < 1) {
                            Timber.e("getProviders. Successful Response, but no providers\n" + response);
                            mView.showEmptyMessage(mContext.getString(R.string.no_providers));
                            mView.providersListError();
                        } else {
                            Timber.e("getProviders. Response, but not successful?\n" + response);
                            mView.showErrorMessage(mContext.getString(R.string.something_went_wrong) + "\n" + mContext.getString(R.string.api_error_message));
                            mView.providersListError();
                        }
                        mView.showProgress(false);
                    }

                    @Override
                    public void onFailure(Call<ProvidersResponse> call, Throwable t) {
                        Timber.e("getProviders. Something failed! :/");
                        Timber.e("Throwable = " + t);
                        mView.showProgress(false);
                        mView.showErrorMessage(mContext.getString(R.string.something_went_wrong) + "\n" + mContext.getString(R.string.api_error_message));
                        mView.providersListError();
                    }
                });

    }

    /**
     * go to provider details
     */
    @Override
    public void providerDetails() {

    }

    @Override
    public List<SearchSuggestionResponse> getQuickSearchSuggestions() {
        List<SearchSuggestionResponse> list = new ArrayList<>();
        SearchSuggestionResponse response = new SearchSuggestionResponse("Primary Care", "Search");
        SearchSuggestionResponse response1 = new SearchSuggestionResponse("Cardiology", "Search");
        SearchSuggestionResponse response2 = new SearchSuggestionResponse("Gynecology", "Search");
        list.add(response);
        list.add(response1);
        list.add(response2);
        return list;
    }
}
