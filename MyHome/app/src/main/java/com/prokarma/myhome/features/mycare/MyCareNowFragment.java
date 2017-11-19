package com.prokarma.myhome.features.mycare;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.SDKOptionsActivity;
import com.televisit.SDKUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareNowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareNowFragment extends BaseFragment implements View.OnClickListener {

    private TextView infoEdit;
    private TextView historyEdit;
    private TextView medicationsEdit;
    private TextView pharmacyEdit;

    public MyCareNowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareFragment.
     */
    public static MyCareNowFragment newInstance() {
        MyCareNowFragment fragment = new MyCareNowFragment();
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
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.mycare_now));
        View view = inflater.inflate(R.layout.fragment_my_care, container, false);

        infoEdit = (TextView) view.findViewById(R.id.infoEdit);
        historyEdit = (TextView) view.findViewById(R.id.historyEdit);
        medicationsEdit = (TextView) view.findViewById(R.id.medicationsEdit);
        pharmacyEdit = (TextView) view.findViewById(R.id.pharmacyEdit);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.personalInfoLayout);
        Button waitingRoom = (Button) view.findViewById(R.id.waitingRoomButton);

        infoEdit.setOnClickListener(this);
        historyEdit.setOnClickListener(this);
        medicationsEdit.setOnClickListener(this);
        pharmacyEdit.setOnClickListener(this);
        waitingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Enable for sample app Demo
//                SampleApplication.getInstance().initVisit(getActivity().getApplicationContext());
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent);

                //TODO: Enable video visit in application
                ((NavigationActivity) getActivity()).loadFragment(
                        Constants.ActivityTag.MY_CARE_SERVICES, null);
            }
        });

        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_NOW;
    }

    @Override
    public void onClick(View v) {
        int Id = v.getId();

        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                R.anim.slide_in_right, R.anim.slide_out_left);
        Intent intent = new Intent(getActivity(), SDKOptionsActivity.class);

        switch (Id) {
            case R.id.infoEdit:
                NavigationActivity.setActivityTag(Constants.ActivityTag.PROFILE_VIEW);
                break;
            case R.id.historyEdit:
                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_MED_HISTORY);
                break;
            case R.id.medicationsEdit:
                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_MEDICATIONS);
                break;
            case R.id.pharmacyEdit:
                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_PHARMACY);
                break;
        }
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    private void getConsumerPharmacy() {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConsumerPharmacy(
                SDKUtils.getInstance().getConsumer(), new SDKCallback<Pharmacy, SDKError>() {
                    @Override
                    public void onResponse(Pharmacy pharmacy, SDKError sdkError) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }
}
