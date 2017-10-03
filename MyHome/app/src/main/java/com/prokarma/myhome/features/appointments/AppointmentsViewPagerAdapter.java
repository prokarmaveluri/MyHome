package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;

import java.util.ArrayList;

/**
 * Created by kwelsh on 8/3/17.
 */

public class AppointmentsViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int PAGE_COUNT = 2;

    private Context context;
    private String tabTitles[];
    private ArrayList<Appointment> appointments;

    public AppointmentsViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        tabTitles = new String[]{context.getString(R.string.upcoming), context.getString(R.string.past)};
    }

    public AppointmentsViewPagerAdapter(Context context, FragmentManager fm, ArrayList<Appointment> appointments) {
        super(fm);
        this.context = context;
        this.appointments = appointments;
        tabTitles = new String[]{context.getString(R.string.upcoming), context.getString(R.string.past)};
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AppointmentsListFragment.newInstance(CommonUtil.getFutureAppointments(appointments), false);
            case 1:
                return AppointmentsListFragment.newInstance(CommonUtil.getPastAppointments(appointments), true);
            default:
                return AppointmentsListFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
