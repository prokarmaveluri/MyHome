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
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.americanwell.sdk.entity.securemessage.detail.MessageDraft;
import com.americanwell.sdk.entity.securemessage.mailbox.InboxMessage;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.Locale;

import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for managing secure message inbox
 */
@RequiresPresenter(InboxPresenter.class)
public class InboxActivity extends MailboxActivity<InboxMessage, InboxPresenter> {

    public static Intent makeIntent(final Context context) {
        return new Intent(context, InboxActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_inbox);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().getMessages();
    }

    @Override
    protected ArrayAdapter createAdapter() {
        return new InboxAdapter(this, getPresenter().getPreferredLocale());
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_activity_inbox;
    }

    // Floating button here goes to create a new message
    @OnClick(R.id.fab)
    public void newMessage() {
        getPresenter().getNewMessageDraft();
    }

    // presenter calls this when a draft is ready to open
    public void setNewMessageDraft(final MessageDraft messageDraft) {
        startActivity(MessageDraftActivity.makeIntent(this, messageDraft));
    }

    @Override
    public void onRefresh() {
        getPresenter().getMessages();
    }

    /**
     * MailboxAdapter for inbox messages
     */
    public class InboxAdapter extends MailboxAdapter {
        public InboxAdapter(Context context, Locale locale) {
            super(context, locale);
        }

        @Override
        protected String getName(final InboxMessage mailboxMessage) {
            return mailboxMessage.getSenderName();
        }

        @Override
        protected boolean useBold(InboxMessage mailboxMessage) {
            // we want to bold the subject and name fields if the message is unread
            return mailboxMessage.isUnread();
        }
    }

}
