package com.televisit.previousvisit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitRx;
import com.prokarma.myhome.R;

import java.util.List;

/**
 * Created by veluri on 12/02/17.
 */

public class PrescriptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<VisitRx> prescriptionsList;

    public PrescriptionsAdapter(List<VisitRx> prescriptionsList) {
        this.prescriptionsList = prescriptionsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;
        VisitRx prescription = prescriptionsList.get(position);

        myHolder.prescriptionName.setText("Prescription: " + prescription.getName());

        if (prescription.getDescription() != null) {
            myHolder.prescriptionDesc.setText("Description: " + prescription.getDescription());
        } else {
            myHolder.prescriptionDesc.setVisibility(View.GONE);
        }

        if (prescription.getDosage() != null) {
            myHolder.prescriptionDose.setText("Dosage: " + prescription.getDosage());
        } else {
            myHolder.prescriptionDose.setVisibility(View.GONE);
        }

        if (prescription.getDuration() != null) {
            myHolder.prescriptionDuration.setText("Duration: " + prescription.getDuration());
        } else {
            myHolder.prescriptionDuration.setVisibility(View.GONE);
        }

        if (prescription.getFrequency() != null) {
            myHolder.prescriptionFrequency.setText("Frequency: " + prescription.getFrequency());
        } else {
            myHolder.prescriptionFrequency.setVisibility(View.GONE);
        }

        if (prescription.getInstructions() != null) {
            myHolder.prescriptionInstructions.setText("Instructions: " + prescription.getInstructions());
        } else {
            myHolder.prescriptionInstructions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (prescriptionsList == null || prescriptionsList.isEmpty()) {
            return 0;
        }
        return prescriptionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView prescriptionName;
        public TextView prescriptionDesc;
        public TextView prescriptionDose;
        public TextView prescriptionDuration;
        public TextView prescriptionFrequency;
        public TextView prescriptionInstructions;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            prescriptionName = (TextView) view.findViewById(R.id.prescription_name);
            prescriptionDesc = (TextView) view.findViewById(R.id.prescription_desc);
            prescriptionDose = (TextView) view.findViewById(R.id.prescription_dose);
            prescriptionDuration = (TextView) view.findViewById(R.id.prescription_duration);
            prescriptionFrequency = (TextView) view.findViewById(R.id.prescription_frequency);
            prescriptionInstructions = (TextView) view.findViewById(R.id.prescription_instructions);
        }
    }

}
