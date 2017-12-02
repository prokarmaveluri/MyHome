package com.televisit.summary;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.americanwell.sdk.entity.visit.VisitRx;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;
import com.televisit.previousvisit.PrescriptionsAdapter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import timber.log.Timber;

public class SummaryFragment extends Fragment {
    public static final String SUMMARY_TAG = "summary_tag";
    public static final String VISIT_LIST_POSITION = "visit_list_position";

    private ProgressBar progressBar;
    private TextView providerName;
    private TextView pharmacyName;
    private TextView pharmacyDistance;
    private TextView pharmacyAddress;
    private TextView costDesc;
    private CircularImageView docImage;
    private RecyclerView prescriptionsList;
    private Button viewReport;

    private int visitReportPosition;
    private VisitReport visitReport;
    private VisitReportDetail visitReportDetail;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.visit_summary));

        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);
        providerName = (TextView) view.findViewById(R.id.provider_name);

        pharmacyName = (TextView) view.findViewById(R.id.pharmacy_name);
        pharmacyDistance = (TextView) view.findViewById(R.id.pharmacy_distance);
        pharmacyAddress = (TextView) view.findViewById(R.id.pharmacy_address);

        costDesc = (TextView) view.findViewById(R.id.cost_description);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        prescriptionsList = (RecyclerView) view.findViewById(R.id.prescriptions_list);
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
                ));

        if (visitReportDetail != null && visitReportDetail.getAssignedProviderInfo() != null) {

            // preferred method for loading image
            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(visitReportDetail.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.drawable.img_provider_photo_placeholder))
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

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getName() == null) {
                                pharmacyName.setVisibility(View.GONE);
                            } else {
                                pharmacyName.setText(visitReportDetail.getPharmacy().getName());
                            }

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getDistance() < 0) {
                                pharmacyDistance.setVisibility(View.GONE);
                            } else {
                                pharmacyDistance.setText(String.valueOf(visitReportDetail.getPharmacy().getDistance()) + " mi");
                            }

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getAddress() == null) {
                                pharmacyAddress.setVisibility(View.GONE);
                            } else {

                                pharmacyAddress.setText(CommonUtil.getPharmacyAddress(visitReportDetail.getPharmacy()));
                            }

                            if (visitReportDetail.getProviderEntries() != null && visitReportDetail.getProviderEntries().getPrescriptions() != null) {
                                displayPrescriptions(visitReportDetail.getProviderEntries().getPrescriptions());
                            }

                            updateDoctorImage();
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

                            progressBar.setVisibility(View.GONE);

                            Timber.d("file ExternalStorageState = " + Environment.getExternalStorageState());
                            Timber.d("file Permission = " + CommonUtil.checkExternalStoragePermission(getContext()));

                            try {
                                if (pdfFile == null) {
                                    Toast.makeText(getContext(), "Visit report not available. ", Toast.LENGTH_LONG).show();
                                    viewReport.setEnabled(false);
                                } else if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    Toast.makeText(getContext(), "Storage not available. ", Toast.LENGTH_LONG).show();
                                    viewReport.setEnabled(false);
                                } else if (!CommonUtil.checkExternalStoragePermission(getContext())) {
                                    Toast.makeText(getContext(), "Storage Access permission denied.", Toast.LENGTH_LONG).show();
                                    viewReport.setEnabled(false);
                                } else {
                                    Timber.d("file DownloadCache Directory = " + Environment.getDownloadCacheDirectory());
                                    Timber.d("file Root Directory = " + Environment.getRootDirectory());
                                    Timber.d("file Data Directory = " + Environment.getDataDirectory());
                                    Timber.d("file getExternalStorageDirectory = " + Environment.getExternalStorageDirectory());

                                    /*Timber.d("file ExternalCacheDir = " + getContext().getExternalCacheDir().toString());
                                    Timber.d("file CacheDir = " + getContext().getCacheDir().toString());
                                    Timber.d("file DIRECTORY_DOWNLOADS = " + getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
                                    */

                                    String fileNameWithEntirePath = Environment.getExternalStorageDirectory().toString() + File.separator + "Report.pdf";

                                    boolean fileSaved = CommonUtil.saveFileToStorage(getContext(), fileNameWithEntirePath, IOUtils.toByteArray(pdfFile.getInputStream()));

                                    if (fileSaved) {
                                        Toast.makeText(getContext(), "Visit report is available. ", Toast.LENGTH_LONG).show();
                                    }
                                    CommonUtil.openPdf(getContext(), fileNameWithEntirePath);
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
}
