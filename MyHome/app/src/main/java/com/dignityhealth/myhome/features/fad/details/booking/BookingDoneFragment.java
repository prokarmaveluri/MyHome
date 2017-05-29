package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDoneFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_done_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingDoneInterface bookingDoneInterface;

    View bookingView;

    public static BookingDoneFragment newInstance() {
        return new BookingDoneFragment();
    }

    public static BookingDoneFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingDoneFragment bookingFragment = new BookingDoneFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        bookingView = inflater.inflate(R.layout.book_done, container, false);

        TextView directions = (TextView) bookingView.findViewById(R.id.directions_text);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookingDoneInterface != null){
                    bookingDoneInterface.onClickDirections();
                }
            }
        });

        TextView share = (TextView) bookingView.findViewById(R.id.share_text);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookingDoneInterface != null){
                    bookingDoneInterface.onClickShare();
                }
            }
        });

        TextView calendar = (TextView) bookingView.findViewById(R.id.calendar_text);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookingDoneInterface != null){
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
