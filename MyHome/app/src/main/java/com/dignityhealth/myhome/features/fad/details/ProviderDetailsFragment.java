package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.features.fad.Provider;
import com.dignityhealth.myhome.utils.Constants;

public class ProviderDetailsFragment extends BaseFragment {
    public static final String PROVIDER_KEY = "PROVIDER_KEY";
    public static final String PROVIDER_DETAILS_TAG = "provider_details_tag";

    private Provider provider;
    private View providerDetailsView;

    public ProviderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderDetailsFragment.
     */
    public static ProviderDetailsFragment newInstance() {
        ProviderDetailsFragment fragment = new ProviderDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            provider = getArguments().getParcelable(PROVIDER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        providerDetailsView = inflater.inflate(R.layout.fragment_provider_details, container, false);
        return providerDetailsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROVIDER_DETAILS;
    }

}