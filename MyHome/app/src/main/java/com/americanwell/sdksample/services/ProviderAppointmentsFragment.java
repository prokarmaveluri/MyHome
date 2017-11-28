/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.AvailableProvider;
import com.americanwell.sdk.entity.provider.AvailableProviders;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.SampleUtils;
import com.americanwell.sdksample.widget.SwipeRefreshLayoutCheckChild;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Fragment to show providers and their appointment availability
 */
public class ProviderAppointmentsFragment extends Fragment {

    @Inject
    protected SampleUtils sampleUtils;

    @Inject
    protected LocaleUtils localeUtils;

    @BindView(R.id.appointments_prompt)
    View appointmentsPrompt;
    @BindView(R.id.appointments_date)
    TextView appointmentsDate;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    View emptyView;

    protected ArrayAdapter<AvailableProvider> adapter;

    private Unbinder unbinder;

    private OnProviderAppointmentsFragmentListener mListener;

    public ProviderAppointmentsFragment() {
        // Required empty public constructor
    }

    public static ProviderAppointmentsFragment newInstance() {
        ProviderAppointmentsFragment fragment = new ProviderAppointmentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProvidersAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_appointments, container, false);
        SampleApplication.getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);
        listView.setAdapter(adapter);
        setAvailableProviders(mListener.getPresenter().getFutureAvailableProviders());
        mListener.getSwipeRefreshLayout().setChildView(listView);

        return view;
    }

    public void setAvailableProviders(final AvailableProviders availableProviders) {
        if (availableProviders != null) {
            setListItems(availableProviders.getAvailableProviders());
            if (availableProviders.getDate() != null) {
                setAppointmentsDate(availableProviders.getDate());
            }
            else {
                setAppointmentsDate(mListener.getPresenter().getAppointmentsDate()); // we didn't get back providers so show last tried dates so they can pick another
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProviderAppointmentsFragmentListener) {
            mListener = (OnProviderAppointmentsFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAvailableProvidersFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnProviderAppointmentsFragmentListener {
        ServicePresenter getPresenter();

        SwipeRefreshLayoutCheckChild getSwipeRefreshLayout();
    }

    public void setAppointmentsDate(Date date) {
        if (appointmentsDate != null && date != null) {
            appointmentsDate.setVisibility(View.VISIBLE);
            appointmentsDate.setText(localeUtils.formatAppointmentDate(date));
        }
    }

    @OnClick(R.id.appointments_date)
    public void onAppointmentsDateClick() {

        Date date = mListener.getPresenter().getAppointmentsDate();
        if (date == null) {
            date = new Date();
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                final Calendar newCalendar = Calendar.getInstance();
                                newCalendar.set(Calendar.YEAR, year);
                                newCalendar.set(Calendar.MONTH, monthOfYear);
                                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mListener.getPresenter().setAppointmentsDate(newCalendar.getTime());
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.setTitle(null);
        datePickerDialog.show();
    }


    /**
     * the presenter will call this when there are new items to add to the list
     *
     * @param items
     */
    public void setListItems(@NonNull final List<AvailableProvider> items) {
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(items);
        }
        if (listView != null) {
            listView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public class ProvidersAdapter extends ArrayAdapter<AvailableProvider> {
        public ProvidersAdapter(final Context context) {
            super(context, 0);
        }

        public class ViewHolder {
            @BindView(R.id.image_view)
            ImageView imageView;
            @BindView(R.id.name_text_view)
            TextView nameTextView;
            @BindView(R.id.specialty_text_view)
            TextView specialtyTextView;
            @BindView(R.id.availability_text_view)
            TextView availabilityTextView;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_provider, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final AvailableProvider availableProvider = getItem(position);
            final ProviderInfo providerInfo = availableProvider.getProviderInfo();
            final int slots = availableProvider.getAvailableAppointmentTimeSlots().size();
            viewHolder.imageView.setImageBitmap(null);

            mListener.getPresenter().loadProviderImage(
                    providerInfo,
                    viewHolder.imageView,
                    getResources().getDrawable(R.drawable.img_provider_photo_placeholder));

            viewHolder.nameTextView.setText(providerInfo.getFullName());

            String availability = getResources().getQuantityString(R.plurals.provider_appointments_plurals, slots, slots);

            viewHolder.availabilityTextView.setText(availability);
            viewHolder.nameTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.availabilityTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.availabilityTextView.setTextColor(getResources().getColor(R.color.color_text_gray));

            viewHolder.specialtyTextView.setText(providerInfo.getSpecialty().getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ProviderActivity.makeIntent(getContext(), availableProvider, mListener.getPresenter().getAppointmentsDate()));
                }
            });

            return view;
        }
    }

}
