package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingFragment extends BaseFragment {
    public static final String BOOKING_TAG = "booking_tag";
    View bookingView;

    public static BookingFragment newInstance() {
        return new BookingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bookingView = inflater.inflate(R.layout.settings, container, false);
        return bookingView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return null;
    }
}
