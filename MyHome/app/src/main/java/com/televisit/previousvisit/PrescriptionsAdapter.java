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

        if (prescription.getQuantity() != null) {
            myHolder.prescriptionQuantity.setText("Quantity: " + prescription.getQuantity());
        } else {
            myHolder.prescriptionQuantity.setVisibility(View.GONE);
        }

        if (prescription.getDosage() != null) {
            myHolder.prescriptionDose.setText("Dosage: " + prescription.getDosage());
        } else {
            myHolder.prescriptionDose.setVisibility(View.GONE);
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
        public TextView prescriptionDose;
        public TextView prescriptionDesc;
        public TextView prescriptionQuantity;
        public TextView prescriptionInstructions;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            prescriptionName = (TextView) view.findViewById(R.id.prescription_name);
            prescriptionDose = (TextView) view.findViewById(R.id.prescription_dose);
            prescriptionDesc = (TextView) view.findViewById(R.id.prescription_desc);
            prescriptionQuantity = (TextView) view.findViewById(R.id.prescription_quantity);
            prescriptionInstructions = (TextView) view.findViewById(R.id.prescription_instructions);
        }
    }

}
