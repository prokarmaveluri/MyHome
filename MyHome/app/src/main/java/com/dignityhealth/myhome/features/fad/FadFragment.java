package com.dignityhealth.myhome.features.fad;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.FragmentFadBinding;
import com.dignityhealth.myhome.features.fad.filter.FilterDialog;
import com.dignityhealth.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.dignityhealth.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.Constants;

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
        ProvidersAdapter.IProviderClick, SearchView.OnQueryTextListener,
        SuggestionsAdapter.ISuggestionClick, View.OnFocusChangeListener {

    private FragmentFadBinding binding;
    private SuggestionsAdapter suggestionAdapter;
    private ProvidersAdapter adapter;
    private FadInteractor.Presenter presenter;
    private SearchView searchView;
    private List<ProvidersResponse.Provider> providerList = new ArrayList<>();

    private enum State {
        LIST,
        MESSAGE,
        SUGGESTION
    }

    public static final String FAD_TAG = "fad_tag";

    public static FadFragment newInstance() {
        return new FadFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fad, container, false);

        presenter = new FadPresenter(this, getActivity());
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.find_a_doctor));

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ProvidersAdapter(providerList, getActivity(), this);
        binding.providersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.providersList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        presenter.start();
        showViews(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fad_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.fad_search).getActionView();

        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);
        SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
        searchView.setSearchableInfo(info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fad_search:
                break;

            case R.id.fad_filter:
                startFilterDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
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
        showErrorMessage(true, message);
    }

    @Override
    public void updateLocationSuggestions() {

    }

    @Override
    public void updateProviderList(List<ProvidersResponse.Provider> providers) {
        providerList.clear();
        providerList.addAll(providers);
        adapter.notifyDataSetChanged();
        showErrorMessage(false, "");
    }

    @Override
    public void providersListError() {

    }

    private void showViews() {

    }

    private void showErrorMessage(boolean show, String message) {
        if (show) {
            viewState(State.MESSAGE);
            binding.message.setText(message);
        } else {
            viewState(State.LIST);
        }
    }

    @Override
    public void providerClick(int position) {

        return;
//        Bundle bundle = new Bundle();
//        bundle.putString(ProviderDetailsFragment.PROVIDER_ID, providerList.get(position).getProviderId());
//        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        binding.suggestionList.setVisibility(View.GONE);
        searchForQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getSearchSuggestions(newText);
        return false;
    }

    private void searchForQuery(String query) {
        try {
            if (query.length() <= 0) {
                Toast.makeText(getActivity(), "Enter valid query", Toast.LENGTH_LONG).show();
                return;
            }
            if (null == FadManager.getInstance().getLocation()) {
                Toast.makeText(getActivity(), "user location not available, select location",
                        Toast.LENGTH_LONG).show();
                return;
            }
            showProgress(true);

            View view = getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            presenter.getProviderList(query,
                    FadManager.getInstance().getLocation().getLat(),
                    FadManager.getInstance().getLocation().getLong(),
                    FadManager.getInstance().getLocation().getDisplayName(),
                    FadManager.getInstance().getLocation().getZipCode());
        } catch (NullPointerException ex) {
        }
    }

    private void getSearchSuggestions(String query) {
        if (query.length() <= 0) {
            updateSuggestionList(presenter.getQuickSearchSuggestions());
            Timber.i("Quick Search");
            return;
        }
        if (null == FadManager.getInstance().getLocation()) {
            Toast.makeText(getActivity(), "user location not available, select location",
                    Toast.LENGTH_LONG).show();
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

                            binding.suggestionList.setVisibility(View.VISIBLE);
                            updateSuggestionList(response.body());
                        } else {
                            binding.suggestionList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchSuggestionResponse>> call, Throwable t) {
                        binding.suggestionList.setVisibility(View.GONE);
                    }
                });
    }

    private void updateSuggestionList(List<SearchSuggestionResponse> list) {

        suggestionAdapter = new SuggestionsAdapter(list, getActivity(),
                FadFragment.this);
        binding.suggestionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.suggestionList.setAdapter(suggestionAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void suggestionClick(String query) {
        binding.suggestionList.setVisibility(View.GONE);
        searchView.setQuery(query, false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            binding.suggestionList.setVisibility(View.GONE);
        } else {
            binding.suggestionList.setVisibility(View.VISIBLE);
            if (searchView.getQuery().length() <= 0) {
                updateSuggestionList(presenter.getQuickSearchSuggestions());
                Timber.i("Quick Search on Focus");
            }
        }

    }

    private void viewState(State current) {
        if (current == State.LIST) {
            binding.providersList.setVisibility(View.VISIBLE);
            binding.message.setVisibility(View.GONE);

        } else if (current == State.MESSAGE) {
            binding.message.setVisibility(View.VISIBLE);
            binding.providersList.setVisibility(View.GONE);
        }
    }

    private void startFilterDialog(){
        FilterDialog dialog = new FilterDialog();
        dialog.show(getFragmentManager(), "Filter Dialog");
    }
}
