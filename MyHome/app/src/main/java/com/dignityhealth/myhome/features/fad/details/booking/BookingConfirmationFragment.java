package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingConfirmationFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_confirmation_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingConfirmationInterface bookingConfirmationInterface;

    View bookingView;

    public static BookingConfirmationFragment newInstance() {
        return new BookingConfirmationFragment();
    }

    public static BookingConfirmationFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingConfirmationFragment bookingFragment = new BookingConfirmationFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        bookingView = inflater.inflate(R.layout.book_confirmation, container, false);

        Button book = (Button) bookingView.findViewById(R.id.book_confirmed);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookingConfirmationInterface != null){
                    bookingConfirmationInterface.onClickBook();
                }
            }
        });

        return bookingView;
    }

    public void setBookingConfirmationInterface(BookingConfirmationInterface bookingConfirmationInterface) {
        this.bookingConfirmationInterface = bookingConfirmationInterface;
    }
}
