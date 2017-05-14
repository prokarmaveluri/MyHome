package com.dignityhealth.myhome.features.fad;

import com.dignityhealth.myhome.app.mvp.BasePresenter;
import com.dignityhealth.myhome.app.mvp.BaseView;

import java.util.List;

/**
 * Created by cmajji on 5/12/17.
 */

public interface FadInteractor {

    interface View extends BaseView<Presenter> {

        void showViews(boolean show);

        void showProgress(boolean inProgress);

        void showErrorMessage(String message);

        void updateLocationSuggestions();

        void updateProviderList(List<ProvidersResponse.Provider> providers);

        void providersListError();
    }

    interface Presenter extends BasePresenter {

        void getLocationSuggestions();

        void getProviderList(String queryString, String lat, String lon, String displayName,
                             String zipCode);

        void providerDetails();
    }
}
