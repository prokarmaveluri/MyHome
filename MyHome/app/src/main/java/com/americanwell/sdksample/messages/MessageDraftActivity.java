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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.AttachmentReference;
import com.americanwell.sdk.entity.securemessage.TopicType;
import com.americanwell.sdk.entity.securemessage.detail.MessageDraft;
import com.americanwell.sdk.entity.securemessage.detail.SecureMessageContact;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.AttachmentsAdapter;
import com.americanwell.sdksample.util.SampleNamedNothingSelectedSpinnerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for handling a message draft - new, forward, or reply
 */
@RequiresPresenter(MessageDraftPresenter.class)
public class MessageDraftActivity extends BaseSampleNucleusActivity<MessageDraftPresenter> {

    private static final String EXTRA_DRAFT = "draft";
    private int ATTACH_REQUEST_CODE = 10000;

    @BindView(R.id.contact_spinner)
    Spinner contactSpinner;
    @BindView(R.id.contact_text)
    TextView contactText;
    @BindView(R.id.topic_type_section)
    View topicTypeSection;
    @BindView(R.id.topic_type_spinner)
    Spinner topicTypeSpinner;
    @BindView(R.id.topic_type_text)
    TextView topicTypeText;
    @BindView(R.id.subject_edit_text)
    EditText subjectEditText;
    @BindView(R.id.message_body)
    EditText messageBodyEditText;
    @BindView(R.id.attachment_layout)
    View attachmentLayout;
    @BindView(R.id.attachment_icon)
    ImageView attachmentIcon;
    @BindView(R.id.attachment_text)
    TextView attachmentText;
    @BindView(R.id.source_attachments_section)
    View sourceAttachmentsSection;
    @BindView(R.id.source_attachments_list)
    ListView sourceAttachmentsList;
    @BindView(R.id.share_health_history)
    Switch shareHealthHistory;

    @BindString(R.string.msg_error_no_contacts_title)
    String noContactsTitle;
    @BindString(R.string.msg_error_no_contacts_message)
    String noContactsMessage;

    private SampleNamedNothingSelectedSpinnerAdapter<SecureMessageContact> contactsAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter<TopicType> topicTypeAdapter;

    private AttachmentsAdapter sourceAttachmentsAdapter;

    public static Intent makeIntent(final Context context, final MessageDraft draft) {
        final Intent intent = new Intent(context, MessageDraftActivity.class);
        intent.putExtra(EXTRA_DRAFT, draft);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_message_draft);

        if (savedInstanceState == null) {
            // calling activity has to provide the message draft
            getPresenter().setDraft((MessageDraft) getIntent().getParcelableExtra(EXTRA_DRAFT));
        }

