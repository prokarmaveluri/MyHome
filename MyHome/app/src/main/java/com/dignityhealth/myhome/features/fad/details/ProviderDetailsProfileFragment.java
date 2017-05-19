package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.CommonUtil;

public class ProviderDetailsProfileFragment extends Fragment {
    public static final String PROVIDER_DETAILS_PROFILE_TAG = "provider_details_profile_tag";
    public static final String PAGER_TITLE = "Profile";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response_key";

    private ProviderDetailsResponse providerDetailsResponse;

    private View profileView;
    private TextView acceptingNewPatients;
    private TextView languages;
    private TextView gender;
    private TextView experience;
    private TextView philosophy;
    private TextView locations;


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

    public static ProviderDetailsProfileFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        ProviderDetailsProfileFragment fragment = new ProviderDetailsProfileFragment();
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
        profileView = inflater.inflate(R.layout.provider_details_profile, container, false);

        acceptingNewPatients = (TextView) profileView.findViewById(R.id.accepting_new_patients);
        languages = (TextView) profileView.findViewById(R.id.languages);
        gender = (TextView) profileView.findViewById(R.id.gender);
        experience = (TextView) profileView.findViewById(R.id.experience);
        philosophy = (TextView) profileView.findViewById(R.id.philosophy);
        locations = (TextView) profileView.findViewById(R.id.locations);

        setupViews();

        return profileView;
    }

    private void setupViews() {
        if (providerDetailsResponse == null) {
            return;
        }

        acceptingNewPatients.setText(providerDetailsResponse.getAcceptsNewPatients() ? "Yes" : "No");
        languages.setText(providerDetailsResponse.getLanguages() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getLanguages()) : "Unknown");
        gender.setText(providerDetailsResponse.getGender() != null ? providerDetailsResponse.getGender() : "Unknown");
        experience.setText(providerDetailsResponse.getYearsOfExperience() != null ? providerDetailsResponse.getYearsOfExperience() : "Unknown");
        philosophy.setText(providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty() ? providerDetailsResponse.getPhilosophy() : "Unknown");
        locations.setText(providerDetailsResponse.getOffices() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getOffices()) : "Unknown");
    }
}