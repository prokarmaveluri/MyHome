package com.televisit.summary;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import stencil.parser.Parser;
import timber.log.Timber;

public class SummaryFragment extends Fragment {
    public static final String SUMMARY_TAG = "summary_tag";
    public static final String VISIT_END_REASON_KEY = "visit_end_reason_key";
    public static final String VISIT_LIST_POSITION = "visit_list_position";

    private ProgressBar progressBar;
    private TextView providerName;
    private TextView pharmacyName;
    private TextView pharmacyDistance;
    private TextView pharmacyAddress;
    private TextView costDesc;
    private CircularImageView docImage;
    private Button viewReport;

    //private VisitSummary summary;
    //private VisitEndReason endReason;

    private int visitReportPosition;
    private VisitReport visitReport;
    private VisitReportDetail visitReportDetail;
    private FileAttachment visitReportFileAttachement;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(VISIT_END_REASON_KEY)) {
            //endReason = (VisitEndReason) getArguments().getSerializable(VISIT_END_REASON_KEY);
        }
        if (getArguments() != null && getArguments().containsKey(VISIT_LIST_POSITION)) {
            visitReportPosition = getArguments().getInt(VISIT_LIST_POSITION);
            if (visitReportPosition >= 0 && visitReportPosition < AwsManager.getInstance().getVisitReports().size()) {
                visitReport = AwsManager.getInstance().getVisitReports().get(visitReportPosition);
                getVisitReportDetails(visitReport);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.visit_summary));
        View view = inflater.inflate(R.layout.visit_summary, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);
        providerName = (TextView) view.findViewById(R.id.provider_name);

        pharmacyName = (TextView) view.findViewById(R.id.pharmacy_name);
        pharmacyDistance = (TextView) view.findViewById(R.id.pharmacy_distance);
        pharmacyAddress = (TextView) view.findViewById(R.id.pharmacy_address);

        costDesc = (TextView) view.findViewById(R.id.cost_description);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        viewReport = (Button) view.findViewById(R.id.view_report);

        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVisitReportAttachment(visitReport);
            }
        });

        return view;
    }

    private void updateDoctorImage() {

        Timber.d("visit. Image available = " +
                (visitReportDetail != null
                        && visitReportDetail.getAssignedProviderInfo() != null
                        && visitReportDetail.getAssignedProviderInfo().hasImage()
                ) );

        if (visitReportDetail != null && visitReportDetail.getAssignedProviderInfo() != null && visitReportDetail.getAssignedProviderInfo().hasImage()) {

            // preferred method for loading image
            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(visitReportDetail.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .build()
                    .load();
        }
    }

    private void getVisitReportDetails(final VisitReport visitReport) {

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            CommonUtil.log(this.getClass().getSimpleName(), "visits VisitReportDetails. isHasInitializedAwsdk: FALSE ");
            return;
        }

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReportDetail(
                AwsManager.getInstance().getConsumer(),
                visitReport,
                new SDKCallback<VisitReportDetail, SDKError>() {
                    @Override
                    public void onResponse(VisitReportDetail detail, SDKError sdkError) {
                        if (sdkError == null) {

                            visitReportDetail = detail;
                            progressBar.setVisibility(View.GONE);

                            providerName.setText(visitReport.getProviderName());
                            costDesc.setText(getString(R.string.visit_cost_desc) + detail.getVisitCost().getExpectedConsumerCopayCost());

                            viewReport.setEnabled(true);

                            updateDoctorImage();

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getName() != null) {
                                Timber.d("visit. Pharmacy Name is NULL ");
                                pharmacyName.setVisibility(View.GONE);
                            }
                            else {
                                pharmacyName.setText(visitReportDetail.getPharmacy().getName());
                            }

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getDistance() > 0) {
                                Timber.d("visit. Pharmacy Distance is NULL ");
                                pharmacyDistance.setVisibility(View.GONE);
                            }
                            else {
                                pharmacyDistance.setText(String.valueOf(visitReportDetail.getPharmacy().getDistance()) + " mi");
                            }

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getAddress() == null) {
                                Timber.d("visit. Pharmacy Address is NULL ");
                                pharmacyAddress.setVisibility(View.GONE);
                            }
                            else {
                                StringBuilder sb = new StringBuilder();

                                if (visitReportDetail.getPharmacy().getAddress().getAddress1() != null
                                        && visitReportDetail.getPharmacy().getAddress().getAddress1().isEmpty()) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getAddress1());
                                }

                                if (visitReportDetail.getPharmacy().getAddress().getAddress2() != null
                                        && visitReportDetail.getPharmacy().getAddress().getAddress2().isEmpty()) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getAddress2());
                                }

                                if (visitReportDetail.getPharmacy().getAddress().getCity() != null
                                        && visitReportDetail.getPharmacy().getAddress().getCity().isEmpty()) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getCity().toString());
                                }

                                if (visitReportDetail.getPharmacy().getAddress().getState() != null) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getState().getName());
                                }

                                if (visitReportDetail.getPharmacy().getAddress().getZipCode() != null
                                        && visitReportDetail.getPharmacy().getAddress().getZipCode().isEmpty()) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getZipCode().toString());
                                }

                                if (visitReportDetail.getPharmacy().getAddress().getCountry() != null) {
                                    sb.append(", " + visitReportDetail.getPharmacy().getAddress().getCountry().getName());
                                }

                                pharmacyAddress.setText(sb.toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable != null) {
                            Timber.d("visit. getVisitReportDetail: " + throwable.getMessage());
                        }
                        Timber.e(throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void getVisitReportAttachment(final VisitReport visitReport) {

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            CommonUtil.log(this.getClass().getSimpleName(), "visits VisitReportDetails. isHasInitializedAwsdk: FALSE ");
            return;
        }

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReportAttachment(
                AwsManager.getInstance().getConsumer(),
                visitReport,
                new SDKCallback<FileAttachment, SDKError>() {
                    @Override
                    public void onResponse(FileAttachment pdfFile, SDKError sdkError) {
                        if (sdkError == null) {
                            visitReportFileAttachement = pdfFile;

                            progressBar.setVisibility(View.GONE);
                            try {
                                if (pdfFile != null) {
                                    Toast.makeText(getContext(), "visit. pdf available ", Toast.LENGTH_LONG).show();
                                    String fileNameWithEntirePath = Environment.getExternalStorageDirectory() + "/visit_reports/" + "Report.pdf";

                                    boolean fileSaved = CommonUtil.saveFileToStorage(fileNameWithEntirePath, IOUtils.toByteArray(pdfFile.getInputStream()));

                                    CommonUtil.openPdf(getContext(), fileNameWithEntirePath);
                                }
                                else {
                                    Timber.d ("visit. Report is NULL ");
                                    Toast.makeText(getContext(), "visit. pdf is NULL ", Toast.LENGTH_LONG).show();

                                }

                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable != null) {
                            Timber.d("visit. getVisitReportAttachment: " + throwable.getMessage());
                        }
                        Timber.e(throwable);
                        progressBar.setVisibility(View.GONE);

                        //viewReport.setEnabled(false);
                    }
                }
        );
    }

}
