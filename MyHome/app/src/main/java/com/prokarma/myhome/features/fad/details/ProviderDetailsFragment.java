package com.prokarma.myhome.features.fad.details;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.features.fad.Appointment;
import com.prokarma.myhome.features.fad.Office;
import com.prokarma.myhome.features.fad.Provider;
import com.prokarma.myhome.features.fad.details.booking.BookingBackButton;
import com.prokarma.myhome.features.fad.details.booking.BookingConfirmationFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingConfirmationInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDateHeaderInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDialogFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingDialogInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDoneFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingDoneInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectCalendarFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectPersonFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectPersonInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectStatusFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectStatusInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectTimeFragment;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DeviceDisplayManager;
import com.prokarma.myhome.utils.MapUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProviderDetailsFragment extends BaseFragment implements OnMapReadyCallback, BookingSelectPersonInterface, BookingSelectStatusInterface, BookingDateHeaderInterface, BookingDialogInterface, BookingConfirmationInterface, BookingDoneInterface, BookingBackButton {
    public static final String PROVIDER_KEY = "PROVIDER_KEY";
    public static final String PROVIDER_DETAILS_TAG = "provider_details_tag";

    private Provider provider;
    private ProviderDetailsResponse providerDetailsResponse;

    private boolean isBookingAppointment = false;

    private Office currentOffice;

    private SupportMapFragment myMap;
    private View providerDetailsView;
    private CircularImageView doctorImage;
    private TextView name;
    private TextView speciality;
    private TextView address;
    private TextView phone;
    private Button bookAppointment;
    private ExpandableLinearLayout expandableLinearLayout;

    private LinearLayout footerLayout;
    private ProgressBar statsProgressBar;
    private TextView statsUnavailable;
    private RelativeLayout statsView;

    //stats profile
    private LinearLayout statsProfileView;
    private TextView acceptingNewPatients;
    private TextView languages;
    private TextView gender;
    private TextView experience;
    private TextView philosophy;
    private RecyclerView locations;
    private TextView locationsLabel;

    //stats Experience
    private LinearLayout statsExperienceView;
    private TextView certificationsLabel;
    private TextView certifications;
    private TextView awardsLabel;
    private TextView awards;

    //stats Education
    private LinearLayout statsEducationView;
    private RecyclerView educationList;

    private GoogleMap providerMap;
    private ArrayList<Marker> markers = new ArrayList<>();

    //Booking
    private BookingDialogFragment bookingRegistrationDialog;
    private Date bookingDate;
    private boolean isBookingForMe = true;
    private boolean isNewPatient = false;
    private Appointment bookedAppointment;
    private Profile bookingProfile;

    public ProviderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderDetailsFragment.
     */
    public static ProviderDetailsFragment newInstance() {
        ProviderDetailsFragment fragment = new ProviderDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            provider = getArguments().getParcelable(PROVIDER_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RecentlyViewedDataSourceDB.getInstance().createEntry(provider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        providerDetailsView = inflater.inflate(R.layout.fragment_provider_details, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.find_care));

        myMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.provider_map));
        myMap.getMapAsync(this);

        doctorImage = (CircularImageView) providerDetailsView.findViewById(R.id.doctor_image);
        name = (TextView) providerDetailsView.findViewById(R.id.doctor_name);
        speciality = (TextView) providerDetailsView.findViewById(R.id.speciality);
        address = (TextView) providerDetailsView.findViewById(R.id.facility_address);
        phone = (TextView) providerDetailsView.findViewById(R.id.phone);

        footerLayout = (LinearLayout) providerDetailsView.findViewById(R.id.provider_details_footer);
        statsProgressBar = (ProgressBar) footerLayout.findViewById(R.id.stats_progress_bar);
        statsUnavailable = (TextView) footerLayout.findViewById(R.id.stats_unavailable);
        statsView = (RelativeLayout) footerLayout.findViewById(R.id.stats_view);
        statsProfileView = (LinearLayout) footerLayout.findViewById(R.id.stats_profile);
        statsEducationView = (LinearLayout) footerLayout.findViewById(R.id.stats_education);
        statsExperienceView = (LinearLayout) footerLayout.findViewById(R.id.stats_experience);

        getProviderDetails();

        expandableLinearLayout = (ExpandableLinearLayout) providerDetailsView.findViewById(R.id.expandable_layout);
        bookAppointment = (Button) providerDetailsView.findViewById(R.id.book_appointment);

        setupInitialView();
        return providerDetailsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROVIDER_DETAILS;
    }

    private void setupInitialView() {
        if (providerDetailsView == null) {
            return;
        }

        String url = provider.getImageUrl() != null ? provider.getImageUrl().replace(DeviceDisplayManager.W60H80, DeviceDisplayManager.W120H160) : null;
        Picasso.with(getActivity())
                .load(url)
                .into(doctorImage);

        name.setText(provider.getDisplayFullName() != null ? provider.getDisplayFullName() : getString(R.string.name_unknown));
        speciality.setText(provider.getSpecialties() != null ? provider.getSpecialties().get(0) : getString(R.string.specialities_unknown));
        address.setText(provider.getOffices() != null ? provider.getOffices().get(0).getAddress1() + "\n" + provider.getOffices().get(0).getAddress() : getString(R.string.address_unknown));
        phone.setText(provider.getOffices() != null ? CommonUtil.constructPhoneNumber(provider.getOffices().get(0).getPhone()) : getString(R.string.phone_number_unknown));
        currentOffice = provider.getOffices() != null ? provider.getOffices().get(0) : null;

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + phone.getText().toString()));
                intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentPhone);
            }
        });
    }

    private void getProviderDetails() {
        showStatsLoading();
        NetworkManager.getInstance().getProviderDetails(provider.getProviderId()).enqueue(new Callback<ProviderDetailsResponse>() {
            @Override
            public void onResponse(Call<ProviderDetailsResponse> call, Response<ProviderDetailsResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        providerDetailsResponse = response.body();

                        if (providerDetailsResponse == null) {
                            showStatsUnavailable();
                            return;
                        }

                        try {
                            showStatsView();
                            updateStatsView(providerDetailsResponse);

                            MapUtil.clearMarkers(getContext(), providerMap);
                            markers = MapUtil.addMapMarkers(getActivity(), providerMap, providerDetailsResponse.getOffices(),
                                    BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue), new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            handleMarkerClick(marker);
                                            //marker.showInfoWindow(); Won't fit with the zoom if states apart
                                            return true;
                                        }
                                    });

                            MapUtil.setMarkerSelectedIcon(getContext(), markers, address.getText().toString());

                            //Setup Booking
                            currentOffice = providerDetailsResponse.getOffices().get(0);
                            bookAppointment.setVisibility(currentOffice.getAppointments() != null && !currentOffice.getAppointments().isEmpty() ? View.VISIBLE : View.INVISIBLE);
                            bookAppointment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isBookingAppointment) {
                                        expandableLinearLayout.collapse();
                                    } else {
                                        bookAppointment.setVisibility(View.INVISIBLE);
                                        expandableLinearLayout.expand();
                                    }

                                    isBookingAppointment = !isBookingAppointment;
                                }
                            });

                            BookingSelectPersonFragment bookingFragment = BookingSelectPersonFragment.newInstance();
                            bookingFragment.setSelectPersonInterface(ProviderDetailsFragment.this);
                            getChildFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.booking_frame, bookingFragment)
                                    .addToBackStack(null)
                                    .commit();
                            getChildFragmentManager().executePendingTransactions();
                            expandableLinearLayout.initLayout();
                        } catch (NullPointerException ex) {
                        }
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        showStatsUnavailable();
                    }

                    MapUtil.zoomMap(getContext(), providerMap, markers);
                }
            }

            @Override
            public void onFailure(Call<ProviderDetailsResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    MapUtil.zoomMap(getContext(), providerMap, markers);
                    showStatsUnavailable();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.v("Map is ready\n" + googleMap);
        providerMap = googleMap;

        //Add markers
        markers = MapUtil.addMapMarkers(getActivity(), providerMap, provider.getOffices(), BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue), new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                handleMarkerClick(marker);
                //marker.showInfoWindow(); Won't fit with the zoom if states apart
                return true;
            }
        });

        MapUtil.setMarkerSelectedIcon(getContext(), markers, address.getText().toString());
        //MapUtil.zoomMap(getContext(), providerMap, markers);
    }

    //Set address text, then make sure to change selected icon
    private void handleMarkerClick(Marker marker) {
        //set the current office
        if (providerDetailsResponse != null && providerDetailsResponse.getOffices() != null) {
            for (Office office : providerDetailsResponse.getOffices()) {
                if (MapUtil.isOfficeSelected(office, marker)) {
                    currentOffice = office;
                    break;
                }
            }
        }

        bookAppointment.setVisibility(currentOffice.getAppointments() != null && !currentOffice.getAppointments().isEmpty() ? View.VISIBLE : View.INVISIBLE);

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.booking_frame);
        if (fragment != null) {
            //Close book appointments and reset flow again
            restartSchedulingFlow();
        }

        address.setText(marker.getSnippet());
        phone.setText(CommonUtil.constructPhoneNumber(currentOffice.getPhone()));
        MapUtil.setMarkerSelectedIcon(getContext(), markers, address.getText().toString());
    }

    private void showStatsUnavailable() {
        statsProgressBar.setVisibility(View.GONE);
        statsUnavailable.setVisibility(View.VISIBLE);
        statsView.setVisibility(View.GONE);
    }

    private void showStatsView() {
        statsProgressBar.setVisibility(View.GONE);
        statsUnavailable.setVisibility(View.GONE);
        statsView.setVisibility(View.VISIBLE);
    }

    private void showStatsLoading() {
        statsProgressBar.setVisibility(View.VISIBLE);
        statsUnavailable.setVisibility(View.GONE);
        statsView.setVisibility(View.GONE);
    }

    private void updateStatsView(final ProviderDetailsResponse providerDetailsResponse) {
        updateStatsViewProfile(providerDetailsResponse);
        updateStatsViewEducation(providerDetailsResponse);
        updateStatsViewExperience(providerDetailsResponse);
    }

    private void updateStatsViewProfile(final ProviderDetailsResponse providerDetailsResponse) {
        acceptingNewPatients = (TextView) statsProfileView.findViewById(R.id.accepting_new_patients);
        languages = (TextView) statsProfileView.findViewById(R.id.languages);
        gender = (TextView) statsProfileView.findViewById(R.id.gender);
        experience = (TextView) statsProfileView.findViewById(R.id.experience);
        philosophy = (TextView) statsProfileView.findViewById(R.id.philosophy);
        locations = (RecyclerView) statsProfileView.findViewById(R.id.locations_list);
        locationsLabel = (TextView) statsProfileView.findViewById(R.id.label_locations);

        acceptingNewPatients.setText(providerDetailsResponse.getAcceptsNewPatients() ? getString(R.string.yes) : getString(R.string.no));
        languages.setText(providerDetailsResponse.getLanguages() != null ? CommonUtil.prettyPrint(providerDetailsResponse.getLanguages()) : getString(R.string.unknown));

        if (providerDetailsResponse.getGender() != null && !providerDetailsResponse.getGender().isEmpty()) {
            if (providerDetailsResponse.getGender().equalsIgnoreCase("M") || providerDetailsResponse.getGender().equalsIgnoreCase(getString(R.string.male))) {
                gender.setText(getString(R.string.male));
            } else if (providerDetailsResponse.getGender().equalsIgnoreCase("F") || providerDetailsResponse.getGender().equalsIgnoreCase(getString(R.string.female))) {
                gender.setText(getString(R.string.female));
            } else {
                gender.setText(getString(R.string.unknown));
            }
        }

        experience.setText(providerDetailsResponse.getYearsOfExperience() != null ? providerDetailsResponse.getYearsOfExperience() + " " + getString(R.string.years) : getString(R.string.unknown));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            philosophy.setText(providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResponse.getPhilosophy(), Html.FROM_HTML_MODE_COMPACT) : getString(R.string.unknown));
        } else {
            //noinspection deprecation
            philosophy.setText(providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResponse.getPhilosophy()) : getString(R.string.unknown));
        }

        //Adjust Margin to account for HTML paragraph break
        if (providerDetailsResponse.getPhilosophy() != null && !providerDetailsResponse.getPhilosophy().isEmpty()) {
            philosophy.setVisibility(View.VISIBLE);
            statsProfileView.findViewById(R.id.label_philosophy).setVisibility(View.VISIBLE);
        } else {
            //Adjust to no philosophy
            philosophy.setVisibility(View.GONE);
            statsProfileView.findViewById(R.id.label_philosophy).setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DeviceDisplayManager.dpToPx(getContext(), 24), 0, 0);
            locationsLabel.setLayoutParams(params);
        }

        locations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        if (providerDetailsResponse != null && providerDetailsResponse.getOffices() != null) {
            locations.setAdapter(new ProviderDetailsLocationAdapter(getActivity(), providerDetailsResponse.getOffices(), new RecyclerViewListener() {
                @Override
                public void onItemClick(Object model, int position) {
                    //Do nothing on location item click...
                }

                @Override
                public void onPinClick(Object model, int position) {
                    Office office = (Office) model;
                    CommonUtil.getDirections(getActivity(), office.getAddress1(), office.getCity(), office.getState());
                }
            }));
        } else {
            locations.setAdapter(new ProviderDetailsLocationAdapter(getActivity(), null, null));
        }
    }

    private void updateStatsViewEducation(final ProviderDetailsResponse providerDetailsResponse) {
        if ((providerDetailsResponse.getMedicalSchools() == null || providerDetailsResponse.getMedicalSchools().isEmpty()) &&
                (providerDetailsResponse.getResidencies() == null || providerDetailsResponse.getResidencies().isEmpty()) &&
                (providerDetailsResponse.getFellowships() == null || providerDetailsResponse.getFellowships().isEmpty()) &&
                (providerDetailsResponse.getInternships() == null || providerDetailsResponse.getInternships().isEmpty())) {
            statsEducationView.setVisibility(View.GONE);
        } else {
            statsEducationView.setVisibility(View.VISIBLE);
            educationList = (RecyclerView) statsEducationView.findViewById(R.id.education_list);
            educationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });

            if (providerDetailsResponse != null) {
                List<String> curriculum = new ArrayList<String>() {{
                    addAll(providerDetailsResponse.getMedicalSchools());
                    addAll(providerDetailsResponse.getResidencies());
                    addAll(providerDetailsResponse.getFellowships());
                    addAll(providerDetailsResponse.getInternships());
                }};

                educationList.setAdapter(new ProviderDetailsEducationAdapter(getActivity(), curriculum));
            } else {
                educationList.setAdapter(new ProviderDetailsEducationAdapter(getActivity(), null));
            }
        }
    }

    private void updateStatsViewExperience(ProviderDetailsResponse providerDetailsResponse) {
        if ((providerDetailsResponse.getAwards() == null || providerDetailsResponse.getAwards().isEmpty()) &&
                (providerDetailsResponse.getCertifications() == null || providerDetailsResponse.getCertifications().isEmpty())) {
            statsExperienceView.setVisibility(View.GONE);
        } else {
            statsExperienceView.setVisibility(View.VISIBLE);
            certificationsLabel = (TextView) statsExperienceView.findViewById(R.id.certifications_label);
            certifications = (TextView) statsExperienceView.findViewById(R.id.certifications);
            awardsLabel = (TextView) statsExperienceView.findViewById(R.id.awards_label);
            awards = (TextView) statsExperienceView.findViewById(R.id.awards);

            if (providerDetailsResponse.getCertifications() != null && !providerDetailsResponse.getCertifications().isEmpty()) {
                certificationsLabel.setVisibility(View.VISIBLE);
                certifications.setVisibility(View.VISIBLE);
                CommonUtil.prettyPrint(providerDetailsResponse.getCertifications());
            } else {
                certificationsLabel.setVisibility(View.GONE);
                certifications.setVisibility(View.GONE);

                //Remove Margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                awardsLabel.setLayoutParams(params);
            }

            if (providerDetailsResponse.getAwards() != null && !providerDetailsResponse.getAwards().isEmpty()) {
                awardsLabel.setVisibility(View.VISIBLE);
                awards.setVisibility(View.VISIBLE);
                CommonUtil.prettyPrint(providerDetailsResponse.getAwards());
            } else {
                awardsLabel.setVisibility(View.GONE);
                awards.setVisibility(View.GONE);
            }
        }
    }

    private ArrayList<Appointment> filterAppointments(boolean isNewPatient, ArrayList<Appointment> appointments) {
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (!isNewPatient && appointment.AppointmentTypes.get(0).WellKnown.equalsIgnoreCase(Appointment.TYPE_EXISTING)) {
                filteredAppointments.add(appointment);
            } else if (isNewPatient && appointment.AppointmentTypes.get(0).WellKnown.equalsIgnoreCase(Appointment.TYPE_NEW)) {
                filteredAppointments.add(appointment);
            }
        }

        return filteredAppointments;
    }

    private void restartSchedulingFlow() {
        isBookingAppointment = false;
        if (isAdded()) {
            expandableLinearLayout.collapse();
            bookAppointment.setVisibility(currentOffice.getAppointments() != null && !currentOffice.getAppointments().isEmpty() ? View.VISIBLE : View.INVISIBLE);
            BookingSelectPersonFragment bookingFragment = BookingSelectPersonFragment.newInstance();
            bookingFragment.setSelectPersonInterface(ProviderDetailsFragment.this);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
            expandableLinearLayout.initLayout();
        }
    }

    @Override
    public void onPersonSelected(boolean isBookingForMe) {
        this.isBookingForMe = isBookingForMe;
        BookingSelectStatusFragment bookingFragment = BookingSelectStatusFragment.newInstance(!filterAppointments(true, currentOffice.getAppointments()).isEmpty(), !filterAppointments(false, currentOffice.getAppointments()).isEmpty());
        bookingFragment.setSelectStatusInterface(this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onStatusSelected(boolean isUserNew) {
        isNewPatient = isUserNew;
        BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance(filterAppointments(isNewPatient, currentOffice.getAppointments()));
        bookingFragment.setSelectTimeInterface(this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onTimeSelected(Appointment appointment) {
        bookedAppointment = appointment;

        BookingDialogFragment dialogFragment = BookingDialogFragment.newInstance(bookedAppointment.ScheduleId, isBookingForMe);
        dialogFragment.setBookingDialogInterface(this);
        dialogFragment.show(getChildFragmentManager(), BookingDialogFragment.BOOKING_DIALOG_TAG);
    }

    @Override
    public void onBackArrowClicked() {
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onFrontArrowClicked() {
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onMonthHeaderClicked() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.booking_frame);

        if (fragment instanceof BookingSelectCalendarFragment) {
            //You're on the calendar
            BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance(filterAppointments(isNewPatient, currentOffice.getAppointments()), bookingDate);
            bookingFragment.setSelectTimeInterface(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
            expandableLinearLayout.initLayout();
            expandableLinearLayout.expand();
        } else if (fragment instanceof BookingSelectTimeFragment) {
            //You were on the times

            BookingSelectCalendarFragment bookingFragment = BookingSelectCalendarFragment.newInstance(bookingDate, filterAppointments(isNewPatient, currentOffice.getAppointments()));
            bookingFragment.setSelectTimeInterface(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
            expandableLinearLayout.initLayout();
            expandableLinearLayout.expand();
        }
    }

    @Override
    public void onDateChanged(Date date) {
        bookingDate = date;
    }

    @Override
    public void onPhoneNumberClicked() {
        Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + currentOffice.getPhone()));
        intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentPhone);
    }

    @Override
    public void onBookingDialogFinished(Profile bookingProfile) {
        this.bookingProfile = bookingProfile;
        BookingConfirmationFragment bookingFragment = BookingConfirmationFragment.newInstance(bookingProfile, bookedAppointment);
        bookingFragment.setBookingConfirmationInterface(this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onClickBook() {
        BookingDoneFragment bookingFragment = BookingDoneFragment.newInstance(providerDetailsResponse.getDisplayFullName(), currentOffice.getName(), currentOffice.getPhone(), bookingProfile, bookedAppointment, isNewPatient, isBookingForMe);
        bookingFragment.setBookingDoneInterface(ProviderDetailsFragment.this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
        expandableLinearLayout.initLayout();
        expandableLinearLayout.expand();
    }

    @Override
    public void onBookingSuccess() {
        //Booking Successful!
    }

    @Override
    public void onBookingFailed(String errorMessage) {
        if (isAdded()) {
            Toast.makeText(getActivity(), getString(R.string.booking_failed), Toast.LENGTH_LONG).show();

            //Go to Time Fragment, then open up the Registration Forms Again
            BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance(filterAppointments(isNewPatient, currentOffice.getAppointments()));
            bookingFragment.setSelectTimeInterface(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
            expandableLinearLayout.initLayout();
            expandableLinearLayout.expand();

            bookingRegistrationDialog = BookingDialogFragment.newInstance(bookedAppointment.ScheduleId, isBookingForMe);
            bookingRegistrationDialog.setBookingDialogInterface(this);
            bookingRegistrationDialog.setCancelable(false);
            bookingRegistrationDialog.show(getChildFragmentManager(), BookingDialogFragment.BOOKING_DIALOG_TAG);
        }
    }

    @Override
    public void onClickDirections() {
        CommonUtil.getDirections(
                getActivity(),
                new Address(bookedAppointment.FacilityAddress, null, bookedAppointment.FacilityCity, bookedAppointment.FacilityState, bookedAppointment.FacilityZip, null)
        );
    }

    @Override
    public void onClickShare() {
        CommonUtil.shareAppointment(
                getActivity(),
                bookedAppointment.Time,
                provider.getDisplayFullName(),
                currentOffice.getName(),
                new Address(currentOffice.getAddress1(), currentOffice.getAddress2(), currentOffice.getCity(), currentOffice.getState(), currentOffice.getZipCode(), null),
                currentOffice.getPhone(),
                bookingProfile.reasonForVisit
        );
    }

    @Override
    public void onClickAddToCalendar() {
        CommonUtil.addCalendarEvent(
                getActivity(),
                bookedAppointment.Time,
                provider.getDisplayFullName(),
                new Address(currentOffice.getAddress1(), currentOffice.getAddress2(), currentOffice.getCity(), currentOffice.getState(), currentOffice.getZipCode(), null),
                currentOffice.getPhone(),
                bookingProfile.reasonForVisit
        );
    }

    /**
     * @return true if you wish to eat the back button action, false if you don't (calls the super)
     */
    @Override
    public boolean onBackButtonPressed() {
        if (bookingRegistrationDialog != null && bookingRegistrationDialog.isVisible()) {
            bookingRegistrationDialog.dismiss();
            return true;
        } else if (isBookingAppointment) {
            restartSchedulingFlow();
            return true;
        } else {
            return false;
        }
    }
}