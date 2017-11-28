/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.AttachmentReference;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLaunchParams;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.UploadAttachment;
import com.americanwell.sdk.entity.billing.CreatePaymentRequest;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.entity.consumer.AppointmentReadiness;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.consumer.DependentUpdate;
import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdk.entity.consumer.RecoverEmailResponse;
import com.americanwell.sdk.entity.consumer.RemindOptions;
import com.americanwell.sdk.entity.enrollment.ConsumerEnrollment;
import com.americanwell.sdk.entity.enrollment.DependentEnrollment;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.insurance.SubscriptionUpdateRequest;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.AvailableProviders;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.securemessage.detail.MessageDetail;
import com.americanwell.sdk.entity.securemessage.detail.MessageDraft;
import com.americanwell.sdk.entity.securemessage.detail.SecureMessageContact;
import com.americanwell.sdk.entity.securemessage.mailbox.Inbox;
import com.americanwell.sdk.entity.securemessage.mailbox.InboxMessage;
import com.americanwell.sdk.entity.securemessage.mailbox.MailboxMessage;
import com.americanwell.sdk.entity.securemessage.mailbox.SentMessages;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.ConsumerFeedbackQuestion;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.entity.visit.Vitals;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.manager.SecureMessageManager;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * This (rather monolithic) class is a bridge between the SDK's asychnronous manager calls
 * and RxAndroid.  Provides Observable wrappers for all calls.
 * <p>
 * This is just one way of handling this, one which we think is a good practice. The use of
 * RxAndroid here, in combination with an MVP framework such as Nucleus, allows for nice handling
 * of long-running asynchronous requests - detached from the tricks of the Activity lifecycle.
 * If you have a preferred pattern, of course give it a try.
 * <p>
 * The Subscriber for the Observable is passed into one of several variations of
 * SubscriberSDKCallback which handles the callback methods and delegates to the Subscriber methods
 */
@SuppressWarnings("unchecked")
public class ObservableService {

    AWSDK awsdk;

    public ObservableService(AWSDK awsdk) {
        this.awsdk = awsdk;
    }

