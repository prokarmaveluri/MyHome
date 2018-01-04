package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingMessageFragment extends Fragment {
    public static final String BOOKING_MESSAGE_TAG = "booking_message_tag";

    public BookingRefreshInterface refreshInterface;

    View bookingView;

    public static BookingMessageFragment newInstance() {
        return new BookingMessageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bookingView = inflater.inflate(R.layout.book_message, container, false);
        CommonUtil.setTitle(getActivity(), getResources().getString(R.string.find_care), true);
        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }
}
