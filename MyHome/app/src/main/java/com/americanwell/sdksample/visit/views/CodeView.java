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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View used to display data with code/description combos such as {@link
 * com.americanwell.sdk.entity.visit.VisitDiagnosis} and
 * {@link com.americanwell.sdk.entity.visit.VisitProcedure}.
 */
public class CodeView extends LinearLayout {
    @BindView(R.id.cv_code)
    TextView codeView;
    @BindView(R.id.cv_description)
    TextView descriptionView;

    public CodeView(Context context) {
        super(context);
        init(context);
    }

    public CodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_code, this, true);
        ButterKnife.bind(this);
    }

    public void setCode(String code) {
        codeView.setText(code);
    }

    public void setDescription(String description) {
        descriptionView.setText(description);
    }
}