        sourceAttachmentsAdapter = new AttachmentsAdapter(this, getPresenter());
        sourceAttachmentsList.setAdapter(sourceAttachmentsAdapter);
    }

    @OnClick(R.id.fab)
    public void send() {
        getPresenter().sendMessage();
    }

    public void setMessageSent() {
        Toast.makeText(this, R.string.msg_sent_toast, Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.attachment_layout)
    public void onAttachmentClick() {
        if (getPresenter().hasAttachment()) {
            getPresenter().setAttachmentUri(null);
        }
        else {
            final Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, ATTACH_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ATTACH_REQUEST_CODE && resultCode == RESULT_OK) {
            getPresenter().setAttachmentUri(data.getData());
        }
    }

    public void setAttachmentName(final String attachmentName) {
        if (TextUtils.isEmpty(attachmentName)) {
            attachmentText.setText(R.string.msg_attachment_prompt);
        }
        else {
            attachmentText.setText(attachmentName);
        }
    }

    public void setHasAttachment(final boolean hasAttachment) {
        Picasso.with(this).load(hasAttachment
                ? R.drawable.ic_delete_forever_black_18dp
                : R.drawable.ic_add_a_photo_black_18dp).into(attachmentIcon);
    }

    @OnTextChanged(value = R.id.subject_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSubjectChanged(final CharSequence value) {
        getPresenter().setSubject(value.toString().trim());
    }

    public void setSubject(final String subject) {
        if (!TextUtils.isEmpty(subject)) {
            subjectEditText.setText(subject);
            subjectEditText.setSelection(subjectEditText.getText().length()); // move cursor to end
        }
    }

    @OnTextChanged(value = R.id.message_body, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onBodyChanged() {
        Editable editable = messageBodyEditText.getText();
        getPresenter().setMessageBody(editable);
    }

    public void setMessageBody(final Spanned messageBody) {
        if (!TextUtils.isEmpty(messageBody)) {
            messageBodyEditText.setText(messageBody); // may be pre-populated for fwd and reply
        }
    }

    public void setTopicTypes(final List<TopicType> topicTypes) {
        if (topicTypes != null) {
            topicTypeAdapter = new SampleNamedNothingSelectedSpinnerAdapter<>(this, topicTypes, R.layout.spinner_row_unselected_topic);
            topicTypeSpinner.setAdapter(topicTypeAdapter);
            topicTypeSection.setVisibility(View.VISIBLE);
        }
        else {
            topicTypeSection.setVisibility(View.GONE);
        }
    }

    public void setTopicType(final TopicType topicType) {
        if (topicType != null) {
            topicTypeSpinner.setSelection(topicTypeAdapter.getPosition(topicType));
        }
    }

    public void setTopicTypeText(final TopicType topicType) {
        if (topicType != null) {
            topicTypeText.setText(topicType.getName());
            topicTypeText.setVisibility(View.VISIBLE);
        }
        else {
            topicTypeText.setVisibility(View.GONE);
        }
    }

    @OnItemSelected(R.id.topic_type_spinner)
    public void onTopicSelected() {
        getPresenter().setTopicType((TopicType) topicTypeSpinner.getSelectedItem());
    }

    public void setContacts(final List<SecureMessageContact> contacts) {
        if (contacts != null) {
            if (!contacts.isEmpty()) {
                contactsAdapter = new SampleNamedNothingSelectedSpinnerAdapter<>(this, contacts, R.layout.spinner_row_unselected_contact);
                contactSpinner.setAdapter(contactsAdapter);
                contactSpinner.setVisibility(View.VISIBLE);
            }
            else {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
                sampleUtils.handleError(this, noContactsTitle, noContactsMessage, listener);
            }
        }
        else {
            contactSpinner.setVisibility(View.GONE);
        }
    }

    @OnItemSelected(R.id.contact_spinner)
    public void onContactSelected() {
        getPresenter().setRecipient((SecureMessageContact) contactSpinner.getSelectedItem());
    }

    public void setRecipient(final SecureMessageContact recipient) {
        if (recipient != null) {
            contactSpinner.setSelection(contactsAdapter.getPosition(recipient));
        }
    }

    public void setRecipientName(final String name) {
        if (TextUtils.isEmpty(name)) {
            contactText.setVisibility(View.GONE);
        }
        else {
            contactText.setText(name);
            contactText.setVisibility(View.VISIBLE);
        }
    }

    public void setShareHealthHistory(final boolean share) {
        shareHealthHistory.setChecked(share);
    }

    @OnCheckedChanged(R.id.share_health_history)
    public void onShareHistoryChecked(boolean checked) {
        getPresenter().setShareHealthHistory(checked);
    }

    // if we're replying or forwarding and the source message has attachments, show them here.
    // they're read-only but go ahead and read them :)
    public void setSourceAttachments(final List<AttachmentReference> attachments) {
        if (attachments != null && attachments.size() > 0) {
            sourceAttachmentsAdapter.clear();
            sourceAttachmentsAdapter.addAll(attachments);
            sourceAttachmentsSection.setVisibility(View.VISIBLE);
        }
        else {
            sourceAttachmentsSection.setVisibility(View.GONE);
        }
    }


}
