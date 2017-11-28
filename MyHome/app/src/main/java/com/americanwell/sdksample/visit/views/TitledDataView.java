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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for showing data with bolded header text followed by normal subtext
 */
public class TitledDataView extends LinearLayout {
    @BindView(R.id.titled_data_super_text)
    TextView superView;
    @BindView(R.id.titled_data_sub_text)
    TextView subView;

    public TitledDataView(Context context) {
        super(context, null);
        init(context, null);
    }

    public TitledDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_titled_data, this, true);
        ButterKnife.bind(this);
    }

    public void setSuperText(String superText) {
        superView.setText(superText);
        superView.setVisibility(TextUtils.isEmpty(superText) ? GONE : VISIBLE);

    }

    public void setSubText(String subText) {
        subView.setText(subText);
        subView.setVisibility(TextUtils.isEmpty(subText) ? GONE : VISIBLE);
    }
}
