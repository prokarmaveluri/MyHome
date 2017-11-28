/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.messages;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.UploadAttachment;
import com.americanwell.sdk.entity.securemessage.TopicType;
import com.americanwell.sdk.entity.securemessage.detail.AddressableMessageDraft;
import com.americanwell.sdk.entity.securemessage.detail.MessageDraft;
import com.americanwell.sdk.entity.securemessage.detail.NewMessageDraft;
import com.americanwell.sdk.entity.securemessage.detail.SecureMessageContact;
import com.americanwell.sdk.manager.SecureMessageManager;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.americanwell.sdksample.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for MessageDraftActivity
 */
public class MessageDraftPresenter extends FileAttachmentProviderPresenter<MessageDraftActivity> {

    private static final int GET_CONTACTS = 721;
    private static final int SEND_MESSAGE = 722;

    @Inject
    FileUtils fileUtils;
    @Inject
    SecureMessageManager secureMessageManager;

    @State
    MessageDraft draft;
    @State
    ArrayList<TopicType> topicTypes;
    @State
    ArrayList<SecureMessageContact> contacts;
    @State
    String attachmentName;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_CONTACTS,
                new SampleRequestFunc0<SDKResponse<List<SecureMessageContact>, SDKError>>(GET_CONTACTS) {
                    @Override
                    public Observable<SDKResponse<List<SecureMessageContact>, SDKError>> go() {
                        return observableService.getContacts(consumer);
                    }
                },
                new SampleResponseAction2<List<SecureMessageContact>, SDKError, SDKResponse<List<SecureMessageContact>, SDKError>>(GET_CONTACTS) {
                    @Override
                    public void onSuccess(MessageDraftActivity messageDraftActivity, List<SecureMessageContact> secureMessageContacts) {
                        setContacts((ArrayList<SecureMessageContact>) secureMessageContacts);
                    }
                },
                new SampleFailureAction2(GET_CONTACTS));

        restartableLatestCache(SEND_MESSAGE,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(SEND_MESSAGE) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.sendMessage(consumer, draft);
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(SEND_MESSAGE) {
                    @Override
                    public void onSuccess(MessageDraftActivity messageDraftActivity, Void aVoid) {
                        stop(SEND_MESSAGE);
                        messageDraftActivity.setMessageSent();
                    }
                },
                new SampleFailureAction2(SEND_MESSAGE));

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(MessageDraftActivity view) {
        super.onTakeView(view);
        if (draft != null) { // draft gets set earlier

            view.setSubject(draft.getSubject());
            view.setMessageBody(draft.getBodySpanned());
            if (!TextUtils.isEmpty(attachmentName)) {
                view.setAttachmentName(attachmentName);
            }

            if (draft instanceof NewMessageDraft) {
                if (topicTypes == null) {
                    topicTypes = (ArrayList<TopicType>) secureMessageManager.getTopicTypes();
                }
                setTopicTypes(topicTypes);
                view.setTopicType(draft.getTopicType()); // if we have a stored type, use it
                view.setTopicTypeText(null); // use spinner not text here
            }
            else {
                view.setTopicTypes(null); // hide spinner
                view.setTopicTypeText(draft.getTopicType()); // set text
            }

            if (draft instanceof AddressableMessageDraft) {
                if (contacts == null) {
                    start(GET_CONTACTS);
                }
                else {
                    setContacts(contacts);
                    view.setRecipient(((AddressableMessageDraft) draft).getRecipientContact());
                }
                view.setRecipientName(null); // use spinner not text here
            }
            else {
                view.setContacts(null); // hide spinner
                view.setRecipientName(draft.getRecipientName()); // set text
            }

            view.setShareHealthHistory(draft.isAttachHealthSummary());
            view.setSourceAttachments(draft.getSourceAttachments());
        }
        view.setHasAttachment(hasAttachment());

    }

    public void setTopicTypes(final ArrayList<TopicType> topicTypes) {
        this.topicTypes = topicTypes;
        view.setTopicTypes(topicTypes);
    }

    public void setContacts(final ArrayList<SecureMessageContact> secureMessageContacts) {
        this.contacts = secureMessageContacts;
        final List<SecureMessageContact> availableContacts = new ArrayList<>();
        // loop thru contacts and remove those that don't accept secure messages
        // this is also validated when sending the message, but this filter avoids that issue
        for (SecureMessageContact contact : secureMessageContacts) {
            if (contact.acceptsSecureMessage()) {
                availableContacts.add(contact);
            }
        }
        view.setContacts(availableContacts);
    }

    public void setDraft(final MessageDraft draft) {
        this.draft = draft;
    }

    public void setSubject(final String subject) {
        draft.setSubject(subject);
    }

    public void setRecipient(SecureMessageContact recipient) {
        if (draft instanceof AddressableMessageDraft) {
            ((AddressableMessageDraft) draft).setRecipientContact(recipient);
        }
    }

    public void setTopicType(TopicType topicType) {
        if (draft instanceof NewMessageDraft) {
            ((NewMessageDraft) draft).setTopicType(topicType);
        }
    }

    public void setMessageBody(Editable messageBody) {
        draft.setBody(messageBody);
    }

    public void sendMessage() {
        start(SEND_MESSAGE);
    }

    public void setAttachmentUri(final Uri attachmentUri) {
        try {
            if (attachmentUri != null) {
                final UploadAttachment uploadAttachment = fileUtils.getUploadAttachment(view, awsdk, attachmentUri);
                attachmentName = uploadAttachment.getName();
                draft.setAttachment(uploadAttachment);
                view.setAttachmentName(uploadAttachment.getName());
                view.setHasAttachment(true);
            }
            else {
                attachmentName = null;
                draft.setAttachment(null);
                view.setAttachmentName(null);
                view.setHasAttachment(false);
            }
        }
        catch (Exception e) {
            // clear attachment
            attachmentName = null;
            view.setAttachmentName(null);
            view.setHasAttachment(false);

            view.setError(e);
        }
    }

    public boolean hasAttachment() {
        return !TextUtils.isEmpty(attachmentName);
    }

    public void setShareHealthHistory(final boolean share) {
        draft.setAttachHealthSummary(share);
    }

}
