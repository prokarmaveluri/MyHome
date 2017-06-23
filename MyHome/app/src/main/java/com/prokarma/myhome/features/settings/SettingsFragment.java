package com.prokarma.myhome.features.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 4/26/17.
 */

public class SettingsFragment extends BaseFragment {
    public static final String SETTINGS_TAG = "settings_tag";
    View settingsView;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.settings, container, false);
        return settingsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.SETTINGS_SCREEN, null);
    }
}
