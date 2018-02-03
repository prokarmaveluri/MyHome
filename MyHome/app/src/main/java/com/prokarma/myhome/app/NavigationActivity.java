package com.prokarma.myhome.app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.prokarma.myhome.features.fad.dashboard.FadDashboardFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.home.HomeFragment;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.mycare.MyCareNowFragment;
import com.prokarma.myhome.features.profile.ProfileEditFragment;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.profile.ProfileViewFragment;
import com.prokarma.myhome.features.settings.SettingsFragment;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.Constants.ActivityTag;
import com.prokarma.myhome.utils.SessionUtil;
import com.prokarma.myhome.views.PdfRendererZoomFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.televisit.AwsManager;
import com.televisit.cost.MyCareCreditCardFragment;
import com.televisit.cost.MyCareVisitCostFragment;
import com.televisit.cost.MyCareVisitIntakeFragment;
import com.televisit.cost.PrivacyPolicyPdfFragment;
import com.televisit.feedback.FeedbackFragment;
import com.televisit.history.HistoryListAdapter;
import com.televisit.history.MedicalHistoryFragment;
import com.televisit.medications.MedicationsFragment;
import com.televisit.pharmacy.PharmaciesFragment;
import com.televisit.pharmacy.PharmacyDetailsFragment;
import com.televisit.previousvisit.PreviousVisitsFragment;
import com.televisit.profile.MyCareProfileFragment;
import com.televisit.profile.MyCareProfileViewDependentFragment;
import com.televisit.providers.MyCareProvidersFragment;
import com.televisit.services.MyCareServicesFragment;
import com.televisit.summary.VisitSummaryFragment;
import com.televisit.waitingroom.MyCareWaitingRoomFragment;

import java.util.ArrayList;
import java.util.TimeZone;

import timber.log.Timber;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;

