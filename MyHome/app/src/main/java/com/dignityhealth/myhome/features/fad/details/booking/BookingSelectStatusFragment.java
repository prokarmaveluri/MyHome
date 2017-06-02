package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dignityhealth.myhome.R;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectStatusFragment extends Fragment {
    public static final String BOOKING_SELECT_STATUS_TAG = "booking_select_status_tag";
    public static final String SHOW_NEW_KEY = "show_new";
    public static final String SHOW_EXISTING_KEY = "show_existing";

    public BookingSelectStatusInterface selectStatusInterface;

    View bookingView;
    boolean showNew;
    boolean showExisting;

    public static BookingSelectStatusFragment newInstance() {
        BookingSelectStatusFragment bookingFragment = new BookingSelectStatusFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_NEW_KEY, true);
        args.putBoolean(SHOW_EXISTING_KEY, true);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    public static BookingSelectStatusFragment newInstance(boolean showNew, boolean showExisting) {
        BookingSelectStatusFragment bookingFragment = new BookingSelectStatusFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_NEW_KEY, showNew);
        args.putBoolean(SHOW_EXISTING_KEY, showExisting);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        showNew = args.getBoolean(SHOW_NEW_KEY);
        showExisting = args.getBoolean(SHOW_EXISTING_KEY);

        bookingView = inflater.inflate(R.layout.book_select_status, container, false);
        final TextView header = (TextView) bookingView.findViewById(R.id.status_header);
        final TextView noAppointments = (TextView) bookingView.findViewById(R.id.no_times_available);
        final Button buttonNew = (Button) bookingView.findViewById(R.id.book_new);
        final Button buttonExisting = (Button) bookingView.findViewById(R.id.book_existing);

        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showNew) {
                    if (selectStatusInterface != null) {
                        selectStatusInterface.onStatusSelected(true);
                    }
                } else {
                    header.setVisibility(View.GONE);
                    buttonNew.setVisibility(View.GONE);
                    buttonExisting.setVisibility(View.GONE);
                    noAppointments.setVisibility(View.VISIBLE);
                    noAppointments.setText(getString(R.string.no_appointments_new_patients));
                }
            }
        });

        buttonExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showExisting) {
                    if (selectStatusInterface != null) {
                        selectStatusInterface.onStatusSelected(false);
                    }
                } else {
                    header.setVisibility(View.GONE);
                    buttonNew.setVisibility(View.GONE);
                    buttonExisting.setVisibility(View.GONE);
                    noAppointments.setVisibility(View.VISIBLE);
                    noAppointments.setText(getString(R.string.no_appointments_existing_patients));
                }
            }
        });

        return bookingView;
    }

    public void setSelectStatusInterface(BookingSelectStatusInterface selectStatusInterface) {
        this.selectStatusInterface = selectStatusInterface;
    }
}
