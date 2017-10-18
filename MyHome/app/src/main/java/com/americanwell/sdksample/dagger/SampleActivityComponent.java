/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.dagger;

import com.americanwell.sdksample.appointments.AppointmentActivity;
import com.americanwell.sdksample.appointments.AppointmentsActivity;
import com.americanwell.sdksample.cost.VisitCostActivity;
import com.americanwell.sdksample.documents.HealthDocumentsActivity;
import com.americanwell.sdksample.health.AddMedicationActivity;
import com.americanwell.sdksample.health.AllergiesActivity;
import com.americanwell.sdksample.health.MedicalHistoryActivity;
import com.americanwell.sdksample.health.MedicationsActivity;
import com.americanwell.sdksample.health.VitalsActivity;
import com.americanwell.sdksample.home.HomeActivity;
import com.americanwell.sdksample.intake.FirstAvailableActivity;
import com.americanwell.sdksample.intake.TriageQuestionsActivity;
import com.americanwell.sdksample.login.CompleteEnrollmentActivity;
import com.americanwell.sdksample.login.GuestLoginActivity;
import com.americanwell.sdksample.login.LoginActivity;
import com.americanwell.sdksample.login.LoginAssistanceActivity;
import com.americanwell.sdksample.login.OutstandingDisclaimerActivity;
import com.americanwell.sdksample.login.StartupActivity;
import com.americanwell.sdksample.login.ThirdPartyLoginActivity;
import com.americanwell.sdksample.messages.InboxActivity;
import com.americanwell.sdksample.messages.MessageDetailActivity;
import com.americanwell.sdksample.messages.MessageDraftActivity;
import com.americanwell.sdksample.messages.SentMessagesActivity;
import com.americanwell.sdksample.profile.AddCreditCardActivity;
import com.americanwell.sdksample.profile.AddDependentActivity;
import com.americanwell.sdksample.profile.FindPharmacyActivity;
import com.americanwell.sdksample.profile.PharmacyActivity;
import com.americanwell.sdksample.profile.UpdateConsumerActivity;
import com.americanwell.sdksample.profile.UpdateDependentActivity;
import com.americanwell.sdksample.profile.UpdateInsuranceActivity;
import com.americanwell.sdksample.readiness.TechCheckActivity;
import com.americanwell.sdksample.registration.RegistrationActivity;
import com.americanwell.sdksample.services.AppointmentScheduledActivity;
import com.americanwell.sdksample.services.AvailableProvidersFragment;
import com.americanwell.sdksample.services.ProviderActivity;
import com.americanwell.sdksample.services.ProviderAppointmentsFragment;
import com.americanwell.sdksample.services.ProviderView;
import com.americanwell.sdksample.services.ScheduleAppointmentActivity;
import com.americanwell.sdksample.services.ServiceActivity;
import com.americanwell.sdksample.util.FileUtils;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.SampleUtils;
import com.americanwell.sdksample.visit.GuestFinishedActivity;
import com.americanwell.sdksample.visit.GuestWaitingRoomActivity;
import com.americanwell.sdksample.visit.IVRActivity;
import com.americanwell.sdksample.visit.VisitFinishedActivity;
import com.americanwell.sdksample.visit.VisitReportDetailActivity;
import com.americanwell.sdksample.visit.VisitReportsActivity;
import com.americanwell.sdksample.visit.VisitSummaryActivity;
import com.americanwell.sdksample.visit.WaitingRoomActivity;
import com.americanwell.sdksample.visit.views.VisitReportHeaderView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Sample Dagger 2 Component to provide injectables for Activities and Views
 */
@Singleton
@Component(modules = SampleActivityModule.class)
public interface SampleActivityComponent {
    // inject all the activities - must be done with concrete classes
    void inject(StartupActivity activity);

    void inject(LoginActivity activity);

    void inject(CompleteEnrollmentActivity activity);

    void inject(ThirdPartyLoginActivity activity);

    void inject(LoginAssistanceActivity activity);

    void inject(GuestLoginActivity activity);

    void inject(RegistrationActivity activity);

    void inject(ServiceActivity activity);

    void inject(ProviderActivity activity);

    void inject(InboxActivity activity);

    void inject(SentMessagesActivity activity);

    void inject(MessageDetailActivity activity);

    void inject(MessageDraftActivity activity);

    void inject(AppointmentsActivity activity);

    void inject(VisitReportsActivity activity);

    void inject(AppointmentActivity activity);

    void inject(HealthDocumentsActivity activity);

    void inject(AddDependentActivity activity);

    void inject(TriageQuestionsActivity activity);

    void inject(UpdateInsuranceActivity activity);

    void inject(VitalsActivity activity);

    void inject(MedicalHistoryActivity activity);

    void inject(MedicationsActivity activity);

    void inject(AddMedicationActivity activity);

    void inject(AllergiesActivity activity);

    void inject(PharmacyActivity activity);

    void inject(FindPharmacyActivity activity);

    void inject(VisitCostActivity activity);

    void inject(AddCreditCardActivity activity);

    void inject(WaitingRoomActivity activity);

    void inject(GuestWaitingRoomActivity activity);

    void inject(VisitSummaryActivity activity);

    void inject(VisitReportDetailActivity activity);

    void inject(FirstAvailableActivity activity);

    void inject(HomeActivity activity);

    void inject(UpdateConsumerActivity activity);

    void inject(AppointmentScheduledActivity activity);

    void inject(ScheduleAppointmentActivity activity);

    void inject(IVRActivity activity);

    void inject(UpdateDependentActivity activity);

    void inject(OutstandingDisclaimerActivity activity);

    void inject(VisitFinishedActivity activity);

    void inject(GuestFinishedActivity activity);

    void inject(TechCheckActivity activity);

    // inject views
    void inject(ProviderView view);

    void inject(VisitReportHeaderView view);

    // inject fragments
    void inject(AvailableProvidersFragment fragment);

    void inject(ProviderAppointmentsFragment fragment);

    // things available to inject
    SampleUtils sampleUtils();

    FileUtils fileUtils();

    LocaleUtils localeUtils();
}
