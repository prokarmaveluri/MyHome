/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.UserType;
import com.americanwell.sdk.entity.visit.ChatItem;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.util.SampleUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment for rendering chats in the waiting room
 *
 * @since AWSDK 2.1
 */
public class WaitingRoomChatFragment extends Fragment {

    private final static String LOG_TAG = WaitingRoomChatFragment.class.getName();

    private WaitingRoomPresenter presenter;
    private ChatAdapter chatAdapter;

    @BindView(R.id.chatItems)
    ListView chatItemsView;
    @BindView(R.id.chatItemEdit)
    EditText chatItemEdit;
    @BindView(R.id.chatSendButton)
    Button chatSendButton;

    public static WaitingRoomChatFragment newInstance() {
        return new WaitingRoomChatFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.waiting_room_chat_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = ((WaitingRoomActivity) getActivity()).getPresenter();
        chatAdapter = new ChatAdapter(getActivity(), presenter.getChatItems());
        chatItemsView.setAdapter(chatAdapter);
        return view;
    }

    @OnClick(R.id.chatSendButton)
    protected void onSendChatClick() {
        if (!TextUtils.isEmpty(chatItemEdit.getText())) {
            presenter.setChatMessage(chatItemEdit.getText().toString().trim());
            chatItemEdit.getText().clear();
            SampleUtils.hideKeyboard(getActivity(), chatItemEdit);
        }
    }

    public void setHasNewChatItems() {
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * adapter for chat items
     */
    public class ChatAdapter extends ArrayAdapter<ChatItem> {

        public ChatAdapter(Context context, List<ChatItem> objects) {
            super(context, 0, objects);
        }

        public class ViewHolder {
            @BindView(R.id.chatMessage)
            TextView messageView;
            @BindView(R.id.chatInfo)
            TextView infoView;
            @BindView(R.id.chatItem)
            LinearLayout chatItem;
            @BindView(R.id.chatBubble)
            LinearLayout chatBubble;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.chat_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ChatItem chatItem = getItem(position);
            handleMessage(holder, chatItem);

            return convertView;
        }

        private void handleMessage(final ViewHolder holder, final ChatItem chatItem) {
            setAlignment(holder, chatItem.isSelf());

            int infoVisibility = View.GONE;
            int bubbleVisibility = View.VISIBLE;
            String infoText = null;

            if (chatItem.getUserType().equals(UserType.ADMIN)) {
                infoVisibility = View.VISIBLE;
                bubbleVisibility = View.GONE;
                infoText = chatItem.getDecodedMessage() + "\n"; // add a CRLF for spacing
            }
            else if (!chatItem.isSelf()) {
                infoVisibility = View.VISIBLE;
                infoText = chatItem.getFullName();
            }

            holder.infoView.setText(infoText);
            holder.infoView.setVisibility(infoVisibility);

            holder.chatBubble.setVisibility(bubbleVisibility);
            holder.messageView.setText(chatItem.getDecodedMessage());
        }

        private void setAlignment(ViewHolder holder, boolean isMe) {
            if (isMe) {
                align(holder, R.drawable.chat_item_me_bg, Gravity.RIGHT, RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            else {
                align(holder, R.drawable.chat_item_them_bg, Gravity.LEFT, RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }

        private void align(final ViewHolder holder,
                           final int backgroundId,
                           final int gravity,
                           final int align1,
                           final int align2) {

            holder.chatBubble.setBackgroundResource(backgroundId);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.chatBubble.getLayoutParams();
            layoutParams.gravity = gravity;
            holder.chatBubble.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.chatItem.getLayoutParams();
            lp.addRule(align1, 0);
            lp.addRule(align2);
            holder.chatItem.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.messageView.getLayoutParams();
            layoutParams.gravity = gravity;
            holder.messageView.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.infoView.getLayoutParams();
            layoutParams.gravity = gravity;
            holder.infoView.setLayoutParams(layoutParams);
        }

    }

}
