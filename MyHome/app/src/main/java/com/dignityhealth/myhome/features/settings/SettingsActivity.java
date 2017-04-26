package com.dignityhealth.myhome.features.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseActivity;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 4/26/17.
 */

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.SETTINGS;
    }
}
