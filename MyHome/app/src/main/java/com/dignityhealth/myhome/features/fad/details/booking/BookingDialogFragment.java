package com.dignityhealth.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.views.WrappingViewPager;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDialogFragment extends DialogFragment {
    public static final String BOOKING_DIALOG_TAG = "booking_dialog_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;

    View bookingView;

    public static BookingDialogFragment newInstance() {
        return new BookingDialogFragment();
    }

    public static BookingDialogFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingDialogFragment bookingFragment = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        //providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        bookingView = inflater.inflate(R.layout.book_dialog, container, false);

        Toolbar toolbar = (Toolbar) bookingView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.find_care));

        WrappingViewPager bookingViewPager = (WrappingViewPager) bookingView.findViewById(R.id.booking_dialog_view_pager);
        bookingViewPager.setAdapter(new BookingDialogAdapter(getContext()));

        return bookingView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }
}
