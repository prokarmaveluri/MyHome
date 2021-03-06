package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDoneFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_done_tag";
    public static final String DOCTOR_NAME_KEY = "doctor_name";
    public static final String DOCTOR_NPI_KEY = "doctor_npi";
    public static final String OFFICE_NAME_KEY = "office_name_key";
    public static final String OFFICE_PHONE_KEY = "office_phone_key";

    public BookingDoneInterface doneInterface;
    public BookingRefreshInterface refreshInterface;

    View bookingView;
    TextView bookSuccess;
    TextView date;
    TextView time;
    TextView address;
    TextView directions;
    TextView share;
    TextView calendar;
    ImageView directionsIcon;
    ImageView shareIcon;
    ImageView calendarIcon;
    ProgressBar progressBar;

    String doctorName;
    String providerNpi;
    String officeName;
    String officePhone;

    public static BookingDoneFragment newInstance() {
        return new BookingDoneFragment();
    }

    public static BookingDoneFragment newInstance(String doctorName, String providerNpi, String officeName, String officePhone) {
        BookingDoneFragment bookingFragment = new BookingDoneFragment();
        Bundle args = new Bundle();
        args.putString(DOCTOR_NAME_KEY, doctorName);
        args.putString(DOCTOR_NPI_KEY, providerNpi);
        args.putString(OFFICE_NAME_KEY, officeName);
        args.putString(OFFICE_PHONE_KEY, officePhone);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            doctorName = args.getString(DOCTOR_NAME_KEY);
            providerNpi = args.getString(DOCTOR_NPI_KEY);
            officeName = args.getString(OFFICE_NAME_KEY);
            officePhone = args.getString(OFFICE_PHONE_KEY);
        }

        bookingView = inflater.inflate(R.layout.book_done, container, false);
        progressBar = (ProgressBar) bookingView.findViewById(R.id.progress_bar);

        bookSuccess = (TextView) bookingView.findViewById(R.id.book_success);
        date = (TextView) bookingView.findViewById(R.id.date);
        time = (TextView) bookingView.findViewById(R.id.time);
        address = (TextView) bookingView.findViewById(R.id.address);
        directions = (TextView) bookingView.findViewById(R.id.directions_text);
        share = (TextView) bookingView.findViewById(R.id.share_text);
        calendar = (TextView) bookingView.findViewById(R.id.calendar_text);
        directionsIcon = (ImageView) bookingView.findViewById(R.id.directions_icon);
        shareIcon = (ImageView) bookingView.findViewById(R.id.share_icon);
        calendarIcon = (ImageView) bookingView.findViewById(R.id.calendar_icon);

        scheduleAppointment();

        directionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDirections();
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDirections();
            }
        });

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShare();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShare();
            }
        });

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddToCalendar();
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddToCalendar();
            }
        });

        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setDoneInterface(BookingDoneInterface doneInterface) {
        this.doneInterface = doneInterface;
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    private void updateVisibility(boolean isLoading) {
        bookSuccess.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        date.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        time.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        address.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        directions.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        share.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        calendar.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        directionsIcon.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        shareIcon.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
        calendarIcon.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void scheduleAppointment() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorName, providerNpi, BookingManager.getScheduleId(), BookingManager.getBookingLocation(), BookingManager.getBookingProfile(), BookingManager.getBookingAppointment(), BookingManager.getBookingAppointmentType(), BookingManager.isBookingForMe());

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Timber.i("Request = " + request);
        Timber.i("Request JSON = " + gson.toJson(request));
        updateVisibility(true);
        NetworkManager.getInstance().createAppointment(AuthManager.getInstance().getBearerToken(), request).enqueue(new Callback<CreateAppointmentResponse>() {
            @Override
            public void onResponse(Call<CreateAppointmentResponse> call, Response<CreateAppointmentResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        Timber.i("Request JSON = " + gson.toJson(response.body()));
                        CommonUtil.setTitle(getActivity(), getString(R.string.appointment_confirmed), true);
                        updateVisibility(false);
                        date.setText(DateUtil.getDateWords2FromUTC(BookingManager.getBookingAppointment().getTime()));
                        time.setText(DateUtil.getTime(BookingManager.getBookingAppointment().getTime()) + " " + DateUtil.getReadableTimeZone(BookingManager.getBookingLocation().getState(), BookingManager.getBookingAppointment().getTime()));
                        address.setText(CommonUtil.constructAddress(BookingManager.getBookingLocation().getAddress(), null, BookingManager.getBookingLocation().getCity(), BookingManager.getBookingLocation().getState(), BookingManager.getBookingLocation().getZip()));

                        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
                            date.setContentDescription(getString(R.string.appointment_date) + " " + date.getText());
                            time.setContentDescription(getString(R.string.appointment_time) + " " + time.getText());
                            address.setContentDescription(getString(R.string.location) + AddressUtil.getAddressForAccessibilityUser(BookingManager.getBookingLocation()));
                        }
                        if (doneInterface != null) {
                            doneInterface.onBookingSuccess();
                        }
                        ProfileManager.setAppointments(null);
                        coachmarkBookingDone();
                    } else {
                        Timber.e("scheduleAppointment Response, but not successful?\n" + response);
                        ApiErrorUtil.getInstance().createAppointmentError(getContext(), bookingView, response);
                        CommonUtil.setTitle(getActivity(), getString(R.string.unable_to_book), true);
                        updateVisibility(false);

                        if (doneInterface != null) {
                            doneInterface.onBookingFailed(response.message());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateAppointmentResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("scheduleAppointment Something failed! :/");
                    Timber.e("Throwable = " + t);
                    ApiErrorUtil.getInstance().createAppointmentFailed(getContext(), bookingView, t);
                    CommonUtil.setTitle(getActivity(), getString(R.string.unable_to_book), true);
                    updateVisibility(false);

                    if (doneInterface != null) {
                        doneInterface.onBookingFailed(t.getMessage());
                    }
                }
            }
        });

    }

    public void onClickDirections() {
        CommonUtil.getDirections(
                getActivity(),
                new Address(BookingManager.getBookingLocation().getAddress(), null, BookingManager.getBookingLocation().getCity(), BookingManager.getBookingLocation().getState(), BookingManager.getBookingLocation().getZip(), null)
        );
    }

    public void onClickShare() {
        CommonUtil.shareAppointment(
                getActivity(),
                BookingManager.getBookingAppointment().getTime(),
                BookingManager.getBookingProvider().getDisplayName(),
                BookingManager.getBookingLocation().getName(),
                new Address(BookingManager.getBookingLocation().getAddress(), "", BookingManager.getBookingLocation().getCity(), BookingManager.getBookingLocation().getState(), BookingManager.getBookingLocation().getZip(), null),
                BookingManager.getBookingLocation().getPhones().get(0),
                BookingManager.getBookingProfile().reasonForVisit
        );
    }

    public void onClickAddToCalendar() {
        CommonUtil.addCalendarEvent(
                getActivity(),
                BookingManager.getBookingAppointment().getTime(),
                BookingManager.getBookingProvider().getDisplayName(),
                new Address(BookingManager.getBookingLocation().getAddress(), "", BookingManager.getBookingLocation().getCity(), BookingManager.getBookingLocation().getState(), BookingManager.getBookingLocation().getZip(), null),
                BookingManager.getBookingLocation().getPhones().get(0),
                BookingManager.getBookingProfile().reasonForVisit
        );
    }

    private void coachmarkBookingDone() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        if (shareIcon != null && shareIcon.getVisibility() != View.VISIBLE)
            return;
        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.BOOKING_DONE_SKIP_COACH_MARKS);
        if (skip)
            return;
        TapTargetView.showFor(getActivity(),
                TapTarget.forView(shareIcon, getString(R.string.coachmark_share_apt))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.BOOKING_DONE_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.BOOKING_DONE_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }
}
