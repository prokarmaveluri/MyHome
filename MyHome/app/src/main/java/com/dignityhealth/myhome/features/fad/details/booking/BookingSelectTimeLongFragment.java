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

public class BookingSelectTimeLongFragment extends Fragment {
    public static final String BOOKING_SELECT_TIME_LONG_TAG = "booking_select_time_long_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;

    View bookingView;

    public static BookingSelectTimeLongFragment newInstance() {
        return new BookingSelectTimeLongFragment();
    }

    public static BookingSelectTimeLongFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingSelectTimeLongFragment bookingFragment = new BookingSelectTimeLongFragment();
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
        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        return bookingView;
    }
}
