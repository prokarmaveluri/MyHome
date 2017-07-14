package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.Appointment;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingConfirmationFragment extends Fragment {
    public static final String BOOKING_CONFIRMATION_TAG = "booking_confirmation_tag";
    public static final String BOOKING_PROFILE_KEY = "booking_profile";
    public static final String BOOKING_APPOINTMENT_KEY = "booking_appointment";

    public BookingConfirmationInterface confirmationInterface;
    public BookingRefreshInterface refreshInterface;

    View bookingView;
    Profile bookingProfile;
    Appointment bookingAppointment;

    public static BookingConfirmationFragment newInstance() {
        return new BookingConfirmationFragment();
    }

    public static BookingConfirmationFragment newInstance(Profile bookingProfile, Appointment bookingAppointment) {
        BookingConfirmationFragment bookingFragment = new BookingConfirmationFragment();
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

        bookingView = inflater.inflate(R.layout.book_confirmation, container, false);

        Button book = (Button) bookingView.findViewById(R.id.book_confirmed);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectionUtil.isConnected(getActivity())) {
                    Toast.makeText(getActivity(),
                            R.string.no_network_msg,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (confirmationInterface != null) {
                    confirmationInterface.onClickBook();
                }
            }
        });

        TextView date = (TextView) bookingView.findViewById(R.id.date);
        TextView time = (TextView) bookingView.findViewById(R.id.time);
        TextView address = (TextView) bookingView.findViewById(R.id.address);
        TextView reason = (TextView) bookingView.findViewById(R.id.reason);

        date.setText(DateUtil.getDateWords2FromUTC(bookingAppointment.Time));
        time.setText(DateUtil.getTime(bookingAppointment.Time));
        address.setText(CommonUtil.constructAddress(bookingAppointment.FacilityAddress, null, bookingAppointment.FacilityCity, bookingAppointment.FacilityState, bookingAppointment.FacilityZip));
        reason.setText(bookingProfile.reasonForVisit);

        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(refreshInterface != null){
            refreshInterface.onRefreshView(true);
        }
    }

    public void setConfirmationInterface(BookingConfirmationInterface confirmationInterface) {
        this.confirmationInterface = confirmationInterface;
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }
}
