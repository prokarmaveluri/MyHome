package com.prokarma.myhome.app;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.care.MyCareFragment;
import com.prokarma.myhome.features.contact.ContactUsFragment;
import com.prokarma.myhome.features.dev.ApiFragment;
import com.prokarma.myhome.features.dev.DeveloperFragment;
import com.prokarma.myhome.features.faq.FaqFragment;
import com.prokarma.myhome.features.home.HomeDidYouKnowFragment;
import com.prokarma.myhome.features.profile.ProfileEditFragment;
import com.prokarma.myhome.features.profile.ProfileViewFragment;
import com.prokarma.myhome.features.settings.ChangePasswordFragment;
import com.prokarma.myhome.features.settings.ChangeSecQuestionFragment;
import com.prokarma.myhome.features.settings.SettingsFragment;
import com.prokarma.myhome.features.settings.TouchIDFragment;
import com.prokarma.myhome.features.tos.TosFragment;
import com.prokarma.myhome.utils.Constants;

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

            case MY_CARE:
                MyCareFragment careFragment = MyCareFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, careFragment, MyCareFragment.MY_CARE_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_CARE);
                break;

            case FAQ:
                FaqFragment faqFragment = FaqFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, faqFragment, FaqFragment.FAQ_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.FAQ);
                break;

            case CONTACT_US:
                ContactUsFragment contactUsFragment = ContactUsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, contactUsFragment, ContactUsFragment.CONTACT_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.CONTACT_US);
                break;

            case PROFILE_VIEW:
                ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, profileViewFragment, ProfileViewFragment.PROFILE_VIEW_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.PROFILE_VIEW);
                break;

            case SETTINGS:
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.SETTINGS);
                break;

            case PREFERENCES:
                SettingsFragment preferencesFragment = SettingsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, preferencesFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.PREFERENCES);
                break;

            case HELP:
                SettingsFragment helpFragment = SettingsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, helpFragment, SettingsFragment.SETTINGS_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.HELP);
                break;

            case TERMS_OF_SERVICE:
                TosFragment tosFragment = TosFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, tosFragment, TosFragment.TOS_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.TERMS_OF_SERVICE);
                break;

            case DEVELOPER:
                DeveloperFragment developerFragment = DeveloperFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, developerFragment, DeveloperFragment.DEVELOPER_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.DEVELOPER);
                break;

            case API:
                ApiFragment apiFragment = ApiFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, apiFragment, ApiFragment.API_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.API);
                break;

            case CHANGE_PASSWORD:

                ChangePasswordFragment changeFragment = ChangePasswordFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, changeFragment, ChangePasswordFragment.CHANGE_PASSWORD_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.CHANGE_PASSWORD);
                break;
            case ENTER_PASSWORD_SEC_QUESTION:
                ChangeSecQuestionFragment enterPasswordFragment = ChangeSecQuestionFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, enterPasswordFragment, ChangeSecQuestionFragment.CHANGE_SEC_PASSWORD_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.CHANGE_PASSWORD);
                break;
            case TOUCH_ID:
                TouchIDFragment touchIDFragment = TouchIDFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, touchIDFragment, TouchIDFragment.TOUCH_ID_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.TOUCH_ID);
                break;
            case HOME_DID_YOU_KNOW_SEC_1:
                HomeDidYouKnowFragment fragment = HomeDidYouKnowFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("SECTION_TYPE", "HOME_DID_YOU_KNOW_SEC_1");
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, fragment, HomeDidYouKnowFragment.DID_YOU_KNOW_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_1);
                break;
            case HOME_DID_YOU_KNOW_SEC_2:
                fragment = HomeDidYouKnowFragment.newInstance();
                bundle = new Bundle();
                bundle.putString("SECTION_TYPE", "HOME_DID_YOU_KNOW_SEC_2");
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, fragment, HomeDidYouKnowFragment.DID_YOU_KNOW_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_2);
                break;
            case PROFILE_EDIT:
                ProfileEditFragment profileEditFragment = ProfileEditFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, profileEditFragment, ProfileEditFragment.PROFILE_EDIT_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.PROFILE_EDIT);
                break;
            default:
                Timber.w("Options Activity found an activity tag that isn't being handled!");
                Toast.makeText(this, getString(R.string.unknown_activity_tag), Toast.LENGTH_SHORT).show();
                break;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //If you do the back button via the manifest, you won't get the proper animation when you click back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
