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
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.americanwell.sdk.entity.visit.TriageQuestion;
import com.prokarma.myhome.R;

/**
 * View for displaying triage question data
 */
public class TriageQuestionView extends TitledDataView {

    public TriageQuestionView(Context context) {
        super(context);
    }

    public TriageQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTriageQuestion(TriageQuestion triageQuestion) {
        setSuperText(triageQuestion.getQuestion());
        String answer = triageQuestion.getAnswer();
        if (!TextUtils.isEmpty(answer)) {
            subView.setTypeface(subView.getTypeface(), Typeface.NORMAL);
        }
        else {
            answer = getContext().getString(R.string.visit_reports_title_triage_no_answer);
            subView.setTypeface(subView.getTypeface(), Typeface.ITALIC);
        }
        setSubText(answer);
    }
}
