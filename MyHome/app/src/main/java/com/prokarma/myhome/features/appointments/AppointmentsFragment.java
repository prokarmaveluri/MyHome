package com.prokarma.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsFragment extends BaseFragment {
    public static final String APPOINTMENTS_TAG = "appointment_tag";

    private View appointmentsView;
    private ProgressBar progressBar;
    private TabLayout appointmentsTabLayout;
    private ViewPager appointmentsViewPager;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointmentsView = inflater.inflate(R.layout.appointments, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.appointments));

        progressBar = (ProgressBar) appointmentsView.findViewById(R.id.appointments_progress);

        appointmentsViewPager = (ViewPager) appointmentsView.findViewById(R.id.appointment_viewpager);
        appointmentsViewPager.setOffscreenPageLimit(2);
        appointmentsViewPager.setAdapter(new AppointmentsViewPagerAdapter(getContext(), getChildFragmentManager()));

        appointmentsTabLayout = (TabLayout) appointmentsView.findViewById(R.id.appointment_tablayout);
        appointmentsTabLayout.setupWithViewPager(appointmentsViewPager);

        getMyAppointments();
        return appointmentsView;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        appointmentsTabLayout.setVisibility(View.GONE);
        appointmentsViewPager.setVisibility(View.GONE);
    }

    private void showScreen() {
        progressBar.setVisibility(View.GONE);
        appointmentsTabLayout.setVisibility(View.VISIBLE);
        appointmentsViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.APPOINTMENTS_SCREEN, null);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }

    private void getMyAppointments() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showLoading();

        NetworkManager.getInstance().getMyAppointments(AuthManager.getInstance().getBearerToken(),
                new MyAppointmentsRequest()).enqueue(new Callback<MyAppointmentsResponse>() {
            @Override
            public void onResponse(Call<MyAppointmentsResponse> call, Response<MyAppointmentsResponse> response) {
                if (isAdded()) {
                    showScreen();
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        MyAppointmentsResponse myAppointmentsResponse = response.body();
                        Timber.d("My Appointments Response: " + myAppointmentsResponse);

                        if (myAppointmentsResponse.getData() != null && myAppointmentsResponse.getData().getUser() != null) {
                            ArrayList<Appointment> appointments = (ArrayList<Appointment>) myAppointmentsResponse.getData().getUser().getAppointments();
                            Timber.i("Appointments: " + Arrays.deepToString(appointments.toArray()));

                            try {
                                //Attempt to sort the appointments by startTime
                                Collections.sort(appointments);
                                ProfileManager.setAppointments(appointments);

                                appointmentsViewPager.setAdapter(new AppointmentsViewPagerAdapter(getContext(), getChildFragmentManager(), appointments));
                            } catch (Exception e) {
                                appointmentsViewPager.setAdapter(null);
                            }
                        }

                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        ApiErrorUtil.getInstance().getMyAppointmentsError(getContext(), appointmentsView, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    showScreen();
                    ApiErrorUtil.getInstance().getMyAppointmentsFailed(getContext(), appointmentsView, t);
                }
            }
        });
    }
}
