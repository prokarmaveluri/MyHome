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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.ProviderEntries;
import com.americanwell.sdk.entity.visit.VisitDiagnosis;
import com.americanwell.sdk.entity.visit.VisitProcedure;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.CodeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Sub Adapter used for controlling diagnoses and procedures data in the RangedAdapter
 */
public class DiagnosesAndProceduresAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<VisitDiagnosis> mDiagnoses;
    List<VisitProcedure> mProcedures;

    DiagnosesAndProceduresAdapter(@NonNull ProviderEntries providerEntries) {
        mDiagnoses = new ArrayList<>(providerEntries.getDiagnoses());
        mProcedures = new ArrayList<>(providerEntries.getProcedures());
    }

    @Override
    int getCount() {
        int count = 0;
        if (mDiagnoses != null) {
            count += mDiagnoses.size();
        }
        if (mProcedures != null) {
            count += mProcedures.size();
        }
        if (count > 0) {
            count += 1;//add one for the subtitle
        }

        return count;
    }

    @Override
    int getItemViewType(int position) {
        int type;
        if (position != 0) {
            if (mDiagnoses != null) {
                type = position < mDiagnoses.size() ?
                        VisitAdapterConstants.TYPE_DIAGNOSES :
                        VisitAdapterConstants.TYPE_PROCEDURES;
            }
            else {
                type = VisitAdapterConstants.TYPE_PROCEDURES;
            }
        }
        else {
            type = VisitAdapterConstants.TYPE_TITLE_HEADER;
        }
        return type;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        switch (viewType) {
            case VisitAdapterConstants.TYPE_PROCEDURES:
            case VisitAdapterConstants.TYPE_DIAGNOSES:
                view = new CodeView(context);
                break;
            case VisitAdapterConstants.TYPE_TITLE_HEADER:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_code_header, parent, false);
                break;
        }

        return new VisitReportHolder(view);
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        String code;
        String description;
        if (position != 0) {
            int index = position - 1;
            if (mDiagnoses != null) {
                if (index < mDiagnoses.size()) {
                    VisitDiagnosis diagnosis = mDiagnoses.get(index);
                    code = diagnosis.getCode();
                    description = diagnosis.getDescription();
                }
                else {
                    index = index - mDiagnoses.size();
                    VisitProcedure procedure = mProcedures.get(index);
                    code = procedure.getCode();
                    description = procedure.getDescription();
                }
            }
            else {
                VisitProcedure procedure = mProcedures.get(index);
                code = procedure.getCode();
                description = procedure.getDescription();
            }
            CodeView codeView = (CodeView) holder.itemView;
            codeView.setCode(code);
            codeView.setDescription(description);
        }
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_DIAGNOSES,
                VisitAdapterConstants.TYPE_PROCEDURES, VisitAdapterConstants.TYPE_TITLE_HEADER};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_DIAGNOSES ||
                viewType == VisitAdapterConstants.TYPE_PROCEDURES ||
                viewType == VisitAdapterConstants.TYPE_TITLE_HEADER;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_diagnoses;
    }

}
