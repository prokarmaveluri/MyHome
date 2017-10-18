/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.content.Intent;
import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.ChatItem;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.entity.visit.VisitTransfer;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKStartVisitResponse;
import com.americanwell.sdksample.rx.SDKVisitTransferResponse;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for WaitingRoomActivity
 */
public class WaitingRoomPresenter extends BaseSampleNucleusRxPresenter<WaitingRoomActivity> {

    public static final String EXTRA_AWSDK_STATE = "awsdkState";

    private static final String LOG_TAG = WaitingRoomPresenter.class.getName();

    private static final int START_VISIT = 1120;
    private static final int CANCEL_VISIT = 1121;
    private static final int ACCEPT_SUGGESTED_TRANSFER = 1122;
    private static final int DECLINE_SUGGESTED_TRANSFER = 1123;
    private static final int ADD_CHAT_ITEM = 1124;
    private static final int ACCEPT_DECLINE_AND_TRANSFER = 1126;

    @State
    Visit visit;
    @State
    boolean started;
    @State
    boolean canceled;
    @State
    boolean transferAccepted;
    @State
    Visit transferVisit;
    @State
    String chatMessage;
    @State
    ArrayList<ChatItem> chatItems = new ArrayList<>();
    @State
    ArrayList<Long> ordinals = new ArrayList<>();
    @State
    Integer waitingRoomCount;
    @State
    String providerName;
    @State
    Visit newVisit;
    @State
    boolean declined;
    @State
    VisitTransfer lastVisitTransfer;
    @State
    Intent visitFinishedIntent;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(START_VISIT,
                new SampleRequestFunc0<SDKStartVisitResponse<Void, SDKError>>(START_VISIT) {
                    @Override
                    public Observable<SDKStartVisitResponse<Void, SDKError>> go() {
                        started = true;

                        // We're calling abandon() before startVisit() to ensure that there are no orphaned
                        // "startVisit()" processes and to avoid a VisitAlreadyStartedException being sent to
                        // onFailure(). This situation may happen if StartVisit has been called but somehow it has
                        // not finished gracefully.
                        abandon();

                        return observableService.startVisit(visit, visit.getConsumer().getAddress(), visitFinishedIntent);
                    }
                },
                new SampleStartVisitResponseAction2<Void, SDKError, SDKStartVisitResponse<Void, SDKError>>(START_VISIT) {
                    @Override
                    public void onProviderEntered(WaitingRoomActivity activity, Intent intent) {
                        stop(START_VISIT); // do not restart
                        view.setVisitIntent(intent);
                    }

                    @Override
                    public void onWaitingRoomEnded(WaitingRoomActivity activity, VisitEndReason visitEndReason) {
                        stop(START_VISIT); // do not restart

                        // if we have a transfer and one of the decline and transfer codes, process it
                        if (visit.getDeclineAndTransfer() != null) {
                            setTransferVisit(visit);
                        }
                        else if (!canceled && (visitEndReason != VisitEndReason.MEMBER_TRANSFER)) {
                            view.setEndReason(visitEndReason);
                        }
                    }

                    @Override
                    public void onPatientsAheadOfYouCountChanged(WaitingRoomActivity waitingRoomActivity, int count) {
                        setWaitingRoomCount(count, visit.getAssignedProvider().getFullName());
                    }

                    @Override
                    public void onSuggestedTransfer(WaitingRoomActivity waitingRoomActivity) {
                        setTransferVisit(visit);
                    }

                    @Override
                    public void onChat(WaitingRoomActivity waitingRoomActivity, ChatReport chatReport) {
                        setChatReport(chatReport);
                    }
                },
                new SampleFailureAction2(START_VISIT)
        );

        restartableLatestCache(CANCEL_VISIT,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(CANCEL_VISIT) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.cancelVisit(visit);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(CANCEL_VISIT) {
                    @Override
                    public void onSuccess(WaitingRoomActivity activity, Void aVoid) {
                        canceled = true;
                        view.setConsumerCancel();
                    }

                    @Override
                    public void onError(WaitingRoomActivity waitingRoomActivity, SDKError sdkError) {
                        handleCancelVisitProblem();
                    }
                },
                new SampleFailureAction2(CANCEL_VISIT) {
                    @Override
                    public void onFailure(WaitingRoomActivity waitingRoomActivity, Throwable throwable) {
                        handleCancelVisitProblem();
                    }
                }
        );

