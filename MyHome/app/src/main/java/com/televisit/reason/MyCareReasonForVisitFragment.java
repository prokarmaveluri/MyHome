package com.televisit.reason;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareReasonForVisitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareReasonForVisitFragment extends BaseFragment {

    private ProgressBar progressBar;
    public static final String MY_CARE_REASON_TAG = "my_care_reason_tag";

    public MyCareReasonForVisitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareReasonForVisitFragment newInstance() {
        MyCareReasonForVisitFragment fragment = new MyCareReasonForVisitFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_care_reason, container, false);

        Button nextButton = (Button) view.findViewById(R.id.next_button);
        final TextInputEditText reasonEmail = (TextInputEditText) view.findViewById(R.id.reasonEmail);
        final TextInputEditText reasonPhone = (TextInputEditText) view.findViewById(R.id.reasonPhone);
        final TextInputEditText reasonForVisit = (TextInputEditText) view.findViewById(R.id.reasonForVisit);

        final TextInputLayout reasonLayout = (TextInputLayout) view.findViewById(R.id.reason_layout);
        final TextInputLayout emailLayout = (TextInputLayout) view.findViewById(R.id.email_layout);
        final TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.phone_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.services_progress);

        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.intake));

        createVisit();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AwsManager.getInstance().getVisit() == null)
                    return;

                if (reasonPhone.getText().toString().length() == 10 &&
                        reasonForVisit.getText().toString().length() > 0) {

                    if (getActivity() != null) {
                        ((NavigationActivity) getActivity()).onBackPressed();
                    }
                    if (!AwsManager.getInstance().getVisit().getVisitCost().isFree()) {
                        ((NavigationActivity) getActivity()).loadFragment(
                                Constants.ActivityTag.MY_CARE_COST, null);
                    } else {
                        ((NavigationActivity) getActivity()).loadFragment(
                                Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
                    }
                } else if (reasonPhone.getText().toString().length() != 10) {
                    phoneLayout.setError("Enter valid phone number");
                } else if (reasonForVisit.getText().toString().length() <= 0) {
                    reasonLayout.setError("Enter valid reason for visit");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        //return Constants.ActivityTag.MY_CARE_REASON;
        return null;
    }

    private void createVisit() {
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getVisitManager().createOrUpdateVisit(
                AwsManager.getInstance().getVisitContext(),
                new SDKValidatedCallback<Visit, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.i("Failure " + map.toString());
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(Visit visit, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisit(visit);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }
}
