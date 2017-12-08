package com.televisit.summary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.views.CircularImageView;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsGetVisitSummary;

public class VisitSummaryFragment extends BaseFragment implements AwsGetVisitSummary {
    public static final String SUMMARY_TAG = "summary_tag";
    public static final String VISIT_KEY = "visit_key";

    private LinearLayout summaryLayout;
    private ProgressBar progressBar;
    private TextView providerName;
    private TextView pharmacyName;
    private RelativeLayout pharmacyPhoneLayout;
    private TextView pharmacyPhone;
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
        View view = inflater.inflate(R.layout.visit_summary, container, false);

        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.visit_summary));
        } else {
            getActivity().setTitle(getString(R.string.visit_summary));
        }

        visit = AwsManager.getInstance().getVisit();

        summaryLayout = (LinearLayout) view.findViewById(R.id.summary_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.summary_progress);

        providerName = (TextView) view.findViewById(R.id.provider_name);

        pharmacyName = (TextView) view.findViewById(R.id.pharmacy_name);
        pharmacyPhoneLayout = (RelativeLayout) view.findViewById(R.id.pharmacy_phone_layout);
        pharmacyPhone = (TextView) view.findViewById(R.id.pharmacy_phone);
        pharmacyAddress = (TextView) view.findViewById(R.id.pharmacy_address);

        costDesc = (TextView) view.findViewById(R.id.cost_description);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        viewReport = (Button) view.findViewById(R.id.view_report);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoading();
        AwsNetworkManager.getInstance().getVisitSummary(visit, this);
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
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.VIDEO_VISIT_FEEDBACK, null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDoctorImage(VisitSummary visitSummary) {
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

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
    }

    private void showLayout() {
        progressBar.setVisibility(View.GONE);
        summaryLayout.setVisibility(View.VISIBLE);
    }

    private void errorLoading() {
        progressBar.setVisibility(View.GONE);
        summaryLayout.setVisibility(View.GONE);
    }

    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {
        showLayout();

        updateDoctorImage(visitSummary);

        providerName.setText(visitSummary.getAssignedProviderInfo().getFullName());

        pharmacyName.setText(visitSummary.getPharmacy().getName());
        pharmacyAddress.setText(CommonUtil.getPharmacyAddress(visitSummary.getPharmacy()));

        pharmacyPhone.setText(CommonUtil.constructPhoneNumberDots(visitSummary.getPharmacy().getPhone()));
        pharmacyPhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + pharmacyPhone.getText().toString()));
                intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentPhone);
            }
        });

        costDesc.setText(getString(R.string.summary_cost_desc) + visitSummary.getVisitCost().getExpectedConsumerCopayCost());
    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {
        errorLoading();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.VIDEO_VISIT_SUMMARY;
    }
}
