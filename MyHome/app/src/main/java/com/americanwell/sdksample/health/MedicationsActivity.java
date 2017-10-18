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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to show and select medications
 */
@RequiresPresenter(MedicationsPresenter.class)
public class MedicationsActivity extends BaseSampleNucleusActivity<MedicationsPresenter> {

    private static final int REQUEST_ADD_MEDICATION = 1000;

    @BindView(R.id.medications_list_view)
    ListView medicationsListView;
    @BindView(R.id.add_medication_button)
    Button addMedicationButton;
    @BindView(R.id.medications_empty)
    TextView medicationsEmpty;
    @BindView(R.id.medications_footer)
    TextView medicationsFooter;

    private MedicationsAdapter medicationsAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, MedicationsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_medications);

        medicationsAdapter = new MedicationsAdapter(this);
        medicationsListView.setAdapter(medicationsAdapter);
    }

    public void setMedications(final List<Medication> medications) {
        if (medications.isEmpty()) {
            medicationsListView.setVisibility(View.GONE);
            medicationsEmpty.setVisibility(View.VISIBLE);
            medicationsFooter.setVisibility(View.GONE);
        }
        else {
            medicationsAdapter.clear();
            medicationsAdapter.addAll(medications);
            medicationsListView.setVisibility(View.VISIBLE);
            medicationsEmpty.setVisibility(View.GONE);
            medicationsFooter.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fab)
    public void updateMedications() {
        getPresenter().updateMedications();
    }

    public void setMedicationsUpdated() {
        Toast.makeText(this, R.string.medications_updated, Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.add_medication_button)
    public void onAddMedicationClick() {
        startActivityForResult(AddMedicationActivity.makeIntent(this), REQUEST_ADD_MEDICATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_MEDICATION && resultCode == RESULT_OK) {
            // if a medication has been added, via the AddMedicationPresenter, when it comes back - we display it here.
            final Medication medication = data.getParcelableExtra(AddMedicationActivity.EXTRA_SELECTED_MEDICATION);
            getPresenter().addMedication(medication);
        }
    }

    @OnItemClick(R.id.medications_list_view)
    public void onMedicationClick(int position) {
        final Medication medication = medicationsAdapter.getItem(position);
        getPresenter().toggleMedicationSelection(medication);
        medicationsAdapter.notifyDataSetChanged();
    }

    public class MedicationsAdapter extends ArrayAdapter<Medication> {
        public MedicationsAdapter(final Context context) {
            super(context, 0);
        }

        private class ViewHolder {
            CheckedTextView checkedTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(android.R.layout.simple_list_item_multiple_choice, null);
                viewHolder = new ViewHolder();
                viewHolder.checkedTextView = (CheckedTextView) view;
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Medication medication = getItem(position);
            viewHolder.checkedTextView.setText(medication.getName());
            viewHolder.checkedTextView.setChecked(getPresenter().isSelected(medication));
            return view;
        }
    }

}
