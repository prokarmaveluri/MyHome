package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectCalendarFragment extends Fragment {
    public static final String BOOKING_SELECT_CALENDAR_TAG = "booking_select_calendar_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;

    View bookingView;

    public static BookingSelectCalendarFragment newInstance() {
        return new BookingSelectCalendarFragment();
    }

    public static BookingSelectCalendarFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingSelectCalendarFragment bookingFragment = new BookingSelectCalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        bookingView = inflater.inflate(R.layout.book_calendar, container, false);
        return bookingView;
    }
}
