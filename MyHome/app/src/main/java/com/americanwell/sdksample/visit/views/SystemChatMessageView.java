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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.ChatItem;
import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for displaying system chat messages
 */
public class SystemChatMessageView extends FrameLayout implements BaseChatMessageView {
    @BindView(R.id.system_chat_text)
    TextView systemMessage;

    public SystemChatMessageView(Context context) {
        super(context);
        init(context);
    }

    public SystemChatMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_system_chat_message, this, true);


        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        int margin = getResources().getDimensionPixelOffset(R.dimen.chat_view_padding);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) getLayoutParams();
        params.setMargins(margin, margin, margin, margin);
        setLayoutParams(params);
    }

    @Override
    public void setChatMessage(@NonNull final ChatItem message) {
        systemMessage.setText(message.getDecodedMessage());
    }
}
