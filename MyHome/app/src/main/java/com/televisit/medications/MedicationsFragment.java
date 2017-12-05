package com.televisit.medications;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.MedicationRecyclerViewListener;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MedicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationsFragment extends BaseFragment implements TextWatcher, SuggestionsAdapter.ISuggestionClick {

    private RecyclerView searchSuggestions;
    private RecyclerView medicationsList;
    private LinearLayout noMedicationsLayout;
    private AppCompatCheckBox noMedicationsCheckbox;
    private EditText searchQuery;
    private RelativeLayout searchLayout;
    private ProgressBar progressBar;

    private List<Medication> searchList;

    public static final String MEDICATIONS_TAG = "medications_tag";

    public MedicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static MedicationsFragment newInstance() {
        MedicationsFragment fragment = new MedicationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.medications));
            ((NavigationActivity) getActivity()).setActionBarLineVisibility(false);
        } else {
            getActivity().setTitle(getString(R.string.medications));
        }

        View view = inflater.inflate(R.layout.fragment_medications, container, false);

        searchQuery = (EditText) view.findViewById(R.id.searchQuery);
        searchSuggestions = (RecyclerView) view.findViewById(R.id.searchSuggestions);
        medicationsList = (RecyclerView) view.findViewById(R.id.medicationsList);
        noMedicationsLayout = (LinearLayout) view.findViewById(R.id.no_medications_layout);
        noMedicationsCheckbox = (AppCompatCheckBox) view.findViewById(R.id.no_medications_checkbox);
        searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.medications_progress);

        noMedicationsCheckbox.setChecked(AwsManager.getInstance().isHasMedicationsFilledOut());

        searchQuery.addTextChangedListener(this);
        noMedicationsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMedicationsAdapter(AwsManager.getInstance().getMedications());
            }
        });

        getMedications();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setSuggestionAdapter(List<String> list) {
        SuggestionsAdapter adapter = new SuggestionsAdapter(list, getActivity(), MedicationsFragment.this);
        searchSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchSuggestions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setMedicationsAdapter(List<Medication> list) {
        if (list != null && list.size() > 0) {
            noMedicationsLayout.setVisibility(View.GONE);
            medicationsList.setVisibility(View.VISIBLE);
            noMedicationsCheckbox.setChecked(false);

            MedicationsAdapter adapter = new MedicationsAdapter(list, new MedicationRecyclerViewListener() {
                @Override
                public void onItemClick(Object model, int position) {

                }

                @Override
                public void onDeleteClick(Object model, int position) {
                    List<Medication> medications = AwsManager.getInstance().getMedications();
                    if (medications == null)
                        medications = new ArrayList<>();
                    if (model != null) {
                        medications.remove((Medication) model);
                        updateMedications();
                    }
                }
            });

            medicationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            medicationsList.addItemDecoration(itemDecoration);
            medicationsList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            noMedicationsLayout.setVisibility(View.VISIBLE);
            medicationsList.setVisibility(View.GONE);
        }

        if ((list != null && list.size() > 0) || noMedicationsCheckbox.isChecked()) {
            AwsManager.getInstance().setHasMedicationsFilledOut(true);
        } else {
            AwsManager.getInstance().setHasMedicationsFilledOut(false);
        }
    }

    private void getMedications() {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getMedications(
                AwsManager.getInstance().getConsumer(),
                new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(medications);
                        }
                        searchLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        setMedicationsAdapter(medications);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            searchLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void updateMedications() {
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateMedications(
                AwsManager.getInstance().getConsumer(),
                AwsManager.getInstance().getMedications(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);
                        getMedications();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void searchMedications(String searchText) {
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().searchMedications(
                AwsManager.getInstance().getConsumer(),
                searchText,
                new SDKValidatedCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        searchSuggestions.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        searchList = medications;
                        searchSuggestions.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        setSuggestionAdapter(getSuggestions(medications));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            searchSuggestions.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchSuggestions.setVisibility(View.GONE);
        if (s.toString().trim().length() > 0) {
            searchMedications(s.toString().trim());
        }
    }

    @Override
    public void suggestionClick(String text, int position) {
        searchSuggestions.setVisibility(View.GONE);
        CommonUtil.hideSoftKeyboard(getActivity());

        List<Medication> medications = AwsManager.getInstance().getMedications();
        if (medications == null)
            medications = new ArrayList<>();
        if (searchList != null && searchList.size() > position) {
            medications.add(searchList.get(position));
            updateMedications();
        }
    }

    private List<String> getSuggestions(List<Medication> list) {
        List<String> sugList = new ArrayList<>();
        for (Medication med : list) {
            sugList.add(med.getName());
        }
        return sugList;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_MEDICATIONS;
    }
}
