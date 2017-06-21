package com.dignityhealth.myhome.features.fad;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Chandra on 5/21/17.
 */

public class FadPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 2;
    private ArrayList<Provider> providerList;
    private String message;

    public FadPagerAdapter(FragmentManager fragmentManager, ArrayList<Provider> list,
                           String message) {
        super(fragmentManager);
        this.providerList = list;
        this.message = message;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //List
                ProviderListFragment fragment = new ProviderListFragment();
                fragment.setListener(null);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
                bundle.putString("PROVIDER_MSG", message);
                bundle.putBoolean("PROVIDER_PAGINATION", true);
                fragment.setArguments(bundle);
                return fragment;
            case 1: // Map
                Fragment mapFragment = ProvidersMapFragment.newInstance();
                bundle = new Bundle();
                bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
                mapFragment.setArguments(bundle);
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
