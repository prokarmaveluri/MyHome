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

import com.prokarma.myhome.R;

/**
 * Created by kwelsh on 12/13/17.
 */

public class EnvironmentSelectorFragment extends DialogFragment {
    public static final String ENVIRONMENT_SELECTOR_TAG = "environment_selector_tag";
    public static final String ENVIRONMENT_BROADCAST_RECEIVER_INTENT = "environment-selected-event";
    public static final String INTENT_EXTRA_ENVIRONMENT = "envType";

    EnvironmentSelectorPresenter presenter;

    public static EnvironmentSelectorFragment newInstance() {
        return new EnvironmentSelectorFragment();
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
        View envSelectorView = inflater.inflate(R.layout.environment_selector, container, false);
        getActivity().setTitle(getString(R.string.developer_settings));

        presenter = new EnvironmentSelectorPresenter(getContext(), this, envSelectorView);
        presenter.onCreate();

        return envSelectorView;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }
}
