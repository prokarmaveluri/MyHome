package com.dignityhealth.myhome.features.fad.filter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

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
        RadioGroup.OnCheckedChangeListener, TextView.OnEditorActionListener,
        SeekBar.OnSeekBarChangeListener, FilterExpandableList.GroupSelectionListener {

    private int distanceRange = 100;
    private ArrayList<CommonModel> newPatients = new ArrayList<>();
    private ArrayList<CommonModel> specialties = new ArrayList<>();
    private ArrayList<CommonModel> gender = new ArrayList<>();
    private ArrayList<CommonModel> languages = new ArrayList<>();
    private ArrayList<CommonModel> hospitals = new ArrayList<>();
    private ArrayList<CommonModel> practices = new ArrayList<>();
    private String sortBy = "";

    private int selectedGroup = -1;
    private FragmentFilterBinding binding;
    private SuggestionsAdapter adapter;
    private boolean isHide = true;
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
            distanceRange = getArguments().getInt("DISTANCE");

            ArrayList<CommonModel> list = getArguments().getParcelableArrayList("NEW_PATIENTS");
            newPatients.addAll(list);
            list = getArguments().getParcelableArrayList("SPECIALITY");
            specialties.addAll(list);
            list = getArguments().getParcelableArrayList("GENDER");
            gender.addAll(list);
            list = getArguments().getParcelableArrayList("LANGUAGE");
            languages.addAll(list);
            list = getArguments().getParcelableArrayList("HOSPITALS");
            hospitals.addAll(list);
            list = getArguments().getParcelableArrayList("PRACTICES");
            practices.addAll(list);

            location = getArguments().getParcelable("LOCATION");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false);

        sortBy = AppPreferences.getInstance().getPreference("SORT_BY");
        try {
            binding.filterLocation.addTextChangedListener(new SuggestionTextSwitcher());
            binding.expandableList.setAdapter(new FilterExpandableList(getActivity(), specialties,
                    gender, languages, hospitals, practices, this));

            binding.sortByGroup.setOnCheckedChangeListener(this);
            binding.filterLocation.getBackground().mutate().setColorFilter(getResources().getColor(R.color.accent),
                    PorterDuff.Mode.SRC_ATOP);

            binding.filterLocation.setOnEditorActionListener(this);

            if (newPatients.size() > 0)
                binding.newPatientsSwitch.setChecked(newPatients.get(0).getSelected());

            listListeners();
            updateSortBy();
            setHasOptionsMenu(true);
            if (null != location) {
                binding.filterLocation.setText(location.getDisplayName());
                binding.filterLocation.setSelection(location.getDisplayName().length());
            }
            binding.locationSugg.setVisibility(View.GONE);
            binding.distanceRange.setProgress(distanceRange);
            binding.distanceRangeText.setText(distanceRange + " mi");
            binding.distanceRange.setOnSeekBarChangeListener(this);
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
//                    binding.expandableList.collapseGroup(selectedGroup);
                }
                CommonUtil.setExpandedListViewHeight(binding.expandableList, groupPosition, selectedGroup);
                selectedGroup = groupPosition;
            }
        });

        binding.expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                CommonUtil.setExpandedListViewHeight(binding.expandableList, groupPosition, selectedGroup);
                return false;
            }
        });
    }

    private void setResults() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        try {
            if (null != sortBy)
                AppPreferences.getInstance().setPreference("SORT_BY", sortBy);
            else
                AppPreferences.getInstance().setPreference("SORT_BY", "");

            if (newPatients.size() > 0)
                newPatients.get(0).setSelected(binding.newPatientsSwitch.isChecked());
            bundle.putParcelableArrayList("NEW_PATIENTS", newPatients);
            bundle.putParcelableArrayList("SPECIALITY", specialties);
            bundle.putParcelableArrayList("GENDER", gender);
            bundle.putParcelableArrayList("LANGUAGE", languages);
            bundle.putParcelableArrayList("HOSPITALS", hospitals);
            bundle.putParcelableArrayList("PRACTICES", practices);
            bundle.putParcelable("LOCATION", location);
            bundle.putInt("DISTANCE", distanceRange);
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
            FadManager.getInstance().setLocation(locationSug.get(position - 1));
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
                updateSortByButtons(binding.distance, true);
                break;
            case R.id.bestMatch:
                sortBy = "";
                updateSortByButtons(binding.bestMatch, true);
                break;
            case R.id.lastName:
                sortBy = "4";
                updateSortByButtons(binding.lastName, true);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            binding.locationSugg.setVisibility(View.GONE);

            return false;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Timber.i("distance progress " + progress);
        distanceRange = progress;
        binding.distanceRangeText.setText(progress + " mi");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void selectedGroup(int position) {
        CommonUtil.setExpandedListViewHeight(binding.expandableList, position, selectedGroup);
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
            if (s.length() > 1 && !isHide) {
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

    @Override
    public void onResume() {
        super.onResume();
        expandGroups();
    }

    private void expandGroups() {
        for (int index = 0; index < 5; index++)
            binding.expandableList.expandGroup(index);
    }
}
