package com.prokarma.myhome.features.dev;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 12/13/17.
 */

public class EnvironmentSelectorFragment extends DialogFragment {
    public static final String ENVIRONMENT_SELECTOR_TAG = "environment_selector_tag";

    View envSelectorView;
    RadioGroup envMyHomeGroup;
    RadioGroup envAmWellGroup;
    AppCompatCheckBox checkBox;
    TextInputLayout usernameLayout;
    TextInputEditText user;
    TextInputLayout passwordLayout;
    TextInputEditText password;

    EnvironmentSelectorInterface environmentSelectorInterface;

    public static EnvironmentSelectorFragment newInstance() {
        return new EnvironmentSelectorFragment();
    }

    public void setEnvironmentSelectorInterface(EnvironmentSelectorInterface environmentSelectorInterface) {
        this.environmentSelectorInterface = environmentSelectorInterface;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogTheame);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        envSelectorView = inflater.inflate(R.layout.environment_selector, container, false);
        getActivity().setTitle(getString(R.string.developer_settings));

        Toolbar toolbar = (Toolbar) envSelectorView.findViewById(R.id.toolbar);
        toolbar.setTitle("Please Select Environment");
        toolbar.inflateMenu(R.menu.environment_selector_dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.finish_dialog:
                        finishSelecting();
                        break;
                }
                return true;
            }
        });

        envMyHomeGroup = (RadioGroup) envSelectorView.findViewById(R.id.env_myhome_radio_group);
        envAmWellGroup = (RadioGroup) envSelectorView.findViewById(R.id.env_amwell_radio_group);
        checkBox = (AppCompatCheckBox) envSelectorView.findViewById(R.id.checkbox_mutual_auth);
        usernameLayout = (TextInputLayout) envSelectorView.findViewById(R.id.user_layout);
        user = (TextInputEditText) envSelectorView.findViewById(R.id.user);
        passwordLayout = (TextInputLayout) envSelectorView.findViewById(R.id.password_layout);
        password = (TextInputEditText) envSelectorView.findViewById(R.id.password);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    usernameLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    passwordLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                }
            }
        });

        return envSelectorView;
    }

    private void finishSelecting() {
        if (envMyHomeGroup.getCheckedRadioButtonId() == -1 || envAmWellGroup.getCheckedRadioButtonId() == -1) {
            CommonUtil.showToast(getContext(), getString(R.string.you_must_chose_an_environment));
            return;
        }

        if (!checkBox.isChecked()) {
            if ((user.getText() == null || user.getText().toString().isEmpty()) || (password.getText() == null || password.getText().toString().isEmpty())) {
                CommonUtil.showToast(getContext(),getString(R.string.please_provide_user_for_AmWell));
                return;
            }
        }

        if (environmentSelectorInterface != null) {
            environmentSelectorInterface.attemptMutualAuth(checkBox.isChecked());

            if (user.getText() != null && password.getText() != null) {
                environmentSelectorInterface.hardcodedUser(user.getText().toString(), password.getText().toString());
            } else {

            }

            switch (envAmWellGroup.getCheckedRadioButtonId()) {
                case R.id.radio_amwell_dev:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.DEV);
                    break;
                case R.id.radio_amwell_stage:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.STAGE);
                    break;
                case R.id.radio_amwell_iot:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.IOT);
                    break;
                case R.id.radio_amwell_prod:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.PROD);
                    break;
            }

            switch (envMyHomeGroup.getCheckedRadioButtonId()) {
                case R.id.radio_demo:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.DEMO);
                    break;
                case R.id.radio_dev:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.DEV);
                    break;
                case R.id.radio_test:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.TEST);
                    break;
                case R.id.radio_slot1:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.SLOT1);
                    break;
                case R.id.radio_stage:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.STAGE);
                    break;
                case R.id.radio_prod:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.PROD);
                    break;
                default:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.PROD);
                    break;
            }

            dismiss();
        }
    }
}
