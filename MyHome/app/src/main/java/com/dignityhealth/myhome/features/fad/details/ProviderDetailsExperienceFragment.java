package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.CommonUtil;

public class ProviderDetailsExperienceFragment extends Fragment {
    public static final String PROVIDER_DETAILS_EXPERIENCE_TAG = "provider_details_experience_tag";
    public static final String PAGER_TITLE = "Experience";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response_key";

    private ProviderDetailsResponse providerDetailsResponse;

    private View experienceView;
    private TextView certificationsLabel;
    private TextView certifications;
    private TextView awardsLabel;
    private TextView awards;
    private ProgressBar progressBar;
    private View profileDetailsInfo;

    public ProviderDetailsExperienceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderDetailsFragment.
     */
    public static ProviderDetailsExperienceFragment newInstance() {
        ProviderDetailsExperienceFragment fragment = new ProviderDetailsExperienceFragment();
        return fragment;
    }

    public static ProviderDetailsExperienceFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        ProviderDetailsExperienceFragment fragment = new ProviderDetailsExperienceFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerDetailsResponse = getArguments().getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        experienceView = inflater.inflate(R.layout.provider_details_experience, container, false);
        profileDetailsInfo = experienceView.findViewById(R.id.profile_details_info);
        progressBar = (ProgressBar) experienceView.findViewById(R.id.progress_bar);

        certificationsLabel = (TextView) experienceView.findViewById(R.id.certifications_label);
        certifications = (TextView) experienceView.findViewById(R.id.certifications);
        awardsLabel = (TextView) experienceView.findViewById(R.id.awards_label);
        awards = (TextView) experienceView.findViewById(R.id.awards);

        setupViews();

        return experienceView;
    }

    private void setupViews() {
        if (providerDetailsResponse == null) {
            showProgressBar();
            return;
        }

        showView();
        if (providerDetailsResponse.getCertifications() != null && !providerDetailsResponse.getCertifications().isEmpty()) {
            certificationsLabel.setVisibility(View.VISIBLE);
            certifications.setVisibility(View.VISIBLE);
            CommonUtil.prettyPrint(providerDetailsResponse.getCertifications());
        } else {
            certificationsLabel.setVisibility(View.GONE);
            certifications.setVisibility(View.GONE);

            //Remove Margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            awardsLabel.setLayoutParams(params);
        }

        if (providerDetailsResponse.getAwards() != null && !providerDetailsResponse.getAwards().isEmpty()) {
            awardsLabel.setVisibility(View.VISIBLE);
            awards.setVisibility(View.VISIBLE);
            CommonUtil.prettyPrint(providerDetailsResponse.getAwards());
        } else {
            awardsLabel.setVisibility(View.GONE);
            awards.setVisibility(View.GONE);
        }
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        profileDetailsInfo.setVisibility(View.GONE);
    }

    private void showView(){
        progressBar.setVisibility(View.GONE);
        profileDetailsInfo.setVisibility(View.VISIBLE);
    }
}