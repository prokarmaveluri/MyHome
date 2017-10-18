/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.messages;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.securemessage.detail.MessageDetail;
import com.americanwell.sdk.entity.securemessage.mailbox.InboxMessage;
import com.americanwell.sdk.entity.securemessage.mailbox.MailboxMessage;
import com.americanwell.sdk.exception.MessageNotReplyableException;
import com.americanwell.sdk.manager.SecureMessageManager;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for MessageDetailActivity
 */
public class MessageDetailPresenter extends FileAttachmentProviderPresenter<MessageDetailActivity> {

    private static final int GET_MESSAGE_DETAIL = 710;
    private static final int UPDATE_MESSAGE_READ = 711;
    private static final int REMOVE_MESSAGE = 713;

    @State
    MailboxMessage mailboxMessage;
    @State
    MessageDetail messageDetail;

    @Inject
    SecureMessageManager secureMessageManager;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_MESSAGE_DETAIL,
                new SampleRequestFunc0<SDKResponse<MessageDetail, SDKError>>(GET_MESSAGE_DETAIL) {
                    @Override
                    public Observable<SDKResponse<MessageDetail, SDKError>> go() {
                        return observableService.getMessageDetail(consumer, mailboxMessage);
                    }
                },
                new SampleResponseAction2<MessageDetail, SDKError, SDKResponse<MessageDetail, SDKError>>(GET_MESSAGE_DETAIL) {
                    @Override
                    public void onSuccess(MessageDetailActivity activity, MessageDetail mD) {
                        setMessageDetail(mD);
                    }
                },
                new SampleFailureAction2(GET_MESSAGE_DETAIL));

        restartableLatestCache(UPDATE_MESSAGE_READ,
                new SampleRequestFunc0<SDKResponse<MessageDetail, SDKError>>(UPDATE_MESSAGE_READ) {
                    @Override
                    public Observable<SDKResponse<MessageDetail, SDKError>> go() {
                        return observableService.updateMessageRead(
                                consumer,
                                (InboxMessage) mailboxMessage
                        );
                    }
                },
                new SampleResponseAction2<MessageDetail, SDKError, SDKResponse<MessageDetail, SDKError>>(UPDATE_MESSAGE_READ) {
                    @Override
                    public void onSuccess(MessageDetailActivity activity, MessageDetail messageDetail) {
                        start(GET_MESSAGE_DETAIL);
                    }
                },
                new SampleFailureAction2(UPDATE_MESSAGE_READ));

        restartableLatestCache(REMOVE_MESSAGE,
                new SampleRequestFunc0<SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public Observable<SDKResponse<MessageDetail, SDKError>> go() {
                        return observableService.removeMessage(consumer, messageDetail);
                    }
                },
                new SampleResponseAction2<MessageDetail, SDKError, SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public void onSuccess(MessageDetailActivity activity, MessageDetail messageDetail) {
                        activity.setMessageDeleted();
                    }
                },
                new SampleFailureAction2(REMOVE_MESSAGE));

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    // initialization - the activity will hand this over early on
    public void setMailboxMessage(final MailboxMessage mailboxMessage) {
        this.mailboxMessage = mailboxMessage;
    }

    // set and update view with message details
    private void setMessageDetail(final MessageDetail mD) {
        messageDetail = mD;
        view.setSubject(mD.getSubject());
        view.setRecipients(mD.getRecipients().isEmpty() ? null : mD.getRecipientsNames());
        view.setSender(mD.getSender().getFullName());
        view.setTopicType(mD.getTopicType());
        view.setBodyText(mD.getBodySpanned());
        view.setAttachments(mD.getAttachments());
        view.setDateTime(formatTimeStamp(mD.getTimestamp()));
        view.setCanReply(mD.canReply());
    }

    public boolean canReply() {
        return messageDetail != null && messageDetail.canReply();
    }


    @Override
    public void onTakeView(MessageDetailActivity view) {
        super.onTakeView(view);
        if (messageDetail == null) {
            if (isUnread()) { // if the message is unread, mark it read, which will then fetch the message
                start(UPDATE_MESSAGE_READ);
            }
            else {
                start(GET_MESSAGE_DETAIL); // just fetch it
            }
        }
        else {
            setMessageDetail(messageDetail); // if we already have it don't bother fetching it!
        }
    }

    private boolean isUnread() {
        return mailboxMessage instanceof InboxMessage &&
                ((InboxMessage) mailboxMessage).isUnread();
    }

    public void removeMessage() {
        start(REMOVE_MESSAGE);
    }

    public void replyToMessage() {
        try {
            view.setMessageDraft(secureMessageManager.getReplyDraft(messageDetail));
        }
        catch (MessageNotReplyableException e) {
            view.setError(e);
        }
    }

    public void forwardMessage() {
        view.setMessageDraft(secureMessageManager.getForwardDraft(messageDetail));
    }
}
