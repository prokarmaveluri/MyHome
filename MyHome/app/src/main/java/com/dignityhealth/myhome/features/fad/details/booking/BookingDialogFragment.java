package com.dignityhealth.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.views.WrappingViewPager;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDialogFragment extends DialogFragment implements BookingDialogToolbarInterface, BookingSaveProfileInterface {
    public static final String BOOKING_DIALOG_TAG = "booking_dialog_tag";
    public static final String IS_BOOKING_FOR_ME_KEY = "is_booking_for_me";

    public BookingDialogInterface bookingDialogInterface;

    View bookingView;
    WrappingViewPager bookingViewPager;
    Toolbar toolbar;

    private boolean isBookingForMe = true;

    public static BookingDialogFragment newInstance() {
        return new BookingDialogFragment();
    }

    public static BookingDialogFragment newInstance(boolean isBookingForMe) {
        BookingDialogFragment bookingFragment = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_BOOKING_FOR_ME_KEY, isBookingForMe);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args != null){
            isBookingForMe = args.getBoolean(IS_BOOKING_FOR_ME_KEY);
        }

        //providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        bookingView = inflater.inflate(R.layout.book_dialog, container, false);

        toolbar = (Toolbar) bookingView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.find_care));
        toolbar.inflateMenu(R.menu.booking_dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.next_page:
                        bookingViewPager.setCurrentItem(bookingViewPager.getCurrentItem() + 1, true);
                        break;
                    case R.id.finish_dialog:
                        finishBooking();
                        break;
                }
                return true;
            }
        });

        bookingViewPager = (WrappingViewPager) bookingView.findViewById(R.id.booking_dialog_view_pager);
        bookingViewPager.setAdapter(new BookingDialogAdapter(getContext(), this, isBookingForMe, ProfileManager.getProfile()));

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

    @Override
    public void setToolbarMenu(int position) {
        switch (position) {
            case 0:
                //Insurance Page
                if (toolbar != null && toolbar.getMenu() != null) {
                    toolbar.getMenu().findItem(R.id.next_page).setVisible(true);
                    toolbar.getMenu().findItem(R.id.finish_dialog).setVisible(false);
                }
                break;
            case 1:
                //Personal Page
                if (toolbar != null && toolbar.getMenu() != null) {
                    toolbar.getMenu().findItem(R.id.next_page).setVisible(true);
                    toolbar.getMenu().findItem(R.id.finish_dialog).setVisible(false);
                }
                break;
            case 2:
                //Dynamic Page
                if (toolbar != null && toolbar.getMenu() != null) {
                    toolbar.getMenu().findItem(R.id.next_page).setVisible(false);
                    toolbar.getMenu().findItem(R.id.finish_dialog).setVisible(true);
                }
                break;
        }
    }

    public void setBookingDialogInterface(BookingDialogInterface bookingDialogInterface) {
        this.bookingDialogInterface = bookingDialogInterface;
    }

    private void finishBooking() {
        if (bookingDialogInterface != null) {
            bookingDialogInterface.onBookingDialogFinished();
        }

        final Profile formsProfile = ((BookingDialogAdapter) bookingViewPager.getAdapter()).getProfile();

        if (!formsProfile.equalsSansEmails(ProfileManager.getProfile())) {
            BookingSaveProfileDialog dialog = BookingSaveProfileDialog.newInstance(formsProfile);
            dialog.setSaveProfileInterface(this);
            dialog.show(getChildFragmentManager(), "BookingSaveProfileDialog");
        } else {
            this.getDialog().dismiss();
        }
    }

    @Override
    public void onPromptDismissed(boolean isProfileUpdated) {
        this.getDialog().dismiss();
    }
}
