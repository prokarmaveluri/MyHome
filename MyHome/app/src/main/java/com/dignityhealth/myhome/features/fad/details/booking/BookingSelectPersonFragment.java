package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectPersonFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_select_person_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingSelectPersonInterface selectPersonInterface;

    View bookingView;

    public static BookingSelectPersonFragment newInstance() {
        return new BookingSelectPersonFragment();
    }

    public static BookingSelectPersonFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingSelectPersonFragment bookingFragment = new BookingSelectPersonFragment();
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
        bookingView = inflater.inflate(R.layout.book_select_person, container, false);

        final ToggleButton buttonMe = (ToggleButton) bookingView.findViewById(R.id.book_me);
        final ToggleButton buttonOther = (ToggleButton) bookingView.findViewById(R.id.book_other);
        buttonMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPersonInterface != null) {
                    selectPersonInterface.onPersonSelected(true);
                }
            }
        });

        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPersonInterface != null) {
                    selectPersonInterface.onPersonSelected(false);
                }
            }
        });


        return bookingView;
    }

    public void setSelectPersonInterface(BookingSelectPersonInterface selectPersonInterface) {
        this.selectPersonInterface = selectPersonInterface;
    }
}
