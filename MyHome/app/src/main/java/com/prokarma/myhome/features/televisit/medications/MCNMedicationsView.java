package com.prokarma.myhome.features.televisit.medications;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.health.Medication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.MedicationRecyclerViewListener;
import com.prokarma.myhome.features.fad.suggestions.SuggestionsAdapter;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.medications.ui.MedicationsAdapter;
import com.prokarma.myhome.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsView implements MCNMedicationsContract.View,
        TextWatcher,
        SuggestionsAdapter.ISuggestionClick {

    private MCNMedicationsPresenter presenter;

    private RecyclerView searchSuggestions;
    private TextView noResults;
    private TextView medicationDesc;
    private TextView addAdditionalMedication;
    private RecyclerView medicationsList;
    private View divider;
    private LinearLayout noMedicationsLayout;
    private AppCompatCheckBox noMedicationsCheckbox;
    private EditText searchQuery;
    private RelativeLayout searchLayout;
    private ProgressBar progressBar;

    private List<Medication> searchList;
    private List<Medication> medicationsListToSave;

    public MCNMedicationsView(final View view, final MCNMedicationsPresenter presenter) {
        this.presenter = presenter;

        searchQuery = (EditText) view.findViewById(R.id.searchQuery);

        noResults = (TextView) view.findViewById(R.id.no_results);
        medicationDesc = (TextView) view.findViewById(R.id.medicationDesc);
        addAdditionalMedication = (TextView) view.findViewById(R.id.add_additional_medication);
        searchSuggestions = (RecyclerView) view.findViewById(R.id.searchSuggestions);
        medicationsList = (RecyclerView) view.findViewById(R.id.medicationsList);
        divider = (View) view.findViewById(R.id.divider);
        noMedicationsLayout = (LinearLayout) view.findViewById(R.id.no_medications_layout);
        noMedicationsCheckbox = (AppCompatCheckBox) view.findViewById(R.id.no_medications_checkbox);
        searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.medications_progress);

        medicationsList.setLayoutManager(new LinearLayoutManager(presenter.context, LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(presenter.context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(presenter.context, R.drawable.list_divider));
        medicationsList.addItemDecoration(itemDecoration);

        medicationsListToSave = new ArrayList<>();

        searchCancelClickEvent();
    }

    @Override
    public void showMedications(List<Medication> medications, String errorMessage) {

        progressBar.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

        if (medications != null) {
            medicationsListToSave.clear();
            medicationsListToSave.addAll(medications);
            setMedicationsAdapter(medications);
        }
    }

    @Override
    public void showUpdateMedications(List<Medication> medications, String errorMessage) {

        progressBar.setVisibility(View.GONE);

        AwsManager.getInstance().setMedications(medicationsListToSave);

        //presenter.activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showSearchMedications(List<Medication> medications, String errorMessage) {

        searchList = medications;
        progressBar.setVisibility(View.GONE);

        setSuggestionAdapter(getSuggestions(medications));

        if (medications == null || medications.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
            searchSuggestions.setVisibility(View.GONE);
            showSearchView();
        } else {
            noResults.setVisibility(View.GONE);
            searchSuggestions.setVisibility(View.VISIBLE);
        }
    }

    private void showSearchView() {
        addAdditionalMedication.setVisibility(View.GONE);
        medicationDesc.setVisibility(View.GONE);
        noMedicationsLayout.setVisibility(View.GONE);
        medicationsList.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
    }

    private void setSuggestionAdapter(List<String> list) {
        SuggestionsAdapter adapter = new SuggestionsAdapter(list, presenter.context, this);
        searchSuggestions.setLayoutManager(new LinearLayoutManager(presenter.context));
        searchSuggestions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setMedicationsAdapter(List<Medication> list) {

        if ((list != null && list.size() > 0) || noMedicationsCheckbox.isChecked()) {
            AwsManager.getInstance().setHasMedicationsFilledOut(true);
        }

        noResults.setVisibility(View.GONE);
        if (list != null && list.size() > 0) {

            addAdditionalMedication.setVisibility(View.VISIBLE);
            medicationDesc.setVisibility(View.VISIBLE);
            noMedicationsLayout.setVisibility(View.GONE);
            medicationsList.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            noMedicationsCheckbox.setChecked(false);

            MedicationsAdapter adapter = new MedicationsAdapter(list, new MedicationRecyclerViewListener() {
                @Override
                public void onItemClick(Object model, int position) {

                }

                @Override
                public void onDeleteClick(Object model, int position) {
                    if (model != null) {
                        medicationsListToSave.remove((Medication) model);
                        bindMedications();
                    }
                }
            });

            medicationsList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            noMedicationsLayout.setVisibility(View.VISIBLE);
            medicationsList.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            noMedicationsCheckbox.setChecked(AwsManager.getInstance().isHasMedicationsFilledOut());
        }
    }

    private void bindMedications() {
        searchLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        setMedicationsAdapter(medicationsListToSave);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        performSearch(s.toString());
    }

    private void performSearch(String searchText) {
        searchSuggestions.setVisibility(View.GONE);
        if (searchText.length() > 0) {
            presenter.searchMedications(searchText);
        } else {
            cancelSearch();
        }
    }

    @Override
    public void suggestionClick(String text, int position) {
        searchSuggestions.setVisibility(View.GONE);
        CommonUtil.hideSoftKeyboard(presenter.activity);

        if (medicationsListToSave == null) {
            medicationsListToSave = new ArrayList<>();
        }
        if (searchList != null && searchList.size() > position) {
            if (!medicationsListToSave.contains(searchList.get(position))) {
                medicationsListToSave.add(searchList.get(position));
            }
            searchQuery.setText("");
            bindMedications();
        }
    }

    private List<String> getSuggestions(List<Medication> list) {
        List<String> sugList = new ArrayList<>();
        for (Medication med : list) {
            sugList.add(med.getName());
        }
        return sugList;
    }

    private void searchCancelClickEvent() {
        searchQuery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if ((int) event.getRawX() >= (searchQuery.getRight() -
                            searchQuery.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        searchQuery.setText("");
                        cancelSearch();
                        return true;
                    } else {
                        showSearchView();
                    }
                }
                return false;
            }
        });
    }

    private void cancelSearch() {

        noResults.setVisibility(View.GONE);
        addAdditionalMedication.setVisibility(View.VISIBLE);
        medicationDesc.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        setMedicationsAdapter(medicationsListToSave);

        CommonUtil.hideSoftKeyboard(presenter.activity);
    }
}
