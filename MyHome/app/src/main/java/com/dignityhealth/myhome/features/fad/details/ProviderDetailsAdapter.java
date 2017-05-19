package com.dignityhealth.myhome.features.fad.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by kwelsh on 5/18/17.
 */

public class ProviderDetailsAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    private ProviderDetailsResponse providerDetailsResponse;

    public ProviderDetailsAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public ProviderDetailsAdapter(FragmentManager fragmentManager, ProviderDetailsResponse providerDetailsResponse){
        super(fragmentManager);
        this.providerDetailsResponse = providerDetailsResponse;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Profile
                return ProviderDetailsProfileFragment.newInstance(providerDetailsResponse);
            case 1: // Education
                return ProviderDetailsEducationFragment.newInstance(providerDetailsResponse);
            case 2: // Experience
                return ProviderDetailsExperienceFragment.newInstance(providerDetailsResponse);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: // Profile
                return ProviderDetailsProfileFragment.PAGER_TITLE;
            case 1: // Education
                return ProviderDetailsEducationFragment.PAGER_TITLE;
            case 2: // Experience
                return ProviderDetailsExperienceFragment.PAGER_TITLE;
            default:
                return "";
        }
    }
}
