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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
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
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorName, providerNpi, officeName, officePhone, BookingManager.getBookingProfile(), BookingManager.getBookingAppointment(), BookingManager.isNewPatient(), BookingManager.isBookingForMe());

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
                        updateVisibility(false);
                        date.setText(DateUtil.getDateWords2FromUTC(BookingManager.getBookingAppointment().Time));
                        time.setText(DateUtil.getTime(BookingManager.getBookingAppointment().Time));
                        address.setText(CommonUtil.constructAddress(BookingManager.getBookingAppointment().FacilityAddress, null, BookingManager.getBookingAppointment().FacilityCity, BookingManager.getBookingAppointment().FacilityState, BookingManager.getBookingAppointment().FacilityZip));

                        if (doneInterface != null) {
                            doneInterface.onBookingSuccess();
                        }
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
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
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
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
                new Address(BookingManager.getBookingAppointment().FacilityAddress, null, BookingManager.getBookingAppointment().FacilityCity, BookingManager.getBookingAppointment().FacilityState, BookingManager.getBookingAppointment().FacilityZip, null)
        );
    }

    public void onClickShare() {
        CommonUtil.shareAppointment(
                getActivity(),
                BookingManager.getBookingAppointment().Time,
                BookingManager.getBookingProvider().getDisplayFullName(),
                BookingManager.getBookingOffice().getName(),
                new Address(BookingManager.getBookingOffice().getAddress1(), BookingManager.getBookingOffice().getAddress2(), BookingManager.getBookingOffice().getCity(), BookingManager.getBookingOffice().getState(), BookingManager.getBookingOffice().getZipCode(), null),
                BookingManager.getBookingOffice().getPhone(),
                BookingManager.getBookingProfile().reasonForVisit
        );
    }

    public void onClickAddToCalendar() {
        CommonUtil.addCalendarEvent(
                getActivity(),
                BookingManager.getBookingAppointment().Time,
                BookingManager.getBookingProvider().getDisplayFullName(),
                new Address(BookingManager.getBookingOffice().getAddress1(), BookingManager.getBookingOffice().getAddress2(), BookingManager.getBookingOffice().getCity(), BookingManager.getBookingOffice().getState(), BookingManager.getBookingOffice().getZipCode(), null),
                BookingManager.getBookingOffice().getPhone(),
                BookingManager.getBookingProfile().reasonForVisit
        );
    }
}
