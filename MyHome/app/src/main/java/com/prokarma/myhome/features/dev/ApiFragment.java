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
    SwitchCompat profileGetSwitch;
    SwitchCompat profileUpdateSwitch;
    SwitchCompat getMyAppointmentsSwitch;
    SwitchCompat createAppointmentSwitch;
    SwitchCompat getValidationRulesSwitch;
    SwitchCompat getProviderDetailsSwitch;
    SwitchCompat signInSwitch;
    SwitchCompat signInRefreshSwitch;
    SwitchCompat signOutSwitch;
    SwitchCompat registerSwitch;

    public static ApiFragment newInstance() {
        return new ApiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        apiView = inflater.inflate(R.layout.api, container, false);
        getActivity().setTitle(getString(R.string.api_settings));

        profileGetSwitch = (SwitchCompat) apiView.findViewById(R.id.profile_get_switch);
        profileUpdateSwitch = (SwitchCompat) apiView.findViewById(R.id.profile_update_switch);
        getMyAppointmentsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_my_appointments_switch);
        createAppointmentSwitch = (SwitchCompat) apiView.findViewById(R.id.create_appointments_switch);
        getValidationRulesSwitch = (SwitchCompat) apiView.findViewById(R.id.get_validation_rules_switch);
        getProviderDetailsSwitch = (SwitchCompat) apiView.findViewById(R.id.get_provider_details_switch);
        signInSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_in_switch);
        signInRefreshSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_in_refresh_switch);
        signOutSwitch = (SwitchCompat) apiView.findViewById(R.id.sign_out_switch);
        registerSwitch = (SwitchCompat) apiView.findViewById(R.id.register_switch);

        setupSwitches();

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

        registerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreferences.getInstance().setBooleanPreference(Constants.API_REGISTER_FORCE_ERROR, !isChecked);
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
        profileGetSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_GET_FORCE_ERROR));
        profileUpdateSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_UPDATE_FORCE_ERROR));
        getMyAppointmentsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_MY_APPOINTMENTS_FORCE_ERROR));
        createAppointmentSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_CREATE_APPOINTMENT_FORCE_ERROR));
        getValidationRulesSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_VALIDATION_RULES_FORCE_ERROR));
        getProviderDetailsSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_PROVIDER_DETAILS_FORCE_ERROR));
        signInSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_FORCE_ERROR));
        signInRefreshSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_REFRESH_FORCE_ERROR));
        signOutSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_OUT_FORCE_ERROR));
        registerSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(Constants.API_REGISTER_FORCE_ERROR));
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.API;
    }
}
