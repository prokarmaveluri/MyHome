package com.dignityhealth.myhome.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.contact.ContactUsFragment;
import com.dignityhealth.myhome.features.settings.SettingsFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/8/17.
 */

public class OptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        switch (NavigationActivity.getActivityTag()) {
            case CONTACT_US:
                ContactUsFragment contactUsFragment = ContactUsFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, contactUsFragment, ContactUsFragment.CONTACT_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.CONTACT_US);
                break;
            case SETTINGS:
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.SETTINGS);
                break;
            default:
                Toast.makeText(this, "Unknown Activity Tag", Toast.LENGTH_SHORT).show();
                break;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
