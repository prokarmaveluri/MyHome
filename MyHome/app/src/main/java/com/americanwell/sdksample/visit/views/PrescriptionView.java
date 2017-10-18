/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitRx;
import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View used to display {@link VisitRx} data.
 */
public class PrescriptionView extends LinearLayout {
    @BindView(R.id.prescription_name)
    TextView nameView;
    @BindView(R.id.prescription_directions)
    TextView directionsView;

    public PrescriptionView(Context context) {
        super(context);
        init(context);
    }

    public PrescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_prescription, this, true);
        ButterKnife.bind(this);
    }

    public void setPrescription(@NonNull VisitRx prescription) {
        setName(prescription.getName());
        setDirections(prescription.getRxInstructions());
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setDirections(String directions) {
        directionsView.setText(directions);
    }
}
