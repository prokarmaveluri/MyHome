package com.televisit.medications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.SampleApplication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.prokarma.myhome.utils.CommonUtil;
import com.televisit.SDKUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MedicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationsFragment extends Fragment implements TextWatcher, SuggestionsAdapter.ISuggestionClick {

    private RecyclerView searchSuggestions;
    private RecyclerView medicationsList;
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
        getActivity().setTitle(getString(R.string.my_medications));
        View view = inflater.inflate(R.layout.fragment_medications, container, false);

        searchQuery = (EditText) view.findViewById(R.id.searchQuery);
        searchSuggestions = (RecyclerView) view.findViewById(R.id.searchSuggestions);
        medicationsList = (RecyclerView) view.findViewById(R.id.medicationsList);
        searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.medications_progress);

        searchQuery.addTextChangedListener(this);

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

    private void setMedicationsAdapter(List<String> list) {
        SuggestionsAdapter adapter = new SuggestionsAdapter(list, getActivity(), null);
        medicationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicationsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getMedications() {
        SampleApplication.getInstance().getAWSDK().getConsumerManager().getMedications(
                SDKUtils.getInstance().getConsumer(),
                new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            SDKUtils.getInstance().setMedications(medications);
                        }
                        searchLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        setMedicationsAdapter(getSuggestions(medications));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        searchLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void updateMedications() {
        progressBar.setVisibility(View.GONE);
        SampleApplication.getInstance().getAWSDK().getConsumerManager().updateMedications(
                SDKUtils.getInstance().getConsumer(),
                SDKUtils.getInstance().getMedications(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);
                        getMedications();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void searchMedications(String searchText) {
        progressBar.setVisibility(View.VISIBLE);
        SampleApplication.getInstance().getAWSDK().getConsumerManager().searchMedications(
                SDKUtils.getInstance().getConsumer(),
                searchText,
                new SDKValidatedCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onValidationFailure(Map<String, ValidationReason> map) {
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
                        searchSuggestions.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
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
        if (s.toString().length() > 1) {
            searchMedications(s.toString());
        }
    }

    @Override
    public void suggestionClick(String text, int position) {
        searchSuggestions.setVisibility(View.GONE);
        CommonUtil.hideSoftKeyboard(getActivity());

        List<Medication> medications = SDKUtils.getInstance().getMedications();
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
}