/**
 * Created by kwelsh on 4/25/17.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationInterface,
        FragmentManager.OnBackStackChangedListener, NetworkManager.ISessionExpiry {

    private static ActivityTag activityTag = ActivityTag.NONE;
    private BottomNavigationViewEx bottomNavigationView;
    private ProgressBar progressBar;

    public static Bus eventBus;
    public Toolbar toolbar;
    public View toolbarLine;

    private BroadcastReceiver timezoneChangedReceiver;
    private static boolean didTimeZoneChange = false;
    private MenuItem currentSelectedMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //try {
        setContentView(R.layout.navigation_activity);

        NetworkManager.getInstance().setExpiryListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarLine = (View) findViewById(R.id.toolbar_line);

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

        progressBar = (ProgressBar) findViewById(R.id.dash_progress);
        setActivityTag(ActivityTag.NONE);

        MapsInitializer.initialize(getApplicationContext());
        LinearLayout bottomNavigationLayout = (LinearLayout) findViewById(R.id.bottom_navigation_layout);
        bottomNavigationView = (BottomNavigationViewEx) bottomNavigationLayout.findViewById(R.id.bottom_navigation);

        if (AuthManager.getInstance().hasMyCare()) {
            bottomNavigationView.inflateMenu(R.menu.navigation_menu);
            currentSelectedMenuItem = bottomNavigationView.getMenu().getItem(0);
            MenuItemCompat.setContentDescription(currentSelectedMenuItem, currentSelectedMenuItem.getTitle() + ", selected");
        } else {
            bottomNavigationView.inflateMenu(R.menu.navigation_menu_profile);
        }

        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.setTextVisibility(true);
        bottomNavigationView.setTextSize(13f);

        initializeBottomView();

        eventBus = new Bus(ThreadEnforcer.MAIN);
        RecentlyViewedDataSourceDB.getInstance().open(getApplicationContext());

        //Inspired by https://stackoverflow.com/a/26905894/2128921
        timezoneChangedReceiver = new MyBroadcastReceiver();
        registerReceiver(timezoneChangedReceiver, new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        clearBackstack();

                        if (null != currentSelectedMenuItem) {
                            MenuItemCompat.setContentDescription(currentSelectedMenuItem, currentSelectedMenuItem.getTitle());
                        }

                        currentSelectedMenuItem = item;
                        MenuItemCompat.setContentDescription(currentSelectedMenuItem, currentSelectedMenuItem.getTitle() + ", selected");

                        switch (item.getItemId()) {
                            case R.id.home:
                                loadFragment(ActivityTag.HOME, null);
                                break;

                            case R.id.fad:
                                loadFragment(ActivityTag.FAD_DASH_BOARD, null);
                                break;

                            case R.id.appointments:
                                loadFragment(ActivityTag.APPOINTMENTS, null);
                                break;

                            case R.id.profile:
                                if (AuthManager.getInstance().hasMyCare()) {
                                    loadFragment(ActivityTag.MY_CARE_NOW, null);
                                } else {
                                    loadFragment(ActivityTag.PROFILE_VIEW, null);
                                }
                                break;
                        }

                        return true;
                    }
                });

        /*} catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }*/

        if (getIntent() != null && getIntent().hasExtra("VISIT")) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
            loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, getIntent().getExtras());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus = null;
        mHandler.removeCallbacks(runnable);

        if (AwsManager.getInstance().getVisit() != null && AwsManager.getInstance().isVisitOngoing()) {
            // Once provider is entered in video visit, automatically NavActivity is getting killed on its own.
            // as we are passing visitFinishedIntent(), this activity will get restarted as soon as visit ends.
            // so for subsequent visits intake screen needs profile phone number...so do not set it as null here.
            Timber.d("wait. visit is Ongoing. ");
        }
        else {
            ProfileManager.setProfile(null);
        }

        NetworkManager.getInstance().setExpiryListener(null);
        RecentlyViewedDataSourceDB.getInstance().close();
        unregisterReceiver(timezoneChangedReceiver);
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
        try {
            loadFragment(ActivityTag.HOME, null);
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
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
            case MY_CARE:
                bottomNavigationView.setSelectedItemId(R.id.profile);
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    public void setMyCareVisibility() {
        BottomNavigationItemView view = bottomNavigationView.getBottomNavigationItemView(3);
        if (AuthManager.getInstance().hasMyCare()) {
            view.setTitle(getApplicationContext().getString(R.string.mycare_now));
        } else {
            view.setTitle(getApplicationContext().getString(R.string.profile));
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
                    homeFragment.setArguments(bundle);
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
                    fadFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, fadFragment, FadFragment.FAD_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();

                    setActivityTag(ActivityTag.FAD);
                }
                break;

            case FAD_DASH_BOARD:
                if (getActivityTag() != ActivityTag.FAD_DASH_BOARD) {
                    getSupportFragmentManager().executePendingTransactions();
                    FadDashboardFragment fadDashboardFragment = FadDashboardFragment.newInstance();
                    fadDashboardFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, fadDashboardFragment, FadDashboardFragment.FAD_DASHBOARD_TAG)
                            .commitAllowingStateLoss();

                    setActivityTag(ActivityTag.FAD_DASH_BOARD);
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
                    fadFragment.setArguments(bundle);
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
                    appointmentsFragment.setArguments(bundle);
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
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    bundle.putString("from", "dashboard");
                    profileViewFragment.setArguments(bundle);
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
                    profileEditFragment.setArguments(bundle);
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
                    settingsFragment.setArguments(bundle);
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
                    developerFragment.setArguments(bundle);
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
                    contactUsFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, contactUsFragment, ContactUsFragment.CONTACT_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.CONTACT_US);
                }
                break;
            case MY_CARE_NOW:
                if (getActivityTag() != ActivityTag.MY_CARE_NOW) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareNowFragment myCareNowFragment = MyCareNowFragment.newInstance();
                    myCareNowFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareNowFragment, MyCareNowFragment.MCN_DASHBOARD_TAG)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_NOW);
                }
                break;
            case MY_CARE_SERVICES:
                if (getActivityTag() != ActivityTag.MY_CARE_SERVICES) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareServicesFragment fragment = MyCareServicesFragment.newInstance();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, fragment, MyCareServicesFragment.MY_CARE_SERVICES_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_SERVICES);
                }
                break;
            case MY_CARE_PROVIDERS:
                if (getActivityTag() != ActivityTag.MY_CARE_PROVIDERS) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareProvidersFragment myCareProvidersFragment = MyCareProvidersFragment.newInstance();
                    myCareProvidersFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareProvidersFragment, MyCareProvidersFragment.MY_CARE_PROVIDERS_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_PROVIDERS);
                }
                break;
            case MY_CARE_INTAKE:
                if (getActivityTag() != ActivityTag.MY_CARE_INTAKE) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareVisitIntakeFragment myCareVisitIntakeFragment = MyCareVisitIntakeFragment.newInstance();
                    myCareVisitIntakeFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareVisitIntakeFragment, MyCareVisitIntakeFragment.MY_CARE_INTAKE_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_INTAKE);
                }
                break;
            case MY_CARE_COST:
                if (getActivityTag() != ActivityTag.MY_CARE_COST) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareVisitCostFragment myCareVisitCostFragment = MyCareVisitCostFragment.newInstance();
                    myCareVisitCostFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareVisitCostFragment, MyCareVisitCostFragment.MY_CARE_COST_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_COST);
                }
                break;
            case MY_CARE_CREDIT_CARD:
                if (getActivityTag() != ActivityTag.MY_CARE_CREDIT_CARD) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareCreditCardFragment myCareCreditCardFragment = MyCareCreditCardFragment.newInstance();
                    myCareCreditCardFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareCreditCardFragment, MyCareCreditCardFragment.MY_CARE_CREDIT_CARD_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_CREDIT_CARD);
                }
                break;
            case MY_CARE_WAITING_ROOM:
                if (getActivityTag() != ActivityTag.MY_CARE_WAITING_ROOM) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareWaitingRoomFragment myCareWaitingRoomFragment = MyCareWaitingRoomFragment.newInstance();
                    myCareWaitingRoomFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareWaitingRoomFragment, MyCareWaitingRoomFragment.MY_CARE_WAITING_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_WAITING_ROOM);
                }
                break;
            case MY_CARE_PROFILE:
                if (getActivityTag() != ActivityTag.MY_CARE_PROFILE) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareProfileFragment myCareProfileFragment = MyCareProfileFragment.newInstance();
                    myCareProfileFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareProfileFragment, MyCareProfileFragment.MY_PROFILE_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_PROFILE);
                }
                break;
            case MY_CARE_PROFILE_DEPENDENT:
                if (getActivityTag() != ActivityTag.MY_CARE_PROFILE_DEPENDENT) {
                    getSupportFragmentManager().executePendingTransactions();
                    MyCareProfileViewDependentFragment myCareProfileViewDependentFragment = MyCareProfileViewDependentFragment.newInstance();
                    myCareProfileViewDependentFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, myCareProfileViewDependentFragment, MyCareProfileViewDependentFragment.MY_PROFILE_TAG)
                            .addToBackStack(null)
                            .commit();

                    setActivityTag(ActivityTag.MY_CARE_PROFILE_DEPENDENT);
                }
                break;
            case MY_MED_HISTORY:
                if (getActivityTag() != ActivityTag.MY_MED_HISTORY) {
                    getSupportFragmentManager().executePendingTransactions();
                    MedicalHistoryFragment historyFragment = MedicalHistoryFragment.newInstance();
                    historyFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, historyFragment, MedicalHistoryFragment.MED_HISTORY_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.MY_MED_HISTORY);
                }
                break;
            case MY_MEDICATIONS:
                if (getActivityTag() != ActivityTag.MY_MEDICATIONS) {
                    getSupportFragmentManager().executePendingTransactions();
                    MedicationsFragment medicationsFragment = MedicationsFragment.newInstance();
                    medicationsFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, medicationsFragment, MedicationsFragment.MEDICATIONS_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.MY_MEDICATIONS);
                }
                break;
            case MY_PHARMACY:
                if (getActivityTag() != ActivityTag.MY_PHARMACY) {
                    getSupportFragmentManager().executePendingTransactions();
                    PharmaciesFragment pharmaciesFragment = PharmaciesFragment.newInstance();
                    pharmaciesFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, pharmaciesFragment, PharmaciesFragment.PHARMACIES_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.MY_PHARMACY);
                }
                break;
            case MY_PHARMACY_DETAILS:
                if (getActivityTag() != ActivityTag.MY_PHARMACY_DETAILS) {
                    getSupportFragmentManager().executePendingTransactions();
                    PharmacyDetailsFragment pharmacyDetailsFragment = PharmacyDetailsFragment.newInstance();
                    pharmacyDetailsFragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, pharmacyDetailsFragment, PharmacyDetailsFragment.PHARMACY_DETAILS_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.MY_PHARMACY_DETAILS);
                }
                break;


            case PREVIOUS_VISITS_SUMMARIES:
                if (getActivityTag() != ActivityTag.PREVIOUS_VISITS_SUMMARIES) {
                    getSupportFragmentManager().executePendingTransactions();
                    PreviousVisitsFragment peviousVisitFragment = PreviousVisitsFragment.newInstance();
                    peviousVisitFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, peviousVisitFragment, PreviousVisitsFragment.PREVIOUS_VISITS_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.PREVIOUS_VISITS_SUMMARIES);
                }
                break;

            case PREVIOUS_VISIT_SUMMARY:

                if (getActivityTag() != ActivityTag.PREVIOUS_VISIT_SUMMARY) {
                    getSupportFragmentManager().executePendingTransactions();
                    VisitSummaryFragment visitSummaryFragment = VisitSummaryFragment.newInstance();
                    visitSummaryFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, visitSummaryFragment, VisitSummaryFragment.SUMMARY_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY);
                }
                break;

            case PREVIOUS_VISIT_SUMMARY_PDF:
                //if (getActivityTag() != ActivityTag.PREVIOUS_VISIT_SUMMARY_PDF) {
                getSupportFragmentManager().executePendingTransactions();
                PdfRendererZoomFragment fragment = new PdfRendererZoomFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.frame, fragment, PdfRendererZoomFragment.PDF_TAG)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                setActivityTag(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY_PDF);
                //}
                break;

            case MY_CARE_PRIVACY_POLICY:
                if (getActivityTag() != ActivityTag.MY_CARE_PRIVACY_POLICY) {
                    getSupportFragmentManager().executePendingTransactions();
                    PrivacyPolicyPdfFragment ppPdfFragment = PrivacyPolicyPdfFragment.newInstance();
                    ppPdfFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, ppPdfFragment, PrivacyPolicyPdfFragment.MY_CARE_PRIVACY_POLICY_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.MY_CARE_PRIVACY_POLICY);
                }
                break;

            case VIDEO_VISIT_SUMMARY:
                if (getActivityTag() != ActivityTag.VIDEO_VISIT_SUMMARY) {
                    getSupportFragmentManager().executePendingTransactions();
                    VisitSummaryFragment visitSummaryFragment = VisitSummaryFragment.newInstance();
                    visitSummaryFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, visitSummaryFragment, VisitSummaryFragment.SUMMARY_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.VIDEO_VISIT_SUMMARY);
                }
                break;

            case VIDEO_VISIT_FEEDBACK:
                if (getActivityTag() != ActivityTag.VIDEO_VISIT_FEEDBACK) {
                    getSupportFragmentManager().executePendingTransactions();
                    FeedbackFragment feedbackFragment = FeedbackFragment.newInstance();
                    feedbackFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.frame, feedbackFragment, FeedbackFragment.FEEDBACK_TAG)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();

                    setActivityTag(Constants.ActivityTag.VIDEO_VISIT_FEEDBACK);
                }
                break;
        }
        setMyCareVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        //users with no access to MyCareNow can see PROFILE right on the bottom tab navigation, hence hiding it here in the Options to avoid duplicates.
        if (!AuthManager.getInstance().hasMyCare()) {
            menu.findItem(R.id.profile).setVisible(false);
        }

        //Allow developer settings
        if (BuildConfig.SHOW_SETTINGS) {
            menu.findItem(R.id.developer).setVisible(true);
        } else {
            menu.findItem(R.id.developer).setVisible(false);
        }
        menu.findItem(R.id.version).setTitle("Version - v" + BuildConfig.VERSION_CODE);
        menu.findItem(R.id.release_date).setTitle("Release Date - " + BuildConfig.BUILD_TIME);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);

        switch (item.getItemId()) {
//            case R.id.help:
//                return true;
            case R.id.settings:
                setActivityTag(ActivityTag.SETTINGS);
                Intent intentSettings = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentSettings, options.toBundle());
                return true;
