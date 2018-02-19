package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Appointment;
import com.prokarma.myhome.entities.MyAppointmentsResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.ApiErrorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsView implements AppointmentsContract.View {
    private Context context;
    private FragmentManager fragmentManager;

    private View masterView;
    private ProgressBar progressBar;
    private TabLayout appointmentsTabLayout;
    private ViewPager appointmentsViewPager;

    public AppointmentsView(final Context context, final View masterView, final AppointmentsPresenter presenter, final FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.masterView = masterView;

        progressBar = (ProgressBar) masterView.findViewById(R.id.appointments_progress);

        appointmentsViewPager = (ViewPager) masterView.findViewById(R.id.appointment_viewpager);
        appointmentsViewPager.setOffscreenPageLimit(2);
        appointmentsViewPager.setAdapter(new AppointmentsViewPagerAdapter(context, fragmentManager, ProfileManager.getAppointments()));

        appointmentsTabLayout = (TabLayout) masterView.findViewById(R.id.appointment_tablayout);
        appointmentsTabLayout.setupWithViewPager(appointmentsViewPager);
    }

    private void showScreen() {
        progressBar.setVisibility(View.GONE);
        appointmentsTabLayout.setVisibility(View.VISIBLE);
        appointmentsViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAppointmentsSuccess(Response<MyAppointmentsResponse> response) {
        showScreen();

        Timber.d("Successful Response\n" + response);
        MyAppointmentsResponse myAppointmentsResponse = response.body();
        Timber.d("My Appointments Response: " + myAppointmentsResponse);

        if (myAppointmentsResponse != null && myAppointmentsResponse.getData() != null && myAppointmentsResponse.getData().getUser() != null) {
            ArrayList<Appointment> appointments = (ArrayList<Appointment>) myAppointmentsResponse.getData().getUser().getAppointments();
            Timber.i("Appointments: " + Arrays.deepToString(appointments.toArray()));

            try {
                //Attempt to sort the appointments by startTime
                Collections.sort(appointments);
                ProfileManager.setAppointments(appointments);

                appointmentsViewPager.setAdapter(new AppointmentsViewPagerAdapter(context, fragmentManager, appointments));
            } catch (Exception e) {
                appointmentsViewPager.setAdapter(null);
            }
        }
    }

    @Override
    public void onAppointmentsFailed(Response<MyAppointmentsResponse> response) {
        Timber.e("getMyAppointments. Response, but not successful?\n" + response);

        showScreen();
        ApiErrorUtil.getInstance().getMyAppointmentsError(context, masterView, response);
    }

    @Override
    public void onAppointmentsFailed(Throwable throwable) {
        Timber.e("Throwable = " + throwable);

        showScreen();
        ApiErrorUtil.getInstance().getMyAppointmentsFailed(context, masterView, throwable);
    }
}
