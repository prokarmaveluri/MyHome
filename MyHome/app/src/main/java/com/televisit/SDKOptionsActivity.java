package com.televisit;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.ProfileViewFragment;
import com.prokarma.myhome.utils.Constants;
import com.televisit.history.MedicalHistoryFragment;
import com.televisit.medications.MedicationsFragment;
import com.televisit.pharmacy.PharmaciesFragment;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/8/17.
 */

public class SDKOptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        switch (NavigationActivity.getActivityTag()) {

            case PROFILE_VIEW:
                ProfileViewFragment profileFragment = ProfileViewFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, profileFragment, ProfileViewFragment.PROFILE_VIEW_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.PROFILE_VIEW);
                break;

            case MY_MED_HISTORY:
                MedicalHistoryFragment historyFragment = MedicalHistoryFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, historyFragment, MedicalHistoryFragment.MED_HISTORY_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_MED_HISTORY);
                break;
            case MY_MEDICATIONS:
                MedicationsFragment medicationsFragment = MedicationsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, medicationsFragment, MedicationsFragment.MEDICATIONS_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();
                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_MEDICATIONS);
                break;
            case MY_PHARMACY:
                PharmaciesFragment pharmaciesFragment = PharmaciesFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, pharmaciesFragment, PharmaciesFragment.PHARMACIES_TAG)
                        .commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();

                NavigationActivity.setActivityTag(Constants.ActivityTag.MY_PHARMACY);
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
