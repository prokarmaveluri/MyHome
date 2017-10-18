/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.List;

import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to add medications to consumer's profile
 */
@RequiresPresenter(AddMedicationPresenter.class)
public class AddMedicationActivity extends BaseSampleNucleusActivity<AddMedicationPresenter> {

    public static String EXTRA_SELECTED_MEDICATION = "selectedMedication";

    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private MedicationsAdapter medicationsAdapter;

    public static Intent makeIntent(@NonNull final Context context) {
        return new Intent(context, AddMedicationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_add_medication);

        medicationsAdapter = new MedicationsAdapter(this);
        listView.setAdapter(medicationsAdapter);

        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getPresenter().searchMedications(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                })
        );
    }

    // presenter will call this when meds are found
    public void setMedications(final List<Medication> medications) {
        medicationsAdapter.clear();
        medicationsAdapter.addAll(medications);
    }

    // for restoring state on rotate
    public void setSearchTerm(final String searchTerm) {
        searchView.setQuery(searchTerm, false);
    }

    /**
     * Select a medication to add to the list of existing medications.
     *
     * @param medication (Medication) the selected medication
     */
    private void selectMedication(@NonNull final Medication medication) {
        final Intent result = new Intent();
        result.putExtra(EXTRA_SELECTED_MEDICATION, medication);
        setResult(RESULT_OK, result);
        finish();
    }

    public void setEmptyMedications(final boolean empty, final String query) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty) {
            emptyView.setText(getString(R.string.add_medication_empty, query));
        }
    }

    public class MedicationsAdapter extends ArrayAdapter<Medication> {
        public MedicationsAdapter(final Context context) {
            super(context, 0);
        }

        private class ViewHolder {
            TextView textView;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view;
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final Medication medication = getItem(position);
            viewHolder.textView.setText(medication.getName());
            viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMedication(medication);
                }
            });
            return view;
        }
    }

}
