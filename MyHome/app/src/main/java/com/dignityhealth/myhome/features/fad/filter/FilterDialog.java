package com.dignityhealth.myhome.features.fad.filter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.FragmentFilterBinding;
import com.dignityhealth.myhome.features.fad.CommonModel;
import com.dignityhealth.myhome.features.fad.LocationSuggestionsResponse;
import com.dignityhealth.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/*
 * Fragment dialog to display the password criteria.
 *
 * Created by cmajji on 1/03/17.
 */
public class FilterDialog extends DialogFragment implements SuggestionsAdapter.ISuggestionClick {

    private ArrayList<CommonModel> specialties;
    private ArrayList<CommonModel> gender;
    private ArrayList<CommonModel> languages;
    private ArrayList<CommonModel> hospitals;
    private ArrayList<CommonModel> practices;

    private int selectedGroup = -1;
    private FragmentFilterBinding binding;
    private SuggestionsAdapter adapter;
    private boolean isHide = false;

    public FilterDialog() {
        // Required empty public constructor
    }

    public static FilterDialog newInstance() {
        FilterDialog fragment = new FilterDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

        if (getArguments() != null) {
            specialties = getArguments().getParcelableArrayList("SPECIALITY");
            gender = getArguments().getParcelableArrayList("GENDER");
            languages = getArguments().getParcelableArrayList("LANGUAGE");
            hospitals = getArguments().getParcelableArrayList("HOSPITALS");
            practices = getArguments().getParcelableArrayList("PRACTICES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false);

        binding.filterLocation.addTextChangedListener(new SuggestionTextSwitcher());
        binding.expandableList.setAdapter(new FilterExpandableList(getActivity(), specialties,
                gender, languages, hospitals, practices));

        listListeners();
        setHasOptionsMenu(true);

        binding.setHandlers(new DialogClick());
        return binding.getRoot();
    }

    private void listListeners() {
        binding.expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (selectedGroup != -1 && groupPosition != selectedGroup) {
                    binding.expandableList.collapseGroup(selectedGroup);
                }
                selectedGroup = groupPosition;
            }
        });

        binding.expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                CommonUtil.setListViewHeight(binding.expandableList, groupPosition, selectedGroup);
                return false;
            }
        });
    }

    private void setResults() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("SPECIALITY", specialties);
        bundle.putParcelableArrayList("GENDER", gender);
        bundle.putParcelableArrayList("LANGUAGE", languages);
        bundle.putParcelableArrayList("HOSPITALS", hospitals);
        bundle.putParcelableArrayList("PRACTICES", practices);
        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @Override
    public void suggestionClick(String text) {
        isHide = true;
        binding.locationSugg.setVisibility(View.GONE);
        binding.filterLocation.setText(text);
    }

    public class DialogClick {
        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.dialog_close:
                    dismiss();
                    break;
                case R.id.save_filter:
                    setResults();
                    dismiss();
                    break;
            }
        }
    }

    private void locationSuggestions(List<String> list) {
        binding.locationSugg.setVisibility(View.VISIBLE);
        adapter = new SuggestionsAdapter(list, getActivity(), FilterDialog.this);
        binding.locationSugg.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.locationSugg.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getLocationSuggestions(String query) {
        NetworkManager.getInstance().getLocationSuggestions(query)
                .enqueue(new Callback<List<LocationSuggestionsResponse>>() {
                    @Override
                    public void onResponse(Call<List<LocationSuggestionsResponse>> call,
                                           Response<List<LocationSuggestionsResponse>> response) {
                        if (response.isSuccessful()) {
                            locationSuggestions(getLocationNames(response.body()));
                        } else {
                            Timber.i("Something went wrong, LocationSuggestions");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LocationSuggestionsResponse>> call, Throwable t) {
                        Timber.i("LocationSuggestions, onFailure");
                    }
                });
    }

    private List<String> getLocationNames(List<LocationSuggestionsResponse> list) {
        List<String> sug = new ArrayList<>();
        for (LocationSuggestionsResponse resp : list) {
            sug.add(resp.getDisplayName());

        }
        return sug;
    }

    public class SuggestionTextSwitcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0 && !isHide) {
                getLocationSuggestions(s.toString());
            }
            isHide = false;
        }
    }
}
