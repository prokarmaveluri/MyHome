package com.dignityhealth.myhome.features.fad;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.app.OptionsActivity;
import com.dignityhealth.myhome.databinding.FragmentFadBinding;
import com.dignityhealth.myhome.features.fad.filter.FilterDialog;
import com.dignityhealth.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.dignityhealth.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.AppPreferences;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.RESTConstants;
import com.dignityhealth.myhome.utils.SessionUtil;

import java.util.ArrayList;
import java.util.List;

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
        TextWatcher, SuggestionsAdapter.ISuggestionClick {

    private static final int FILTER_REQUEST = 100;
    private static String currentSearchQuery = "";
    private static int currentPageSelection = 0;

    private FragmentFadBinding binding;
    private boolean isSugShow = false;
    private SuggestionsAdapter suggestionAdapter;
    private FadInteractor.Presenter presenter;
    private FragmentStatePagerAdapter pagerAdapter;

    private static ArrayList<Provider> providerList = new ArrayList<>();
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

        ((NavigationActivity) getActivity()).getNavigationActionBar().hide();

        binding.suggestionList.setVisibility(View.GONE);
        binding.searchLayout.setVisibility(View.GONE);
        binding.fadProgress.setVisibility(View.GONE);

        presenter = new FadPresenter(this, getActivity());
        setPager();

        binding.fadMore.setOnClickListener(this);
        binding.fadFilter.setOnClickListener(this);
        binding.fadSearch.setOnClickListener(this);

        binding.searchQuery.setOnEditorActionListener(this);
        binding.searchQuery.setOnFocusChangeListener(this);
        binding.searchQuery.addTextChangedListener(this);

        drawableClickEvent();
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD;
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.start();
        ((NavigationActivity) getActivity()).getNavigationActionBar().hide();
        setActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                R.anim.slide_in_right, R.anim.slide_out_left);
        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.settings:
                NavigationActivity.setActivityTag(Constants.ActivityTag.SETTINGS);
                Intent intentSettings = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intentSettings, options.toBundle());
                return true;
            case R.id.preferences:
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
                SessionUtil.logout(getActivity(), binding.fadProgress);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getSearchSuggestions(String query) {
        if (query.length() <= 0) {
            //get quick suggestions
            Timber.i("Quick Search");
            binding.suggestionList.setVisibility(View.VISIBLE);
            updateSuggestionList(presenter.getQuickSearchSuggestions());
            return;
        }
        if (null == FadManager.getInstance().getLocation()) {
            Toast.makeText(getActivity(), getString(R.string.query_location_unavailable),
                    Toast.LENGTH_LONG).show();
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
                            Timber.d("Successful Response\n" + response);
                            updateSuggestionList(response.body());
                            if (isSugShow && binding.searchLayout.isShown())
                                binding.suggestionList.setVisibility(View.VISIBLE);
                        } else {
                            Timber.e("Response, but not successful?\n" + response);
                            binding.suggestionList.setVisibility(View.GONE);
                            isSugShow = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchSuggestionResponse>> call, Throwable t) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + t);
                        binding.suggestionList.setVisibility(View.GONE);
                        isSugShow = false;
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            binding.suggestionList.setVisibility(View.GONE);
        } else {
            binding.suggestionList.setVisibility(View.VISIBLE);
            if (((EditText) v).getText().toString().length() <= 0) {
                //Display Quick suggestions
                Timber.i("Quick Search on Focus");
                binding.suggestionList.setVisibility(View.VISIBLE);
                updateSuggestionList(presenter.getQuickSearchSuggestions());
            }
        }
    }

    private void startFilterDialog() {
        FilterDialog dialog = new FilterDialog();
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList("NEW_PATIENTS", newPatients);
        bundle.putParcelableArrayList("SPECIALITY", specialties);
        bundle.putParcelableArrayList("GENDER", gender);
        bundle.putParcelableArrayList("LANGUAGE", languages);
        bundle.putParcelableArrayList("HOSPITALS", hospitals);
        bundle.putParcelableArrayList("PRACTICES", practices);
        bundle.putParcelable("LOCATION", FadManager.getInstance().getLocation());

        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, FILTER_REQUEST);
        dialog.show(getChildFragmentManager(), "Filter Dialog");
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
                searchForQuery(currentSearchQuery);
            }
        }
    }


    private void setActionBar() {
        Toolbar appToolbar = (Toolbar) binding.getRoot().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650,
                    getActivity().getTheme()));
        } else {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        appToolbar.setTitle("Find Care");
    }

    public boolean onCreateOptionsMenu(ImageView actionMore) {
        PopupMenu popup = new PopupMenu(getActivity(), actionMore);
        popup.getMenuInflater().inflate(R.menu.toolbar_menu, popup.getMenu());

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
                binding.searchLayout.setVisibility(View.VISIBLE);
                binding.searchQuery.requestFocus();
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
            searchForQuery(v.getText().toString());
            return true;
        }
        return false;
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
        isSugShow = true;
        return;
    }


    private void updateSuggestionList(List<SearchSuggestionResponse> list) {
        try {
            suggestionAdapter = new SuggestionsAdapter(getSuggestions(list), getActivity(),
                    FadFragment.this);
            binding.suggestionList.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.suggestionList.setAdapter(suggestionAdapter);
            suggestionAdapter.notifyDataSetChanged();
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    @Override
    public void suggestionClick(String query, int position) {
        isSugShow = false;
        binding.searchQuery.setText(query);
        binding.searchQuery.setSelection(query.length());
        binding.suggestionList.setVisibility(View.GONE);
        CommonUtil.hideSoftKeyboard(getActivity());
        searchForQuery(query);
    }

    private List<String> getSuggestions(List<SearchSuggestionResponse> list) {
        List<String> sug = new ArrayList<>();
        try {
            for (SearchSuggestionResponse resp : list) {
                if (resp.getType().contains("Search") || resp.getType().contains("Provider")) {
                    sug.add(resp.getTitle());
                }
            }
        } catch (NullPointerException ex) {
        }
        return sug;
    }

    private void searchForQuery(String query) {
        try {
            if (query.length() <= 0) {
                Toast.makeText(getActivity(), getString(R.string.query_empty), Toast.LENGTH_LONG).show();
                return;
            }
            if (null == FadManager.getInstance().getLocation()) {
                Toast.makeText(getActivity(), getString(R.string.query_location_unavailable),
                        Toast.LENGTH_LONG).show();
                return;
            }
            Timber.i("search, request ");
            showProgress(true);
            currentSearchQuery = query;
            binding.suggestionList.setVisibility(View.GONE);
            presenter.getProviderList(query,
                    FadManager.getInstance().getLocation().getLat(),
                    FadManager.getInstance().getLocation().getLong(),
                    FadManager.getInstance().getLocation().getDisplayName(),
                    FadManager.getInstance().getLocation().getZipCode(),
                    RESTConstants.PROVIDER_PAGE_NO,
                    RESTConstants.PROVIDER_PAGE_SIZE,
                    RESTConstants.PROVIDER_DISTANCE,
                    getSortBy(),
                    getParam(gender),
                    getParam(languages),
                    getParam(specialties),
                    getParam(hospitals),
                    getParam(practices),
                    getParam(newPatients));
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public void updateProviderList(List<Provider> providers,
                                   List<CommonModel> newPatients,
                                   List<CommonModel> specialties,
                                   List<CommonModel> gender,
                                   List<CommonModel> languages,
                                   List<CommonModel> hospitals,
                                   List<CommonModel> practices) {
        providerList.clear();
        providerList.addAll(providers);
        // Update list

        try {
            if (isResumed() && null == getActivity() && getChildFragmentManager() == null)
                return;

            CommonUtil.hideSoftKeyboard(getActivity());
            binding.searchLayout.setVisibility(View.GONE);
            setPager();

            clearFilters();

            this.newPatients.addAll(newPatients);
            this.specialties.addAll(specialties);
            this.gender.addAll(gender);
            this.languages.addAll(languages);
            this.hospitals.addAll(hospitals);
            this.practices.addAll(practices);
        } catch (IllegalStateException | NullPointerException ex) {
        }
    }

    private String getParam(List<CommonModel> listModel) {
        StringBuilder build = new StringBuilder();

        for (CommonModel model : listModel) {
            if (build.length() <= 0 && model.getSelected() && !model.getValue().isEmpty())
                build.append(model.getValue());
            else if (model.getSelected() && !model.getValue().isEmpty())
                build.append("," + model.getValue());
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
        binding.searchLayout.setVisibility(View.GONE);
        pagerAdapter =
                new FadPagerAdapter(getChildFragmentManager(), providerList, message);
        binding.fadPager.setAdapter(pagerAdapter);
        binding.fadTabs.setupWithViewPager(binding.fadPager);

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

                                binding.searchLayout.setVisibility(View.GONE);
                                binding.suggestionList.setVisibility(View.GONE);
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setPager(){
        pagerAdapter =
                new FadPagerAdapter(getChildFragmentManager(), providerList, "");
        binding.fadPager.setAdapter(pagerAdapter);
        binding.fadTabs.setupWithViewPager(binding.fadPager);
        binding.fadPager.addOnPageChangeListener(this);

        binding.fadTabs.setScrollPosition(currentPageSelection, 0f, true);
        binding.fadPager.setCurrentItem(currentPageSelection);
    }
}
