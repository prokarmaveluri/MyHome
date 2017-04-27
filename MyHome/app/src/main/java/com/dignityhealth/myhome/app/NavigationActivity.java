package com.dignityhealth.myhome.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.contact.ContactUsActivity;
import com.dignityhealth.myhome.features.fad.FadActivity;
import com.dignityhealth.myhome.features.home.HomeActivity;
import com.dignityhealth.myhome.features.profile.ProfileActivity;
import com.dignityhealth.myhome.features.settings.SettingsActivity;
import com.dignityhealth.myhome.utils.Constants.ActivityTag;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/25/17.
 */

public class NavigationActivity {
    private final AppCompatActivity appCompatActivity;
    private ActivityTag activityTag;

    private BottomNavigationView bottomNavigationView;

    public NavigationActivity(final AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void initialize() {
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView)
                appCompatActivity.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(appCompatActivity, R.anim.slide_in_right, R.anim.slide_out_left);

                        switch (item.getItemId()) {
                            case R.id.home:
                                if (activityTag != ActivityTag.HOME) {
                                    Intent intentHome = new Intent(appCompatActivity, HomeActivity.class);
                                    intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.startActivity(appCompatActivity, intentHome, options.toBundle());
                                }
                                break;

                            case R.id.fad:
                                if (activityTag != ActivityTag.FAD) {
                                    Intent intentFad = new Intent(appCompatActivity, FadActivity.class);
                                    intentFad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.startActivity(appCompatActivity, intentFad, options.toBundle());
                                }
                                break;

                            case R.id.profile:
                                if (activityTag != ActivityTag.PROFILE) {
                                    Intent intentProfile = new Intent(appCompatActivity, ProfileActivity.class);
                                    intentProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.startActivity(appCompatActivity, intentProfile, options.toBundle());
                                }
                                break;

                            case R.id.settings:
                                if (activityTag != ActivityTag.SETTINGS) {
                                    Intent intentSettings = new Intent(appCompatActivity, SettingsActivity.class);
                                    intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.startActivity(appCompatActivity, intentSettings, options.toBundle());
                                }
                                break;

                            case R.id.contact_us:
                                if (activityTag != ActivityTag.CONTACT_US) {
                                    Intent intentContact = new Intent(appCompatActivity, ContactUsActivity.class);
                                    intentContact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityCompat.startActivity(appCompatActivity, intentContact, options.toBundle());
                                }
                                break;

                        }
                        return true;
                    }
                });
    }

    public void setActivityTag(ActivityTag activityTag) {
        this.activityTag = activityTag;

        switch (activityTag) {
            case HOME:
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                break;
            case FAD:
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;
            case PROFILE:
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                break;
            case SETTINGS:
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                break;
            case CONTACT_US:
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
                break;
            default:
                Timber.e("Unknown Drawer Menu Item");
                break;
        }
    }

    public ActivityTag getActivityTag() {
        return activityTag;
    }
}
