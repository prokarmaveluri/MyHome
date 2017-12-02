package com.prokarma.myhome.features.mycare;


import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.DependentsSpinnerAdapter;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsUserAuthentication;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareNowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareNowFragment extends BaseFragment implements View.OnClickListener, AwsUserAuthentication, AwsInitialization, AwsConsumer {

    private TextView infoEdit;
    private TextView historyDesc;
    private TextView historyEdit;
    private TextView medicationsDesc;
    private TextView medicationsEdit;
    private TextView pharmacyDesc;
    private TextView pharmacyEdit;
    private Spinner consumerSpinner;
    private ProgressBar progressBar;
    private RelativeLayout userLayout;

    private Consumer patient;

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
        historyDesc = (TextView) view.findViewById(R.id.medical_history_desc);
        historyEdit = (TextView) view.findViewById(R.id.medical_history_edit);
        medicationsDesc = (TextView) view.findViewById(R.id.medications_desc);
        medicationsEdit = (TextView) view.findViewById(R.id.medications_edit);
        pharmacyDesc = (TextView) view.findViewById(R.id.pharmacy_desc);
        pharmacyEdit = (TextView) view.findViewById(R.id.pharmacy_edit);
        progressBar = (ProgressBar) view.findViewById(R.id.mcn_progressbar);
        userLayout = (RelativeLayout) view.findViewById(R.id.mcn_user_info);
        consumerSpinner = (Spinner) view.findViewById(R.id.mcn_dependents_spinner);

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            showLoading();
            AwsManager.getInstance().initializeAwsdk(BuildConfig.awsdkurl, BuildConfig.awsdkkey, null, this);
        } else if (!AwsManager.getInstance().isHasAuthenticated()) {
            showLoading();
            this.initializationComplete();
        } else if (!AwsManager.getInstance().isHasConsumer()) {
            showLoading();
            this.authenticationComplete(AwsManager.getInstance().getAuthentication());
        } else {
            setDependentsSpinner(AwsManager.getInstance().getConsumer(), AwsManager.getInstance().getConsumer().getDependents());
            setConsumerMedications();
            setConsumerPharmacy();
            setConsumerMedicalHistory();
        }

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

        TextView previousVisit = (TextView) view.findViewById(R.id.previous_visits);
        previousVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Vijaya, do your fragment stuff here. Also, look at visit_summary.xml && SummaryFragment.java
                ((NavigationActivity) getActivity()).loadFragment(
                        Constants.ActivityTag.PREVIOUS_VISITS_SUMMARY, null);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AwsManager.getInstance().isHasConsumer()) {
            setConsumerMedications();
            setConsumerPharmacy();
            setConsumerMedicalHistory();
        }
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

    private void showLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
            userLayout.setVisibility(View.GONE);
        }
    }

    private void finishLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            userLayout.setVisibility(View.VISIBLE);
        }
    }

    private void errorLoading() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            userLayout.setVisibility(View.GONE);
        }
    }

    private void getConsumerMedications() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsManager.getInstance().getAWSDK().getConsumerManager().getMedications(
                patient, new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        AwsManager.getInstance().setMedications(medications);
                        setConsumerMedications();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    private void getConsumerPharmacy() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConsumerPharmacy(
                patient, new SDKCallback<Pharmacy, SDKError>() {
                    @Override
                    public void onResponse(Pharmacy pharmacy, SDKError sdkError) {
                        AwsManager.getInstance().setConsumerPharmacy(pharmacy);
                        setConsumerPharmacy();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    private void getConsumerConditions() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConditions(
                patient,
                new SDKCallback<List<Condition>, SDKError>() {
                    @Override
                    public void onResponse(List<Condition> conditions, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConditions(conditions);
                            getConsumerAllergies();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                }
        );
    }

    private void getConsumerAllergies() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsManager.getInstance().getAWSDK().getConsumerManager().getAllergies(
                patient,
                new SDKCallback<List<Allergy>, SDKError>() {
                    @Override
                    public void onResponse(List<Allergy> allergies, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setAllergies(allergies);
                            setConsumerMedicalHistory();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                });
    }

    private void setConsumerMedications() {
        List<Medication> medicationsList = AwsManager.getInstance().getMedications();

        if (isAdded()) {
            if (!AwsManager.getInstance().isHasMedicationsFilledOut()) {
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
    }

    private void setConsumerPharmacy() {
        Pharmacy pharmacy = AwsManager.getInstance().getConsumerPharmacy();

        if (isAdded()) {
            if (pharmacy != null) {
                pharmacyDesc.setText(pharmacy.getName() + "\n" + CommonUtil.getPharmacyAddress(pharmacy));
            } else {
                pharmacyDesc.setText(getString(R.string.choose_your_preferred_pharmacy));
            }
        }
    }

    private void setConsumerMedicalHistory() {
        List<Allergy> allergies = CommonUtil.getCurrentAllergies(AwsManager.getInstance().getAllergies());
        List<Condition> conditions = CommonUtil.getCurrentConditions(AwsManager.getInstance().getConditions());

        if (isAdded()) {

            if ((conditions != null && !conditions.isEmpty()) || (allergies != null && !allergies.isEmpty())) {
                StringBuilder medicalHistory = new StringBuilder();

                for (int i = 0; i < conditions.size(); i++) {
                    medicalHistory.append(conditions.get(i).getName());

                    if (i < conditions.size() - 1) {
                        medicalHistory.append(", ");
                    }
                }

                if (medicalHistory.length() > 0 && !allergies.isEmpty()) {
                    medicalHistory.append("\n" + getContext().getResources().getString(R.string.allergic_to));
                }

                for (int i = 0; i < allergies.size(); i++) {
                    medicalHistory.append(allergies.get(i).getName());

                    if (i < allergies.size() - 1) {
                        medicalHistory.append(", ");
                    }
                }

                historyDesc.setText(medicalHistory.toString());

            } else if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.NOT_FILLED_OUT || AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.NOT_FILLED_OUT) {
                historyDesc.setText(getString(R.string.complete_your_medical_history));
            } else {
                historyDesc.setText(getString(R.string.complete_your_medical_history));  //no_medical_complications_listed));
            }
        }
    }

    private void setDependentsSpinner(Consumer me, List<Consumer> dependents) {
        List<Consumer> consumers = new ArrayList(dependents);
        consumers.add(0, me);
        DependentsSpinnerAdapter dependentsSpinnerAdapter = new DependentsSpinnerAdapter(getContext(), R.layout.dependents_spinner_item, consumers);
        consumerSpinner.setAdapter(dependentsSpinnerAdapter);
        consumerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Selected Me
                    patient = AwsManager.getInstance().getConsumer();
                    AwsManager.getInstance().setDependent(null);
                    refreshDashboard();
                } else {
                    //Selected a Dependent - need to minus one due to adding yourself to the list
                    patient = AwsManager.getInstance().getConsumer().getDependents().get(position - 1);
                    AwsManager.getInstance().setDependent(patient);
                    refreshDashboard();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void refreshDashboard(){


    }

    @Override
    public void initializationComplete() {
        if (BuildConfig.awsdkurl.equals("https://sdk.myonlinecare.com")) {
            //Dev
            AwsManager.getInstance().getUsersAuthentication("cmajji@mailinator.com", "Pass123*", this);
        } else {
            //IoT
            AwsManager.getInstance().getUsersAuthentication("jjjj@pk.com", "Password1", this);
        }
    }

    @Override
    public void initializationFailed(String errorMessage) {
        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        errorLoading();
    }

    @Override
    public void authenticationComplete(Authentication authentication) {
        AwsManager.getInstance().getConsumer(authentication, this);
    }

    @Override
    public void authentciationFailed(String errorMessage) {
        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        errorLoading();
    }

    @Override
    public void consumerComplete(Consumer consumer) {
        if (isAdded()) {
            setDependentsSpinner(consumer, consumer.getDependents());
            finishLoading();
        }
    }

    @Override
    public void consumerFailed(String errorMessage) {
        errorLoading();
    }

}
