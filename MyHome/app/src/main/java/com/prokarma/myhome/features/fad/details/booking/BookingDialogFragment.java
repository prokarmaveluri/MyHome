package com.prokarma.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.views.WrappingViewPager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDialogFragment extends DialogFragment implements BookingDialogToolbarInterface, BookingSaveProfileInterface, DialogInterface {
    public static final String BOOKING_DIALOG_TAG = "booking_dialog_tag";
    public static final String SCHEDULE_ID_KEY = "schedule_id";
    public static final String AUTOPOPULATE_INSURANCE_PLAN_KEY = "autopopulate_insurance_plan";
    public static final String IS_BOOKING_FOR_ME_KEY = "is_booking_for_me";
    public static final String BOOKING_PROFILE = "booking_profile";

    public BookingDialogInterface bookingDialogInterface;

    View bookingView;
    WrappingViewPager bookingViewPager;
    Toolbar toolbar;

    private String scheduleId;
    private boolean autoPopulateInsurancePlan;

    public static BookingDialogFragment newInstance() {
        return new BookingDialogFragment();
    }

    public static BookingDialogFragment newInstance(String scheduleId, boolean autoPopulateInsurancePlan) {
        BookingDialogFragment bookingFragment = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putString(SCHEDULE_ID_KEY, scheduleId);
        args.putBoolean(AUTOPOPULATE_INSURANCE_PLAN_KEY, autoPopulateInsurancePlan);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogTheame);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            scheduleId = args.getString(SCHEDULE_ID_KEY);
            autoPopulateInsurancePlan = args.getBoolean(AUTOPOPULATE_INSURANCE_PLAN_KEY);
        }

        //providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
        bookingView = inflater.inflate(R.layout.book_dialog, container, false);

        bookingViewPager = (WrappingViewPager) bookingView.findViewById(R.id.booking_dialog_view_pager);
        bookingViewPager.setSwipeAllowed(false);
        bookingViewPager.setAdapter(new BookingDialogAdapter(getActivity(), this, BookingManager.getBookingProfile() != null, autoPopulateInsurancePlan, BookingManager.getBookingProfile()));

        toolbar = (Toolbar) bookingView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.find_care));
        toolbar.inflateMenu(R.menu.booking_dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.next_page:
                        if (((BookingDialogAdapter) bookingViewPager.getAdapter()).validateForm(0)) {
                            bookingViewPager.setCurrentItem(bookingViewPager.getCurrentItem() + 1, true);
                        }
                        break;
                    case R.id.finish_dialog:
                        if (((BookingDialogAdapter) bookingViewPager.getAdapter()).validateForm(1)) {
                            finishBooking();
                        }
                        break;
                }
                return true;
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.mipmap.xblue));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        getValidationRules();

        return bookingView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BookingDialog dialog = new BookingDialog(getContext(), getTheme());
        dialog.setDialogInterface(this);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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
                    toolbar.getMenu().findItem(R.id.next_page).setVisible(false);
                    toolbar.getMenu().findItem(R.id.finish_dialog).setVisible(true);
                }
                break;
        }
    }

    public void setBookingDialogInterface(BookingDialogInterface bookingDialogInterface) {
        this.bookingDialogInterface = bookingDialogInterface;
    }

    private void getValidationRules() {
        NetworkManager.getInstance().getValidationRules(scheduleId, "insurance,schedule-properties").enqueue(new Callback<RegValidationResponse>() {
            @Override
            public void onResponse(Call<RegValidationResponse> call, Response<RegValidationResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ((BookingDialogAdapter) bookingViewPager.getAdapter()).setupInsurancePlanSpinner(response.body());
                    ((BookingDialogAdapter) bookingViewPager.getAdapter()).setupValidationRules(response.body());
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                    getDialog().dismiss();
                    Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegValidationResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
                getDialog().dismiss();
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void finishBooking() {
        final Profile formsProfile = ((BookingDialogAdapter) bookingViewPager.getAdapter()).getProfile();   //Profile from the booking flow
        final Profile savingProfile = Profile.copySansBookingInfo(formsProfile);    //Profile to save
        savingProfile.setEmail(ProfileManager.getProfile().email);

        if (BookingManager.isBookingForMe() && !formsProfile.shouldAskToSave(ProfileManager.getProfile())) {
            //Not equal and all the data empty
            ProfileManager.updateProfile(AuthManager.getInstance().getBearerToken(), savingProfile);
            this.getDialog().dismiss();
        } else if (BookingManager.isBookingForMe() && formsProfile.shouldAskToSave(ProfileManager.getProfile())) {
            //Send the profile for saving, but without the booking info-specfic fields
            BookingSaveProfileDialog dialog = BookingSaveProfileDialog.newInstance(savingProfile);
            dialog.setSaveProfileInterface(this);
            dialog.show(getChildFragmentManager(), BookingSaveProfileDialog.BOOKING_SAVE_PROFILE_DIALOG_TAG);
        } else {
            this.getDialog().dismiss();
        }

        if (bookingDialogInterface != null) {
            bookingDialogInterface.onBookingDialogFinished(formsProfile);
        }
    }

    @Override
    public void onPromptDismissed(boolean isProfileUpdated) {
        this.getDialog().dismiss();
    }

    @Override
    public void onBackPressed() {
        if (bookingViewPager.getCurrentItem() == 0) {
            this.dismiss();
        } else {
            bookingViewPager.setCurrentItem(bookingViewPager.getCurrentItem() - 1, true);
        }
    }
}
