package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;

public class ProviderDetailsProfileFragment extends Fragment {
    public static final String PROVIDER_DETAILS_PROFILE_TAG = "provider_details_profile_tag";
    public static final String PAGER_TITLE = "Profile";
    private View profileView;

    public ProviderDetailsProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderDetailsFragment.
     */
    public static ProviderDetailsProfileFragment newInstance() {
        ProviderDetailsProfileFragment fragment = new ProviderDetailsProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.provider_details_profile, container, false);
        return profileView;
    }
}