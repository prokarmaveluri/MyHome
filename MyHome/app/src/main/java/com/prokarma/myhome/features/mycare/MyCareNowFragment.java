package com.prokarma.myhome.features.mycare;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsGetAllergies;
import com.televisit.interfaces.AwsGetConditions;
import com.televisit.interfaces.AwsGetMedications;
import com.televisit.interfaces.AwsGetPharmacy;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsUserAuthentication;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareNowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareNowFragment extends BaseFragment implements View.OnClickListener, AwsUserAuthentication, AwsInitialization,
        AwsConsumer, AwsGetPharmacy, AwsGetMedications, AwsGetConditions, AwsGetAllergies {

    public static final String MCN_DASHBOARD_TAG = "mcn_dashboard_tag";

    private TextView infoEdit;
    private TextView historyDesc;
    private TextView historyEdit;
    private TextView medicationsDesc;
    private TextView medicationsEdit;
    private TextView pharmacyDesc;
    private TextView pharmacyEdit;
    private ImageView pharmacyPin;
    private Spinner consumerSpinner;
    private ProgressBar progressBar;
    private RelativeLayout userLayout;
    private TextView previousVisit;
    private Button waitingRoom;

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
        return new MyCareNowFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        pharmacyPin = (ImageView) view.findViewById(R.id.pharmacy_pin_icon);

        progressBar = (ProgressBar) view.findViewById(R.id.mcn_progressbar);
        userLayout = (RelativeLayout) view.findViewById(R.id.mcn_user_info);
        consumerSpinner = (Spinner) view.findViewById(R.id.mcn_dependents_spinner);
        waitingRoom = (Button) view.findViewById(R.id.waiting_room_button);

        infoEdit.setOnClickListener(this);
        historyEdit.setOnClickListener(this);
        medicationsEdit.setOnClickListener(this);
        pharmacyEdit.setOnClickListener(this);
        previousVisit = (TextView) view.findViewById(R.id.previous_visits);
        previousVisit = (TextView) view.findViewById(R.id.previous_visits);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pharmacyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdded()) {
                    return;
                }
                Pharmacy pharmacy = AwsManager.getInstance().getConsumerPharmacy();
                if (pharmacy != null) {
                    CommonUtil.getDirections(getActivity(), pharmacy.getAddress().getAddress1(), pharmacy.getAddress().getCity(), pharmacy.getAddress().getState().getCode());
                } else {
                    Toast.makeText(getContext(), getString(R.string.directions_not_found), Toast.LENGTH_LONG).show();
                }
            }
        });

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

        previousVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).loadFragment(
                        Constants.ActivityTag.PREVIOUS_VISITS_SUMMARIES, null);
            }
        });

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            showLoading();
            AwsNetworkManager.getInstance().initializeAwsdk(BuildConfig.awsdkurl, BuildConfig.awsdkkey, null, this);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDashboard(false);
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
                if (!AwsManager.getInstance().isPatientMainConsumer()) {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_PROFILE_DEPENDENT, null);
                } else {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_PROFILE, null);
                }
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

        AwsNetworkManager.getInstance().getMedications(AwsManager.getInstance().getPatient(), this);
    }

    private void getConsumerPharmacy() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }

        AwsNetworkManager.getInstance().getPharmacy(AwsManager.getInstance().getPatient(), this);
    }

    private void getConsumerConditions() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsNetworkManager.getInstance().getConditions(AwsManager.getInstance().getPatient(), this);
    }

    private void getConsumerAllergies() {
        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            return;
        }
        AwsNetworkManager.getInstance().getAllergies(AwsManager.getInstance().getPatient(), this);
    }

    private void setConsumerMedications() {

        if (!isAdded()) {
            return;
        }

        List<Medication> medicationsList = AwsManager.getInstance().getMedications();
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

    private void setConsumerPharmacy() {
        if (!isAdded()) {
            return;
        }
        Pharmacy pharmacy = AwsManager.getInstance().getConsumerPharmacy();
        if (pharmacy != null) {
            pharmacyDesc.setText(pharmacy.getName() + "\n" + CommonUtil.getPharmacyAddress(pharmacy));
        } else {
            pharmacyDesc.setText(getString(R.string.choose_your_preferred_pharmacy));
            pharmacyPin.setVisibility(View.GONE);
        }
    }

    private void setConsumerMedicalHistory() {
        if (!isAdded()) {
            return;
        }

        List<Allergy> allergies = CommonUtil.getCurrentAllergies(AwsManager.getInstance().getAllergies());
        List<Condition> conditions = CommonUtil.getCurrentConditions(AwsManager.getInstance().getConditions());

        if ((conditions != null && !conditions.isEmpty()) || (allergies != null && !allergies.isEmpty())) {

            StringBuilder medicalHistory = new StringBuilder();

            if (conditions != null && !conditions.isEmpty()) {
                for (int i = 0; i < conditions.size(); i++) {
                    medicalHistory.append(conditions.get(i).getName());

                    if (i < conditions.size() - 1) {
                        medicalHistory.append(", ");
                    }
                }
            } else if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.FILLED_OUT_HAVE_NONE) {
                medicalHistory.append(getContext().getResources().getString(R.string.no_conditions));
            } else if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.NOT_FILLED_OUT) {
                medicalHistory.append(getContext().getResources().getString(R.string.complete_your_medical_conditions));
            }

            if (allergies != null && !allergies.isEmpty()) {

                if (medicalHistory.length() > 0) {
                    medicalHistory.append("\n\n" + getContext().getResources().getString(R.string.allergic_to));
                }
                for (int i = 0; i < allergies.size(); i++) {
                    medicalHistory.append(allergies.get(i).getName());

                    if (i < allergies.size() - 1) {
                        medicalHistory.append(", ");
                    }
                }
            } else if (AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.FILLED_OUT_HAVE_NONE) {
                medicalHistory.append("\n\n" + getContext().getResources().getString(R.string.no_allergies));
            } else if (AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.NOT_FILLED_OUT) {
                medicalHistory.append("\n\n" + getContext().getResources().getString(R.string.complete_your_medical_allergies));
            }

            historyDesc.setText(medicalHistory.toString());

        } else if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.FILLED_OUT_HAVE_NONE
                && AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.FILLED_OUT_HAVE_NONE) {

            historyDesc.setText(getContext().getResources().getString(R.string.no_conditions)
                    + "\n" + getContext().getResources().getString(R.string.no_allergies));

        } else if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.NOT_FILLED_OUT
                && AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.NOT_FILLED_OUT) {

            historyDesc.setText(getString(R.string.complete_your_medical_history));
        }
    }

    private void setDependentsSpinner(Consumer me, List<Consumer> dependents) {
        List<Consumer> consumers = new ArrayList(dependents);
        consumers.add(0, me);
        DependentsSpinnerAdapter dependentsSpinnerAdapter = new DependentsSpinnerAdapter(getContext(), R.layout.dependents_spinner_item, consumers);
        consumerSpinner.setAdapter(dependentsSpinnerAdapter);
        consumerSpinner.setSelection(AwsManager.getInstance().getPatientNumber());
        consumerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Selected Me
                    AwsManager.getInstance().setPatient(AwsManager.getInstance().getConsumer());
                    refreshDashboard(true);
                } else {
                    //Selected a Dependent - need to minus one due to adding yourself to the list
                    AwsManager.getInstance().setPatient(AwsManager.getInstance().getConsumer().getDependents().get(position - 1));
                    refreshDashboard(true);
                }

                AwsManager.getInstance().setPatientNumber(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void refreshDashboard(boolean forceRefresh) {
        if (AwsManager.getInstance().isHasConsumer() && !forceRefresh) {
            setConsumerMedications();
            setConsumerPharmacy();
            setConsumerMedicalHistory();
        } else if (AwsManager.getInstance().isHasConsumer()) {
            getConsumerMedications();
            getConsumerPharmacy();
            getConsumerConditions();
            setConsumerMedications();
            setConsumerPharmacy();
            setConsumerMedicalHistory();
        }
    }

    @Override
    public void initializationComplete() {
        if (BuildConfig.awsdkurl.equals("https://sdk.myonlinecare.com")) {
            //Dev
            AwsNetworkManager.getInstance().getUsersAuthentication("jj@prokarma.com", "Pass123*", this);
        } else {
            //IoT
            AwsNetworkManager.getInstance().getUsersAuthentication("jjjj@pk.com", "Password1", this);
            //AwsNetworkManager.getInstance().getUsersMutualAuthneticaion(AuthManager.getAmWellToken());
        }
    }

    @Override
    public void initializationFailed(String errorMessage) {
        CommonUtil.showToast(getContext(), "Error: " + errorMessage);
        errorLoading();
    }

    @Override
    public void authenticationComplete(Authentication authentication) {
        AwsNetworkManager.getInstance().getConsumer(authentication, this);
    }

    @Override
    public void authentciationFailed(String errorMessage) {
        CommonUtil.showToast(getContext(), "Error: " + errorMessage);
        errorLoading();
    }

    @Override
    public void getConsumerComplete(Consumer consumer) {
        if (!isAdded()) {
            return;
        }
        setDependentsSpinner(consumer, consumer.getDependents());
        finishLoading();
    }

    @Override
    public void getConsumerFailed(String errorMessage) {
        errorLoading();
    }

    @Override
    public void getPharmacyComplete(Pharmacy pharmacy) {
        setConsumerPharmacy();
    }

    @Override
    public void getPharmacyFailed(String errorMessage) {

    }

    @Override
    public void getMedicationsComplete(List<Medication> medications) {
        setConsumerMedications();
    }

    @Override
    public void getMedicationsFailed(String errorMessage) {

    }

    @Override
    public void getConditionsComplete(List<Condition> conditions) {
        getConsumerAllergies();
    }

    @Override
    public void getConditionsFailed(String errorMessage) {

    }

    @Override
    public void getAllergiesComplete(List<Allergy> allergy) {
        setConsumerMedicalHistory();
    }

    @Override
    public void getAllergiesFailed(String errorMessage) {

    }
}
