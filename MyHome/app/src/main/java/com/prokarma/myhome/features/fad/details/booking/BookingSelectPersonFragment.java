package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectPersonFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_select_person_tag";

    public BookingSelectPersonInterface selectPersonInterface;
    public BookingRefreshInterface refreshInterface;

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

        CommonUtil.setTitle(getActivity(), getResources().getString(R.string.provider_info), true);
        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setSelectPersonInterface(BookingSelectPersonInterface selectPersonInterface) {
        this.selectPersonInterface = selectPersonInterface;
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }
}
