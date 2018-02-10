package com.prokarma.myhome.features.televisit.medications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.health.Medication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.MedicationRecyclerViewListener;

import java.util.List;

/**
 * Created by kwelsh on 11/19/17.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Medication> medicationsList;
    private final MedicationRecyclerViewListener medicationRecyclerViewListener;

    public MedicationsAdapter(List<Medication> medicationsList, MedicationRecyclerViewListener medicationRecyclerViewListener) {
        this.medicationsList = medicationsList;
        this.medicationRecyclerViewListener = medicationRecyclerViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.medication_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;
        Medication medication = medicationsList.get(position);

        myHolder.medicationText.setText(medication.getName());
        myHolder.setOnItemClickListener(medication, medicationRecyclerViewListener);
        myHolder.setOnDeleteClickListener(medication, medicationRecyclerViewListener);
    }

    @Override
    public int getItemCount() {
        if (medicationsList == null || medicationsList.isEmpty()) {
            return 0;
        }

        return medicationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView medicationText;
        public TextView delete;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            medicationText = (TextView) view.findViewById(R.id.medication_text);
            delete = (TextView) view.findViewById(R.id.delete);
        }

        public void setOnItemClickListener(final Medication medication, final MedicationRecyclerViewListener medicationRecyclerViewListener) {
            medicationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medicationRecyclerViewListener.onItemClick(medication, getAdapterPosition());
                }
            });
        }

        public void setOnDeleteClickListener(final Medication medication, final MedicationRecyclerViewListener medicationRecyclerViewListener) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medicationRecyclerViewListener.onDeleteClick(medication, getAdapterPosition());
                }
            });
        }

    }

}
