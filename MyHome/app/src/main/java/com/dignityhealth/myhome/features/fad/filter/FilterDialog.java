package com.dignityhealth.myhome.features.fad.filter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.FragmentFilterBinding;
import com.dignityhealth.myhome.features.fad.CommonModel;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.features.fad.LocationResponse;
import com.dignityhealth.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.AppPreferences;
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
public class FilterDialog extends DialogFragment implements SuggestionsAdapter.ISuggestionClick,
        RadioGroup.OnCheckedChangeListener {

    private ArrayList<CommonModel> newPatients;
    private ArrayList<CommonModel> specialties;
    private ArrayList<CommonModel> gender;
    private ArrayList<CommonModel> languages;
    private ArrayList<CommonModel> hospitals;
    private ArrayList<CommonModel> practices;
    private String sortBy;

    private int selectedGroup = -1;
    private FragmentFilterBinding binding;
    private SuggestionsAdapter adapter;
    private boolean isHide = false;
    private List<String> currentLocationSug = new ArrayList<>();
    private List<LocationResponse> locationSug = new ArrayList<>();
    private LocationResponse location = null;

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
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light);

        if (getArguments() != null) {
            newPatients = getArguments().getParcelableArrayList("NEW_PATIENTS");
            specialties = getArguments().getParcelableArrayList("SPECIALITY");
            gender = getArguments().getParcelableArrayList("GENDER");
            languages = getArguments().getParcelableArrayList("LANGUAGE");
            hospitals = getArguments().getParcelableArrayList("HOSPITALS");
            practices = getArguments().getParcelableArrayList("PRACTICES");
            location = getArguments().getParcelable("LOCATION");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false);

        try {
            binding.filterLocation.addTextChangedListener(new SuggestionTextSwitcher());
            binding.expandableList.setAdapter(new FilterExpandableList(getActivity(), specialties,
                    gender, languages, hospitals, practices));

            binding.sortByGroup.setOnCheckedChangeListener(this);
            binding.filterLocation.getBackground().mutate().setColorFilter(getResources().getColor(R.color.accent),
                    PorterDuff.Mode.SRC_ATOP);

            if (newPatients.size() > 0)
                binding.newPatientsSwitch.setChecked(newPatients.get(0).getSelected());

            listListeners();
            updateSortBy();
            setHasOptionsMenu(true);
            if (null != location) {
                binding.filterLocation.setText(location.getDisplayName());
                binding.filterLocation.setSelection(location.getDisplayName().length());
            }
        } catch (NullPointerException ex) {
        }
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
        try {
            if (newPatients.size() > 0)
                newPatients.get(0).setSelected(binding.newPatientsSwitch.isChecked());
            bundle.putParcelableArrayList("NEW_PATIENTS", newPatients);
            bundle.putParcelableArrayList("SPECIALITY", specialties);
            bundle.putParcelableArrayList("GENDER", gender);
            bundle.putParcelableArrayList("LANGUAGE", languages);
            bundle.putParcelableArrayList("HOSPITALS", hospitals);
            bundle.putParcelableArrayList("PRACTICES", practices);
            bundle.putParcelable("LOCATION", location);
            intent.putExtras(bundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public void suggestionClick(String text, int position) {
        isHide = true;
        binding.locationSugg.setVisibility(View.GONE);
        binding.filterLocation.setText(text);
        if (position == 0) {
            location = FadManager.getInstance().getCurrentLocation();
        } else {
            if (locationSug.size() >= position + 1)
                location = locationSug.get(position - 1);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        updateSortByButtons(binding.distance, false);
        updateSortByButtons(binding.bestMatch, false);
        updateSortByButtons(binding.lastName, false);
        switch (checkedId) {
            case R.id.distance:
                sortBy = "5";
                AppPreferences.getInstance().setPreference("SORT_BY", sortBy);
                updateSortByButtons(binding.distance, true);
                break;
            case R.id.bestMatch:
                sortBy = "";
                AppPreferences.getInstance().setPreference("SORT_BY", sortBy);
                updateSortByButtons(binding.bestMatch, true);
                break;
            case R.id.lastName:
                sortBy = "4";
                AppPreferences.getInstance().setPreference("SORT_BY", sortBy);
                updateSortByButtons(binding.lastName, true);
                break;
        }
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
                .enqueue(new Callback<List<LocationResponse>>() {
                    @Override
                    public void onResponse(Call<List<LocationResponse>> call,
                                           Response<List<LocationResponse>> response) {
                        if (response.isSuccessful()) {
                            Timber.e("Response, but not successful?\n" + response);
                            locationSug.clear();
                            locationSug.addAll(response.body());
                            locationSuggestions(getLocationNames(response.body()));
                        } else {
                            Timber.e("Response, but not successful?\n" + response);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + t);
                    }
                });
    }

    private List<String> getLocationNames(List<LocationResponse> list) {
        currentLocationSug.clear();
        currentLocationSug.add("User Location");
        for (LocationResponse resp : list) {
            currentLocationSug.add(resp.getDisplayName());

        }
        return currentLocationSug;
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
                binding.locationSugg.setVisibility(View.VISIBLE);
                getLocationSuggestions(s.toString());
            } else {
                binding.locationSugg.setVisibility(View.GONE);
            }
            isHide = false;
        }
    }

    private void updateSortByButtons(RadioButton view, boolean isChecked) {

        if (isChecked) {
            view.setBackgroundResource(R.drawable.button_enabled);
            view.setTextColor(Color.WHITE);
        } else {
            view.setBackgroundResource(R.drawable.button_boarder_acent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setTextColor(getResources().getColor(R.color.accent, getActivity().getTheme()));
            } else {
                view.setTextColor(getResources().getColor(R.color.accent));
            }
        }
    }

    private void updateSortBy() {
        String sort = AppPreferences.getInstance().getPreference("SORT_BY");
        if (sort == null)
            return;
        if (sort.equals("5")) {
            updateSortByButtons(binding.distance, true);
        } else if (sort.equals("4")) {
            updateSortByButtons(binding.lastName, true);
        } else if (sort.equals("")) {
            updateSortByButtons(binding.bestMatch, true);
        }
    }
}
