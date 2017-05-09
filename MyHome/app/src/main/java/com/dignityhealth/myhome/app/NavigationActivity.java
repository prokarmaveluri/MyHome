package com.dignityhealth.myhome.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.appointments.AppointmentsFragment;
import com.dignityhealth.myhome.features.contact.ContactUsFragment;
import com.dignityhealth.myhome.features.fad.FadFragment;
import com.dignityhealth.myhome.features.home.HomeFragment;
import com.dignityhealth.myhome.features.profile.ProfileEditFragment;
import com.dignityhealth.myhome.features.profile.ProfileViewFragment;
import com.dignityhealth.myhome.features.settings.SettingsFragment;
import com.dignityhealth.myhome.utils.Constants.ActivityTag;

/**
 * Created by kwelsh on 4/25/17.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationInterface {
    private static ActivityTag activityTag = ActivityTag.NONE;
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
                        clearBackstack();

                        switch (item.getItemId()) {
                            case R.id.home:
                                loadFragment(ActivityTag.HOME);
                                break;

                            case R.id.fad:
                                loadFragment(ActivityTag.FAD);
                                break;

                            case R.id.appointments:
                                loadFragment(ActivityTag.APPOINTMENTS);
                                break;

                            case R.id.profile:
                                loadFragment(ActivityTag.PROFILE_VIEW);
                                break;
                        }
                        return true;
                    }
                });
    }

    /**
     * Sets the activity tag. The tag is used to determine what page the user is on
     *
     * @param tag
     */
    public static void setActivityTag(ActivityTag tag) {
        activityTag = tag;
    }

    /**
     * Gets the activity tag. The tag is used to determine what page the user is on
     */
    public static ActivityTag getActivityTag() {
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
        clearBackstack();

        switch (activityTag) {
            case HOME:
                bottomNavigationView.setSelectedItemId(R.id.home);
                break;
            case FAD:
                bottomNavigationView.setSelectedItemId(R.id.fad);
                break;
            case PROFILE_VIEW:
                bottomNavigationView.setSelectedItemId(R.id.profile);
                break;
            case APPOINTMENTS:
                bottomNavigationView.setSelectedItemId(R.id.appointments);
                break;
        }
    }

    /**
     * Method that handles loading the fragments for the proper page.
     * This method differs from goToPage in that it only loads the fragment and pays no attention to the bottom navigation bar.
     *
     * @param activityTag The page we want to navigate to
     */
    public void loadFragment(ActivityTag activityTag) {
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

            case APPOINTMENTS:
                if (getActivityTag() != ActivityTag.APPOINTMENTS) {
                    AppointmentsFragment appointmentsFragment = AppointmentsFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, appointmentsFragment, AppointmentsFragment.APPOINTMENTS_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.APPOINTMENTS);
                }
                break;

            case PROFILE_VIEW:
                if (getActivityTag() != ActivityTag.PROFILE_VIEW) {
                    ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, profileViewFragment, ProfileViewFragment.PROFILE_VIEW_TAG)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.PROFILE_VIEW);
                }
                break;

            case PROFILE_EDIT:
                if (getActivityTag() != ActivityTag.PROFILE_EDIT) {
                    ProfileEditFragment profileEditFragment = ProfileEditFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, profileEditFragment, ProfileEditFragment.PROFILE_EDIT_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.PROFILE_EDIT);
                }
                break;

            case SETTINGS:
                if (getActivityTag() != ActivityTag.SETTINGS) {
                    SettingsFragment settingsFragment = SettingsFragment.newInstance();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                            .addToBackStack(null)
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
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getFragmentManager().executePendingTransactions();

                    setActivityTag(ActivityTag.CONTACT_US);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);

        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.settings:
                setActivityTag(ActivityTag.SETTINGS);
                Intent intentSettings = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentSettings, options.toBundle());
                return true;
            case R.id.preferences:
                return true;
            case R.id.contact_us:
                setActivityTag(ActivityTag.CONTACT_US);
                Intent intentContactUs = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentContactUs, options.toBundle());
                return true;
            case R.id.terms_of_service:
                setActivityTag(ActivityTag.TERMS_OF_SERVICE);
                Intent intentTos = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentTos, options.toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When back is pressed,
     * either pop off the fragment from the backstack (for stacked fragments in the "More" section),
     * go to the Home tab if you're not on it,
     * or simply perform the normal back action.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else if (activityTag != ActivityTag.HOME) {
            goToPage(ActivityTag.HOME);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Clears the fragment stack
     * Code inspired from: http://stackoverflow.com/a/17107067/2128921
     */
    private void clearBackstack() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}