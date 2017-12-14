package com.televisit.summary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.entity.visit.VisitRx;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsGetVisitSummary;
import com.televisit.previousvisit.PrescriptionsAdapter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

import timber.log.Timber;

public class SummaryFragment extends BaseFragment implements AwsGetVisitSummary {
    public static final String SUMMARY_TAG = "previous_visit_summary_tag";
    public static final String VISIT_LIST_POSITION = "visit_list_position";

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
    private File report;

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

        visitReportPosition = -1;
        if (getArguments() != null && getArguments().containsKey(VISIT_LIST_POSITION)) {
            visitReportPosition = getArguments().getInt(VISIT_LIST_POSITION);
        }

        if (visitReportPosition >= 0 && visitReportPosition < AwsManager.getInstance().getVisitReports().size()) {

            visitReport = AwsManager.getInstance().getVisitReports().get(visitReportPosition);
            getVisitReportDetails(visitReport);

        } else {
            Visit visit = AwsManager.getInstance().getVisit();
            AwsNetworkManager.getInstance().getVisitSummary(visit, this);
        }
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
                AwsManager.getInstance().getConsumer(),
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

                            DecimalFormat amountFormat = new DecimalFormat("0.00");

                            if (AwsManager.getInstance().isDependent()) {
                                costDesc.setText(getString(R.string.visit_total_cost_desc_dependent) + amountFormat.format(detail.getVisitCost().getExpectedConsumerCopayCost()));
                            } else {
                                costDesc.setText(getString(R.string.visit_total_cost_desc) + amountFormat.format(detail.getVisitCost().getExpectedConsumerCopayCost()));
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
                AwsManager.getInstance().getConsumer(),
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
                                            report = f;

                                            canBeViewed = true;
                                            viewReport.setEnabled(true);
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


    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {

        progressBar.setVisibility(View.GONE);

        if (visitSummary.getAssignedProviderInfo() != null) {
            providerName.setText(visitSummary.getAssignedProviderInfo().getFullName());
        }

        DecimalFormat amountFormat = new DecimalFormat("0.00");
        if (AwsManager.getInstance().isDependent()) {
            costDesc.setText(getString(R.string.visit_total_cost_desc_dependent) + amountFormat.format(visitSummary.getVisitCost().getExpectedConsumerCopayCost()));
        } else {
            costDesc.setText(getString(R.string.visit_total_cost_desc) + amountFormat.format(visitSummary.getVisitCost().getExpectedConsumerCopayCost()));
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
