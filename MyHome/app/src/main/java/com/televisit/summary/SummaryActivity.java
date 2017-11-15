package com.televisit.summary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdksample.SampleApplication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.SDKUtils;

public class SummaryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView costDesc;
    private Button viewPDF;
    private CircularImageView docImage;

    private VisitSummary summary;

    /*
     * Get an intent for login activity.
     */
    public static Intent getSummaryIntent(Context context) {

        return new Intent(context, SummaryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_summary);

        progressBar = (ProgressBar) findViewById(R.id.summary_progress);
        costDesc = (TextView) findViewById(R.id.costDesc);
        viewPDF = (Button) findViewById(R.id.viewPDF);
        docImage = (CircularImageView) findViewById(R.id.docImage);
        getVisitSummary();

        viewPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateCost(String costInfo) {
        costDesc.setText(costInfo);
    }

    private void getVisitSummary() {
        progressBar.setVisibility(View.VISIBLE);
        SampleApplication.getInstance().getAWSDK()
                .getVisitManager().getVisitSummary(SDKUtils.getInstance().getVisit(),
                new SDKCallback<VisitSummary, SDKError>() {
                    @Override
                    public void onResponse(VisitSummary visitSummary, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);
                        updateCost(getString(R.string.visit_cost_desc) +
                                visitSummary.getVisitCost().getExpectedConsumerCopayCost());
                        summary = visitSummary;
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
            SampleApplication.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(summary.getAssignedProviderInfo(), docImage, ProviderImageSize.EXTRA_LARGE)
                    .build()
                    .load();
        }
    }
}
