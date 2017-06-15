package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dignityhealth.myhome.R;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectPersonFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_select_person_tag";

    public BookingSelectPersonInterface selectPersonInterface;

    View bookingView;

    public static BookingSelectPersonFragment newInstance() {
        return new BookingSelectPersonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bookingView = inflater.inflate(R.layout.book_select_person, container, false);

        final Button buttonMe = (Button) bookingView.findViewById(R.id.book_me);
        final Button buttonOther = (Button) bookingView.findViewById(R.id.book_other);
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
