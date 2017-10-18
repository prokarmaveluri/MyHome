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
import android.support.annotation.StringRes;
import android.widget.ArrayAdapter;

import com.americanwell.sdk.entity.securemessage.mailbox.SentMessage;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import nucleus.factory.RequiresPresenter;

/**
 * Activity for Sent Messages mailbox
 */
@RequiresPresenter(SentMessagesPresenter.class)
public class SentMessagesActivity extends MailboxActivity<SentMessage, SentMessagesPresenter> {

    public static Intent makeIntent(final Context context) {
        return new Intent(context, SentMessagesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_sent_messages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().getMessages();
    }

    @Override
    protected ArrayAdapter createAdapter() {
        // uses base impl of adapter
        return new MailboxAdapter(this, getPresenter().getPreferredLocale());
    }

    @Override
    @StringRes
    protected int getTitleRes() {
        return 0; // don't use it here
    }

    @Override
    public void onRefresh() {
        getPresenter().getMessages();
    }
}
