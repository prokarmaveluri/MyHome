package com.prokarma.myhome.features.mycare;


import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.SDKUtils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareNowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareNowFragment extends BaseFragment implements View.OnClickListener {

    private TextView infoEdit;
    private TextView historyEdit;
    private TextView medicationsDesc;
    private TextView medicationsEdit;
    private TextView pharmacyDesc;
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

        infoEdit = (TextView) view.findViewById(R.id.personal_info_edit);
        historyEdit = (TextView) view.findViewById(R.id.medical_history_edit);
        medicationsDesc = (TextView) view.findViewById(R.id.medications_desc);
        medicationsEdit = (TextView) view.findViewById(R.id.medications_edit);
        pharmacyDesc = (TextView) view.findViewById(R.id.pharmacy_desc);
        pharmacyEdit = (TextView) view.findViewById(R.id.pharmacy_edit);
        Button waitingRoom = (Button) view.findViewById(R.id.waiting_room_button);

        infoEdit.setOnClickListener(this);
        historyEdit.setOnClickListener(this);
        medicationsEdit.setOnClickListener(this);
        pharmacyEdit.setOnClickListener(this);
        waitingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Services is ignored
//                ((NavigationActivity) getActivity()).loadFragment(
//                        Constants.ActivityTag.MY_CARE_SERVICES, null);

                ((NavigationActivity) getActivity()).loadFragment(
                        Constants.ActivityTag.MY_CARE_PROVIDERS, null);
            }
        });

        getConditionsFetchOnly();
        getAllergiesFetchOnly();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setConsumerMedications();
        setConsumerPharmacy();
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

        switch (Id) {
            case R.id.personal_info_edit:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROFILE_VIEW, null);
                break;
            case R.id.medical_history_edit:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_MED_HISTORY, null);
                break;
            case R.id.medications_edit:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_MEDICATIONS, null);
                break;
            case R.id.pharmacy_edit:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_PHARMACY, null);
                break;
        }
    }

    private void getConsumerPharmacy() {
        SDKUtils.getInstance().getAWSDK().getConsumerManager().getConsumerPharmacy(
                SDKUtils.getInstance().getConsumer(), new SDKCallback<Pharmacy, SDKError>() {
                    @Override
                    public void onResponse(Pharmacy pharmacy, SDKError sdkError) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    private void getConditionsFetchOnly() {
        SDKUtils.getInstance().getAWSDK().getConsumerManager().getConditions(
                SDKUtils.getInstance().getConsumer(),
                new SDKCallback<List<Condition>, SDKError>() {
                    @Override
                    public void onResponse(List<Condition> conditions, SDKError sdkError) {
                        if (sdkError == null) {
                            SDKUtils.getInstance().setConditions(conditions);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                }
        );
    }

    private void getAllergiesFetchOnly() {
        SDKUtils.getInstance().getAWSDK().getConsumerManager().getAllergies(
                SDKUtils.getInstance().getConsumer(),
                new SDKCallback<List<Allergy>, SDKError>() {
                    @Override
                    public void onResponse(List<Allergy> allergies, SDKError sdkError) {
                        if (sdkError == null) {
                            SDKUtils.getInstance().setAllergies(allergies);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                });
    }

    private void setConsumerMedications() {
        List<Medication> medicationsList = SDKUtils.getInstance().getMedications();

        if (!SDKUtils.getInstance().isHasMedicationsFilledOut()) {
            medicationsDesc.setText(getString(R.string.what_medications_are_you_taking));
        } else if (medicationsList != null && medicationsList.size() > 0) {
            StringBuilder medications = new StringBuilder();

            for (int i = 0; i < medicationsList.size(); i++) {
                medications.append(medicationsList.get(i).getName());

                if (i < medicationsList.size() - 1) {
                    medications.append(", ");
                }
            }

            medicationsDesc.setText(medications.toString());
        } else {
            medicationsDesc.setText(getString(R.string.no_medications_listed));
        }
    }

    private void setConsumerPharmacy() {
        Pharmacy pharmacy = SDKUtils.getInstance().getConsumerPharmacy();

        if (pharmacy != null) {
            pharmacyDesc.setText(pharmacy.getName() + "\n" + CommonUtil.getPharmacyAddress(pharmacy));
        } else {
            pharmacyDesc.setText(getString(R.string.choose_your_preferred_pharmacy));
        }
    }
}
