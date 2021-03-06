package com.prokarma.myhome.features.fad;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.OptionsActivity;
import com.prokarma.myhome.databinding.FragmentFadBinding;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.filter.FilterDialog;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.fad.suggestions.FadSuggestions;
import com.prokarma.myhome.features.fad.suggestions.ProviderSuggestionsAdapter;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.RESTConstants;
import com.prokarma.myhome.utils.SessionUtil;
import com.prokarma.myhome.utils.TealiumUtil;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class FadFragment extends BaseFragment implements FadInteractor.View,
        View.OnClickListener, ViewPager.OnPageChangeListener,
        TextView.OnEditorActionListener, View.OnFocusChangeListener,
        TextWatcher, ProviderSuggestionsAdapter.IProviderSuggestionClick {

    private static final int FILTER_REQUEST = 100;
    private static final int RECENT_PROVIDERS = 200;
    public static String currentSearchQuery = "";
    private static int currentPageSelection = 0;

    private FragmentFadBinding binding;
    private boolean isSugShow = true;
    private ProviderSuggestionsAdapter suggestionAdapter;
    private FadInteractor.Presenter presenter;
    private List<SearchSuggestionResponse> suggestionList;
    private FragmentStatePagerAdapter pagerAdapter;

    private int distanceRange = 100;
    public static int currentScroll = 0;
    public static int maxCount = 0, mPageIndex = 1;
    public static ArrayList<ProviderDetailsResponse> providerList = new ArrayList<>();
    private static ArrayList<CommonModel> newPatients = new ArrayList<>();
    private static ArrayList<CommonModel> specialties = new ArrayList<>();
    private static ArrayList<CommonModel> gender = new ArrayList<>();
    private static ArrayList<CommonModel> languages = new ArrayList<>();
    private static ArrayList<CommonModel> hospitals = new ArrayList<>();
    private static ArrayList<CommonModel> practices = new ArrayList<>();

    public static final String FAD_TAG = "fad_tag";

    public static FadFragment newInstance() {
        return new FadFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fad, container, false);

        ((NavigationActivity) getActivity()).getSupportActionBar().hide();

        binding.suggestionList.setVisibility(View.GONE);
        searchLayoutVisibility(false);
        binding.fadProgress.setVisibility(View.GONE);

        presenter = new FadPresenter(this, getActivity());
        presenter.start();
        setPager();

        binding.fadMore.setOnClickListener(this);
        binding.fadFilter.setOnClickListener(this);
        binding.fadSearch.setOnClickListener(this);
        binding.fadRecent.setOnClickListener(this);
        binding.fadBack.setOnClickListener(this);

        binding.searchQuery.setOnEditorActionListener(this);
        binding.searchQuery.setOnFocusChangeListener(this);
        binding.searchQuery.addTextChangedListener(this);

        if (isSugShow) {
            providerList.clear();
            clearFilters();
        }

        binding.suggestionList.setVisibility(View.VISIBLE);
        searchLayoutVisibility(true);
        binding.searchQuery.requestFocus();
        getSearchSuggestions("");
        CommonUtil.showSoftKeyboard(binding.searchQuery, getActivity());
        updateSuggestionList(presenter.getQuickSearchSuggestions());

        drawableClickEvent();
        if (!CommonUtil.isAccessibilityEnabled(getActivity())) {
            binding.fadScreenTitle.setText(getActivity().getString(R.string.find_care));
        }
        return binding.getRoot();
    }

    private void searchLayoutVisibility(boolean visible) {
        if (visible) {
            binding.searchLayout.setVisibility(View.VISIBLE);
            binding.fadFilter.setVisibility(View.GONE);
            binding.fadRecent.setVisibility(View.GONE);
            binding.fadMore.setVisibility(View.GONE);
            binding.fadRecent.setVisibility(View.GONE);
            binding.fadFilter.setVisibility(View.GONE);
            binding.fadSearch.setVisibility(View.GONE);
            binding.fadBack.setVisibility(View.GONE);
            binding.fadScreenTitle.setVisibility(View.GONE);
        } else {
            binding.searchLayout.setVisibility(View.GONE);
            binding.fadFilter.setVisibility(View.VISIBLE);
            binding.fadRecent.setVisibility(View.VISIBLE);
            binding.fadMore.setVisibility(View.VISIBLE);
            binding.fadRecent.setVisibility(View.VISIBLE);
            binding.fadFilter.setVisibility(View.VISIBLE);
            binding.fadSearch.setVisibility(View.VISIBLE);
            binding.fadBack.setVisibility(View.VISIBLE);
            binding.fadScreenTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != NavigationActivity.eventBus)
            NavigationActivity.eventBus.register(this);
        if (!isSugShow) {
            binding.suggestionList.setVisibility(View.GONE);
            searchLayoutVisibility(false);
            CommonUtil.hideSoftKeyboard(getActivity());
        }
        ((NavigationActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        isSugShow = false;
        CommonUtil.hideSoftKeyboard(getActivity());
        NavigationActivity.eventBus.unregister(this);
        ((NavigationActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                R.anim.slide_in_right, R.anim.slide_out_left);
        switch (item.getItemId()) {
//            case R.id.help:
//                return true;
            case R.id.settings:
                NavigationActivity.setActivityTag(Constants.ActivityTag.SETTINGS);
                Intent intentSettings = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentSettings, options.toBundle());
                return true;
//            case R.id.preferences:
//                return true;
            case R.id.bill_pay:
                NavigationActivity.setActivityTag(Constants.ActivityTag.FAQ);
                Intent intentFAQ = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentFAQ, options.toBundle());
                return true;
            case R.id.profile:
                NavigationActivity.setActivityTag(Constants.ActivityTag.PROFILE_VIEW);
                Intent intentProfile = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentProfile, options.toBundle());
                return true;
            case R.id.contact_us:
                NavigationActivity.setActivityTag(Constants.ActivityTag.CONTACT_US);
                Intent intentContactUs = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentContactUs, options.toBundle());
                return true;
            case R.id.terms_of_service:
                NavigationActivity.setActivityTag(Constants.ActivityTag.TERMS_OF_SERVICE);
                Intent intentTos = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentTos, options.toBundle());
                return true;
            case R.id.developer:
                NavigationActivity.setActivityTag(Constants.ActivityTag.DEVELOPER);
                Intent intentDeveloper = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentDeveloper, options.toBundle());
                return true;
            case R.id.sign_out:
//                SessionUtil.logout(getActivity(), binding.fadProgress);
                SessionUtil.signOutAlert(getActivity(), binding.fadProgress);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getSearchSuggestions(String query) {
        if (query.trim().length() <= 0) {
            //get quick suggestions
            Timber.i("Quick Search");
            binding.suggestionList.setVisibility(View.VISIBLE);
            updateSuggestionList(presenter.getQuickSearchSuggestions());
            return;
        }
        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
            return;
        }
        if (null == FadManager.getInstance().getLocation()) {
            CommonUtil.showToast(getActivity(), getString(R.string.query_location_unavailable));
            binding.suggestionList.setVisibility(View.GONE);
            return;
        }
        NetworkManager.getInstance().getSearchSuggestions(query,
                FadManager.getInstance().getLocation().getLat(),
                FadManager.getInstance().getLocation().getLong(),
                FadManager.getInstance().getLocation().getDisplayName(),
                FadManager.getInstance().getLocation().getZipCode())

                .enqueue(new Callback<List<SearchSuggestionResponse>>() {
                    @Override
                    public void onResponse(Call<List<SearchSuggestionResponse>> call,
                                           Response<List<SearchSuggestionResponse>> response) {
                        if (response.isSuccessful() && response.body().size() > 0) {
                            Timber.d("getSearchSuggestions. Successful Response\n" + response);
                            if (currentSearchQuery.trim().length() > 0)
                                updateSuggestionList(response.body());
                            isSugShow = true;
                            if (isSugShow && binding.searchLayout.isShown())
                                binding.suggestionList.setVisibility(View.VISIBLE);
                        } else {
                            Timber.e("getSearchSuggestions. Response, but not successful?\n" + response);
                            if(response.errorBody() != null){
                                ApiErrorUtil.getInstance().getSearchSuggestionError(getContext(), getView(), response);
                            }

                            binding.suggestionList.setVisibility(View.GONE);
                            isSugShow = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchSuggestionResponse>> call, Throwable t) {
                        Timber.e("getSearchSuggestions. Something failed! :/");
                        Timber.e("Throwable = " + t);
                        ApiErrorUtil.getInstance().getSearchSuggestionFailed(getContext(), getView(), t);
                        binding.suggestionList.setVisibility(View.GONE);
                        isSugShow = false;
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
//        CommonUtil.showSoftKeyboard(getActivity());
    }

    private void startListDialog() {
        ArrayList<String> recentlyViewed = RecentlyViewedDataSourceDB.getInstance().getAllProviderEntry();
        if (recentlyViewed != null && recentlyViewed.size() > 0) {

            ArrayList<ProviderDetailsResponse> providers = new ArrayList<>();
            ProviderListDialog dialog = new ProviderListDialog();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            for (String provider : recentlyViewed) {
                ProviderDetailsResponse providerObj = gson.fromJson(provider, ProviderDetailsResponse.class);
                providers.add(providerObj);
            }
            if (providers.size() <= 0)
                return;

            Fragment fragment = this.getActivity().getSupportFragmentManager().findFragmentByTag(ProviderListDialog.PROVIDER_LIST_DIALOG_TAG);
            if (fragment == null || !fragment.isVisible()) {
                bundle.putParcelableArrayList("PROVIDER_LIST", providers);
                bundle.putBoolean("PROVIDER_RECENT", true);
                dialog.setArguments(bundle);
                dialog.setTargetFragment(this, RECENT_PROVIDERS);
                dialog.show(this.getActivity().getSupportFragmentManager(), ProviderListDialog.PROVIDER_LIST_DIALOG_TAG);
            }
        } else {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_recent_providers));
        }
    }

    private void startFilterDialog() {
        Fragment fragment = this.getActivity().getSupportFragmentManager().findFragmentByTag("Filter Dialog");
        if (fragment == null || !fragment.isVisible()) {
            FilterDialog dialog = new FilterDialog();
            Bundle bundle = new Bundle();

            bundle.putInt("DISTANCE", distanceRange);
            bundle.putParcelableArrayList("NEW_PATIENTS", newPatients);
            bundle.putParcelableArrayList("SPECIALITY", specialties);
            bundle.putParcelableArrayList("GENDER", gender);
            bundle.putParcelableArrayList("LANGUAGE", languages);
            bundle.putParcelableArrayList("HOSPITALS", hospitals);
            bundle.putParcelableArrayList("PRACTICES", practices);
            bundle.putParcelable("LOCATION", FadManager.getInstance().getLocation());

            dialog.setArguments(bundle);
            dialog.setTargetFragment(this, FILTER_REQUEST);
            dialog.show(this.getActivity().getSupportFragmentManager(), "Filter Dialog");
        }
    }

    private void clearFilters() {
        newPatients.clear();
        specialties.clear();
        gender.clear();
        languages.clear();
        hospitals.clear();
        practices.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    distanceRange = data.getExtras().getInt("DISTANCE");
                    newPatients = data.getExtras().getParcelableArrayList("NEW_PATIENTS");
                    specialties = data.getExtras().getParcelableArrayList("SPECIALITY");
                    gender = data.getExtras().getParcelableArrayList("GENDER");
                    languages = data.getExtras().getParcelableArrayList("LANGUAGE");
                    hospitals = data.getExtras().getParcelableArrayList("HOSPITALS");
                    practices = data.getExtras().getParcelableArrayList("PRACTICES");
                    LocationResponse location = data.getExtras().getParcelable("LOCATION");
                    FadManager.getInstance().setLocation(location);
                }
                // update list with filter
                mPageIndex = 1;
                searchForQuery(currentSearchQuery, String.valueOf(distanceRange));
            }
        } else if (requestCode == RECENT_PROVIDERS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    ProviderDetailsResponse provider = data.getExtras().getParcelable("PROVIDER");
                    providerDetails(provider, null);
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(ImageView actionMore) {
        PopupMenu popup = new PopupMenu(getActivity(), actionMore);
        popup.getMenuInflater().inflate(R.menu.toolbar_menu, popup.getMenu());

        //users with no access to MyCareNow can see PROFILE right on the bottom tab navigation, hence hiding it here in the Options to avoid duplicates.
        if (!AuthManager.getInstance().hasMyCare()) {
            popup.getMenu().findItem(R.id.profile).setVisible(false);
        }

        popup.getMenu().findItem(R.id.version).setTitle("Version - v" + BuildConfig.VERSION_CODE);
        popup.getMenu().findItem(R.id.release_date).setTitle("Release Date - " + BuildConfig.BUILD_TIME);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return true;
            }
        });
        popup.show();

        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fad_more:
                onCreateOptionsMenu(binding.fadMore);
                break;
            case R.id.fad_filter:
                startFilterDialog();
                break;
            case R.id.fad_search:
                ApiErrorUtil.getInstance().clearErrorMessage();
                searchLayoutVisibility(true);
                getSearchSuggestions(currentSearchQuery);
                binding.searchQuery.requestFocus();
                CommonUtil.showSoftKeyboard(binding.searchQuery, getActivity());
                break;
            case R.id.fad_recent:
                startListDialog();
                break;
            case R.id.fad_back:
                ((NavigationActivity) getActivity()).onBackPressed();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH) {

            binding.suggestionList.setVisibility(View.GONE);
            clearFilters();
            AppPreferences.getInstance().setPreference("SORT_BY", ""); // default/best match search
            mPageIndex = 1;
            searchForQuery(v.getText().toString(), RESTConstants.PROVIDER_DISTANCE_MILES);
            return true;
        }
        return false;
    }

    private void fadAnalytics(String searchTearm) {

        String searchLocation = "";
        if (null != FadManager.getInstance().getLocation() && null != FadManager.getInstance().getLocation().getCity()) {
            searchLocation = FadManager.getInstance().getLocation().getCity();
            if (null != FadManager.getInstance().getLocation().getState())
                searchLocation = searchLocation + ", " + FadManager.getInstance().getLocation().getState();
        } else if (null != FadManager.getInstance().getLocation()) {
            searchLocation = "Lat: " + FadManager.getInstance().getLocation().getLat() + ", Long: " +
                    FadManager.getInstance().getLocation().getLong();
        }

        Map<String, Object> tealiumData = new HashMap<>();
        tealiumData.put(Constants.FAD_SEARCH_TERM, searchTearm);
        tealiumData.put(Constants.FAD_SEARCH_GEO, searchLocation);
        TealiumUtil.trackEvent(Constants.FAD_SEARCH_STARTED_EVENT, tealiumData);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isSugShow)
            getSearchSuggestions(s.toString());

        if (!currentSearchQuery.equals(s.toString())) {
            isSugShow = true;
            currentSearchQuery = s.toString();
        }
    }

    private void updateSuggestionList(List<SearchSuggestionResponse> list) {
        try {
            suggestionAdapter = new ProviderSuggestionsAdapter(getSuggestions(list), getActivity(),
                    FadFragment.this);
            binding.suggestionList.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.suggestionList.setAdapter(suggestionAdapter);
            suggestionAdapter.notifyDataSetChanged();
            if (CommonUtil.isAccessibilityEnabled(getActivity())) {
                StringBuilder textToAnnounce = new StringBuilder();
                textToAnnounce.append(getString(R.string.showing));
                Map<String, Integer> sectionHeaderCount = getSectionHeaderCount(list);
                for (String sectionHeader :
                        sectionHeaderCount.keySet()) {
                    textToAnnounce.append(sectionHeaderCount.get(sectionHeader));
                    textToAnnounce.append(", ");
                    textToAnnounce.append(sectionHeader);
                    textToAnnounce.append(", ");
                }
                binding.suggestionList.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                binding.suggestionList.announceForAccessibility(textToAnnounce + getString(R.string.suggestions_available));
            }
        } catch (NullPointerException | IllegalStateException ex) {
            Timber.w(ex);
        }
    }

    @Override
    public void suggestionClick(String query, String type, String providerId) {
        isSugShow = false;
        if (type.contains("Provider")) {
            Timber.i("suggestionClick " + query + " type " + type);
            binding.suggestionList.setVisibility(View.GONE);
            CommonUtil.hideSoftKeyboard(getActivity());
            providerDetails(null, providerId);
        } else {
            binding.searchQuery.setText(query);
            binding.searchQuery.setSelection(query.length());
            binding.suggestionList.setVisibility(View.GONE);
            CommonUtil.hideSoftKeyboard(getActivity());
            mPageIndex = 1;
            searchForQuery(query, RESTConstants.PROVIDER_DISTANCE_MILES);
        }
    }

    private List<FadSuggestions> getSuggestions(List<SearchSuggestionResponse> list) {
        List<FadSuggestions> sug = new ArrayList<>();
        try {
            for (SearchSuggestionResponse resp : list) {
                if (resp.getType().contains("Search") || resp.getType().contains("Provider") ||
                        resp.getType().contains("SectionHeader")) {
                    if (null != resp.Category && resp.Category.equals("Provider") &&
                            null != resp.getTitle() && resp.getTitle().contains("Healthcare Providers")) {
                        FadSuggestions sugObj = new FadSuggestions(resp.getType(), "Healthcare Providers",
                                resp.getNpi());
                        sug.add(sugObj);
                    } else {
                        FadSuggestions sugObj = new FadSuggestions(resp.getType(), resp.getTitle(),
                                resp.getNpi());
                        sug.add(sugObj);
                    }
                }
            }
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
        return sug;
    }

    private Map<String, Integer> getSectionHeaderCount(List<SearchSuggestionResponse> list) {
        Map<String, Integer> sectionHeaderCount = new HashMap<>();
        Map<String, String> titleMap = new HashMap<>();
        Map<String, Integer> consolidatedMap = new HashMap<>();
        try {
            for (SearchSuggestionResponse resp : list) {
                if (resp.getType().contains("SectionHeader")) {
                    sectionHeaderCount.put(resp.getCategory(), 0);
                    titleMap.put(resp.getCategory(), resp.getTitle());
                } else {
                    sectionHeaderCount.put(resp.getCategory(), sectionHeaderCount.get(resp.getCategory()) + 1);
                }
            }
            for (String category : titleMap.keySet()) {
                consolidatedMap.put(titleMap.get(category), sectionHeaderCount.get(category));
            }
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
        return consolidatedMap;
    }

    private void searchForQuery(String query, String distanceRange) {
        try {
            if (query.length() <= 0) {
                CommonUtil.showToast(getActivity(), getActivity().getString(R.string.query_empty));
                return;
            }
            if (null == FadManager.getInstance().getLocation()) {
                CommonUtil.showToast(getActivity(), getActivity().getString(R.string.query_location_unavailable));
                return;
            }
            if (!ConnectionUtil.isConnected(getActivity())) {
                CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
                return;
            }
            fadAnalytics(query);
            if (mPageIndex == 1)
                currentScroll = 0;
            showProgress(true);
            currentSearchQuery = query;
            binding.suggestionList.setVisibility(View.GONE);

            String zipCode = FadManager.getInstance().getLocation().getZipCode() == null ? "" : FadManager.getInstance().getLocation().getZipCode();

            presenter.getProviderList(query,
                    FadManager.getInstance().getLocation().getLat(),
                    FadManager.getInstance().getLocation().getLong(),
                    FadManager.getInstance().getLocation().getDisplayName(),
                    zipCode,
                    String.valueOf(mPageIndex),
                    RESTConstants.PROVIDER_PAGE_SIZE,
                    distanceRange,
                    getSortBy(),
                    getParam(gender),
                    getParam(languages),
                    getParam(specialties),
                    getParam(hospitals),
                    getParam(practices),
                    getParam(newPatients));
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
    }

    @Override
    public void updateProviderList(ProvidersResponse response) {
        mPageIndex = response.getCurrentPageNum();
        // Update list
        if (mPageIndex > 1) {
            NewPageData data = new NewPageData();
            data.setPageNo(mPageIndex);
            data.setList(response.getProviders());
            NavigationActivity.eventBus.post(data);
            return;
        }
        providerList.clear();
        providerList.addAll(response.getProviders());
        try {
            if (isResumed() && null == getActivity() && getChildFragmentManager() == null)
                return;
            CommonUtil.hideSoftKeyboard(getActivity());
            searchLayoutVisibility(false);
            setPager();
            pagerAdapter.notifyDataSetChanged();

            clearFilters();
            newPatients.addAll(response.getAcceptsNewPatients());
            specialties.addAll(response.getSpecialties());
            gender.addAll(response.getGenders());
            languages.addAll(response.getLanguages());
            hospitals.addAll(response.getHospitals());
            practices.addAll(response.getPractices());
        } catch (IllegalStateException | NullPointerException ex) {
            Timber.w(ex);
        }
        getHandler().removeMessages(0);
        getHandler().sendEmptyMessageDelayed(0, 300);
    }

    private String getParam(List<CommonModel> listModel) {
        StringBuilder build = new StringBuilder();

        for (CommonModel model : listModel) {
            if (build.length() <= 0 && model.getSelected() && !model.getValue().isEmpty())
                build.append(model.getValue());
            else if (model.getSelected() && !model.getValue().isEmpty())
                build.append(",").append(model.getValue());
        }
        return build.toString();
    }

    private String getSortBy() {
        String sort = AppPreferences.getInstance().getPreference("SORT_BY");
        if (null != sort)
            return sort;
        return "";
    }

    @Override
    public void setPresenter(FadInteractor.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void showViews(boolean show) {

    }

    @Override
    public void showProgress(boolean inProgress) {
        if (inProgress) {
            binding.fadProgress.setVisibility(View.VISIBLE);
        } else {
            binding.fadProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMessage(String message) {
        providerList.clear();
        // Update list

        CommonUtil.hideSoftKeyboard(getActivity());
        searchLayoutVisibility(false);
        if (isResumed()) {
            pagerAdapter =
                    new FadPagerAdapter(getChildFragmentManager(), providerList, message);
            binding.fadPager.setAdapter(pagerAdapter);
            binding.fadTabs.setupWithViewPager(binding.fadPager);
        }

        clearFilters();
    }

    @Override
    public void showEmptyMessage(String message) {
        providerList.clear();
        // Update list

        CommonUtil.hideSoftKeyboard(getActivity());
        searchLayoutVisibility(false);
        if (isResumed()) {
            pagerAdapter =
                    new FadPagerAdapter(getChildFragmentManager(), providerList, message);
            binding.fadPager.setAdapter(pagerAdapter);
            binding.fadTabs.setupWithViewPager(binding.fadPager);
        }

        clearFilters();
    }

    @Override
    public void updateLocationSuggestions() {

    }

    @Override
    public void providersListError() {
        clearFilters();
    }

    private void drawableClickEvent() {
        binding.searchQuery.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_RIGHT = 2;

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if ((int) event.getRawX() >= (binding.searchQuery.getRight() -
                                    binding.searchQuery.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                                try {
                                    if (binding.searchQuery.getText().length() <= 0) {
                                        if (null != getActivity())
                                            CommonUtil.hideSoftKeyboard(getActivity());
                                        searchLayoutVisibility(false);
                                        binding.suggestionList.setVisibility(View.GONE);
                                    } else {
                                        if (null != suggestionList)
                                            suggestionList.clear();
                                        binding.searchQuery.setText("");
                                        currentSearchQuery = "";
                                    }
                                } catch (NullPointerException ex) {
                                    if (null != getActivity())
                                        CommonUtil.hideSoftKeyboard(getActivity());
                                    searchLayoutVisibility(false);
                                    binding.suggestionList.setVisibility(View.GONE);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPageSelection = position;
        if (position == 0) {
            getHandler().removeMessages(0);
            getHandler().sendEmptyMessageDelayed(0, 300);
            TealiumUtil.trackView(Constants.FAD_LIST_SCREEN, null);
        } else {
            TealiumUtil.trackView(Constants.FAD_MAP_SCREEN, null);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setPager() {
        pagerAdapter =
                new FadPagerAdapter(getChildFragmentManager(), providerList, "");
        binding.fadPager.setAdapter(pagerAdapter);
        binding.fadTabs.setupWithViewPager(binding.fadPager);
        binding.fadPager.addOnPageChangeListener(this);

        binding.fadTabs.setScrollPosition(currentPageSelection, 0f, true);
        binding.fadPager.setCurrentItem(currentPageSelection);
    }

    @Subscribe
    public void newLocation(LatLng location) {
        Timber.i("New Location " + location);
        searchForQuery(currentSearchQuery, String.valueOf(distanceRange));
    }

    @Subscribe
    public void newPage(ProviderListFragment.PageData data) {
        Timber.i("New Page " + data.getPageNo());
        if (mPageIndex != data.getPageNo() && data.getPageNo() > mPageIndex) {
            mPageIndex = data.getPageNo();
            searchForQuery(currentSearchQuery, String.valueOf(distanceRange));
        }
    }

    public class NewPageData {
        private List<ProviderDetailsResponse> list;
        private int pageNo;

        public List<ProviderDetailsResponse> getList() {
            return list;
        }

        public void setList(List<ProviderDetailsResponse> list) {
            this.list = list;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }
    }

    public class CoachMarksList {
        boolean listCoachMarks = true;
    }

    /**
     * @param provider
     */
    private void providerDetails(ProviderDetailsResponse provider, String providerId) {
        Bundle bundle = new Bundle();

        if (provider != null) {
            bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, provider.convertToNewProviderDetails());
        } else {
            bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, null);
        }

        bundle.putString(ProviderDetailsFragment.PROVIDER_ID_KEY, providerId);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }

    //FAD coach marks

    private void coachmarkRecent() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(binding.fadRecent, getString(R.string.coachmark_recent))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkList();
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_ACTIONBAR_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_ACTIONBAR_SKIP_COACH_MARKS, true);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private boolean isCoachMArksInProgress = false;

    private void coachmarkFilter() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.FAD_ACTIONBAR_SKIP_COACH_MARKS);
        if (skip || isCoachMArksInProgress || providerList.size() <= 0 ||
                binding.searchLayout.getVisibility() == View.VISIBLE) {
            coachmarkList();
            return;
        }
        isCoachMArksInProgress = true;
        try {
            TapTargetView.showFor(
                    getActivity(),
                    TapTarget.forView(binding.fadFilter, getString(R.string.coachmark_filter))
                            .transparentTarget(true),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            coachmarkRecent();
                            AppPreferences.getInstance().setBooleanPreference(Constants.FAD_ACTIONBAR_SKIP_COACH_MARKS, true);
                        }

                        @Override
                        public void onTargetCancel(TapTargetView view) {
                            super.onTargetCancel(view);
                            AppPreferences.getInstance().setBooleanPreference(Constants.FAD_ACTIONBAR_SKIP_COACH_MARKS, true);
                            AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void coachmarkList() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS);
        if (skip || providerList.size() <= 0 || currentPageSelection != 0) {
            return;
        }
        isCoachMArksInProgress = true;
        if (null == ProvidersAdapter.coachItemLayout) {
            coachmarkListLocation();
            return;
        }

        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(ProvidersAdapter.coachItemLayout,
                        getString(R.string.coachmark_list))
                        .transparentTarget(false),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                        coachmarkListLocation();
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkListLocation() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        if (null == ProvidersAdapter.coachMarkView)
            return;

        TapTargetView.showFor(getActivity(),
                TapTarget.forView(ProvidersAdapter.coachMarkView, getString(R.string.coachmark_fad_location))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.FAD_LIST_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private static class FadHandler extends Handler {
        private final WeakReference<FadFragment> mFadFragment;

        private FadHandler(FadFragment fadFragment) {
            mFadFragment = new WeakReference<FadFragment>(fadFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FadFragment fadFragment = mFadFragment.get();
            if (fadFragment != null) {
                fadFragment.coachmarkFilter();
            }
        }
    }

    private Handler getHandler() {
        return new FadHandler(this);
    }

}
