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

public class BookingSelectStatusFragment extends Fragment {
    public static final String BOOKING_SELECT_STATUS_TAG = "booking_select_status_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingSelectStatusInterface selectStatusInterface;

    View bookingView;

    public static BookingSelectStatusFragment newInstance() {
        return new BookingSelectStatusFragment();
    }

    public static BookingSelectStatusFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingSelectStatusFragment bookingFragment = new BookingSelectStatusFragment();
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
        bookingView = inflater.inflate(R.layout.book_select_status, container, false);

        final ToggleButton buttonNew = (ToggleButton) bookingView.findViewById(R.id.book_new);
        final ToggleButton buttonExisting = (ToggleButton) bookingView.findViewById(R.id.book_existing);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectStatusInterface != null) {
                    selectStatusInterface.onStatusSelected(true);
                }
            }
        });

        buttonExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectStatusInterface != null) {
                    selectStatusInterface.onStatusSelected(false);
                }
            }
        });

        return bookingView;
    }

    public void setSelectStatusInterface(BookingSelectStatusInterface selectStatusInterface) {
        this.selectStatusInterface = selectStatusInterface;
    }
}
