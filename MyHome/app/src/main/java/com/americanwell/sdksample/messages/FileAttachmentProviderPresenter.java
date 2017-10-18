/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.messages;

import android.os.Bundle;

import com.americanwell.sdk.entity.AttachmentReference;
import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.util.FileAttachmentProvider;

import icepick.State;
import rx.Observable;

/**
 * Base presenter for message detail and message draft providing some common base
 * implementation about attachment retrieval
 */
public abstract class FileAttachmentProviderPresenter<V extends BaseSampleNucleusActivity> extends BaseSampleNucleusRxPresenter<V> implements FileAttachmentProvider {

    protected static final int GET_ATTACHMENT = 740;

    @State
    protected AttachmentReference attachmentReference;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(GET_ATTACHMENT,
                new SampleRequestFunc0<SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public Observable<SDKResponse<FileAttachment, SDKError>> go() {
                        return observableService.getAttachment(consumer, attachmentReference);
                    }
                },
                new SampleResponseAction2<FileAttachment, SDKError, SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public void onSuccess(V activity, FileAttachment attachment) {
                        activity.setAttachment(attachment);
                        attachmentReference = null; // clear it out
                        stop(GET_ATTACHMENT); // make sure we don't restart this when returning from viewer
                    }
                },
                new SampleFailureAction2(GET_ATTACHMENT));
    }

    // the activity will call this if one of the attachment list items is clicked on
    public void getFileAttachment(final AttachmentReference attachmentReference) {
        this.attachmentReference = attachmentReference;
        start(GET_ATTACHMENT);
    }

}
