package com.dignityhealth.myhome.features.fad;

import com.dignityhealth.myhome.app.mvp.BasePresenter;
import com.dignityhealth.myhome.app.mvp.BaseView;

import java.util.List;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Interactor for the Find a doctor.
 */

public interface FadInteractor {

    interface View extends BaseView<Presenter> {

        /**
         * update the views actionable or not.
         *
         * @param show
         */
        void showViews(boolean show);

        /**
         * update the progress bar visibility
         *
         * @param inProgress
         */
        void showProgress(boolean inProgress);

        /**
         * error message from presenter to fragment.
         *
         * @param message
         */
        void showErrorMessage(String message);

        /**
         * update the location suggestions
         */
        void updateLocationSuggestions();

        /**
         * update the providers list
         *
         * @param providers
         */
        void updateProviderList(List<ProvidersResponse.Provider> providers);

        /**
         * error message from REST API for providers list.
         */
        void providersListError();
    }

    interface Presenter extends BasePresenter {

        /**
         * fetch location suggestions
         */
        void getLocationSuggestions();

        /**
         * get providers list for the location and query param.
         *
         * @param queryString
         * @param lat
         * @param lon
         * @param displayName
         * @param zipCode
         */
        void getProviderList(String queryString, String lat, String lon, String displayName,
                             String zipCode);

        /**
         * navigate to provider details
         */
        void providerDetails();
    }
}