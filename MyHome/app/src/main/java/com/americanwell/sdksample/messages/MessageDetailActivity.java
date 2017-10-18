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
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.AttachmentReference;
import com.americanwell.sdk.entity.securemessage.TopicType;
import com.americanwell.sdk.entity.securemessage.detail.MessageDraft;
import com.americanwell.sdk.entity.securemessage.mailbox.MailboxMessage;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.AttachmentsAdapter;
import com.americanwell.sdksample.util.FileUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for message detail
 */
@RequiresPresenter(MessageDetailPresenter.class)
public class MessageDetailActivity extends BaseSampleNucleusActivity<MessageDetailPresenter> {

    private final static String EXTRA_MAILBOXMESSAGE = "mailboxMessage";

    @Inject
    FileUtils fileUtils;

    @BindView(R.id.recipient_section)
    View recipientSection;
    @BindView(R.id.recipient_text)
    TextView recipientTextView;
    @BindView(R.id.sender_text)
    TextView senderTextView;
    @BindView(R.id.topic_type_section)
    View topicTypeSection;
    @BindView(R.id.topic_type_text)
    TextView topicTypeTextView;
    @BindView(R.id.subject_text)
    TextView subjectTextView;
    @BindView(R.id.message_body_text)
    TextView bodyTextView;
    @BindView(R.id.attachments_section)
    View attachmentsSection;
    @BindView(R.id.attachments_list)
    ListView attachmentsList;
    @BindView(R.id.timestamp_text)
    TextView timestampTextView;

    @BindString(R.string.msg_no_subject)
    String noSubject;
    @BindString(R.string.format_date_time_detail)
    String dateFormatString;

    private MenuItem deleteMenuItem;
    private MenuItem forwardMenuItem;
    private MenuItem replyMenuItem;

    private AttachmentsAdapter attachmentsAdapter;

    public static Intent makeIntent(final Context context, final MailboxMessage mailboxMessage) {
        final Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra(EXTRA_MAILBOXMESSAGE, mailboxMessage); // the message who's detail we're going to view
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_message_detail);

        attachmentsAdapter = new AttachmentsAdapter(this, getPresenter());
        attachmentsList.setAdapter(attachmentsAdapter);
        if (savedInstanceState == null) {
            getPresenter().setMailboxMessage((MailboxMessage) getIntent().getExtras().getParcelable(EXTRA_MAILBOXMESSAGE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_detail, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete_message);
        deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getPresenter().removeMessage();
                return false;
            }
        });
        replyMenuItem = menu.findItem(R.id.action_reply_to_message);
        forwardMenuItem = menu.findItem(R.id.action_forward_message);
        forwardMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getPresenter().forwardMessage();
                return false;
            }
        });
        setCanReply(getPresenter().canReply());
        return true;
    }

    public void setSubject(final String subject) {
        final String sub = TextUtils.isEmpty(subject) ? noSubject : subject;
        setTitle(sub);
        subjectTextView.setText(sub);
    }

    public void setRecipients(final String recipientNames) {
        if (TextUtils.isEmpty(recipientNames)) {
            recipientSection.setVisibility(View.GONE);
        }
        else {
            recipientTextView.setText(recipientNames);
            recipientSection.setVisibility(View.VISIBLE);
        }
    }

    public void setSender(final String sender) {
        senderTextView.setText(sender);
    }

    public void setTopicType(final TopicType topicType) {
        if (topicType != null) {
            topicTypeTextView.setText(topicType.getName());
            topicTypeSection.setVisibility(View.VISIBLE);
        }
        else {
            topicTypeSection.setVisibility(View.GONE);
        }
    }

    public void setBodyText(final Spanned bodyTextSpanned) {
        bodyTextView.setText(bodyTextSpanned);
        bodyTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void setAttachments(final List<AttachmentReference> attachments) {
        if (attachments.size() > 0) {
            attachmentsAdapter.clear();
            attachmentsAdapter.addAll(attachments);
            attachmentsSection.setVisibility(View.VISIBLE);
        }
        else {
            attachmentsSection.setVisibility(View.GONE);
        }
    }

    public void setDateTime(String dateTime) {
        timestampTextView.setText(dateTime);
    }

    public void setCanReply(final boolean canReply) {
        if (deleteMenuItem != null) {
            replyMenuItem.setVisible(canReply);
            replyMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    getPresenter().replyToMessage();
                    return false;
                }
            });
        }
    }

    public void setMessageDeleted() {
        Toast.makeText(this, R.string.msg_removed, Toast.LENGTH_LONG).show();
        finish();
    }

    // presenter will set this after reply or forward draft has been created
    public void setMessageDraft(final MessageDraft messageDraft) {
        startActivity(MessageDraftActivity.makeIntent(this, messageDraft));
    }

}
