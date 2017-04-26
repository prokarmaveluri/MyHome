package com.dignityhealth.myhome.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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

public class DrawerActivity {
    private final AppCompatActivity appCompatActivity;
    private ActivityTag activityTag;
    private final DrawerToggleInterface drawerToggleInterface;

    private DrawerLayout drawerLayout;
    private NavigationView drawerNavView;

    public DrawerActivity(final AppCompatActivity appCompatActivity, final DrawerToggleInterface drawerToggleInterface) {
        this.appCompatActivity = appCompatActivity;
        this.drawerToggleInterface = drawerToggleInterface;
    }

    public void initialize() {
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) appCompatActivity.findViewById(R.id.drawer_layout);
        drawerNavView = (NavigationView) appCompatActivity.findViewById(R.id.drawer);

        drawerNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();

                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(appCompatActivity, R.anim.slide_in_right, R.anim.slide_out_left);
                switch (menuItem.getItemId()) {

                    case R.id.home:
                        if(activityTag != ActivityTag.HOME){
                            Intent intentHome = new Intent(appCompatActivity, HomeActivity.class);
                            intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityCompat.startActivity(appCompatActivity, intentHome, options.toBundle());
                        }
                        return true;

                    case R.id.fad:
                        if(activityTag != ActivityTag.FAD) {
                            Intent intentFad = new Intent(appCompatActivity, FadActivity.class);
                            intentFad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityCompat.startActivity(appCompatActivity, intentFad, options.toBundle());
                        }
                        return true;

                    case R.id.profile:
                        if(activityTag != ActivityTag.PROFILE) {
                            Intent intentProfile = new Intent(appCompatActivity, ProfileActivity.class);
                            intentProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityCompat.startActivity(appCompatActivity, intentProfile, options.toBundle());
                        }
                        return true;

                    case R.id.settings:
                        if(activityTag != ActivityTag.SETTINGS) {
                            Intent intentSettings = new Intent(appCompatActivity, SettingsActivity.class);
                            intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityCompat.startActivity(appCompatActivity, intentSettings, options.toBundle());
                        }
                        return true;

                    case R.id.contact_us:
                        if(activityTag != ActivityTag.CONTACT_US) {
                            Intent intentContact = new Intent(appCompatActivity, ContactUsActivity.class);
                            intentContact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityCompat.startActivity(appCompatActivity, intentContact, options.toBundle());
                        }
                        return true;

                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(appCompatActivity, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_closed) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (drawerToggleInterface != null) {
                    drawerToggleInterface.onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (drawerToggleInterface != null) {
                    drawerToggleInterface.onDrawerOpened(drawerView);
                }
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    //Is drawer open (on start/left-hand side)
    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void setActivityTag(ActivityTag activityTag) {
        this.activityTag = activityTag;

        switch (activityTag) {
            case HOME:
                drawerNavView.getMenu().getItem(0).setChecked(true);
                break;
            case FAD:
                drawerNavView.getMenu().getItem(1).setChecked(true);
                break;
            case PROFILE:
                drawerNavView.getMenu().getItem(2).setChecked(true);
                break;
            case SETTINGS:
                drawerNavView.getMenu().getItem(3).setChecked(true);
                break;
            case CONTACT_US:
                drawerNavView.getMenu().getItem(4).setChecked(true);
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
