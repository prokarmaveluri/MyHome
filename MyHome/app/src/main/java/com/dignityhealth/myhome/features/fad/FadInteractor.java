package com.dignityhealth.myhome.features.fad;

import com.dignityhealth.myhome.app.mvp.BasePresenter;
import com.dignityhealth.myhome.app.mvp.BaseView;
import com.dignityhealth.myhome.features.fad.suggestions.SearchSuggestionResponse;

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
        void updateProviderList(List<ProvidersResponse.Provider> providers,
                                List<CommonModel> specialties,
                                List<CommonModel> gender,
                                List<CommonModel> languages,
                                List<CommonModel> hospitals,
                                List<CommonModel> practices);

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
                             String zipCode,
                             String page,
                             String pageSize,
                             String distance,
                             String gender,
                             String languages,
                             String specialties,
                             String facilities,
                             String practices,
                             String patients);

        /**
         * navigate to provider details
         */
        void providerDetails();

        /**
         * get most searched suggestionsa
         *
         * @return
         */
        List<SearchSuggestionResponse> getQuickSearchSuggestions();
    }
}
