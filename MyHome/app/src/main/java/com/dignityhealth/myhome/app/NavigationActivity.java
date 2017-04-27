package com.dignityhealth.myhome.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.contact.ContactUsFragment;
import com.dignityhealth.myhome.features.fad.FadFragment;
import com.dignityhealth.myhome.features.home.HomeFragment;
import com.dignityhealth.myhome.features.profile.ProfileFragment;
import com.dignityhealth.myhome.features.settings.SettingsFragment;
import com.dignityhealth.myhome.utils.Constants.ActivityTag;

/**
 * Created by kwelsh on 4/25/17.
 */

public class NavigationActivity extends AppCompatActivity {
    private ActivityTag activityTag;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        initializeBottomView();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                if (activityTag != ActivityTag.HOME) {
                                    HomeFragment homeFragment = HomeFragment.newInstance();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame, homeFragment, HomeFragment.HOME_TAG)
                                            .commitAllowingStateLoss();
                                    getFragmentManager().executePendingTransactions();

                                    setActivityTag(ActivityTag.HOME);
                                }
                                break;

                            case R.id.fad:
                                if (activityTag != ActivityTag.FAD) {
                                    FadFragment fadFragment = FadFragment.newInstance();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame, fadFragment, FadFragment.FAD_TAG)
                                            .commitAllowingStateLoss();
                                    getFragmentManager().executePendingTransactions();

                                    setActivityTag(ActivityTag.FAD);
                                }
                                break;

                            case R.id.profile:
                                if (activityTag != ActivityTag.PROFILE) {
                                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame, profileFragment, ProfileFragment.PROFILE_TAG)
                                            .commitAllowingStateLoss();
                                    getFragmentManager().executePendingTransactions();

                                    setActivityTag(ActivityTag.PROFILE);
                                }
                                break;

                            case R.id.settings:
                                if (activityTag != ActivityTag.SETTINGS) {
                                    SettingsFragment settingsFragment = SettingsFragment.newInstance();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                                            .commitAllowingStateLoss();
                                    getFragmentManager().executePendingTransactions();

                                    setActivityTag(ActivityTag.SETTINGS);
                                }
                                break;

                            case R.id.contact_us:
                                if (activityTag != ActivityTag.CONTACT_US) {
                                    ContactUsFragment contactUsFragment = ContactUsFragment.newInstance();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame, contactUsFragment, ContactUsFragment.CONTACT_TAG)
                                            .commitAllowingStateLoss();
                                    getFragmentManager().executePendingTransactions();

                                    setActivityTag(ActivityTag.CONTACT_US);
                                }
                                break;

                        }
                        return true;
                    }
                });
    }

    public void setActivityTag(ActivityTag activityTag) {
        this.activityTag = activityTag;

//        switch (activityTag) {
//            case HOME:
//                bottomNavigationView.getMenu().getItem(0).setChecked(true);
//                break;
//            case FAD:
//                bottomNavigationView.getMenu().getItem(1).setChecked(true);
//                break;
//            case PROFILE:
//                bottomNavigationView.getMenu().getItem(2).setChecked(true);
//                break;
//            case SETTINGS:
//                bottomNavigationView.getMenu().getItem(3).setChecked(true);
//                break;
//            case CONTACT_US:
//                bottomNavigationView.getMenu().getItem(4).setChecked(true);
//                break;
//            default:
//                Timber.e("Unknown Drawer Menu Item");
//                break;
//        }
    }

    public ActivityTag getActivityTag() {
        return activityTag;
    }

    /**
     * Loads the first fragment when Navigation Activity is created
     * Currently the HomeFragment is the initial fragment shown
     */
    private void initializeBottomView() {
        HomeFragment homeFragment = HomeFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, homeFragment, HomeFragment.HOME_TAG)
                .commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();

        setActivityTag(ActivityTag.HOME);
    }
}