//            case R.id.preferences:
//                return true;
            case R.id.bill_pay:
                setActivityTag(ActivityTag.FAQ);
                Intent intentFAQ = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentFAQ, options.toBundle());
                return true;
            case R.id.profile:
                setActivityTag(ActivityTag.PROFILE_VIEW);
                Intent intentProfile = new Intent(this, OptionsActivity.class);
                ActivityCompat.startActivity(this, intentProfile, options.toBundle());
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
        ApiErrorUtil.getInstance().clearErrorMessage();
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

            } else if (activityTag == ActivityTag.MY_MED_HISTORY) {
                MedicalHistoryFragment frag = ((MedicalHistoryFragment) fm.findFragmentByTag(MedicalHistoryFragment.MED_HISTORY_TAG));
                if (frag != null && frag.selectedGroup == HistoryListAdapter.GROUP.ALLERGIES) {
                    frag.showConditions();
                } else {
                    fm.popBackStack();
                }

            } else if (activityTag == ActivityTag.MY_CARE_WAITING_ROOM) {
                MyCareWaitingRoomFragment frag = ((MyCareWaitingRoomFragment) fm.findFragmentByTag(MyCareWaitingRoomFragment.MY_CARE_WAITING_TAG));
                if (frag != null) {
                    frag.cancelVisit();
                    fm.popBackStack();
                }

            } else if (activityTag == ActivityTag.VIDEO_VISIT_SUMMARY) {
                goToPage(ActivityTag.MY_CARE);

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

    public void setActionBarLineVisibility(boolean visisble) {
        if (toolbarLine != null) {
            if (visisble) {
                toolbarLine.setVisibility(View.VISIBLE);
            } else {
                toolbarLine.setVisibility(View.GONE);
            }
        }
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

        //ALL GRANTED AT SOME POINT
        if (AppPreferences.getInstance().getBooleanPreference(Constants.AMWELL_SDK_ALL_PERMISSIONS_GRANTED)) {

            ArrayList<String> missingPermissions = new ArrayList<>();
            String[] requiredPermissions = AwsManager.getInstance().getAWSDK().getRequiredPermissions();
            if (requiredPermissions != null && requiredPermissions.length != 0) {
                for (String requiredPermission : requiredPermissions) {
                    if (ContextCompat.checkSelfPermission(this, requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                        missingPermissions.add(requiredPermission);
                    }
                }
            }

            if (missingPermissions.isEmpty()) {
                AppPreferences.getInstance().setBooleanPreference(Constants.AMWELL_SDK_ALL_PERMISSIONS_GRANTED, true);
            } else {
                //ALL GRANTED AT SOME POINT, NOW some persmissions seems to have been declined.
                AppPreferences.getInstance().setBooleanPreference(Constants.AMWELL_SDK_ALL_PERMISSIONS_GRANTED, false);

                SessionUtil.logout(this, null);
                CommonUtil.exitApp(this, this);

                Intent intent = SplashActivity.getSplashIntent(this);
                this.startActivity(intent);
                this.finish();
            }
        }

        setMyCareVisibility();

        // session expiry
        if (AuthManager.getInstance().isExpired()) {
            buildSessionAlert(getString(R.string.session_expiry_message));
        }

        //Refresh to homescreen if timezone occurred
        if (didTimeZoneChange) {
            didTimeZoneChange = false;
            goToPage(ActivityTag.HOME);
        } else {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(Constants.PREF_TIME_ZONE,
                    TimeZone.getDefault().getID()).apply();
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

        if (AuthManager.getInstance().isExpired()) {
            buildSessionAlert(getString(R.string.session_expiry_message));
        }
        mHandler.removeCallbacks(runnable);
        AppPreferences.getInstance().setLongPreference("IDLE_TIME", System.currentTimeMillis());
        mHandler.postDelayed(runnable, AuthManager.SESSION_EXPIRY_TIME);
    }

    public Handler mHandler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            buildSessionAlert(getString(R.string.session_expiry_message));
        }
    };

    @Override
    public void expired() {
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, 0);
    }

    //Receiver for handling Timezone changes
    private static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Check to see if timezone actually changed or it was just updated (happens often with automatic network)
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            String oldTimezone = prefs.getString(Constants.PREF_TIME_ZONE, null);
            String newTimezone = TimeZone.getDefault().getID();

            long now = System.currentTimeMillis();

            if (oldTimezone == null || TimeZone.getTimeZone(oldTimezone).getOffset(now) != TimeZone.getTimeZone(newTimezone).getOffset(now)) {
                Timber.i("Timezone changed");
                prefs.edit().putString(Constants.PREF_TIME_ZONE, newTimezone).apply();
                didTimeZoneChange = true;
            }
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public BottomNavigationViewEx getBottomNavigationView() {
        return bottomNavigationView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment != null && currentFragment instanceof MyCareVisitCostFragment) {
            ((MyCareVisitCostFragment) currentFragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else if (currentFragment != null && currentFragment instanceof MyCareVisitIntakeFragment) {
            ((MyCareVisitIntakeFragment) currentFragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (currentFragment != null && currentFragment instanceof MyCareVisitCostFragment) {
            ((MyCareVisitCostFragment) currentFragment).onActivityResult(requestCode, resultCode, data);
        }
        else if (currentFragment != null && currentFragment instanceof MyCareVisitIntakeFragment) {
            ((MyCareVisitIntakeFragment) currentFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

}