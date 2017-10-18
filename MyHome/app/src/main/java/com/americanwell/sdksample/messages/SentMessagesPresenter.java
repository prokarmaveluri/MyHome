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
import com.americanwell.sdk.entity.securemessage.mailbox.SentMessages;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for SentMessagesActivity
 */
public class SentMessagesPresenter extends MailboxPresenter<SentMessagesActivity> {

    private static final int GET_SENT_MESSAGES = 730;

    @State
    SentMessages sentMessages;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_SENT_MESSAGES,
                new SampleRequestFunc0<SDKResponse<SentMessages, SDKError>>(GET_SENT_MESSAGES) {
                    @Override
                    public Observable<SDKResponse<SentMessages, SDKError>> go() {
                        return observableService.getSentMessages(consumer);
                    }
                },
                new SampleResponseAction2<SentMessages, SDKError, SDKResponse<SentMessages, SDKError>>(GET_SENT_MESSAGES) {
                    @Override
                    public void onSuccess(SentMessagesActivity activity, SentMessages sentMessages) {
                        setSentMessages(sentMessages);
                    }
                },
                new SampleFailureAction2(GET_SENT_MESSAGES));

        restartableLatestCache(REMOVE_MESSAGE,
                new SampleRequestFunc0<SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public Observable<SDKResponse<MessageDetail, SDKError>> go() {
                        return observableService.removeMessage(consumer, message);
                    }
                },
                new SampleResponseAction2<MessageDetail, SDKError, SDKResponse<MessageDetail, SDKError>>(REMOVE_MESSAGE) {
                    @Override
                    public void onSuccess(SentMessagesActivity activity, MessageDetail messageDetail) {
                        activity.setMessageRemoved();
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
        start(GET_SENT_MESSAGES);
    }

    public void setSentMessages(final SentMessages sentMessages) {
        this.sentMessages = sentMessages;
        view.setListItems(sentMessages.getMessages());
    }
}
