package com.televisit.summary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.entity.visit.VisitRx;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsGetVisitSummary;
import com.televisit.previousvisit.EmailsAdapter;
import com.televisit.previousvisit.PrescriptionsAdapter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class SummaryFragment extends BaseFragment implements AwsGetVisitSummary {
    public static final String SUMMARY_TAG = "previous_visit_summary_tag";
    public static final String VISIT_LIST_POSITION = "visit_list_position";
    private static int TOTAL_EMAIL_COUNT_ALLOWED = 5;

    private ProgressBar progressBar;
    private TextView providerName;
    private TextView pharmacyName;
    private RelativeLayout pharmacyPhoneLayout;
    private TextView pharmacyPhone;
    private TextView pharmacyAddress;
    private TextView costDesc;
    private TextView endDesc;
    private CircularImageView docImage;
    private RecyclerView prescriptionsList;
    private Button viewReport;
    private TextView doctorNotes;

    private int visitReportPosition;
    private VisitReport visitReport;
    private VisitReportDetail visitReportDetail;
    private String reportNameWithPath;

    private LinearLayout emailLayout;
    private RecyclerView emailsList;
    private CheckBox emailAgree;
    private TextView addAdditionalEmail;
    private RelativeLayout newEmailLayout;
    private TextInputLayout newEmailTextInput;
    private TextInputEditText newEmailEditText;
    private TextView addEmail;

    private List<EmailsAdapter.EmailSelection> emailObjects = null;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.visit_summary));
        View view = inflater.inflate(R.layout.visit_summary, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.visit_summary));

        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);
        providerName = (TextView) view.findViewById(R.id.provider_name);

        pharmacyName = (TextView) view.findViewById(R.id.pharmacy_name);
        pharmacyPhoneLayout = (RelativeLayout) view.findViewById(R.id.pharmacy_phone_layout);
        pharmacyPhone = (TextView) view.findViewById(R.id.pharmacy_phone);
        pharmacyAddress = (TextView) view.findViewById(R.id.pharmacy_address);

        endDesc = (TextView) view.findViewById(R.id.end_description);
        costDesc = (TextView) view.findViewById(R.id.cost_description);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        prescriptionsList = (RecyclerView) view.findViewById(R.id.prescriptions_list);
        doctorNotes = (TextView) view.findViewById(R.id.doctor_notes);
        viewReport = (Button) view.findViewById(R.id.view_report);

        emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        emailsList = (RecyclerView) view.findViewById(R.id.email_list);
        emailAgree = (CheckBox) view.findViewById(R.id.email_agree);
        addAdditionalEmail = (TextView) view.findViewById(R.id.add_additional_email);

        newEmailLayout = (RelativeLayout) view.findViewById(R.id.new_email_layout);
        newEmailTextInput = (TextInputLayout) view.findViewById(R.id.new_email_textinput);
        newEmailEditText = (TextInputEditText) view.findViewById(R.id.new_email_edittext);
        addEmail = (TextView) view.findViewById(R.id.add_email);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AwsManager.getInstance().isDependent()) {
            endDesc.setText(getContext().getString(R.string.your_visit_has_ended_dependent));
        } else {
            endDesc.setText(getContext().getString(R.string.your_visit_has_ended));
        }

        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timber.d("visitsummary. reportNameWithPath = " + reportNameWithPath);

                File f = null;
                if (reportNameWithPath != null && !reportNameWithPath.isEmpty()) {
                    f = new File(reportNameWithPath);
                }

                if (f != null && f.exists()) {

                    Bundle bundle = new Bundle();
                    bundle.putString("FILENAME_WITH_PATH", reportNameWithPath);
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY_PDF, bundle);
                }
            }
        });

        addAdditionalEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newEmailLayout != null) {
                    if (newEmailLayout.getVisibility() == View.VISIBLE) {
                        newEmailLayout.setVisibility(View.GONE);
                        newEmailTextInput.setVisibility(View.GONE);
                        addEmail.setVisibility(View.GONE);
                    } else {
                        newEmailLayout.setVisibility(View.VISIBLE);
                        newEmailTextInput.setVisibility(View.VISIBLE);
                        addEmail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailAddress();
            }
        });

        visitReportPosition = -1;
        if (getArguments() != null && getArguments().containsKey(VISIT_LIST_POSITION)) {
            visitReportPosition = getArguments().getInt(VISIT_LIST_POSITION);
        }

        if (visitReportPosition >= 0 && visitReportPosition < AwsManager.getInstance().getVisitReports().size()) {

            visitReport = AwsManager.getInstance().getVisitReports().get(visitReportPosition);
            getVisitReportDetails(visitReport);

            emailLayout.setVisibility(View.GONE);

        } else {
            emailLayout.setVisibility(View.VISIBLE);
            AwsNetworkManager.getInstance().getVisitSummary(AwsManager.getInstance().getVisit(), this);
        }

        emailObjects = new ArrayList<>();
        EmailsAdapter.EmailSelection emailObj = new EmailsAdapter.EmailSelection();
        emailObj.setEmailId(AwsManager.getInstance().getPatient().getEmail());
        emailObj.setSelected(true);
        emailObjects.add(emailObj);
        displayEmails();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.visit_summary_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                if (emailLayout.getVisibility() == View.VISIBLE) {
                    emailVisitSummaryReport();
                } else {
                    doneVisitSummary();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addEmailAddress() {

        String emailToAdd = newEmailEditText.getText().toString().trim().toLowerCase();

        if (emailObjects != null && emailObjects.size() > 0) {
            for (EmailsAdapter.EmailSelection emailObject : emailObjects) {

                if (emailToAdd.equalsIgnoreCase(emailObject.getEmailId())) {

                    if (emailObject.isSelected()) {
                        Toast.makeText(getActivity(), R.string.email_already_added, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.email_already_added_notchecked, Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }
        }

        boolean isValid = isValidEmail(emailToAdd);
        if (!isValid) {
            return;
        }

        Timber.d("email. size = " + emailObjects.size() + ". allowed = " + TOTAL_EMAIL_COUNT_ALLOWED);

        EmailsAdapter.EmailSelection emailObj = new EmailsAdapter.EmailSelection();
        emailObj.setEmailId(emailToAdd);
        emailObj.setSelected(true);
        emailObjects.add(emailObj);

        displayEmails();

        newEmailEditText.setText("");
        newEmailEditText.clearFocus();

        CommonUtil.hideSoftKeyboard(this.getActivity());
        CommonUtil.hideSoftKeyboard(getContext(), newEmailEditText);

        if (emailObjects.size() >= TOTAL_EMAIL_COUNT_ALLOWED) {
            addAdditionalEmail.setVisibility(View.GONE);
            newEmailLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.visit_summary_email_limit_reached, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteEmailAddress(String emailId) {

        int indexToDelete = -1;

        int i = 0;
        if (emailObjects != null && emailObjects.size() > 0) {
            for (EmailsAdapter.EmailSelection emailObject : emailObjects) {
                if (emailId.equalsIgnoreCase(emailObject.getEmailId())) {
                    indexToDelete = i;
                    break;
                }
                i = i + 1;
            }

            if (indexToDelete >= 0 && indexToDelete < emailObjects.size()) {
                emailObjects.remove(indexToDelete);
            }
        }

        if (emailObjects == null || emailObjects.size() == 0 || emailObjects.size() < TOTAL_EMAIL_COUNT_ALLOWED) {
            addAdditionalEmail.setVisibility(View.VISIBLE);
        }

        displayEmails();
    }

    private void doneVisitSummary() {
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.VIDEO_VISIT_FEEDBACK, null);
    }

    private void updateDoctorImage() {

        Timber.d("visitsummary. Image available = " +
                (visitReportDetail != null
                        && visitReportDetail.getAssignedProviderInfo() != null
                        && visitReportDetail.getAssignedProviderInfo().hasImage()
                ));

        if (visitReportDetail != null && visitReportDetail.getAssignedProviderInfo() != null) {

            // preferred method for loading image
            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(visitReportDetail.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();
        }
    }

    private void updateDoctorImage(VisitSummary visitSummary) {

        Timber.d("visitsummary. visitSummary. Image available = " +
                (visitSummary != null
                        && visitSummary.getAssignedProviderInfo() != null
                        && visitSummary.getAssignedProviderInfo().hasImage()
                ));

        if (visitSummary != null && visitSummary.getAssignedProviderInfo() != null) {

            // preferred method for loading image
            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(visitSummary.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();
        }
    }

    private boolean isValidEmail(String emailAddress) {

        if (emailAddress == null || emailAddress.isEmpty()) {
            newEmailTextInput.setError(getString(R.string.email_required));
            newEmailTextInput.requestFocus();
            return false;
        } else if (!CommonUtil.isValidEmail(emailAddress)) {
            newEmailTextInput.setError(getString(R.string.valid_email));
            newEmailTextInput.requestFocus();
            return false;
        } else {
            newEmailTextInput.setError(null);
            return true;
        }
    }

    private void emailVisitSummaryReport() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            Timber.d("emailVisitSummaryReport. isHasInitializedAwsdk: FALSE ");
            return;
        }

        Set<String> emailIds = new HashSet<>();
        if (emailObjects != null && emailObjects.size() > 0) {
            for (EmailsAdapter.EmailSelection emailObject : emailObjects) {
                if (emailObject.isSelected()) {
                    emailIds.add(emailObject.getEmailId());
                }
            }
        }

        if (emailIds == null || emailIds.size() == 0) {
            doneVisitSummary();
            return;
        }

        if (!emailAgree.isChecked()) {
            Toast.makeText(getActivity(), R.string.email_agreement_missing, Toast.LENGTH_LONG).show();
            return;
        }

        if (AwsManager.getInstance().getAWSDK().getVisitManager() == null) {
            Timber.d("emailVisitSummaryReport. VisitManager object is NULL ");
            return;
        }
        if (AwsManager.getInstance().getVisit() == null) {
            Timber.d("emailVisitSummaryReport. Visit object is NULL ");
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getVisitManager().sendVisitSummaryReport(
                AwsManager.getInstance().getVisit(), emailIds, true,
                new SDKValidatedCallback<Void, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.d("emailVisitSummaryReport. ValidationFailure " + map.toString());

                        Toast.makeText(getActivity(), R.string.visit_summary_email_failed, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(@Nullable Void aVoid, SDKError sdkError) {

                        progressBar.setVisibility(View.GONE);

                        if (sdkError == null) {
                            Timber.d("emailVisitSummaryReport succeeded! ");
                            Toast.makeText(getActivity(), R.string.visit_summary_email_completed, Toast.LENGTH_LONG).show();

                            doneVisitSummary();

                        } else {
                            Timber.e("emailVisitSummaryReport. Something failed while sending visit summary report email! :/");
                            Timber.e("SDK Error: " + sdkError);

                            doneVisitSummary();
                            //CommonUtil.showToastFromSDKError(getContext(), sdkError);
                            //Toast.makeText(getActivity(), R.string.visit_summary_email_failed, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("emailVisitSummaryReport. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.visit_summary_email_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getVisitReportDetails(final VisitReport visitReport) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            Timber.d("visitsummary VisitReportDetails. isHasInitializedAwsdk: FALSE ");
            return;
        }

        if (!isAdded()) {
            Timber.d("visitsummary VisitReportDetails. isAdded: FALSE ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReportDetail(
                AwsManager.getInstance().getPatient(),
                visitReport,
                new SDKCallback<VisitReportDetail, SDKError>() {
                    @Override
                    public void onResponse(VisitReportDetail detail, SDKError sdkError) {
                        if (!isAdded()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if (sdkError == null) {

                            visitReportDetail = detail;

                            if (visitReport.getProviderName() != null && !visitReport.getProviderName().isEmpty()) {
                                providerName.setText(visitReport.getProviderName() + ", MD");
                            }

                            if (detail.getAssignedProviderInfo() != null) {
                                Timber.d("visitsummary. AssignedProvider Name = " + detail.getAssignedProviderInfo().getFullName());
                            }

                            if (AwsManager.getInstance().isDependent()) {
                                costDesc.setText(getString(R.string.visit_total_cost_desc_dependent) + CommonUtil.formatAmount(detail.getVisitCost().getExpectedConsumerCopayCost()));
                            } else {
                                costDesc.setText(getString(R.string.visit_total_cost_desc) + CommonUtil.formatAmount(detail.getVisitCost().getExpectedConsumerCopayCost()));
                            }

                            displayPharmacyDetails(visitReportDetail.getPharmacy());

                            if (visitReportDetail.getProviderEntries() != null && visitReportDetail.getProviderEntries().getPrescriptions() != null) {
                                displayPrescriptions(visitReportDetail.getProviderEntries().getPrescriptions());
                            }

                            updateDoctorImage();

                            getVisitReportAttachment(visitReport);

                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable != null) {
                            Timber.d("visitsummary. getVisitReportDetail: " + throwable.getMessage());
                        }
                        Timber.e(throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void getVisitReportAttachment(final VisitReport visitReport) {

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            Timber.d("visitsummary VisitReportDetails. isHasInitializedAwsdk: FALSE ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReportAttachment(
                AwsManager.getInstance().getPatient(),
                visitReport,
                new SDKCallback<FileAttachment, SDKError>() {
                    @Override
                    public void onResponse(FileAttachment pdfFile, SDKError sdkError) {
                        if (!isAdded()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if (sdkError == null) {

                            progressBar.setVisibility(View.GONE);
                            boolean canBeViewed = false;

                            try {
                                if (pdfFile != null) {

                                    String fileNameWithEntirePath = getContext().getExternalCacheDir().toString() + File.separator + "report.pdf";

                                    boolean fileSaved = CommonUtil.saveFileToStorage(getContext(), fileNameWithEntirePath, IOUtils.toByteArray(pdfFile.getInputStream()));

                                    if (fileSaved) {
                                        File f = new File(fileNameWithEntirePath);
                                        if (f != null & f.exists()) {
                                            reportNameWithPath = fileNameWithEntirePath;

                                            canBeViewed = true;
                                            viewReport.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Timber.e(e);
                            }

                            if (!canBeViewed) {
                                doctorNotes.setVisibility(View.VISIBLE);
                                viewReport.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                        viewReport.setVisibility(View.GONE);
                        doctorNotes.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Visit report not available! ", Toast.LENGTH_LONG).show();

                        if (throwable != null) {
                            Timber.d("visitsummary. getVisitReportAttachment: " + throwable.getMessage());
                        }
                        Timber.e(throwable);
                    }
                }
        );
    }

    private void displayPrescriptions(Set<VisitRx> prescriptions) {

        if (prescriptions != null && prescriptions.size() > 0) {

            prescriptionsList.setVisibility(View.VISIBLE);

            PrescriptionsAdapter adapter = new PrescriptionsAdapter(new ArrayList<>(prescriptions));
            prescriptionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            //prescriptionsList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            prescriptionsList.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        } else {
            prescriptionsList.setVisibility(View.GONE);
        }
    }

    private void displayEmails() {

        if (emailObjects != null && emailObjects.size() > 0) {

            emailsList.setVisibility(View.VISIBLE);

            EmailsAdapter adapter = new EmailsAdapter(emailObjects, this);
            emailsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            //emailsList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            emailsList.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        } else {
            emailsList.setVisibility(View.GONE);
        }
    }


    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {

        progressBar.setVisibility(View.GONE);

        if (visitSummary.getAssignedProviderInfo() != null) {
            providerName.setText(visitSummary.getAssignedProviderInfo().getFullName());
        }

        if (AwsManager.getInstance().isDependent()) {
            costDesc.setText(getString(R.string.visit_total_cost_desc_dependent) + CommonUtil.formatAmount((visitSummary.getVisitCost().getExpectedConsumerCopayCost())));
        } else {
            costDesc.setText(getString(R.string.visit_total_cost_desc) + CommonUtil.formatAmount((visitSummary.getVisitCost().getExpectedConsumerCopayCost())));
        }

        updateDoctorImage(visitSummary);

        displayPharmacyDetails(visitSummary.getPharmacy());

        if (visitSummary.getProviderEntries() != null && visitSummary.getProviderEntries().getPrescriptions() != null) {
            displayPrescriptions(visitSummary.getProviderEntries().getPrescriptions());
        }

        doctorNotes.setVisibility(View.VISIBLE);
        viewReport.setVisibility(View.GONE);
    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {
        Timber.d("visitsummary. getVisitSummaryFailed. errorMessage = " + errorMessage);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Sorry, Pulling up visit summary has failed. Please try again at later time", Toast.LENGTH_LONG).show();
    }

    private void displayPharmacyDetails(Pharmacy pharmacy) {

        if (pharmacy == null || pharmacy.getName() == null) {
            pharmacyName.setVisibility(View.GONE);
        } else {
            pharmacyName.setVisibility(View.VISIBLE);
            pharmacyName.setText(pharmacy.getName());

            if (pharmacy == null || pharmacy.getPhone() == null || pharmacy.getPhone().isEmpty()) {
                pharmacyPhoneLayout.setVisibility(View.GONE);
            } else {
                pharmacyPhoneLayout.setVisibility(View.VISIBLE);
                pharmacyPhone.setText(CommonUtil.constructPhoneNumberDots(pharmacy.getPhone()));
                pharmacyPhoneLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + pharmacyPhone.getText().toString()));
                        intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentPhone);
                    }
                });
            }

            if (pharmacy == null || pharmacy.getAddress() == null) {
                pharmacyAddress.setVisibility(View.GONE);
            } else {
                pharmacyAddress.setVisibility(View.VISIBLE);
                pharmacyAddress.setText(CommonUtil.getPharmacyAddress(pharmacy));

                String addressContentDescription = pharmacy != null && pharmacy.getAddress() != null ?
                        AddressUtil.getAddressForAccessibilityUser(pharmacy.getAddress())
                        : getString(R.string.address_unknown);

                pharmacyAddress.setContentDescription(getString(R.string.location) + addressContentDescription);
            }
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        if (visitReportPosition == -1) {
            return Constants.ActivityTag.VIDEO_VISIT_SUMMARY;
        } else {
            return Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY;
        }
    }
}
