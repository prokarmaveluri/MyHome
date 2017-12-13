package com.prokarma.myhome.features.dev;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 12/13/17.
 */

public class EnvironmentSelectorFragment extends DialogFragment {
    public static final String ENVIRONMENT_SELECTOR_TAG = "environment_selector_tag";

    View envSelectorView;
    EnvironmentSelectorInterface environmentSelectorInterface;

    public static EnvironmentSelectorFragment newInstance() {
        return new EnvironmentSelectorFragment();
    }

    public void setEnvironmentSelectorInterface(EnvironmentSelectorInterface environmentSelectorInterface) {
        this.environmentSelectorInterface = environmentSelectorInterface;
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

        final RadioGroup envMyHomeGroup = (RadioGroup) envSelectorView.findViewById(R.id.env_myhome_radio_group);
        final RadioGroup envAmWellGroup = (RadioGroup) envSelectorView.findViewById(R.id.env_amwell_radio_group);

        Button acceptButton = (Button) envSelectorView.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (envMyHomeGroup.getCheckedRadioButtonId() == -1 || envAmWellGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "You must chose an environment", Toast.LENGTH_LONG).show();
                    return;
                }

                if (environmentSelectorInterface != null) {
                    switch (envAmWellGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_amwell_dev:
                            environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.DEV);
                            break;
                        case R.id.radio_amwell_iot:
                            environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.IOT);
                            break;
                        case R.id.radio_amwell_prod:
                            environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.PROD);
                            break;
                    }

                    switch (envMyHomeGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_dev:
                            environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.DEV);
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
        });

        return envSelectorView;
    }
}
