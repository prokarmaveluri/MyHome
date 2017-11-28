/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.appointments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdksample.BaseSwipeToRefreshActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for list of appointments
 */
@RequiresPresenter(AppointmentsPresenter.class)
public class AppointmentsActivity extends BaseSwipeToRefreshActivity<AppointmentsPresenter, Appointment> implements SwipeRefreshLayout.OnRefreshListener {

    public static Intent makeIntent(@NonNull final Context context) {
        return new Intent(context, AppointmentsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_appointments);
    }

    @Override
    protected ArrayAdapter createAdapter() {
        return new AppointmentsAdapter(this, getPresenter().getPreferredLocale());
    }

    @Override
    @StringRes
    protected int getTitleRes() {
        return R.string.title_activity_appointments;
    }

    @Override
    public void onRefresh() {
        getPresenter().getAppointments();
    }

    public void setAppointmentCanceled() {
        Toast.makeText(this, R.string.appointment_msg_canceled, Toast.LENGTH_SHORT).show();
    }

    // Adapter to handle the Appointment list
    public class AppointmentsAdapter extends ArraySwipeAdapter<Appointment> {

        Locale locale;

        public AppointmentsAdapter(Context context, Locale locale) {
            super(context, 0);
            this.locale = locale;
        }

        public class ViewHolder {
            // we use butterknife to simplify viewholder binding here
            @BindView(R.id.appointment_datetime)
            TextView datetime;
            @BindView(R.id.appointment_provider)
            TextView provider;
            @BindView(R.id.appointment_dependent)
            TextView dependent;
            @BindView(R.id.item_appointment_swiper)
            SwipeLayout swipeLayout;
            SwipeLayout.SwipeListener swipeListener;
            @BindView(R.id.item_appointment_delete)
            View delete;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.item_appointment_swiper;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_appointment, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Appointment appointment = (Appointment) getItem(position);
            viewHolder.datetime.setText(localeUtils.formatTimeStamp(
                    appointment.getSchedule().getScheduledStartTime()));

            // format the provider info
            final StringBuilder provSb = new StringBuilder();
            if (appointment.getAssignedProvider() != null) {
                provSb.append(appointment.getAssignedProvider().getFullName());
                provSb.append(", ");
                provSb.append(appointment.getAssignedProvider().getSpecialty().getName());
                viewHolder.provider.setText(provSb.toString());
            }
            else {
                // if the appointment is for a "first available" provider and one has not yet been assigned
                // show text to that point

                String firstAvailable = getString(
                        R.string.appointments_list_no_provider,
                        appointment.getSpecialty().getName()
                );
                viewHolder.provider.setText(firstAvailable);
            }

            // show dependent name if consumer is proxying for them
            if (appointment.getConsumerProxy() != null) {
                viewHolder.dependent.setVisibility(View.VISIBLE);
                viewHolder.dependent.setText(getString(R.string.appointments_list_for, appointment.getConsumer().getFullName()));
            }
            else {
                viewHolder.dependent.setVisibility(View.GONE);
            }

            // swipe handling
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            // set item on-click to open details
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AppointmentActivity.makeIntent(getContext(), appointment, false));
                }
            });

            // swipe listener
            if (viewHolder.swipeListener != null) {
                viewHolder.swipeLayout.removeSwipeListener(viewHolder.swipeListener);
            }
            viewHolder.swipeListener = new SampleSwipeListener(swipeRefreshLayout);
            viewHolder.swipeLayout.addSwipeListener(viewHolder.swipeListener);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().cancelAppointment(appointment);
                }
            });

            return view;
        }
    }
}
