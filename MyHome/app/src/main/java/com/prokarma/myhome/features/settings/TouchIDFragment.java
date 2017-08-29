package com.prokarma.myhome.features.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.databinding.FragmentTouchIdBinding;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 4/26/17.
 */

public class TouchIDFragment extends BaseFragment {
    public static final String TOUCH_ID_TAG = "touch_id_tag";
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
        if (AppPreferences.getInstance().getBooleanPreference("IS_TOUCH_ID_ENABLED"))
            binding.touchIDSwitch.setChecked(true);

        binding.touchIDSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.touchIDSwitch.isChecked()) {
                    AppPreferences.getInstance().setBooleanPreference("IS_TOUCH_ID_ENABLED", true);
                } else {
                    AppPreferences.getInstance().setBooleanPreference("IS_TOUCH_ID_ENABLED", false);
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
        TealiumUtil.trackView(Constants.TOUCH_ID_SETTINGS, null);
    }
}
