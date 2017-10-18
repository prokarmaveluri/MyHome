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

import com.americanwell.sdk.entity.visit.Topic;

/**
 * View for displaying topic data
 */
public class TopicView extends TitledDataView {

    public TopicView(Context context) {
        super(context);
    }

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopic(Topic topic) {
        setSuperText(topic.getTitle());
        setSubText(topic.getDescription());
    }


}
