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
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.Topic;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.TopicView;

import java.util.List;

/**
 * Sub Adapter used for controlling Topics data in the RangedAdapter
 */
public class TopicsAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<Topic> mTopics;

    TopicsAdapter(VisitReportDetail report) {
        mTopics = report.getConsumerSummaryTopics();
    }

    @Override
    int getCount() {
        return mTopics != null ? mTopics.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_TOPICS;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TopicView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TopicView) holder.itemView).setTopic(mTopics.get(position));
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_TOPICS};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_TOPICS;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_topics;
    }
}
