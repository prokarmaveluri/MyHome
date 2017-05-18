package com.dignityhealth.myhome.features.fad.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by kwelsh on 5/18/17.
 */

public class ProviderDetailsAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public ProviderDetailsAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Profile
                return ProviderDetailsProfileFragment.newInstance();
            case 1: // Education
                return ProviderDetailsEducationFragment.newInstance();
            case 2: // Experience
                return ProviderDetailsExperienceFragment.newInstance();
            default:
                return null;
        }
    }
}
