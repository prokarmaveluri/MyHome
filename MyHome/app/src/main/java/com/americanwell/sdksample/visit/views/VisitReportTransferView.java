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
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;

/**
 * View for VisitDetail transfer information
 */
public class VisitReportTransferView extends LinearLayout {
    public VisitReportTransferView(Context context) {
        super(context, null);
        init(context, null);
    }

    public VisitReportTransferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_visit_report_subhead, this, true);
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs,
                    R.styleable.VisitReportSubHeaderView, 0, 0);
            String titleText = attributes.getString(R.styleable.VisitReportSubHeaderView_titleText);
            setTitle(titleText);
            attributes.recycle();
        }
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.visit_report_subhead_text)).setText(title);
    }

    public void setProvider(@NonNull String provider, String specialty) {
        String text = provider;
        if (!TextUtils.isEmpty(specialty)) {
            text = String.format(getResources().getString(R.string.visit_summary_transfer_provider),
                    provider,
                    specialty);
        }
        ((TextView) findViewById(R.id.xfer_view_provider)).setText(text);
    }

    public void setNotes(String notes) {
        if (!TextUtils.isEmpty(notes)) {
            ((TextView) findViewById(R.id.xfer_view_notes)).setText(notes);
        }
    }
}