        restartableLatestCache(ADD_CHAT_ITEM,
                new SampleRequestFunc0<SDKResponse<ChatReport, SDKError>>(ADD_CHAT_ITEM) {
                    @Override
                    public Observable<SDKResponse<ChatReport, SDKError>> go() {
                        return observableService.sendChat(visit, chatMessage);
                    }
                },
                new SampleResponseAction2<ChatReport, SDKError, SDKResponse<ChatReport, SDKError>>(ADD_CHAT_ITEM) {
                    @Override
                    public void onSuccess(WaitingRoomActivity waitingRoomActivity, ChatReport chatReport) {
                        chatMessage = null;
                        stop(ADD_CHAT_ITEM);
                        setChatReport(chatReport);
                    }
                },
                new SampleFailureAction2(ADD_CHAT_ITEM)

        );

        restartableLatestCache(ACCEPT_SUGGESTED_TRANSFER,
                new SampleRequestFunc0<SDKVisitTransferResponse<VisitContext, SDKError>>(ACCEPT_SUGGESTED_TRANSFER) {
                    @Override
                    public Observable<SDKVisitTransferResponse<VisitContext, SDKError>> go() {
                        return observableService.acceptSuggestedTransfer(transferVisit);
                    }
                },
                new SampleVisitTransferResponseAction2<VisitContext, SDKError, SDKVisitTransferResponse<VisitContext, SDKError>>(ACCEPT_SUGGESTED_TRANSFER) {
                    @Override
                    public void onVisitTransfer(Visit v) {
                        stop(ACCEPT_SUGGESTED_TRANSFER);
                        setTransferVisit(v);
                    }

                    @Override
                    public void onProviderUnavailable() {
                        view.displayTransferFailed(visit.getAssignedProvider().getFullName());
                    }

                    @Override
                    public void onStartNewVisit(Visit v) {
                        stop(ACCEPT_SUGGESTED_TRANSFER);
                        view.startNewVisit(v);
                    }

                    @Override
                    public void onNewVisitContext(VisitContext visitContext) {
                        view.goToIntake(visitContext);
                    }
                },
                new SampleFailureAction2(ACCEPT_SUGGESTED_TRANSFER)
        );

        restartableLatestCache(ACCEPT_DECLINE_AND_TRANSFER,
                new SampleRequestFunc0<SDKVisitTransferResponse<VisitContext, SDKError>>(ACCEPT_DECLINE_AND_TRANSFER) {
                    @Override
                    public Observable<SDKVisitTransferResponse<VisitContext, SDKError>> go() {
                        return observableService.acceptDeclineAndTransfer(transferVisit);
                    }
                },
                new SampleVisitTransferResponseAction2<VisitContext, SDKError, SDKVisitTransferResponse<VisitContext, SDKError>>(ACCEPT_DECLINE_AND_TRANSFER) {
                    @Override
                    public void onVisitTransfer(Visit v) {
                        stop(ACCEPT_DECLINE_AND_TRANSFER);
                        setTransferVisit(v);
                    }

                    @Override
                    public void onProviderUnavailable() {
                        view.displayTransferFailed(visit.getAssignedProvider().getFullName());
                    }

                    @Override
                    public void onStartNewVisit(Visit v) {
                        stop(ACCEPT_DECLINE_AND_TRANSFER);
                        view.startNewVisit(v);
                    }

                    @Override
                    public void onNewVisitContext(VisitContext visitContext) {
                        view.goToIntake(visitContext);
                    }
                },
                new SampleFailureAction2(ACCEPT_DECLINE_AND_TRANSFER)
        );

