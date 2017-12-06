package com.televisit.summary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
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
import com.prokarma.myhome.utils.Constants;
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
    private RelativeLayout pharmacyPhoneLayout;
    private TextView pharmacyPhone;
    private TextView pharmacyAddress;
    private TextView costDesc;
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
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
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

        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("FILENAME_WITH_PATH", reportNameWithPath);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY_PDF, bundle);
            }
        });

        if (getArguments() != null && getArguments().containsKey(VISIT_LIST_POSITION)) {
            visitReportPosition = getArguments().getInt(VISIT_LIST_POSITION);
            if (visitReportPosition >= 0 && visitReportPosition < AwsManager.getInstance().getVisitReports().size()) {
                visitReport = AwsManager.getInstance().getVisitReports().get(visitReportPosition);
                getVisitReportDetails(visitReport);
            }
        }
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
                    .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();
        }
    }

    private void getVisitReportDetails(final VisitReport visitReport) {

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            Timber.d("visits VisitReportDetails. isHasInitializedAwsdk: FALSE ");
            return;
        }

        if (!isAdded()) {
            Timber.d("visits VisitReportDetails. isAdded: FALSE ");
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

                            providerName.setText(visitReport.getProviderName());
                            costDesc.setText(getString(R.string.visit_cost_desc) + detail.getVisitCost().getExpectedConsumerCopayCost());

                            if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getName() == null) {
                                pharmacyName.setVisibility(View.GONE);
                            } else {
                                pharmacyName.setVisibility(View.VISIBLE);
                                pharmacyName.setText(visitReportDetail.getPharmacy().getName());

                                if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getPhone() == null || visitReportDetail.getPharmacy().getPhone().isEmpty()) {
                                    pharmacyPhoneLayout.setVisibility(View.GONE);
                                } else {
                                    pharmacyPhoneLayout.setVisibility(View.VISIBLE);
                                    pharmacyPhone.setText(CommonUtil.constructPhoneNumberDots(visitReportDetail.getPharmacy().getPhone()));
                                    pharmacyPhoneLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + pharmacyPhone.getText().toString()));
                                            intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intentPhone);
                                        }
                                    });
                                }

                                if (visitReportDetail.getPharmacy() == null || visitReportDetail.getPharmacy().getAddress() == null) {
                                    pharmacyAddress.setVisibility(View.GONE);
                                } else {
                                    pharmacyAddress.setVisibility(View.VISIBLE);
                                    pharmacyAddress.setText(CommonUtil.getPharmacyAddress(visitReportDetail.getPharmacy()));
                                }
                            }

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
            Timber.d("visits VisitReportDetails. isHasInitializedAwsdk: FALSE ");
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
                                            doctorNotes.setVisibility(View.GONE);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Timber.e(e);
                            }

                            if (!canBeViewed) {
                                viewReport.setEnabled(false);
                                Toast.makeText(getContext(), "Visit report not available. ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                        viewReport.setEnabled(false);
                        doctorNotes.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Visit report not available! ", Toast.LENGTH_LONG).show();

                        if (throwable != null) {
                            Timber.d("visit. getVisitReportAttachment: " + throwable.getMessage());
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
}
