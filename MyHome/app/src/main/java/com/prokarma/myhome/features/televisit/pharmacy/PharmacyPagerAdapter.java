package com.prokarma.myhome.features.televisit.pharmacy;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Chandra on 5/21/17.
 */

public class PharmacyPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 2;

    public PharmacyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //List
                Fragment fragment = PharmacyListFragment.newInstance();
                return fragment;
            case 1: // Map
                Fragment mapFragment = PharmacyMapFragment.newInstance();
                return mapFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "List";
            case 1:
                return "Map";
            default:
                return "";
        }
    }

    /**
     * Possible fix for TransactionTooLargeException:
     * android.app.ActivityThread$StopInfo.run
     * android.os.TransactionTooLargeException: data parcel size 831636 bytes
     * <p>
     * See: https://stackoverflow.com/a/43193467/2128921
     * See: https://rink.hockeyapp.net/manage/apps/529078/app_versions/16/crash_reasons/174083255
     */
    @Override
    public Parcelable saveState() {
        Bundle bundle = (Bundle) super.saveState();
        if (bundle != null) {
            bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
        }
        return bundle;
    }
}
