package com.televisit.summary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;

public class SummaryFragment extends Fragment {
    public static final String SUMMARY_TAG = "summary_tag";
    public static final String VISIT_END_REASON_KEY = "visit_end_reason_key";

    private ProgressBar progressBar;
    private TextView costDesc;
    private Button viewReport;
    private CircularImageView docImage;

    private VisitSummary summary;
    private VisitEndReason endReason;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            endReason = (VisitEndReason) getArguments().getSerializable(VISIT_END_REASON_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.visit_summary));
        View view = inflater.inflate(R.layout.visit_summary, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);
        costDesc = (TextView) view.findViewById(R.id.cost_description);
        viewReport = (Button) view.findViewById(R.id.view_report);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        getVisitSummary();

        viewReport.setEnabled(summary != null);
        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Summary = " + summary, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void updateCost(String costInfo) {
        costDesc.setText(costInfo);
    }

    private void getVisitSummary() {
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK()
                .getVisitManager().getVisitSummary(AwsManager.getInstance().getVisit(),
                new SDKCallback<VisitSummary, SDKError>() {
                    @Override
                    public void onResponse(VisitSummary visitSummary, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);
                        updateCost(getString(R.string.visit_cost_desc) + visitSummary.getVisitCost().getExpectedConsumerCopayCost());
                        summary = visitSummary;
                        viewReport.setEnabled(summary != null);
                        updateDocImage();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void updateDocImage() {
        if (summary.getAssignedProviderInfo().hasImage()) {
            // preferred method for loading image
            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(summary.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .build()
                    .load();
        }
    }
}
