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

public class BookingFragment extends Fragment {
    public static final String BOOKING_TAG = "booking_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";
    public static final String BOOKING_PAGE_KEY = "booking_page";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingPage bookingPage;

    View bookingView;

    public static BookingFragment newInstance() {
        BookingFragment bookingFragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BOOKING_PAGE_KEY, BookingPage.SELECT_PERSON);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    public static BookingFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingFragment bookingFragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        args.putSerializable(BOOKING_PAGE_KEY, BookingPage.SELECT_PERSON);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    public static BookingFragment newInstance(BookingPage bookingPage, ProviderDetailsResponse providerDetailsResponse) {
        BookingFragment bookingFragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        args.putSerializable(BOOKING_PAGE_KEY, bookingPage);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        bookingPage = (BookingPage) args.getSerializable(BOOKING_PAGE_KEY);

        switch (bookingPage) {
            case SELECT_PERSON:
                bookingView = inflater.inflate(R.layout.book_select_person, container, false);
                break;
            case SELECT_STATUS:
                bookingView = inflater.inflate(R.layout.settings, container, false);
                break;
            case SELECT_DAY_LONG:
                bookingView = inflater.inflate(R.layout.book_calendar, container, false);
                break;
            case SELECT_DAY_SHORT:
                bookingView = inflater.inflate(R.layout.book_calendar, container, false);
                break;
            case SELECT_MONTH:
                bookingView = inflater.inflate(R.layout.book_calendar, container, false);
                break;
            case SELECT_NEXT_AVAILABLE:
                bookingView = inflater.inflate(R.layout.book_calendar, container, false);
                break;
        }

        return bookingView;
    }
}
