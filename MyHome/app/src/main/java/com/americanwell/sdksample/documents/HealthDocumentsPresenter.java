/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.documents;

import android.net.Uri;
import android.os.Bundle;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.UploadAttachment;
import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.americanwell.sdksample.util.FileUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for HealthDocumentsActivity
 */
public class HealthDocumentsPresenter extends BaseSampleNucleusRxPresenter<HealthDocumentsActivity> {

    // TODO - TEST REMOVING AND UPLOADING AND ROTATING

    @Inject
    FileUtils fileUtils;

    private static final int GET_HEALTH_DOCUMENTS = 1;
    private static final int GET_ATTACHMENT = 2;
    private static final int REMOVE_DOCUMENT = 3;
    private static final int UPLOAD_DOCUMENT = 4;

    @State
    ArrayList<DocumentRecord> documentRecords;
    @State
    DocumentRecord documentRecord;
    @State
    UploadAttachment uploadAttachment;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_HEALTH_DOCUMENTS,
                new SampleRequestFunc0<SDKResponse<List<DocumentRecord>, SDKError>>(GET_HEALTH_DOCUMENTS) {
                    @Override
                    public Observable<SDKResponse<List<DocumentRecord>, SDKError>> go() {
                        return observableService.getHealthDocuments(consumer);
                    }
                },
                new SampleResponseAction2<List<DocumentRecord>, SDKError, SDKResponse<List<DocumentRecord>, SDKError>>(GET_HEALTH_DOCUMENTS) {
                    @Override
                    public void onSuccess(HealthDocumentsActivity activity, List<DocumentRecord> documentRecords) {
                        setDocumentRecords((ArrayList<DocumentRecord>) documentRecords);
                    }
                },
                new SampleFailureAction2(GET_HEALTH_DOCUMENTS)
        );

        restartableLatestCache(GET_ATTACHMENT,
                new SampleRequestFunc0<SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public Observable<SDKResponse<FileAttachment, SDKError>> go() {
                        return observableService.getHealthDocumentAttachment(
                                consumer,
                                documentRecord
                        );
                    }
                },
                new SampleResponseAction2<FileAttachment, SDKError, SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public void onSuccess(HealthDocumentsActivity activity, FileAttachment attachment) {
                        activity.setAttachment(attachment);
                        stop(GET_ATTACHMENT); // w/o this it will try to re-get it after we return from the external viewer app
                    }
                },
                new SampleFailureAction2(GET_ATTACHMENT)
        );

        restartableLatestCache(REMOVE_DOCUMENT,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(REMOVE_DOCUMENT) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.removeHealthDocument(
                                consumer,
                                documentRecord
                        );
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(REMOVE_DOCUMENT) {
                    @Override
                    public void onSuccess(HealthDocumentsActivity activity, Void aVoid) {
                        stop(REMOVE_DOCUMENT);
                        activity.setHealthDocumentRemoved();
                        getHealthDocuments();
                    }
                },
                new SampleFailureAction2(REMOVE_DOCUMENT)
        );

        restartableLatestCache(UPLOAD_DOCUMENT,
                new SampleRequestFunc0<SDKValidatedResponse<DocumentRecord, SDKError>>(UPLOAD_DOCUMENT) {
                    @Override
                    public Observable<SDKValidatedResponse<DocumentRecord, SDKError>> go() {
                        return observableService.addHealthDocument(
                                consumer,
                                uploadAttachment
                        );
                    }
                },
                new SampleValidatedResponseAction2<DocumentRecord, SDKError, SDKValidatedResponse<DocumentRecord, SDKError>>(UPLOAD_DOCUMENT) {
                    @Override
                    public void onSuccess(HealthDocumentsActivity activity, DocumentRecord documentRecord) {
                        uploadAttachment = null;
                        activity.setHealthDocumentUploaded();
                        stop(UPLOAD_DOCUMENT);
                        getHealthDocuments();
                    }

                    @Override
                    public void onError(HealthDocumentsActivity healthDocumentsActivity, SDKError sdkError) {
                        super.onError(healthDocumentsActivity, sdkError);
                        uploadAttachment = null;
                    }
                },
                new SampleFailureAction2(UPLOAD_DOCUMENT)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(HealthDocumentsActivity view) {
        super.onTakeView(view);
        if (documentRecords == null) {
            getHealthDocuments();
        }
        else {
            setDocumentRecords(documentRecords);
        }
    }

    public void getHealthDocuments() {
        start(GET_HEALTH_DOCUMENTS);
    }

    public void getHealthDocumentAttachment(final DocumentRecord documentRecord) {
        this.documentRecord = documentRecord;
        start(GET_ATTACHMENT);
    }

    public void removeHealthDocument(final DocumentRecord documentRecord) {
        this.documentRecord = documentRecord;
        start(REMOVE_DOCUMENT);
    }

    public void uploadDocument(final Uri uri) {
        try {
            uploadAttachment = fileUtils.getUploadAttachment(view, awsdk, uri);
            start(UPLOAD_DOCUMENT);
        }
        catch (FileNotFoundException e) {
            view.setError(e);
        }
    }

    public void setDocumentRecords(ArrayList<DocumentRecord> documentRecords) {
        this.documentRecords = documentRecords;
        view.setListItems(documentRecords);
    }
}
