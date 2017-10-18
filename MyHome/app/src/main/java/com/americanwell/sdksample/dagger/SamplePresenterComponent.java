/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.dagger;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdk.manager.SecureMessageManager;
import com.americanwell.sdk.manager.VisitManager;
import com.americanwell.sdksample.appointments.AppointmentPresenter;
import com.americanwell.sdksample.appointments.AppointmentsPresenter;
import com.americanwell.sdksample.cost.VisitCostPresenter;
import com.americanwell.sdksample.documents.HealthDocumentsPresenter;
import com.americanwell.sdksample.health.AddMedicationPresenter;
import com.americanwell.sdksample.health.AllergiesPresenter;
import com.americanwell.sdksample.health.MedicalHistoryPresenter;
import com.americanwell.sdksample.health.MedicationsPresenter;
import com.americanwell.sdksample.health.VitalsPresenter;
import com.americanwell.sdksample.home.HomePresenter;
import com.americanwell.sdksample.intake.FirstAvailablePresenter;
import com.americanwell.sdksample.intake.TriageQuestionsPresenter;
import com.americanwell.sdksample.login.CompleteEnrollmentPresenter;
import com.americanwell.sdksample.login.GuestLoginPresenter;
import com.americanwell.sdksample.login.LoginAssistancePresenter;
import com.americanwell.sdksample.login.LoginPresenter;
import com.americanwell.sdksample.login.OutstandingDisclaimerPresenter;
import com.americanwell.sdksample.login.StartupPresenter;
import com.americanwell.sdksample.login.ThirdPartyLoginPresenter;
import com.americanwell.sdksample.messages.InboxPresenter;
import com.americanwell.sdksample.messages.MessageDetailPresenter;
import com.americanwell.sdksample.messages.MessageDraftPresenter;
import com.americanwell.sdksample.messages.SentMessagesPresenter;
import com.americanwell.sdksample.profile.AddCreditCardPresenter;
import com.americanwell.sdksample.profile.AddDependentPresenter;
import com.americanwell.sdksample.profile.FindPharmacyPresenter;
import com.americanwell.sdksample.profile.PharmacyPresenter;
import com.americanwell.sdksample.profile.UpdateConsumerPresenter;
import com.americanwell.sdksample.profile.UpdateDependentPresenter;
import com.americanwell.sdksample.profile.UpdateInsurancePresenter;
import com.americanwell.sdksample.readiness.TechCheckPresenter;
import com.americanwell.sdksample.registration.RegistrationPresenter;
import com.americanwell.sdksample.rx.ObservableService;
import com.americanwell.sdksample.services.AppointmentScheduledPresenter;
import com.americanwell.sdksample.services.ProviderPresenter;
import com.americanwell.sdksample.services.ScheduleAppointmentPresenter;
import com.americanwell.sdksample.services.ServicePresenter;
import com.americanwell.sdksample.util.FileUtils;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.StateUtils;
import com.americanwell.sdksample.visit.GuestFinishedPresenter;
import com.americanwell.sdksample.visit.GuestWaitingRoomPresenter;
import com.americanwell.sdksample.visit.IVRPresenter;
import com.americanwell.sdksample.visit.VisitFinishedPresenter;
import com.americanwell.sdksample.visit.VisitReportDetailPresenter;
import com.americanwell.sdksample.visit.VisitReportsPresenter;
import com.americanwell.sdksample.visit.VisitSummaryPresenter;
import com.americanwell.sdksample.visit.WaitingRoomPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Sample Dagger 2 Component to provide injectables for Presenters
 */
@Singleton
@Component(modules = SamplePresenterModule.class)
public interface SamplePresenterComponent {
    // inject all the presenters - must be done with concrete classes
    void inject(StartupPresenter presenter);

    void inject(LoginPresenter presenter);

    void inject(CompleteEnrollmentPresenter presenter);

    void inject(ThirdPartyLoginPresenter presenter);

    void inject(LoginAssistancePresenter presenter);

    void inject(GuestLoginPresenter presenter);

    void inject(RegistrationPresenter presenter);

    void inject(ServicePresenter presenter);

    void inject(ProviderPresenter presenter);

    void inject(InboxPresenter presenter);

    void inject(SentMessagesPresenter presenter);

    void inject(MessageDetailPresenter presenter);

    void inject(MessageDraftPresenter presenter);

    void inject(AppointmentsPresenter presenter);

    void inject(VisitReportsPresenter presenter);

    void inject(AppointmentPresenter presenter);

    void inject(HealthDocumentsPresenter presenter);

    void inject(AddDependentPresenter presenter);

    void inject(TriageQuestionsPresenter presenter);

    void inject(UpdateInsurancePresenter presenter);

    void inject(VitalsPresenter presenter);

    void inject(MedicalHistoryPresenter presenter);

    void inject(MedicationsPresenter presenter);

    void inject(AddMedicationPresenter presenter);

    void inject(AllergiesPresenter presenter);

    void inject(PharmacyPresenter presenter);

    void inject(FindPharmacyPresenter presenter);

    void inject(VisitCostPresenter presenter);

    void inject(AddCreditCardPresenter presenter);

    void inject(WaitingRoomPresenter presenter);

    void inject(GuestWaitingRoomPresenter presenter);

    void inject(VisitSummaryPresenter presenter);

    void inject(VisitReportDetailPresenter presenter);

    void inject(FirstAvailablePresenter presenter);

    void inject(HomePresenter presenter);

    void inject(UpdateConsumerPresenter presenter);

    void inject(AppointmentScheduledPresenter presenter);

    void inject(ScheduleAppointmentPresenter presenter);

    void inject(IVRPresenter presenter);

    void inject(UpdateDependentPresenter presenter);

    void inject(OutstandingDisclaimerPresenter presenter);

    void inject(VisitFinishedPresenter presenter);

    void inject(GuestFinishedPresenter presenter);

    void inject(TechCheckPresenter presenter);

    // things available to inject
    AWSDK awsdk();

    ConsumerManager consumerManager();

    PracticeProvidersManager practiceProvidersManager();

    SecureMessageManager secureMessageManager();

    VisitManager visitManager();

    ObservableService observableFactory();

    StateUtils stateUtils();

    FileUtils fileUtils();

    LocaleUtils localeUtils();
}
