package com.dignityhealth.myhome.app;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.contact.ContactUsFragment;
import com.dignityhealth.myhome.features.dev.DeveloperFragment;
import com.dignityhealth.myhome.features.settings.SettingsFragment;
import com.dignityhealth.myhome.features.tos.TosFragment;
import com.dignityhealth.myhome.utils.Constants;

import timber.log.Timber;

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

            case PREFERENCES:
                SettingsFragment preferencesFragment = SettingsFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, preferencesFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.PREFERENCES);
                break;

            case HELP:
                SettingsFragment helpFragment = SettingsFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, helpFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.HELP);
                break;

            case TERMS_OF_SERVICE:
                TosFragment tosFragment = TosFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, tosFragment, TosFragment.TOS_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.TERMS_OF_SERVICE);
                break;

            case DEVELOPER:
                DeveloperFragment developerFragment = DeveloperFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, developerFragment, DeveloperFragment.DEVELOPER_TAG)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.DEVELOPER);
                break;

            default:
                Timber.w("Options Activity found an activity tag that isn't being handled!");
                Toast.makeText(this, "Unknown Activity Tag", Toast.LENGTH_SHORT).show();
                break;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(toolbar);
    }
}
