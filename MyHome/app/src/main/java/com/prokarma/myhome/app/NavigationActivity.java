package com.prokarma.myhome.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.appointments.AppointmentsDetailsFragment;
import com.prokarma.myhome.features.appointments.AppointmentsFragment;
import com.prokarma.myhome.features.contact.ContactUsFragment;
import com.prokarma.myhome.features.dev.DeveloperFragment;
import com.prokarma.myhome.features.fad.FadFragment;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.home.HomeFragment;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.profile.ProfileEditFragment;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.profile.ProfileViewFragment;
import com.prokarma.myhome.features.settings.SettingsFragment;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants.ActivityTag;
import com.prokarma.myhome.utils.SessionUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by kwelsh on 4/25/17.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationInterface, FragmentManager.OnBackStackChangedListener {

    private static ActivityTag activityTag = ActivityTag.NONE;
    private BottomNavigationViewEx bottomNavigationView;
    private ProgressBar progressBar;

    public static Bus eventBus;
    public Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(toolbar);

        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();

        MapsInitializer.initialize(getApplicationContext());
        LinearLayout bottomNavigationLayout = (LinearLayout) findViewById(R.id.bottom_navigation_layout);
        bottomNavigationView = (BottomNavigationViewEx) bottomNavigationLayout.findViewById(R.id.bottom_navigation);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(true);
        bottomNavigationView.setTextSize(13f);

        progressBar = (ProgressBar) findViewById(R.id.dash_progress);
        setActivityTag(ActivityTag.NONE);
        initializeBottomView();

        eventBus = new Bus(ThreadEnforcer.MAIN);
        RecentlyViewedDataSourceDB.getInstance().open(getApplicationContext());

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        clearBackstack();

                        switch (item.getItemId()) {
                            case R.id.home:
                                loadFragment(ActivityTag.HOME, null);
                                break;

                            case R.id.fad:
                                loadFragment(ActivityTag.FAD, null);
                                break;

                            case R.id.appointments:
                                loadFragment(ActivityTag.APPOINTMENTS, null);
                                break;

                            case R.id.profile:
                                loadFragment(ActivityTag.PROFILE_VIEW, null);
                                break;
                        }

                        return true;
                    }
                });

        //Pre-load Google Play Services
//        loadGooglePlayServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus = null;
        mHandler.removeCallbacks(runnable);
        FadManager.getInstance().setLocation(null);
        ProfileManager.setProfile(null);
        RecentlyViewedDataSourceDB.getInstance().close();
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
        loadFragment(ActivityTag.HOME, null);
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
    public void loadFragment(ActivityTag activityTag, @Nullable Bundle bundle) {
        switch (activityTag) {
            case HOME:
                if (getActivityTag() != ActivityTag.HOME) {
                    getSupportFragmentManager().executePendingTransactions();
                    HomeFragment homeFragment = HomeFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, homeFragment, HomeFragment.HOME_TAG)
                            .commitAllowingStateLoss();

                    setActivityTag(ActivityTag.HOME);
                }
                break;

            case FAD:
                if (getActivityTag() != ActivityTag.FAD) {
                    getSupportFragmentManager().executePendingTransactions();
                    FadFragment fadFragment = FadFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, fadFragment, FadFragment.FAD_TAG)
                            .commitAllowingStateLoss();

                    setActivityTag(ActivityTag.FAD);
                }
                break;

            case PROVIDER_DETAILS:
                if (getActivityTag() != ActivityTag.PROVIDER_DETAILS) {
                    getSupportFragmentManager().executePendingTransactions();
                    ProviderDetailsFragment fragment = ProviderDetailsFragment.newInstance();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, fragment, ProviderDetailsFragment.PROVIDER_DETAILS_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.PROVIDER_DETAILS);
                }
                break;

            case PROVIDERS_FILTER:
                //This isn't being used currently; we use a dialog fragment instead
                if (getActivityTag() != ActivityTag.PROVIDERS_FILTER) {
                    getSupportFragmentManager().executePendingTransactions();
                    FadFragment fadFragment = FadFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, fadFragment, FadFragment.FAD_TAG)
                            .commit();

                    setActivityTag(ActivityTag.PROVIDERS_FILTER);
                }
                break;

            case APPOINTMENTS:
                if (getActivityTag() != ActivityTag.APPOINTMENTS) {
                    getSupportFragmentManager().executePendingTransactions();
                    AppointmentsFragment appointmentsFragment = AppointmentsFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, appointmentsFragment, AppointmentsFragment.APPOINTMENTS_TAG)
                            .commit();

                    setActivityTag(ActivityTag.APPOINTMENTS);
                }
                break;

            case APPOINTMENTS_DETAILS:
                if (getActivityTag() != ActivityTag.APPOINTMENTS_DETAILS) {
                    getSupportFragmentManager().executePendingTransactions();
                    AppointmentsDetailsFragment appointmentsDetailsFragment = AppointmentsDetailsFragment.newInstance();
                    appointmentsDetailsFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, appointmentsDetailsFragment, AppointmentsDetailsFragment.APPOINTMENTS_DETAILS_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.APPOINTMENTS_DETAILS);
                }
                break;

            case PROFILE_VIEW:
                if (getActivityTag() != ActivityTag.PROFILE_VIEW) {
                    getSupportFragmentManager().executePendingTransactions();
                    ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, profileViewFragment, ProfileViewFragment.PROFILE_VIEW_TAG)
                            .commit();

                    setActivityTag(ActivityTag.PROFILE_VIEW);
                }
                break;

            case PROFILE_EDIT:
                if (getActivityTag() != ActivityTag.PROFILE_EDIT) {
                    getSupportFragmentManager().executePendingTransactions();
                    ProfileEditFragment profileEditFragment = ProfileEditFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, profileEditFragment, ProfileEditFragment.PROFILE_EDIT_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.PROFILE_EDIT);
                }
                break;

            case SETTINGS:
                if (getActivityTag() != ActivityTag.SETTINGS) {
                    getSupportFragmentManager().executePendingTransactions();
                    SettingsFragment settingsFragment = SettingsFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, settingsFragment, SettingsFragment.SETTINGS_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.SETTINGS);
                }
                break;

            case DEVELOPER:
                if (getActivityTag() != ActivityTag.DEVELOPER) {
                    getSupportFragmentManager().executePendingTransactions();
                    DeveloperFragment developerFragment = DeveloperFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, developerFragment, DeveloperFragment.DEVELOPER_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.DEVELOPER);
                }
                break;

            case CONTACT_US:
                if (getActivityTag() != ActivityTag.CONTACT_US) {
                    getSupportFragmentManager().executePendingTransactions();
                    ContactUsFragment contactUsFragment = ContactUsFragment.newInstance();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, contactUsFragment, ContactUsFragment.CONTACT_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.CONTACT_US);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        //Allow developer settings
        if (BuildConfig.SHOW_SETTINGS) {
            menu.findItem(R.id.developer).setVisible(true);
        } else {
            menu.findItem(R.id.developer).setVisible(false);
        }

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
            case R.id.developer:
                setActivityTag(ActivityTag.DEVELOPER);
                Intent intentDeveloper = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentDeveloper, options.toBundle());
                return true;
            case R.id.sign_out:
