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
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.PostVisitFollowUpItem;
import com.prokarma.myhome.R;

import java.util.List;

/**
 * Sub Adapter used for controlling Post Visit Followup data in the RangedAdapter
 */
public class PostVisitFollowUpAdapter extends BaseSubAdapter<VisitReportHolder> {
    public static final int TYPE_POST_VISIT_FOLLOW_UP = 212;

    List<PostVisitFollowUpItem> followUpItems;

    PostVisitFollowUpAdapter(List<PostVisitFollowUpItem> items) {
        followUpItems = items;
    }

    @Override
    int getCount() {
        return followUpItems != null ? followUpItems.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        return TYPE_POST_VISIT_FOLLOW_UP;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TextView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TextView) holder.itemView).setText(followUpItems.get(position).getDescription());
    }

    @Override
    int[] getViewTypes() {
        return new int[]{TYPE_POST_VISIT_FOLLOW_UP};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == TYPE_POST_VISIT_FOLLOW_UP;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_additional_items;
    }
}
