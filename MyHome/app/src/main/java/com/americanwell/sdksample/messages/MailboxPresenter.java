/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.messages;

import com.americanwell.sdk.entity.securemessage.mailbox.MailboxMessage;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;

import icepick.State;

/**
 * Base implementation for mailboxes (inbox and sent)
 *
 * @since AWSDK 2.0
 */
public abstract class MailboxPresenter<T extends MailboxActivity> extends BaseSampleNucleusRxPresenter<T> {

    protected static final int REMOVE_MESSAGE = 740;

    @State
    protected MailboxMessage message;

    public void removeMessage(final MailboxMessage message) {
        this.message = message;
        start(REMOVE_MESSAGE);
    }
}
