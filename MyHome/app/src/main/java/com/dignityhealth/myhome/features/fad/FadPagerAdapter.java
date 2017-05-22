package com.dignityhealth.myhome.features.fad;

import android.os.Bundle;
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
                Fragment fragment = ProviderListFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
                bundle.putString("PROVIDER_MSG", message);
                fragment.setArguments(bundle);
                return fragment;
            case 1: // Map
                return ProvidersMapFragment.newInstance();
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
}
