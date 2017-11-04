package com.prokarma.myhome.features.fad.details.booking;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.utils.DeviceDisplayManager;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectStatusFragment extends Fragment {
    public static final String BOOKING_SELECT_STATUS_TAG = "booking_select_status_tag";
    public static final String APPOINTMENT_TYPE_KEY = "appointment_type_key";

    public BookingSelectStatusInterface selectStatusInterface;
    public BookingRefreshInterface refreshInterface;

    View bookingView;
    ArrayList<AppointmentType> appointmentTypes;

    public static BookingSelectStatusFragment newInstance(ArrayList<AppointmentType> appointmentTypes) {
        BookingSelectStatusFragment bookingFragment = new BookingSelectStatusFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(APPOINTMENT_TYPE_KEY, appointmentTypes);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        appointmentTypes = args.getParcelableArrayList(APPOINTMENT_TYPE_KEY);

        bookingView = inflater.inflate(R.layout.book_select_status, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));

        final TextView header = (TextView) bookingView.findViewById(R.id.status_header);
        final LinearLayout appointmentTypeLayout = (LinearLayout) bookingView.findViewById(R.id.appointment_type_layout);
        final TextView noAppointments = (TextView) bookingView.findViewById(R.id.no_times_available);

        setAppointmentTypes(appointmentTypeLayout, appointmentTypes);

//        buttonNew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (showNew) {
//                    if (selectStatusInterface != null) {
//                        selectStatusInterface.onStatusSelected(true);
//                    }
//                } else {
//                    header.setVisibility(View.GONE);
//                    buttonNew.setVisibility(View.GONE);
//                    buttonExisting.setVisibility(View.GONE);
//                    noAppointments.setVisibility(View.VISIBLE);
//                    noAppointments.setText(getString(R.string.no_appointments_new_patients));
//                }
//            }
//        });
//
//        buttonExisting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (showExisting) {
//                    if (selectStatusInterface != null) {
//                        selectStatusInterface.onStatusSelected(false);
//                    }
//                } else {
//                    header.setVisibility(View.GONE);
//                    buttonNew.setVisibility(View.GONE);
//                    buttonExisting.setVisibility(View.GONE);
//                    noAppointments.setVisibility(View.VISIBLE);
//                    noAppointments.setText(getString(R.string.no_appointments_existing_patients));
//                }
//            }
//        });

        return bookingView;
    }

    private void setAppointmentTypes(final LinearLayout appointmentTypeLayout, final ArrayList<AppointmentType> appointmentTypes) {
        appointmentTypeLayout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceDisplayManager.dpToPx(getContext(), 40));
        layoutParams.setMargins(DeviceDisplayManager.getInstance().dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5));
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

        View.OnClickListener typeClickedListener;
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.selectableButtonStyle);

        for (final AppointmentType appointmentType : appointmentTypes) {
            final Button typeButton = new Button(contextThemeWrapper, null, R.style.selectableButtonStyle);
            typeButton.setGravity(Gravity.CENTER);
            layoutParams.gravity = Gravity.CENTER;
            typeButton.setTextSize(18);
            typeButton.setTypeface(boldTypeface);
            typeButton.setLayoutParams(layoutParams);
            typeButton.setText(appointmentType.getName());

            typeClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectStatusInterface != null) {
                        selectStatusInterface.onTypeSelected(appointmentType);
                    }
                }
            };

            typeButton.setOnClickListener(typeClickedListener);
            appointmentTypeLayout.addView(typeButton);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setSelectStatusInterface(BookingSelectStatusInterface selectStatusInterface) {
        this.selectStatusInterface = selectStatusInterface;
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }
}
