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
import com.americanwell.sdk.entity.securemessage.mailbox.Inbox;
import com.americanwell.sdk.manager.SecureMessageManager;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for InboxActivity
 */
public class InboxPresenter extends MailboxPresenter<InboxActivity> {

    private static final int GET_INBOX = 700;

    @Inject
    SecureMessageManager secureMessageManager;

    @State
    Inbox inbox;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_INBOX,
                new SampleRequestFunc0<SDKResponse<Inbox, SDKError>>(GET_INBOX) {
                    @Override
                    public Observable<SDKResponse<Inbox, SDKError>> go() {
                        return observableService.getInbox(consumer);
                    }
                },
                new SampleResponseAction2<Inbox, SDKError, SDKResponse<Inbox, SDKError>>(GET_INBOX) {
                    @Override
                    public void onSuccess(InboxActivity inboxActivity, Inbox inbox) {
                        setInbox(inbox);
                    }
                },
                new SampleFailureAction2(GET_INBOX));

        restartableLatestCache(REMOVE_MESSAGE,
                new SampleRequestFunc0<SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public Observable<SDKResponse<MessageDetail, SDKError>> go() {
                        return observableService.removeMessage(consumer, message);
                    }
                },
                new SampleResponseAction2<MessageDetail, SDKError, SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public void onSuccess(InboxActivity inboxActivity, MessageDetail messageDetail) {
                        inboxActivity.setMessageRemoved();
                        getMessages();
                    }
                },
                new SampleFailureAction2(REMOVE_MESSAGE));
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    public void getMessages() {
        start(GET_INBOX);
    }

    public void getNewMessageDraft() {
        view.setNewMessageDraft(secureMessageManager.getNewMessageDraft());
    }

    private void setInbox(final Inbox inbox) {
        this.inbox = inbox;
        view.setTitleCount(Long.valueOf(inbox.getUnread()).intValue());
        view.setListItems(inbox.getMessages());
    }

}