        restartableLatestCache(DECLINE_SUGGESTED_TRANSFER,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(DECLINE_SUGGESTED_TRANSFER) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.declineTransfer(visit);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(DECLINE_SUGGESTED_TRANSFER) {
                    @Override
                    public void onSuccess(WaitingRoomActivity activity, Void aVoid) {
                        //The user has declined the suggested transfer and the server is now aware of it
                    }
                },
                new SampleFailureAction2(DECLINE_SUGGESTED_TRANSFER)
        );

    }

    private void handleCancelVisitProblem() {
        // If we cannot cancel for some reason, we will abandon the visit (client-side cleanup)
        // and finish()
        // This is a relatively simple reaction to the situation and more robust logic may be desired
        // in a production application.
        // Consider your use cases here.  It may be preferable to offer a retry mechanism or something
        // more sophisticated.
        awsdk.getVisitManager().abandonCurrentVisit();
        view.setFailedToCancel();
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(WaitingRoomActivity view) {
        super.onTakeView(view);
        if (!started) {
            startVisit();
        }
        else if (transferVisit != null) {
            setTransferVisit(transferVisit);
        }
        setChatItems(chatItems);
        setWaitingRoomCount(waitingRoomCount, providerName);
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Visit getVisit() {
        return visit;
    }

    public void startVisit() {
        start(START_VISIT);
    }

    public void cancelVisit() {
        start(CANCEL_VISIT);
    }

    public void setAcceptDeclineAndTransfer() {
        view.hideWaitingRoomCount();
        start(ACCEPT_DECLINE_AND_TRANSFER);
    }

    public void setAcceptSuggestedTransfer() {
        view.hideWaitingRoomCount();
        start(ACCEPT_SUGGESTED_TRANSFER);
    }

    public void setDeclineSuggestedTransfer() {
        transferVisit = null;
        start(DECLINE_SUGGESTED_TRANSFER);
    }

    public void setChatMessage(final String chatMessage) {
        this.chatMessage = chatMessage;
        start(ADD_CHAT_ITEM);
    }

    public List<ChatItem> getChatItems() {
        return chatItems;
    }

    private void setChatItems(final List<ChatItem> chatItems) {
        if (chatItems != null && !chatItems.isEmpty()) {
            view.showChat();
            for (ChatItem chatItem : chatItems) {
                if (!ordinals.contains(chatItem.getOrdinal())) { // avoid duplicates
                    ordinals.add(chatItem.getOrdinal());
                    this.chatItems.add(chatItem);
                }
            }
            view.setHasNewChatItems();
        }
    }

    private void setChatReport(final ChatReport chatReport) {
        setChatItems(chatReport.getChatItems());
    }

    private void setWaitingRoomCount(Integer count, final String providerName) {
        if (count != null && providerName != null) {
            this.waitingRoomCount = count;
            this.providerName = providerName;
            view.setWaitingRoomCount(count, providerName);
        }
    }

    private void setTransferVisit(final Visit transferVisit) {
        this.transferVisit = transferVisit;

        //      For the sake of this example app we put priority on the Forced Transfers however this logic is not enforced.
        VisitTransfer priorityTransfer;
        if (transferVisit.getDeclineAndTransfer() != null) {
            priorityTransfer = transferVisit.getDeclineAndTransfer();
        }
        else {
            priorityTransfer = transferVisit.getSuggestedTransfer();
        }

        // only handle this if we haven't already
        if (lastVisitTransfer == null || !lastVisitTransfer.equals(priorityTransfer)) {
            this.lastVisitTransfer = priorityTransfer;
            view.setTransferVisit(transferVisit);
        }
    }

    public String getProviderName() {
        return visit.getAssignedProvider().getFullName();
    }

    public void abandon() {
        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        awsdk.getVisitManager().abandonCurrentVisit();
    }

    public void setVisitFinishedIntent(final Intent intent) {

        // if the app gets killed during the visit console, for example, if a permission is revoked, this will help
        // us recover.  we write the awsdk state into the intent so we can pull it out later if needed
        final Bundle bundle = new Bundle();
        awsdk.saveInstanceState(bundle);
        intent.putExtra(EXTRA_AWSDK_STATE, bundle);

        this.visitFinishedIntent = intent;
    }
}
