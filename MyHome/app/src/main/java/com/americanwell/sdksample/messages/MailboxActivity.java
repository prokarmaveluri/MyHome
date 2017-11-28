/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.messages;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.securemessage.mailbox.MailboxMessage;
import com.americanwell.sdksample.BaseSwipeToRefreshActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.util.SampleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base implementation for mailboxes (inbox and sent)
 */
public abstract class MailboxActivity<T extends MailboxMessage, P extends MailboxPresenter>
        extends BaseSwipeToRefreshActivity<P, T> {

    @BindString(R.string.msg_no_subject)
    String noSubject;

    // presenter calls this after a message has been removed successfully
    public void setMessageRemoved() {
        Toast.makeText(this, R.string.msg_removed, Toast.LENGTH_SHORT).show();
    }

    // base swipe adapter for mailbox messsages
    // the "swipe" here is NOT swipe-to-refresh, but actually item-level swiping...
    // right-to-left swipe on each item will reveal a "delete" option, if it's available
    public class MailboxAdapter extends ArraySwipeAdapter<T> {

        Locale locale;

        public MailboxAdapter(Context context, Locale locale) {
            super(context, 0);
            this.locale = locale;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.item_message_swiper;
        }

        public class ViewHolder {
            @BindView(R.id.paperclip)
            ImageView attachment; // attachment icon
            @BindView(R.id.timestamp)
            TextView timestamp;
            @BindView(R.id.message_name)
            TextView name;
            @BindView(R.id.subject_edit_text)
            TextView subject;
            @BindView(R.id.preview)
            TextView preview;
            @BindView(R.id.item_message_swiper)
            SwipeLayout swipeLayout;
            SwipeLayout.SwipeListener swipeListener;
            @BindView(R.id.item_message_delete)
            View delete;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder;

            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_message, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final T mailboxMessage = (T) getItem(position);

            // paperclip icon (for attachments)
            int paperclipVisibility = mailboxMessage.hasAttachment()
                    ? View.VISIBLE
                    : View.GONE;
            viewHolder.attachment.setVisibility(paperclipVisibility);

            // timestamp
            viewHolder.timestamp.setText(
                    localeUtils.smartFormatTimeStamp(getContext(), mailboxMessage.getTimestamp()));

            // preview
            if (!TextUtils.isEmpty(mailboxMessage.getBodyPreview())) {
                final Spanned previewSpanned = Html.fromHtml(mailboxMessage.getBodyPreview());
                viewHolder.preview.setText(previewSpanned);
            }

            // swipe handling
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            // set item on-click to open details
            // in using the swipe layout - you have to set the onclick on the surfaceview to get it to work
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(MessageDetailActivity.makeIntent(MailboxActivity.this, mailboxMessage));
                }
            });

            // swipe listener
            if (viewHolder.swipeListener != null) {
                viewHolder.swipeLayout.removeSwipeListener(viewHolder.swipeListener);
            }
            viewHolder.swipeListener = new SampleSwipeListener(swipeRefreshLayout);
            viewHolder.swipeLayout.addSwipeListener(viewHolder.swipeListener);

            // name
            viewHolder.name.setText(getName(mailboxMessage));
            viewHolder.name.setTypeface(null, useBold(mailboxMessage) ? Typeface.BOLD : Typeface.NORMAL);

            // subject
            final String subject = mailboxMessage.getSubject() != null ? mailboxMessage.getSubject() : noSubject;
            viewHolder.subject.setText(subject);
            viewHolder.subject.setTypeface(null, useBold(mailboxMessage) ? Typeface.BOLD : Typeface.NORMAL);

            // delete
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().removeMessage(mailboxMessage);
                }
            });

            viewHolder.swipeLayout.setSwipeEnabled(true);

            return view;
        }

        /**
         * return true if this particular message should be bolded
         *
         * @param mailboxMessage
         * @return
         */
        protected boolean useBold(final T mailboxMessage) {
            return false;
        }

        /**
         * return the name to display in the list
         *
         * @param mailboxMessage
         * @return
         */
        protected String getName(final T mailboxMessage) {
            return mailboxMessage.getRecipientsNames();
        }
    }
}
