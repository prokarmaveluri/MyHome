package com.dignityhealth.myhome.features.fad.details;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;

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
    private TextView locationsLabel;
    private ProgressBar progressBar;
    private View profileDetailsInfo;


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
        profileDetailsInfo = profileView.findViewById(R.id.profile_details_info);
        progressBar = (ProgressBar) profileView.findViewById(R.id.progress_bar);

        acceptingNewPatients = (TextView) profileView.findViewById(R.id.accepting_new_patients);
        languages = (TextView) profileView.findViewById(R.id.languages);
        gender = (TextView) profileView.findViewById(R.id.gender);
        experience = (TextView) profileView.findViewById(R.id.experience);
        philosophy = (TextView) profileView.findViewById(R.id.philosophy);
        locations = (TextView) profileView.findViewById(R.id.locations);
        locationsLabel = (TextView) profileView.findViewById(R.id.label_locations);

        setupViews();

        return profileView;
    }

    private void setupViews() {
        if (providerDetailsResponse == null) {
            showProgressBar();
            return;
        }

        showView();
        acceptingNewPatients.setText(providerDetailsResponse.getAcceptsNewPatients() ? "Yes" : "No");
        languages.setText(providerDetailsResponse.getLanguages() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getLanguages()) : "Unknown");

        if (providerDetailsResponse.getGender() != null && !providerDetailsResponse.getGender().isEmpty()) {
            if (providerDetailsResponse.getGender().equalsIgnoreCase("M") || providerDetailsResponse.getGender().equalsIgnoreCase("Male")) {
                gender.setText("Male");
            } else if (providerDetailsResponse.getGender().equalsIgnoreCase("F") || providerDetailsResponse.getGender().equalsIgnoreCase("Female")) {
                gender.setText("Female");
            } else {
                gender.setText("Unknown");
            }
        }

        experience.setText(providerDetailsResponse.getYearsOfExperience() != null ? providerDetailsResponse.getYearsOfExperience() : "Unknown");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            philosophy.setText(providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResponse.getPhilosophy(), Html.FROM_HTML_MODE_COMPACT) : "Unknown");
        } else {
            philosophy.setText(providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResponse.getPhilosophy()) : "Unknown");
        }

        //Adjust Margin to account for HTML paragraph break
        if (providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty()) {
            philosophy.setVisibility(View.VISIBLE);
            profileView.findViewById(R.id.label_philosophy).setVisibility(View.VISIBLE);
        } else {
            //Adjust to no philosophy
            philosophy.setVisibility(View.GONE);
            profileView.findViewById(R.id.label_philosophy).setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, gender.getId());
            params.setMargins(0, DeviceDisplayManager.dpToPx(getContext(), 24), 0, 0);
            locationsLabel.setLayoutParams(params);
        }

        locations.setText(providerDetailsResponse.getOffices() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getOffices()) : "Unknown");
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