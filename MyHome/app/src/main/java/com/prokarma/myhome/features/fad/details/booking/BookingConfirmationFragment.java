package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
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
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.review_your_booking));
        getActivity().getWindow().getDecorView()
                .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        getActivity().getWindow().getDecorView().announceForAccessibility(getResources().getString(R.string.review_your_booking));

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

        date.setText(DateUtil.getDateWords2FromUTC(BookingManager.getBookingAppointment().getTime()));
        time.setText(DateUtil.getTime(BookingManager.getBookingAppointment().getTime()) + " " + DateUtil.getReadableTimeZone(BookingManager.getBookingOffice().getAddresses().get(0).getState(), BookingManager.getBookingAppointment().getTime()));
        address.setText(CommonUtil.constructAddress(BookingManager.getBookingOffice().getAddresses().get(0).getAddress(), null, BookingManager.getBookingOffice().getAddresses().get(0).getCity(), BookingManager.getBookingOffice().getAddresses().get(0).getState(), BookingManager.getBookingOffice().getAddresses().get(0).getZip()));
        reason.setText(BookingManager.getBookingProfile().reasonForVisit);

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
