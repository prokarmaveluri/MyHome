package com.prokarma.myhome.features.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.databinding.FragmentTouchIdBinding;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by cmajji on 8/26/17.
 */

public class TouchIDFragment extends BaseFragment {
    public static final String TOUCH_ID_TAG = "touch_id_tag";
    public static final String TOUCH_ID_KEY = "IS_TOUCH_ID_ENABLED";
    private FragmentTouchIdBinding binding;

    public static TouchIDFragment newInstance() {
        return new TouchIDFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_touch_id, container, false);

        getActivity().setTitle(getString(R.string.touch_id_title));
        binding.touchIDSwitch.setChecked(false);
        if (AppPreferences.getInstance().getBooleanPreference(TOUCH_ID_KEY))
            binding.touchIDSwitch.setChecked(true);

        binding.touchIDSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.touchIDSwitch.isChecked()) {
                    AppPreferences.getInstance().setBooleanPreference(TOUCH_ID_KEY, true);
                    TealiumUtil.trackEvent(Constants.TOUCH_ID_ENABLED_EVENT, null);
                } else {
                    AppPreferences.getInstance().setBooleanPreference(TOUCH_ID_KEY, false);
                    TealiumUtil.trackEvent(Constants.TOUCH_ID_DISABLED_EVENT, null);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.TOUCH_ID;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.TOUCH_ID_SETTINGS_SCREEN, null);
    }
}
