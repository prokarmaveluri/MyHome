package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.SessionUtil;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class ApiFragment extends BaseFragment {
    public static final String API_TAG = "api_tag";

    View apiView;
    SwitchCompat signInSwitch;
    SwitchCompat signInRefreshSwitch;
    SwitchCompat signOutSwitch;
    SwitchCompat profileGetSwitch;
    SwitchCompat profileUpdateSwitch;
    SwitchCompat getMyAppointmentsSwitch;
    SwitchCompat createAppointmentSwitch;
    SwitchCompat getValidationRulesSwitch;
    SwitchCompat getProviderDetailsSwitch;
    SwitchCompat registerSwitch;
    SwitchCompat changePasswordSwitch;
    SwitchCompat changeSecurityQuestionSwitch;
    SwitchCompat getSavedDoctorsSwitch;
    SwitchCompat saveDoctorSwitch;
    SwitchCompat deleteSavedDoctorSwitch;
    SwitchCompat forgotPasswordSwitch;
    SwitchCompat getProvidersSwitch;
    SwitchCompat getLocationSuggestionsSwitch;
    SwitchCompat getSearchSuggestionsSwitch;
    SwitchCompat getLocationSwitch;
    SwitchCompat findEmailSwitch;
    SwitchCompat versionCheckSwitch;

    public static ApiFragment newInstance() {
        return new ApiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        apiView = inflater.inflate(R.layout.api, container, false);
        getActivity().setTitle(getString(R.string.api_settings));

        signInSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_in_switch);
        signInRefreshSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_in_refresh_switch);
        signOutSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_out_switch);
        profileGetSwitch = (SwitchCompat) apiView.findViewById(R.id.profile_get_switch);
        profileUpdateSwitch = (SwitchCompat) apiView.findViewById(R.id.profile_update_switch);
        getMyAppointmentsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_my_appointments_switch);
        createAppointmentSwitch = (SwitchCompat) apiView.findViewById(R.id.create_appointments_switch);
        getValidationRulesSwitch = (SwitchCompat) apiView.findViewById(R.id.get_validation_rules_switch);
        getProviderDetailsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_provider_details_switch);
        registerSwitch = (SwitchCompat) apiView.findViewById(R.id.register_switch);
        changePasswordSwitch = (SwitchCompat) apiView.findViewById(R.id.change_password_switch);
        changeSecurityQuestionSwitch = (SwitchCompat) apiView.findViewById(R.id.change_security_question_switch);
        getSavedDoctorsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_saved_doctors_switch);
        saveDoctorSwitch = (SwitchCompat) apiView.findViewById(R.id.save_doctor_switch);
        deleteSavedDoctorSwitch = (SwitchCompat) apiView.findViewById(R.id.delete_saved_doctor_switch);
        forgotPasswordSwitch = (SwitchCompat) apiView.findViewById(R.id.forgot_password_switch);
        getProvidersSwitch = (SwitchCompat) apiView.findViewById(R.id.get_providers_switch);
        getLocationSuggestionsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_location_suggestions_switch);
        getSearchSuggestionsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_search_suggestions_switch);
        getLocationSwitch = (SwitchCompat) apiView.findViewById(R.id.get_location_switch);
        findEmailSwitch = (SwitchCompat) apiView.findViewById(R.id.find_email_switch);
        versionCheckSwitch = (SwitchCompat) apiView.findViewById(R.id.version_check_switch);

        setupSwitches();

        signInSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_SIGN_IN_FORCE_ERROR, !isChecked);
            }
        });

        signInRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_SIGN_IN_REFRESH_FORCE_ERROR, !isChecked);
            }
        });

        signOutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_SIGN_OUT_FORCE_ERROR, !isChecked);
            }
        });

        profileGetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_PROFILE_GET_FORCE_ERROR, !isChecked);
            }
        });

        profileUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_PROFILE_UPDATE_FORCE_ERROR, !isChecked);
            }
        });

        getMyAppointmentsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_MY_APPOINTMENTS_FORCE_ERROR, !isChecked);
            }
        });

        createAppointmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_CREATE_APPOINTMENT_FORCE_ERROR, !isChecked);
            }
        });

        getValidationRulesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_VALIDATION_RULES_FORCE_ERROR, !isChecked);
            }
        });

        getProviderDetailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_PROVIDER_DETAILS_FORCE_ERROR, !isChecked);
            }
        });

        registerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_REGISTER_FORCE_ERROR, !isChecked);
            }
        });

        changePasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_CHANGE_PASSWORD_FORCE_ERROR, !isChecked);
            }
        });

        changeSecurityQuestionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_CHANGE_SECURITY_QUESTION_FORCE_ERROR, !isChecked);
            }
        });

        getSavedDoctorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_SAVED_DOCTORS_FORCE_ERROR, !isChecked);
            }
        });

        saveDoctorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_SAVE_DOCTOR_FORCE_ERROR, !isChecked);
            }
        });

        deleteSavedDoctorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_DELETE_SAVED_DOCTOR_FORCE_ERROR, !isChecked);
            }
        });

        forgotPasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_FORGOT_PASSWORD_FORCE_ERROR, !isChecked);
            }
        });

        getProvidersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_PROVIDERS_FORCE_ERROR, !isChecked);
            }
        });

        getLocationSuggestionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR, !isChecked);
            }
        });

        getSearchSuggestionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR, !isChecked);
            }
        });

        getLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_GET_LOCATION_FORCE_ERROR, !isChecked);
            }
        });

        findEmailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_FIND_EMAIL_FORCE_ERROR, !isChecked);
            }
        });

        versionCheckSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_VERSION_CHECK_FORCE_ERROR, !isChecked);
            }
        });

        Button save = (Button) apiView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionUtil.logout(getActivity(), null);

                if (getActivity() != null) {
                    CommonUtil.exitApp(getContext(), getActivity());
                }
            }
        });

        return apiView;
    }

    public void setupSwitches() {
        signInSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_FORCE_ERROR));
        signInRefreshSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_REFRESH_FORCE_ERROR));
        signOutSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_OUT_FORCE_ERROR));
        profileGetSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_GET_FORCE_ERROR));
        profileUpdateSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_UPDATE_FORCE_ERROR));
        getMyAppointmentsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_MY_APPOINTMENTS_FORCE_ERROR));
        createAppointmentSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_CREATE_APPOINTMENT_FORCE_ERROR));
        getValidationRulesSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_VALIDATION_RULES_FORCE_ERROR));
        getProviderDetailsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_PROVIDER_DETAILS_FORCE_ERROR));
        registerSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_REGISTER_FORCE_ERROR));
        changePasswordSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_CHANGE_PASSWORD_FORCE_ERROR));
        changeSecurityQuestionSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_CHANGE_SECURITY_QUESTION_FORCE_ERROR));
        getSavedDoctorsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_SAVED_DOCTORS_FORCE_ERROR));
        saveDoctorSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SAVE_DOCTOR_FORCE_ERROR));
        deleteSavedDoctorSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_DELETE_SAVED_DOCTOR_FORCE_ERROR));
        forgotPasswordSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_FORGOT_PASSWORD_FORCE_ERROR));
        getProvidersSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_PROVIDERS_FORCE_ERROR));
        getLocationSuggestionsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR));
        getSearchSuggestionsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR));
        getLocationSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_LOCATION_FORCE_ERROR));
        findEmailSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_FIND_EMAIL_FORCE_ERROR));
        versionCheckSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_VERSION_CHECK_FORCE_ERROR));
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.API;
    }
}
