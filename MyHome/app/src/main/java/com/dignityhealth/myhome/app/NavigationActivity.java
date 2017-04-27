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

public class NavigationActivity extends AppCompatActivity implements NavigationInterface {
    private ActivityTag activityTag = ActivityTag.NONE;
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
                                loadFragment(ActivityTag.HOME);
                                break;

                            case R.id.fad:
                                loadFragment(ActivityTag.FAD);
                                break;

                            case R.id.profile:
                                loadFragment(ActivityTag.PROFILE);
                                break;

                            case R.id.settings:
                                loadFragment(ActivityTag.SETTINGS);
                                break;

                            case R.id.contact_us:
                                loadFragment(ActivityTag.CONTACT_US);
                                break;

                        }
                        return true;
                    }
                });
    }

    public void setActivityTag(ActivityTag activityTag) {
        this.activityTag = activityTag;
    }

    public ActivityTag getActivityTag() {
        return activityTag;
    }

    /**
     * Loads the first fragment when Navigation Activity is created
     * Currently the HomeFragment is the initial fragment shown
     */
    private void initializeBottomView() {
        loadFragment(ActivityTag.HOME);
    }

    /**
     * Navigates the bottom nav bar to the specified page.
     * This differs from loadFragment in that it also correctly handles the bottom navigation bar
     *
     * @param activityTag The page we want to navigate to
     */
    public void goToPage(ActivityTag activityTag) {
        switch (activityTag) {
            case HOME:
                bottomNavigationView.setSelectedItemId(R.id.home);
                break;
            case FAD:
                bottomNavigationView.setSelectedItemId(R.id.fad);
                break;
            case PROFILE:
                bottomNavigationView.setSelectedItemId(R.id.profile);
                break;
            case SETTINGS:
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
            case CONTACT_US:
                bottomNavigationView.setSelectedItemId(R.id.contact_us);
                break;
        }
    }

    /**
     * Method that handles loading the fragments for the proper page.
     * This method differs from goToPage in that it only loads the fragment and pays no attention to the bottom navigation bar.
     *
     * @param activityTag The page we want to navigate to
     */
    private void loadFragment(ActivityTag activityTag) {
        switch (activityTag) {
            case HOME:
                if (getActivityTag() != ActivityTag.HOME) {
                    HomeFragment homeFragment = HomeFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, homeFragment, HomeFragment.HOME_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.HOME);
                }
                break;

            case FAD:
                if (getActivityTag() != ActivityTag.FAD) {
                    FadFragment fadFragment = FadFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, fadFragment, FadFragment.FAD_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.FAD);
                }
                break;

            case PROFILE:
                if (getActivityTag() != ActivityTag.PROFILE) {
                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, profileFragment, ProfileFragment.PROFILE_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.PROFILE);
                }
                break;

            case SETTINGS:
                if (getActivityTag() != ActivityTag.SETTINGS) {
                    SettingsFragment settingsFragment = SettingsFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.SETTINGS);
                }
                break;

            case CONTACT_US:
                if (getActivityTag() != ActivityTag.CONTACT_US) {
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
    }
}