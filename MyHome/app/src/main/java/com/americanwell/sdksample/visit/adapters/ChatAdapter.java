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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.UserType;
import com.americanwell.sdk.entity.visit.ChatItem;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.BaseChatMessageView;
import com.americanwell.sdksample.visit.views.ChatMessageView;
import com.americanwell.sdksample.visit.views.SystemChatMessageView;

import java.util.List;

/**
 * Sub Adapter used for controlling Chat Item data in the RangedAdapter
 */
public class ChatAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<ChatItem> mChatItems;

    ChatAdapter(VisitReportDetail report) {
        mChatItems = report.getChatReport().getChatItems();
    }

    @Override
    int getCount() {
        return mChatItems != null ? mChatItems.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        int type;
        ChatItem chatItem = mChatItems.get(position);

        if (chatItem.getUserType().equals(UserType.ADMIN) ||
                chatItem.getUserType().equals(UserType.SDK)) {
            type = VisitAdapterConstants.TYPE_CHAT_SYSTEM;
        }
        else {
            type = VisitAdapterConstants.TYPE_CHAT_USER;
        }
        return type;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        View view;
        switch (viewType) {
            case VisitAdapterConstants.TYPE_CHAT_USER:
                view = new ChatMessageView(context);
                break;
            case VisitAdapterConstants.TYPE_CHAT_SYSTEM:
            default:
                view = new SystemChatMessageView(context);
                break;
        }
        view.setLayoutParams(params);
        return new VisitReportHolder(view);
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((BaseChatMessageView) holder.itemView).setChatMessage(mChatItems.get(position));
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_CHAT_SYSTEM,
                VisitAdapterConstants.TYPE_CHAT_USER};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return (viewType == VisitAdapterConstants.TYPE_CHAT_SYSTEM ||
                viewType == VisitAdapterConstants.TYPE_CHAT_USER);
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_transcript;
    }
}
