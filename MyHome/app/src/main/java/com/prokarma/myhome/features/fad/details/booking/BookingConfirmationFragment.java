package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingConfirmationFragment extends Fragment {
    public static final String BOOKING_CONFIRMATION_TAG = "booking_confirmation_tag";

    public BookingConfirmationInterface confirmationInterface;
    public BookingRefreshInterface refreshInterface;

    View bookingView;

    public static BookingConfirmationFragment newInstance() {
        return new BookingConfirmationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bookingView = inflater.inflate(R.layout.book_confirmation, container, false);
        ViewCompat.setImportantForAccessibility(getActivity().getWindow().getDecorView(),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        CommonUtil.setTitle(getActivity(), getResources().getString(R.string.review_your_booking), false);
        Button book = (Button) bookingView.findViewById(R.id.book_confirmed);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectionUtil.isConnected(getActivity())) {
                    CommonUtil.showToast(getActivity(),
                            getString(R.string.no_network_msg));
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

        date.setText(DateUtil.getDateWords2FromUTC(BookingManager.getBookingAppointment().getTime()));
        time.setText(DateUtil.getTime(BookingManager.getBookingAppointment().getTime()) + " " + DateUtil.getReadableTimeZone(BookingManager.getBookingLocation().getState(), BookingManager.getBookingAppointment().getTime()));

        address.setText(CommonUtil.constructAddress(
                BookingManager.getBookingLocation().getAddress(),
                null,
                BookingManager.getBookingLocation().getCity(),
                BookingManager.getBookingLocation().getState(),
                BookingManager.getBookingLocation().getZip()));

        String addressContentDescription = BookingManager.getBookingLocation() != null ?
                AddressUtil.getAddressForAccessibilityUser(BookingManager.getBookingLocation())
                : getString(R.string.address_unknown);
        address.setContentDescription(getString(R.string.location) + addressContentDescription);

        if (BookingManager.getBookingProfile() != null) {
            reason.setText(BookingManager.getBookingProfile().reasonForVisit);
            reason.setContentDescription(getString(R.string.appointment_reason) + BookingManager.getBookingProfile().reasonForVisit);
        }

        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            date.setContentDescription(getString(R.string.appointment_date) + " " + date.getText());
            time.setContentDescription(getString(R.string.appointment_time) + " " + time.getText());
            address.setContentDescription(getString(R.string.location) + AddressUtil.getAddressForAccessibilityUser(BookingManager.getBookingLocation()));
        }

        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
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