//                SessionUtil.logout(this, progressBar);
                SessionUtil.signOutAlert(this, progressBar);
                return true;
            case android.R.id.home:
                onBackPressed();
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
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (activityTag == ActivityTag.PROVIDER_DETAILS) {
                ProviderDetailsFragment frag = ((ProviderDetailsFragment) fm.findFragmentByTag(ProviderDetailsFragment.PROVIDER_DETAILS_TAG));

                if (frag == null || !frag.onBackButtonPressed()) {
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }
            } else if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else if (activityTag != ActivityTag.HOME) {
                goToPage(ActivityTag.HOME);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    /**
     * Clears the fragment stack
     * Code inspired from: http://stackoverflow.com/a/17107067/2128921
     */
    private void clearBackstack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Hacky way to avoid terrible load times due to Google Play Services loading for the first time
     * on views with maps (Provider Details).
     * See: http://stackoverflow.com/questions/26265526/what-makes-my-map-fragment-loading-slow
     */
    public void loadGooglePlayServices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                } catch (Exception ignored) {
                    //This almost always causes an exception, but we don't care because Google Play Services is already loaded
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // session expiry
        if (AuthManager.getInstance().isExpiried()) {
            buildSessionAlert(getString(R.string.session_expiry_message));
        }
        mHandler.removeCallbacks(runnable);
        AppPreferences.getInstance().setLongPreference("IDLE_TIME", System.currentTimeMillis());
        mHandler.postDelayed(runnable, AuthManager.SESSION_EXPIRY_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
        AppPreferences.getInstance().setLongPreference("IDLE_TIME", System.currentTimeMillis());
        AuthManager.getInstance().setIdleTime(System.currentTimeMillis());
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    private void buildSessionAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.session_expiry)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = LoginActivity.getLoginIntent(NavigationActivity.this);
                        startActivity(intent);
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        if (AuthManager.getInstance().isExpiried()) {
            buildSessionAlert(getString(R.string.session_expiry_message));
        }
        mHandler.removeCallbacks(runnable);
        AppPreferences.getInstance().setLongPreference("IDLE_TIME", System.currentTimeMillis());
        mHandler.postDelayed(runnable, AuthManager.SESSION_EXPIRY_TIME);
    }

    private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (AuthManager.getInstance().isExpiried()) {
                buildSessionAlert(getString(R.string.session_expiry_message));
            }
        }
    };
}