    // Login-related
    public Observable<SDKResponse<Void, SDKError>> initializeSdk(
            @NonNull final String baseServiceUrl,
            @NonNull final String clientKey,
            @NonNull final Uri launchUri) {
        return Observable.create(
                new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
                    @Override
                    public void call(final Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                        final Map<AWSDK.InitParam, Object> initParams = new HashMap<>();
                        initParams.put(AWSDK.InitParam.BaseServiceUrl, baseServiceUrl);
                        initParams.put(AWSDK.InitParam.ApiKey, clientKey);
                        initParams.put(AWSDK.InitParam.LaunchIntentData, launchUri);
                        try {
                            awsdk.initialize(
                                    initParams,
                                    new SubscriberSDKCallback(subscriber));
                        }
                        catch (AWSDKInitializationException e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

    public Observable<SDKResponse<Authentication, SDKError>> authenticate(
            @NonNull final String email,
            @NonNull final String password) {
        return Observable.create(
                new Observable.OnSubscribe<SDKResponse<Authentication, SDKError>>() {
                    @Override
                    public void call(final Subscriber<? super SDKResponse<Authentication, SDKError>> subscriber) {
                        awsdk.authenticate(
                                email,
                                password,
                                email,
                                new SubscriberSDKCallback(subscriber));
                    }
                }
        );
    }

    public Observable<SDKResponse<Authentication, SDKError>> thirdPartyAuthenticate(
            @NonNull final String authToken) {
        return Observable.create(
                new Observable.OnSubscribe<SDKResponse<Authentication, SDKError>>() {
                    @Override
                    public void call(final Subscriber<? super SDKResponse<Authentication, SDKError>> subscriber) {
                        awsdk.authenticateMutual(authToken, new SubscriberSDKCallback(subscriber));
                    }
                }
        );
    }

    public Observable<SDKResponse<Consumer, SDKError>> getConsumer(
            @NonNull final Authentication authentication) {
        return Observable.create(
                new Observable.OnSubscribe<SDKResponse<Consumer, SDKError>>() {
                    @Override
                    public void call(final Subscriber<? super SDKResponse<Consumer, SDKError>> subscriber) {
                        awsdk.getConsumerManager().getConsumer(
                                authentication,
                                new SubscriberSDKCallback(subscriber)
                        );
                    }
                }
        );
    }

    public Observable<SDKValidatedResponse<Consumer, SDKPasswordError>> updateConsumer(
            @NonNull final ConsumerUpdate consumerUpdate) {
        return Observable.create(
                new Observable.OnSubscribe<SDKValidatedResponse<Consumer, SDKPasswordError>>() {
                    @Override
                    public void call(Subscriber<? super SDKValidatedResponse<Consumer, SDKPasswordError>> subscriber) {
                        awsdk.getConsumerManager().updateConsumer(
                                consumerUpdate,
                                new SubscriberSDKValidatedCallback(subscriber)
                        );
                    }
                }
        );
    }

    public Observable<SDKValidatedResponse<Consumer, SDKError>> updateDependent(
            @NonNull final DependentUpdate dependentUpdate) {
        return Observable.create(
                new Observable.OnSubscribe<SDKValidatedResponse<Consumer, SDKError>>() {
                    @Override
                    public void call(Subscriber<? super SDKValidatedResponse<Consumer, SDKError>> subscriber) {
                        awsdk.getConsumerManager().updateDependent(
                                dependentUpdate,
                                new SubscriberSDKValidatedCallback(subscriber)
                        );
                    }
                }
        );
    }

    public Observable<SDKResponse<Consumer, SDKPasswordError>> completeEnrollment(
            @NonNull final Authentication authentication,
            @Nullable final State legalResidence,
            @Nullable final String newUsername,
            @Nullable final String newPassword) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Consumer, SDKPasswordError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Consumer, SDKPasswordError>> subscriber) {
                awsdk.getConsumerManager().completeEnrollment(
                        authentication,
                        legalResidence,
                        newUsername,
                        newPassword,
                        new SubscriberSDKValidatedCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<Appointment, SDKError>> getAppointment(
            @NonNull final Consumer consumer,
            @NonNull final SDKLaunchParams sdkLaunchParams) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Appointment, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Appointment, SDKError>> subscriber) {
                awsdk.getConsumerManager().getAppointment(
                        consumer,
                        sdkLaunchParams,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKStartConferenceResponse<Void, SDKError>> startGuestConference(
            @NonNull final SDKLaunchParams launchParams,
            @NonNull final String email,
            @NonNull final String name,
            @Nullable final Intent visitFinishedIntent) {
        return Observable.create(new Observable.OnSubscribe<SDKStartConferenceResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKStartConferenceResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().startGuestConference(
                        launchParams,
                        email,
                        name,
                        visitFinishedIntent,
                        new SubscriberSDKStartConferenceCallback((Subscriber<SDKStartConferenceResponse>) subscriber)
                );
            }
        });
    }

    // forgot email / password
    public Observable<SDKValidatedResponse<RecoverEmailResponse, SDKError>> recoverEmail(
            @NonNull final String lastName,
            @NonNull final SDKLocalDate dob) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<RecoverEmailResponse, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<RecoverEmailResponse, SDKError>> subscriber) {
                awsdk.getConsumerManager().recoverEmail(
                        lastName,
                        dob,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Void, SDKError>> resetPassword(
            @NonNull final String email,
            @NonNull final String lastName,
            @NonNull final SDKLocalDate dob) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().resetPassword(
                        email,
                        lastName,
                        dob,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // enrollment
    public Observable<SDKValidatedResponse<Consumer, SDKPasswordError>> enrollConsumer(
            @NonNull final ConsumerEnrollment enrollment) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Consumer, SDKPasswordError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Consumer, SDKPasswordError>> subscriber) {
                awsdk.getConsumerManager().enrollConsumer(
                        enrollment,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Consumer, SDKError>> enrollDependent(
            @NonNull final DependentEnrollment enrollment) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Consumer, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Consumer, SDKError>> subscriber) {
                awsdk.getConsumerManager().enrollDependent(
                        enrollment,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<String, SDKError>> getEnrollmentDisclaimer() {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<String, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<String, SDKError>> subscriber) {
                awsdk.getConsumerManager().getEnrollmentDisclaimer(new SubscriberSDKCallback(subscriber));
            }
        });
    }

    // service key
    public Observable<SDKResponse<Void, SDKError>> addServiceKey(
            @NonNull final Consumer consumer,
            @NonNull final String serviceKey) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().addServiceKey(
                        consumer,
                        serviceKey,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // practices and providers
    public Observable<SDKResponse<List<PracticeInfo>, SDKError>> findPractices(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<PracticeInfo>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<PracticeInfo>, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().findPractices(
                        consumer,
                        null,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    // practices
    public Observable<SDKResponse<List<PracticeInfo>, SDKError>> getPractices(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<PracticeInfo>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<PracticeInfo>, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().getPractices(
                        consumer,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    // secure messaging
    public Observable<SDKResponse<Inbox, SDKError>> getInbox(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Inbox, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Inbox, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().getInbox(
                        consumer,
                        null,
                        SecureMessageManager.GET_ALL_MESSAGES,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<MessageDetail, SDKError>> removeMessage(
            @NonNull final Consumer consumer,
            @NonNull final MailboxMessage message) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<MessageDetail, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<MessageDetail, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().removeMessage(
                        consumer,
                        message,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<SentMessages, SDKError>> getSentMessages(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<SentMessages, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<SentMessages, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().getSentMessages(
                        consumer,
                        null,
                        SecureMessageManager.GET_ALL_MESSAGES,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<MessageDetail, SDKError>> getMessageDetail(
            @NonNull final Consumer consumer,
            @NonNull final MailboxMessage mailboxMessage) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<MessageDetail, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<MessageDetail, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().getMessageDetail(
                        consumer,
                        mailboxMessage,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<MessageDetail, SDKError>> updateMessageRead(
            @NonNull final Consumer consumer,
            @NonNull final InboxMessage inboxMessage) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<MessageDetail, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<MessageDetail, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().updateMessageRead(
                        consumer,
                        inboxMessage,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<FileAttachment, SDKError>> getAttachment(
            @NonNull final Consumer consumer,
            @NonNull final AttachmentReference attachmentReference) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<FileAttachment, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<FileAttachment, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().getAttachment(
                        consumer,
                        attachmentReference,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<MessageDetail, SDKError>> removeMessage(
            @NonNull final Consumer consumer,
            @NonNull final MessageDetail messageDetail) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<MessageDetail, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<MessageDetail, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().removeMessage(
                        consumer,
                        messageDetail,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<List<SecureMessageContact>, SDKError>> getContacts(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<SecureMessageContact>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<SecureMessageContact>, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().getContacts(
                        consumer,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Void, SDKError>> sendMessage(
            @NonNull final Consumer consumer,
            @NonNull final MessageDraft messageDraft) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getSecureMessageManager().sendMessage(
                        consumer,
                        messageDraft,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // appointments
    public Observable<SDKResponse<List<Appointment>, SDKError>> getAppointments(
            @NonNull final Consumer consumer) { // NOTE: Not using "since"
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Appointment>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Appointment>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getAppointments(
                        consumer,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> cancelAppointment(
            @NonNull final Consumer consumer,
            @NonNull final Appointment appointment) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().cancelAppointment(
                        consumer,
                        appointment,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // visit reports
    public Observable<SDKResponse<List<VisitReport>, SDKError>> getVisitReports(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<VisitReport>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<VisitReport>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getVisitReports(
                        consumer,
                        null,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<FileAttachment, SDKError>> getVisitReportAttachment(
            @NonNull final Consumer consumer,
            @NonNull final VisitReport visitReport) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<FileAttachment, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<FileAttachment, SDKError>> subscriber) {
                awsdk.getConsumerManager().getVisitReportAttachment(
                        consumer,
                        visitReport,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<VisitReportDetail, SDKError>> getVisitReportDetail(
            @NonNull final Consumer consumer,
            @NonNull final VisitReport visitReport) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<VisitReportDetail, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<VisitReportDetail, SDKError>> subscriber) {
                awsdk.getConsumerManager().getVisitReportDetail(
                        consumer,
                        visitReport,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // past providers
    public Observable<SDKResponse<List<Provider>, SDKError>> getPastProviders(
            @NonNull final Consumer consumer, @Nullable final Integer maxResults) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Provider>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Provider>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getPastProviders(
                        consumer,
                        maxResults,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // health documents
    public Observable<SDKResponse<List<DocumentRecord>, SDKError>> getHealthDocuments(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<DocumentRecord>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<DocumentRecord>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getHealthDocumentRecords(
                        consumer,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<FileAttachment, SDKError>> getHealthDocumentAttachment(
            @NonNull final Consumer consumer,
            @NonNull final DocumentRecord documentRecord) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<FileAttachment, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<FileAttachment, SDKError>> subscriber) {
                awsdk.getConsumerManager().getHealthDocumentRecordAttachment(
                        consumer,
                        documentRecord,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> removeHealthDocument(
            @NonNull final Consumer consumer,
            @NonNull final DocumentRecord documentRecord) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().removeHealthDocumentRecord(
                        consumer,
                        documentRecord,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<DocumentRecord, SDKError>> addHealthDocument(
            @NonNull final Consumer consumer,
            @NonNull final UploadAttachment uploadAttachment) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<DocumentRecord, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<DocumentRecord, SDKError>> subscriber) {
                try {
                    awsdk.getConsumerManager().addHealthDocument(
                            consumer,
                            uploadAttachment,
                            new SubscriberSDKValidatedCallback(subscriber)
                    );
                }
                catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    // set location
    public Observable<SDKResponse<Void, SDKError>> setLegalResidence(
            @NonNull final Consumer consumer,
            @NonNull final State legalResidence) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().setLegalResidence(
                        consumer,
                        legalResidence,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // Service page
    public Observable<SDKResponse<Practice, SDKError>> getService(
            @NonNull final PracticeInfo practiceInfo) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Practice, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Practice, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().getPractice(
                        practiceInfo,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<List<ProviderInfo>, SDKError>> findProviders(
            @NonNull final Consumer consumer,
            @NonNull final PracticeInfo practiceInfo,
            @Nullable final Language languageSpoken) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<ProviderInfo>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<ProviderInfo>, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().findProviders(
                        consumer,
                        practiceInfo,
                        null,
                        null,
                        null,
                        null,
                        null,
                        languageSpoken,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<List<OnDemandSpecialty>, SDKError>> getSpecialties(
            @NonNull final Consumer consumer,
            @NonNull final PracticeInfo practiceInfo) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<OnDemandSpecialty>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<OnDemandSpecialty>, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().getOnDemandSpecialties(
                        consumer,
                        practiceInfo,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<AvailableProviders, SDKError>> findAvailableProviders(
            @NonNull final Consumer consumer,
            @NonNull final PracticeInfo practiceInfo,
            @Nullable final Date date,
            @Nullable final Language language) {

        return Observable.create(new Observable.OnSubscribe<SDKResponse<AvailableProviders, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<AvailableProviders, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().findFutureAvailableProviders(
                        consumer,
                        practiceInfo,
                        null,
                        language,
                        date,
                        null,
                        null,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Void, SDKError>> scheduleAppointment(
            @NonNull final Consumer consumer,
            @NonNull final ProviderInfo provider,
            @NonNull final Date appointmentDate,
            @Nullable final String phoneNumber,
            @Nullable final RemindOptions consumerReminder) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().scheduleAppointment(
                        consumer,
                        provider,
                        appointmentDate,
                        phoneNumber,
                        consumerReminder,
                        null,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // provider

    public Observable<SDKResponse<Provider, SDKError>> getProvider(
            @Nullable final Consumer consumer,
            @NonNull final ProviderInfo providerInfo) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Provider, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Provider, SDKError>> subscriber) {
                awsdk.getPracticeProvidersManager().getProvider(
                        providerInfo,
                        consumer,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // visit context

    public Observable<SDKResponse<VisitContext, SDKError>> getVisitContext(
            @NonNull final Consumer consumer,
            @NonNull final ProviderInfo provider) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<VisitContext, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<VisitContext, SDKError>> subscriber) {
                awsdk.getVisitManager().getVisitContext(
                        consumer,
                        provider,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<VisitContext, SDKError>> getVisitContext(
            @NonNull final Consumer consumer,
            @NonNull final OnDemandSpecialty specialty) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<VisitContext, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<VisitContext, SDKError>> subscriber) {
                awsdk.getVisitManager().getVisitContext(
                        consumer,
                        specialty,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<VisitContext, SDKError>> getVisitContext(
            @NonNull final Appointment appointment) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<VisitContext, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<VisitContext, SDKError>> subscriber) {
                awsdk.getVisitManager().getVisitContext(
                        appointment,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // insurance subscription
    public Observable<SDKValidatedResponse<Void, SDKError>> updateSubscription(
            @NonNull final SubscriptionUpdateRequest subscriptionUpdateRequest) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateInsuranceSubscription(
                        subscriptionUpdateRequest,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // vitals
    public Observable<SDKValidatedResponse<Void, SDKError>> updateVitals(
            @NonNull final Consumer consumer,
            @NonNull final Vitals vitals,
            @Nullable final VisitContext visitContext) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateVitals(
                        consumer,
                        vitals,
                        visitContext,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // conditions
    public Observable<SDKResponse<List<Condition>, SDKError>> getConditions(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Condition>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Condition>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getConditions(
                        consumer,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> updateConditions(
            @NonNull final Consumer consumer,
            @NonNull final List<Condition> conditions) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateConditions(
                        consumer,
                        conditions,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // medications
    public Observable<SDKResponse<List<Medication>, SDKError>> getMedications(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Medication>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Medication>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getMedications(
                        consumer,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> updateMedications(
            @NonNull final Consumer consumer,
            @NonNull final List<Medication> medications) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateMedications(
                        consumer,
                        medications,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<List<Medication>, SDKError>> searchMedications(
            @NonNull final Consumer consumer,
            @NonNull final String searchText) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<List<Medication>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<List<Medication>, SDKError>> subscriber) {
                awsdk.getConsumerManager().searchMedications(
                        consumer,
                        searchText,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // allergies

    public Observable<SDKResponse<List<Allergy>, SDKError>> getAllergies(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Allergy>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Allergy>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getAllergies(
                        consumer,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> updateAllergies(
            @NonNull final Consumer consumer,
            @NonNull final List<Allergy> allergies) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateAllergies(
                        consumer,
                        allergies,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // pharmacy
    public Observable<SDKResponse<Pharmacy, SDKError>> getPharmacy(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Pharmacy, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Pharmacy, SDKError>> subscriber) {
                awsdk.getConsumerManager().getConsumerPharmacy(
                        consumer,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> updatePharmacy(
            @NonNull final Consumer consumer,
            @NonNull final Pharmacy pharmacy) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateConsumerPharmacy(
                        consumer,
                        pharmacy,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<List<Pharmacy>, SDKError>> findPharmaciesByZip(
            @NonNull final Consumer consumer,
            @NonNull final String zipCode) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<List<Pharmacy>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<List<Pharmacy>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getPharmacies(
                        consumer,
                        null,
                        null,
                        null,
                        zipCode,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<List<Pharmacy>, SDKError>> findPharmaciesNearMe(
            @NonNull final Consumer consumer,
            @NonNull final float latitude,
            @NonNull final float longitude,
            @NonNull final int radius,
            @NonNull final boolean excludeMailOrder) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<List<Pharmacy>, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<List<Pharmacy>, SDKError>> subscriber) {
                awsdk.getConsumerManager().getPharmacies(
                        consumer,
                        latitude,
                        longitude,
                        radius,
                        excludeMailOrder,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Address, SDKError>> getShippingAddress(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Address, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Address, SDKError>> subscriber) {
                awsdk.getConsumerManager().getShippingAddress(
                        consumer,
                        new SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKValidatedResponse<Address, SDKError>> updateShippingAddress(
            @NonNull final Consumer consumer,
            @NonNull final Address address) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Address, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Address, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateShippingAddress(
                        consumer,
                        address,
                        new SubscriberSDKValidatedCallback(subscriber));
            }
        });
    }

    // visit related
    public Observable<SDKValidatedResponse<Visit, SDKError>> createOrUpdateVisit(
            @NonNull final VisitContext visitContext) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Visit, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Visit, SDKError>> subscriber) {
                awsdk.getVisitManager().createOrUpdateVisit(
                        visitContext,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> applyCouponCode(
            @NonNull final Visit visit,
            @NonNull final String couponCode) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().applyCouponCode(
                        visit,
                        couponCode,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<PaymentMethod, SDKError>> getPaymentMethod(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<PaymentMethod, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<PaymentMethod, SDKError>> subscriber) {
                awsdk.getConsumerManager().getPaymentMethod(
                        consumer,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<PaymentMethod, SDKError>> getPaymentMethod(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<PaymentMethod, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<PaymentMethod, SDKError>> subscriber) {
                awsdk.getConsumerManager().getPaymentMethod(
                        visit,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<PaymentMethod, SDKError>> updatePaymentMethod(
            @NonNull final CreatePaymentRequest createPaymentRequest) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<PaymentMethod, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<PaymentMethod, SDKError>> subscriber) {
                awsdk.getConsumerManager().updatePaymentMethod(
                        createPaymentRequest,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    // waiting room
    public Observable<SDKStartVisitResponse<Void, SDKError>> startVisit(
            @NonNull final Visit visit,
            @Nullable final Address location,
            @Nullable final Intent visitFinishedIntent) {
        return Observable.create(new Observable.OnSubscribe<SDKStartVisitResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKStartVisitResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().startVisit(
                        visit,
                        location,
                        visitFinishedIntent,
                        new SubscriberSDKStartVisitCallback((Subscriber<SDKStartVisitResponse>) subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> cancelVisit(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().cancelVisit(
                        visit,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKVisitTransferResponse<VisitContext, SDKError>> acceptSuggestedTransfer(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKVisitTransferResponse<VisitContext, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKVisitTransferResponse<VisitContext, SDKError>> subscriber) {
                awsdk.getVisitManager().acceptSuggestedTransfer(
                        visit,
                        new SubscriberSDKVisitTransferCallback((Subscriber<SDKVisitTransferResponse>) subscriber)
                );
            }
        });
    }

    public Observable<SDKVisitTransferResponse<VisitContext, SDKError>> acceptDeclineAndTransfer(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKVisitTransferResponse<VisitContext, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKVisitTransferResponse<VisitContext, SDKError>> subscriber) {
                awsdk.getVisitManager().acceptDeclineAndTransfer(
                        visit,
                        new SubscriberSDKVisitTransferCallback((Subscriber<SDKVisitTransferResponse>) subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> declineTransfer(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().declineTransfer(
                        visit,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // first available / matchmaker
    public Observable<SDKMatchmakerResponse<Void, SDKError>> startMatchmaking(
            @NonNull final VisitContext visitContext) {
        return Observable.create(new Observable.OnSubscribe<SDKMatchmakerResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKMatchmakerResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().startMatchmaking(
                        visitContext,
                        new SubscriberSDKMatchmakerCallback((Subscriber<SDKMatchmakerResponse>) subscriber));
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> cancelMatchmaking(
            @NonNull final VisitContext visitContext) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().cancelMatchmaking(
                        visitContext,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // visit summary
    public Observable<SDKResponse<VisitSummary, SDKError>> getVisitSummary(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<VisitSummary, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<VisitSummary, SDKError>> subscriber) {
                awsdk.getVisitManager().getVisitSummary(
                        visit,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Void, SDKError>> sendVisitFeedback(
            @NonNull final Visit visit,
            @NonNull final ConsumerFeedbackQuestion consumerFeedbackQuestion) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().sendVisitFeedback(
                        visit,
                        consumerFeedbackQuestion,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKValidatedResponse<Void, SDKError>> sendEmailSummary(
            @NonNull final Visit visit,
            @Nullable final Set<String> emails,
            final boolean hipaaNoticeAccepted) {
        return Observable.create(new Observable.OnSubscribe<SDKValidatedResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKValidatedResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().sendVisitSummaryReport(
                        visit,
                        emails,
                        hipaaNoticeAccepted,
                        new SubscriberSDKValidatedCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> sendRatings(
            @NonNull final Visit visit,
            @Nullable final Integer providerRating,
            @Nullable final Integer visitRating) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().sendRatings(
                        visit,
                        providerRating,
                        visitRating,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    // waiting room chat
    public Observable<SDKResponse<ChatReport, SDKError>> sendChat(
            @NonNull final Visit visit,
            @NonNull final String message) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<ChatReport, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<ChatReport, SDKError>> subscriber) {
                awsdk.getVisitManager().addChatMessage(
                        visit,
                        message,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKIVRResponse<Void, SDKError>> monitorIVRStatus(
            @NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKIVRResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKIVRResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().monitorIVRStatus(
                        visit,
                        new SubscriberSDKIVRCallback((Subscriber<SDKIVRResponse>) subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> retryIVR(@NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().retryIVRCallback(
                        visit,
                        new SubscriberSDKCallback(subscriber)
                );
            }
        });
    }

    public Observable<SDKResponse<Void, SDKError>> cancelIVR(@NonNull final Visit visit) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getVisitManager().cancelIVR(visit, new SubscriberSDKCallback(subscriber));
            }
        });
    }

    // accept outstanding disclaimer
    public Observable<SDKResponse<Void, SDKError>> acceptOutstandingDisclaimer(
            @NonNull final Authentication authentication) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<Void, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<Void, SDKError>> subscriber) {
                awsdk.getConsumerManager().acceptOutstandingDisclaimer(authentication, new
                        SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<AppointmentReadiness, SDKError>> getAppointmentReadiness(
            @NonNull final Consumer consumer) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<AppointmentReadiness, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<AppointmentReadiness, SDKError>> subscriber) {
                awsdk.getConsumerManager().getAppointmentReadiness(consumer, new
                        SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<AppointmentReadiness, SDKError>> updateAppointmentReadiness(
            @NonNull final Visit visit,
            final Boolean techCheckPassed) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<AppointmentReadiness, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<AppointmentReadiness, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateAppointmentReadiness( visit, techCheckPassed, new
                        SubscriberSDKCallback(subscriber));
            }
        });
    }

    public Observable<SDKResponse<AppointmentReadiness, SDKError>> updateAppointmentReadiness(
            @NonNull final Consumer consumer,
            @Nullable final SDKLaunchParams launchParams,
            final Boolean techCheckPassed) {
        return Observable.create(new Observable.OnSubscribe<SDKResponse<AppointmentReadiness, SDKError>>() {
            @Override
            public void call(Subscriber<? super SDKResponse<AppointmentReadiness, SDKError>> subscriber) {
                awsdk.getConsumerManager().updateAppointmentReadiness(consumer, launchParams, techCheckPassed, new
                        SubscriberSDKCallback(subscriber));
            }
        });
    }
}