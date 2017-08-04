package com.prokarma.myhome.features.appointments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.prokarma.myhome.utils.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by kwelsh on 8/3/17.
 */

public class AppointmentsViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;

    private String tabTitles[] = new String[] { "Upcoming", "Past" };
    private ArrayList<Appointment> appointments;

    public AppointmentsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public AppointmentsViewPagerAdapter(FragmentManager fm, ArrayList<Appointment> appointments){
        super(fm);
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AppointmentsListFragment.newInstance(getFutureAppointments(appointments));
            case 1:
                return AppointmentsListFragment.newInstance(getPastAppointments(appointments));
            default:
                return AppointmentsListFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    private ArrayList<Appointment> getPastAppointments(ArrayList<Appointment> allAppointments){
        ArrayList<Appointment> pastAppointments = new ArrayList<>();
        Date todaysDate = new Date();
        Date appointmentDate = new Date();

        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateNoTimeZone(appointment.appointmentStart);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (appointmentDate.before(todaysDate)) {
                pastAppointments.add(appointment);
            }
        }

        return pastAppointments;
    }

    private ArrayList<Appointment> getFutureAppointments(ArrayList<Appointment> allAppointments){
        ArrayList<Appointment> futureAppointments = new ArrayList<>();
        Date todaysDate = new Date();
        Date appointmentDate = new Date();

        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateNoTimeZone(appointment.appointmentStart);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (DateUtil.isOnSameDay(appointmentDate, todaysDate) || appointmentDate.after(todaysDate)) {
                futureAppointments.add(appointment);
            }
        }

        return futureAppointments;
    }
}
