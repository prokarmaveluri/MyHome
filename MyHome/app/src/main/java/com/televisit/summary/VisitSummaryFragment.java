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

import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.R;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsGetVisitSummary;

public class VisitSummaryFragment extends Fragment implements AwsGetVisitSummary {
    public static final String SUMMARY_TAG = "summary_tag";
    public static final String VISIT_KEY = "visit_key";

    private ProgressBar progressBar;
    private TextView providerName;
    private TextView pharmacyName;
    private TextView pharmacyDistance;
    private TextView pharmacyAddress;
    private TextView costDesc;
    private CircularImageView docImage;
    private Button viewReport;

    private Visit visit;

    public VisitSummaryFragment() {
    }

    public static VisitSummaryFragment newInstance() {
        VisitSummaryFragment fragment = new VisitSummaryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.visit_summary));
        View view = inflater.inflate(R.layout.visit_summary, container, false);

        visit = AwsManager.getInstance().getVisit();

        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);
        providerName = (TextView) view.findViewById(R.id.provider_name);

        pharmacyName = (TextView) view.findViewById(R.id.pharmacy_name);
        pharmacyDistance = (TextView) view.findViewById(R.id.pharmacy_distance);
        pharmacyAddress = (TextView) view.findViewById(R.id.pharmacy_address);

        costDesc = (TextView) view.findViewById(R.id.cost_description);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        viewReport = (Button) view.findViewById(R.id.view_report);

        AwsNetworkManager.getInstance().getVisitSummary(visit, this);

        return view;
    }

    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {
        providerName.setText(visitSummary.getAssignedProviderInfo().getFullName());
        pharmacyName.setText(visitSummary.getPharmacy().getName());
        pharmacyDistance.setText(String.valueOf(visitSummary.getPharmacy().getDistance()));
        pharmacyAddress.setText(visitSummary.getPharmacy().getAddress().getAddress1());

        costDesc.setText(String.valueOf(visitSummary.getVisitCost().getExpectedConsumerCopayCost()));


    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {

    }
}
