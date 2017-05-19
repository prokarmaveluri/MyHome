package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.CommonUtil;

public class ProviderDetailsExperienceFragment extends Fragment {
    public static final String PROVIDER_DETAILS_EXPERIENCE_TAG = "provider_details_experience_tag";
    public static final String PAGER_TITLE = "Experience";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response_key";

    private ProviderDetailsResponse providerDetailsResponse;

    private View experienceView;
    private TextView certifications;
    private TextView awards;

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

        certifications = (TextView) experienceView.findViewById(R.id.certifications);
        awards = (TextView) experienceView.findViewById(R.id.awards);

        setupViews();

        return experienceView;
    }

    private void setupViews() {
        if (providerDetailsResponse == null) {
            return;
        }

        certifications.setText(providerDetailsResponse.getCertifications() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getCertifications()) : "Unknown");
        awards.setText(providerDetailsResponse.getAwards() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getAwards()) : "Unknown");
    }
}