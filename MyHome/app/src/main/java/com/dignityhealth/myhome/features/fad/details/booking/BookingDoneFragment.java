package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.Appointment;
import com.dignityhealth.myhome.features.fad.Office;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.dignityhealth.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingDoneFragment extends Fragment {
    public static final String BOOKING_SELECT_PERSON_TAG = "booking_done_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details";
    public static final String CURRENT_OFFICE_KEY = "current_office";
    public static final String BOOKING_PROFILE_KEY = "booking_profile";
    public static final String BOOKING_APPOINTMENT_KEY = "booking_appointment";
    public static final String IS_NEW_PATIENT_KEY = "is_new_patient";
    public static final String IS_BOOKING_FOR_ME = "is_booking_for_me";

    public BookingDoneInterface bookingDoneInterface;

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

    ProviderDetailsResponse providerDetailsResponse;
    Office currentOffice;
    Profile bookingProfile;
    Appointment bookingAppointment;
    boolean isNewPatient;
    boolean isBookingForMe;

    public static BookingDoneFragment newInstance() {
        return new BookingDoneFragment();
    }

    public static BookingDoneFragment newInstance(ProviderDetailsResponse providerDetailsResponse, Office currentOffice, Profile bookingProfile, Appointment bookingAppointment, boolean isNewPatient, boolean isBookingForMe) {
        BookingDoneFragment bookingFragment = new BookingDoneFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        args.putParcelable(CURRENT_OFFICE_KEY, currentOffice);
        args.putParcelable(BOOKING_PROFILE_KEY, bookingProfile);
        args.putParcelable(BOOKING_APPOINTMENT_KEY, bookingAppointment);
        args.putBoolean(IS_NEW_PATIENT_KEY, isNewPatient);
        args.putBoolean(IS_BOOKING_FOR_ME, isBookingForMe);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            providerDetailsResponse = args.getParcelable(PROVIDER_DETAILS_RESPONSE_KEY);
            currentOffice = args.getParcelable(CURRENT_OFFICE_KEY);
            bookingProfile = args.getParcelable(BOOKING_PROFILE_KEY);
            bookingAppointment = args.getParcelable(BOOKING_APPOINTMENT_KEY);
            isNewPatient = args.getBoolean(IS_NEW_PATIENT_KEY);
            isBookingForMe = args.getBoolean(IS_BOOKING_FOR_ME);
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

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickDirections();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickShare();
                }
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookingDoneInterface != null) {
                    bookingDoneInterface.onClickAddToCalendar();
                }
            }
        });

        return bookingView;
    }

    public void setBookingDoneInterface(BookingDoneInterface bookingDoneInterface) {
        this.bookingDoneInterface = bookingDoneInterface;
    }

    private void updateVisibility(boolean isLoading) {
        bookSuccess.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        date.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        time.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        address.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        directions.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        share.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        calendar.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        directionsIcon.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        shareIcon.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        calendarIcon.setVisibility(isLoading ? View.GONE : View.VISIBLE);

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void scheduleAppointment() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(providerDetailsResponse, currentOffice, bookingProfile, bookingAppointment, isNewPatient, isBookingForMe);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Timber.i("Request = " + request);
        Timber.i("Request JSON = " + gson.toJson(request));
        updateVisibility(true);
        NetworkManager.getInstance().createAppointment(AuthManager.getInstance().getBearerToken(), request).enqueue(new Callback<CreateAppointmentResponse>() {
            @Override
            public void onResponse(Call<CreateAppointmentResponse> call, Response<CreateAppointmentResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);

                        updateVisibility(false);
                        date.setText(DateUtil.getDateWords2FromUTC(bookingAppointment.Time));
                        time.setText(DateUtil.getTime(bookingAppointment.Time));
                        address.setText(CommonUtil.constructAddress(bookingAppointment.FacilityAddress, null, bookingAppointment.FacilityCity, bookingAppointment.FacilityState, bookingAppointment.FacilityZip));

                        if (bookingDoneInterface != null) {
                            bookingDoneInterface.onBookingSuccess();
                        }
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        updateVisibility(false);

                        if (bookingDoneInterface != null) {
                            bookingDoneInterface.onBookingFailed(response.message());
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

                    if (bookingDoneInterface != null) {
                        bookingDoneInterface.onBookingFailed(t.getMessage());
                    }
                }
            }
        });

    }

}
