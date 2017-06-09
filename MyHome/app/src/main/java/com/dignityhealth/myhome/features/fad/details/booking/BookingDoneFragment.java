package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.Appointment;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDoneFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_done_tag";
    public static final String BOOKING_PROFILE_KEY = "booking_profile";
    public static final String BOOKING_APPOINTMENT_KEY = "booking_appointment";

    public BookingDoneInterface bookingDoneInterface;

    View bookingView;
    Profile bookingProfile;
    Appointment bookingAppointment;

    public static BookingDoneFragment newInstance() {
        return new BookingDoneFragment();
    }

    public static BookingDoneFragment newInstance(Profile bookingProfile, Appointment bookingAppointment) {
        BookingDoneFragment bookingFragment = new BookingDoneFragment();
        Bundle args = new Bundle();
        args.putParcelable(BOOKING_PROFILE_KEY, bookingProfile);
        args.putParcelable(BOOKING_APPOINTMENT_KEY, bookingAppointment);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            bookingProfile = args.getParcelable(BOOKING_PROFILE_KEY);
            bookingAppointment = args.getParcelable(BOOKING_APPOINTMENT_KEY);
        }

        bookingView = inflater.inflate(R.layout.book_done, container, false);

        TextView date = (TextView) bookingView.findViewById(R.id.date);
        TextView time = (TextView) bookingView.findViewById(R.id.time);
        TextView address = (TextView) bookingView.findViewById(R.id.address);

        date.setText(DateUtil.getDateWords2FromUTC(bookingAppointment.Time));
        time.setText(DateUtil.getTime(bookingAppointment.Time));
        address.setText(CommonUtil.constructAddress(bookingAppointment.FacilityAddress, null, bookingAppointment.FacilityCity, bookingAppointment.FacilityState, bookingAppointment.FacilityZip));

        TextView directions = (TextView) bookingView.findViewById(R.id.directions_text);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickDirections();
                }
            }
        });

        TextView share = (TextView) bookingView.findViewById(R.id.share_text);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickShare();
                }
            }
        });

        TextView calendar = (TextView) bookingView.findViewById(R.id.calendar_text);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickAddToCalendar();
                }
            }
        });

        return bookingView;
    }

    public void setBookingDoneInterface(BookingDoneInterface bookingDoneInterface) {
        this.bookingDoneInterface = bookingDoneInterface;
    }
}
