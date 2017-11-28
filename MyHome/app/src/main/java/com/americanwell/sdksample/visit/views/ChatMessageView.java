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
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.UserType;
import com.americanwell.sdk.entity.visit.ChatItem;
import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for displaying chat message information
 */
public class ChatMessageView extends FrameLayout implements BaseChatMessageView {
    @BindView(R.id.chat_layout)
    LinearLayout layoutView;
    @BindView(R.id.chat_name)
    TextView nameView;
    @BindView(R.id.chat_message)
    TextView messageView;
    private ChatItem mMessage;

    public ChatMessageView(Context context) {
        super(context, null);
        init(context);
    }

    public ChatMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_chat_message, this, true);
        view.findViewById(R.id.chat_name);

        ButterKnife.bind(this);
    }

    @Override
    public void setChatMessage(@NonNull final ChatItem message) {
        mMessage = message;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        int pad = getResources().getDimensionPixelOffset(R.dimen.chat_view_padding);
        setPadding(pad, pad, pad, pad);

        boolean isMember = mMessage.getUserType() == UserType.MEMBER;
        int gravity = isMember ? Gravity.RIGHT : Gravity.LEFT;
        LayoutParams params = (LayoutParams) layoutView.getLayoutParams();
        params.gravity = gravity;
        layoutView.setLayoutParams(params);
        layoutView.setBackgroundResource(isMember ? R.color.color_chat_self_bg : R.color.color_chat_other_bg);
        layoutView.setGravity(gravity);

        int colorRes = isMember ? R.color.color_chat__self_header : R.color.color_chat__other_header;
        nameView.setTextColor(getContext().getResources().getColor(colorRes));
        nameView.setText(mMessage.getFullName());

        messageView.setText(mMessage.getDecodedMessage());
    }

}
