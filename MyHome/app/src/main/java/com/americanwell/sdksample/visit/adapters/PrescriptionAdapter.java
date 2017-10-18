/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.visit.ProviderEntries;
import com.americanwell.sdk.entity.visit.VisitRx;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.PharmacyView;
import com.americanwell.sdksample.visit.views.PrescriptionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Sub Adapter used for controlling prescription data in the RangedAdapter
 */
public class PrescriptionAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<VisitRx> mPrescriptions;
    Pharmacy mPharmacy;

    PrescriptionAdapter(ProviderEntries providerEntries, Pharmacy pharmacy) {
        mPrescriptions = new ArrayList<>(providerEntries.getPrescriptions());
        mPharmacy = pharmacy;
    }

    @Override
    int getCount() {
        int count = 0;
        if (mPrescriptions != null) {
            count += mPrescriptions.size();
            if (mPharmacy != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    int getItemViewType(int position) {
        return position < mPrescriptions.size() ? VisitAdapterConstants.TYPE_PRESCRIPTIONS :
                VisitAdapterConstants.TYPE_PHARMACY;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        if (viewType == VisitAdapterConstants.TYPE_PRESCRIPTIONS) {
            view = new PrescriptionView(context);
        }
        else {
            view = new PharmacyView(context);
        }
        return new VisitReportHolder(view);
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        if (position < mPrescriptions.size()) {
            ((PrescriptionView) holder.itemView).setPrescription(mPrescriptions.get(position));
        }
        else {
            ((PharmacyView) holder.itemView).setPharmacy(mPharmacy);
        }

    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_PHARMACY,
                VisitAdapterConstants.TYPE_PRESCRIPTIONS};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_PRESCRIPTIONS ||
                viewType == VisitAdapterConstants.TYPE_PHARMACY;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_prescriptions;
    }
}
