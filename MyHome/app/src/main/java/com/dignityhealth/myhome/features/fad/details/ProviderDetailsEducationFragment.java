package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;

public class ProviderDetailsEducationFragment extends Fragment {
    public static final String PROVIDER_DETAILS_EDUCATION_TAG = "provider_details_education_tag";
    public static final String PAGER_TITLE = "Education";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response_key";

    private ProviderDetailsResponse providerDetailsResponse;

    private View educationView;

    public ProviderDetailsEducationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderDetailsFragment.
     */
    public static ProviderDetailsEducationFragment newInstance() {
        ProviderDetailsEducationFragment fragment = new ProviderDetailsEducationFragment();
        return fragment;
    }

    public static ProviderDetailsEducationFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        ProviderDetailsEducationFragment fragment = new ProviderDetailsEducationFragment();
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
        educationView = inflater.inflate(R.layout.provider_details_education, container, false);
        return educationView;
    }
}