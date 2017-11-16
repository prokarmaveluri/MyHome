package com.prokarma.myhome.features.fad.details;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.prokarma.myhome.features.fad.Office;
import com.prokarma.myhome.features.fad.details.booking.AppointmentManager;
import com.prokarma.myhome.features.fad.details.booking.AppointmentMonthDetails;
import com.prokarma.myhome.features.fad.details.booking.BookingBackButton;
import com.prokarma.myhome.features.fad.details.booking.BookingConfirmationFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingConfirmationInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDateHeaderInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDialogFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingDialogInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingDoneFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingDoneInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingManager;
import com.prokarma.myhome.features.fad.details.booking.BookingRefreshInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectCalendarFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectPersonFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectPersonInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectStatusFragment;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectStatusInterface;
import com.prokarma.myhome.features.fad.details.booking.BookingSelectTimeFragment;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.preferences.ImagesResponse;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.DeviceDisplayManager;
import com.prokarma.myhome.utils.MapUtil;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProviderDetailsFragment extends BaseFragment implements OnMapReadyCallback,
        BookingSelectPersonInterface, BookingSelectStatusInterface, BookingDateHeaderInterface,
        BookingDialogInterface, BookingConfirmationInterface, BookingDoneInterface, BookingBackButton,
        BookingRefreshInterface {

    public static final String PROVIDER_KEY = "PROVIDER_KEY";
    public static final String PROVIDER_ID_KEY = "PROVIDER_ID";
    public static final String PROVIDER_DETAILS_TAG = "provider_details_tag";
    public static final String TIME_TAG = "TIME_TAG"; //Allows us to go back to 'pop' all the 'time' fragments with one back (so you don't have to back multiple times through SelectTime/SelectCalendar pages)

    private String providerNpi;

    private ProviderDetailsResult provider;

    private ProviderDetailsOffice currentOffice;

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
    private ProgressBar detailsProgressBar;
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
    private ImageView favProvider;

    //stats Experience
    private LinearLayout statsExperienceView;
    private TextView certificationsLabel;
    private TextView certifications;
    private TextView awardsLabel;
    private TextView awards;

    //stats Education
    private LinearLayout statsEducationView;
    private RecyclerView educationList;
    private ScrollView providerDetailsLayout;

    private GoogleMap providerMap;
    private ArrayList<Marker> markers = new ArrayList<>();

    private boolean waitingForAppointmentTypes = false;

    boolean fav = false;

    //Booking
    private BookingDialogFragment bookingRegistrationDialog;

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
            providerNpi = getArguments().getString(PROVIDER_ID_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != provider)
            RecentlyViewedDataSourceDB.getInstance().createEntry(provider.convertToOldProviderDetails());

        Map<String, Object> tealiumData = new HashMap<>();
        tealiumData.put(Constants.FAD_PROVIDER_NPI, provider != null ? provider.getNpi() : providerNpi);
        TealiumUtil.trackView(Constants.PROVIDER_DETAILS_SCREEN, tealiumData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BookingManager.clearBookingData(false);
        AppointmentManager.getInstance().clearAppointmentDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        providerDetailsView = inflater.inflate(R.layout.fragment_provider_details, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));

        myMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.provider_map));
        myMap.getMapAsync(this);

        doctorImage = (CircularImageView) providerDetailsView.findViewById(R.id.doctor_image);
        name = (TextView) providerDetailsView.findViewById(R.id.doctor_name);
        speciality = (TextView) providerDetailsView.findViewById(R.id.speciality);
        address = (TextView) providerDetailsView.findViewById(R.id.facility_address);
        phone = (TextView) providerDetailsView.findViewById(R.id.phone);
        favProvider = (ImageView) providerDetailsView.findViewById(R.id.provider_details_fav);

        CommonUtil.updateFavView(false, favProvider);
        favProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav = !fav;
                if (null != provider && null != provider.getNpi()) {
                    NetworkManager.getInstance().updateFavDoctor(fav, provider.getNpi(),
                            favProvider, getSavedDoctor(provider), false, getActivity(), providerDetailsView);
                }
            }
        });

        footerLayout = (LinearLayout) providerDetailsView.findViewById(R.id.provider_details_footer);
        detailsProgressBar = (ProgressBar) providerDetailsView.findViewById(R.id.details_progress_bar);
        statsProgressBar = (ProgressBar) footerLayout.findViewById(R.id.stats_progress_bar);
        statsUnavailable = (TextView) footerLayout.findViewById(R.id.stats_unavailable);
        statsView = (RelativeLayout) footerLayout.findViewById(R.id.stats_view);
        statsProfileView = (LinearLayout) footerLayout.findViewById(R.id.stats_profile);
        statsEducationView = (LinearLayout) footerLayout.findViewById(R.id.stats_education);
        statsExperienceView = (LinearLayout) footerLayout.findViewById(R.id.stats_experience);

        providerDetailsLayout = (ScrollView) providerDetailsView.findViewById(R.id.provider_details);
        expandableLinearLayout = (ExpandableLinearLayout) providerDetailsView.findViewById(R.id.expandable_layout);
        bookAppointment = (Button) providerDetailsView.findViewById(R.id.book_appointment);

        if (providerNpi == null) {
            setupInitialView();
        }

        if (provider != null) {
            providerNpi = provider.getNpi();
        }

        getProviderDetails();
        return providerDetailsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROVIDER_DETAILS;
    }

    private void setupInitialView() {
        if (providerDetailsView == null || provider == null) {
            return;
        }

        if (provider != null && provider.getImages() != null) {
            Picasso.with(getActivity())
                    .load(CommonUtil.getBestImage(provider.getImages()).getUrl())
                    .into(doctorImage);
        }

        name.setText(provider.getDisplayName() != null ? provider.getDisplayName() : getString(R.string.name_unknown));
        speciality.setText(provider.getPrimarySpecialities() != null ? provider.getPrimarySpecialities().get(0) : getString(R.string.specialities_unknown));
        address.setText(provider.getOffices() != null ?
                provider.getOffices().get(0).getAddresses().get(0).getAddress() + "\n" +
                        provider.getOffices().get(0).getAddresses().get(0).getCity() + ", " +
                        provider.getOffices().get(0).getAddresses().get(0).getState() + " " +
                        provider.getOffices().get(0).getAddresses().get(0).getZip()
                : getString(R.string.address_unknown));
        phone.setText(provider.getOffices() != null ? CommonUtil.constructPhoneNumber(provider.getOffices().get(0).getAddresses().get(0).getPhones().get(0)) : getString(R.string.phone_number_unknown));
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

    private void getAppointmentDetails(String providerNpi, final String fromDate, final String toDate, String addressHash) {
        NetworkManager.getInstance().getProviderAppointments(providerNpi, fromDate, toDate, addressHash).enqueue(new Callback<AppointmentTimeSlots>() {
            @Override
            public void onResponse(Call<AppointmentTimeSlots> call, Response<AppointmentTimeSlots> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    Timber.d("Successful Response\n" + response);

                    try {
                        AppointmentManager.getInstance().addMonthsAppointmentDetails(new AppointmentMonthDetails(DateUtil.getDateFromSlashes(fromDate), DateUtil.getDateFromSlashes(toDate), response.body()));
                        BookingManager.setScheduleId(response.body().getData().get(0).getId());

                        if (waitingForAppointmentTypes) {
                            onPersonSelected(BookingManager.isBookingForMe());
                        }
                    } catch (ParseException parseException) {
                        Timber.e(parseException);
                        Timber.e("Is the date being parsed correctly???");

                        waitingForAppointmentTypes = false;

                        BookingManager.setScheduleId(null);

                        BookingManager.clearBookingData(true);
                        restartSchedulingFlow();
                        expandableLinearLayout.collapse();
                        expandableLinearLayout.initLayout();
                    }


                } else {
                    Timber.e("Response, but not successful?\n" + response);
                    ApiErrorUtil.getInstance().getProviderAppointmentsError(getContext(), providerDetailsView, response);

                    waitingForAppointmentTypes = false;

                    BookingManager.setScheduleId(null);

                    BookingManager.clearBookingData(true);
                    restartSchedulingFlow();
                    expandableLinearLayout.collapse();
                    expandableLinearLayout.initLayout();
                }
            }

            @Override
            public void onFailure(Call<AppointmentTimeSlots> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
                ApiErrorUtil.getInstance().getProviderAppointmentsFailed(getContext(), providerDetailsView, t);

                waitingForAppointmentTypes = false;

                AppointmentManager.getInstance().addMonthsAppointmentDetails(null);
                BookingManager.setScheduleId(null);

                BookingManager.clearBookingData(true);
                restartSchedulingFlow();
                expandableLinearLayout.collapse();
                expandableLinearLayout.initLayout();
            }
        });
    }

    private void getProviderDetails() {
        showStatsLoading();
        detailsProgressBar.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().getNewProviderDetails(providerNpi).enqueue(new Callback<ProviderDetails>() {
            @Override
            public void onResponse(Call<ProviderDetails> call, Response<ProviderDetails> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        detailsProgressBar.setVisibility(View.GONE);
                        Timber.d("Successful Response\n" + response);
                        provider = response.body().getResult().get(0);

                        setupInitialView();
                        if (provider == null) {
                            showStatsUnavailable();
                            ApiErrorUtil.getInstance().getProviderDetailsError(getContext(), providerDetailsView, response);
                            return;
                        }
                        if (null != provider)
                            RecentlyViewedDataSourceDB.getInstance().createEntry(provider.convertToOldProviderDetails());

                        showStatsView();
                        updateStatsView(provider);

                        providerDetailsLayout.smoothScrollTo(0, 0);
                        MapUtil.clearMarkers(getContext(), providerMap);
                        markers = MapUtil.addMapMarkers(getActivity(), providerMap, provider.getOffices(),
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
                        currentOffice = provider.getOffices().get(0);

                        if (provider.getSupportsOnlineBooking()) {
                            AppointmentManager.getInstance().initializeAppointmentDetailsList(false);
                            getAppointmentDetails(providerNpi, DateUtil.getTodayDate(), DateUtil.getEndOfTheMonthDate(new Date()), currentOffice.getAddresses().get(0).getAddressHash());
                        }

                        bookAppointment.setVisibility(provider != null && provider.getSupportsOnlineBooking() ? View.VISIBLE : View.GONE);
                        bookAppointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getAppointmentDetails(providerNpi, DateUtil.getFirstOfTheMonthDate(DateUtil.addOneMonthToDate(new Date())), DateUtil.getEndOfTheMonthDate(DateUtil.addOneMonthToDate(new Date())), currentOffice.getAddresses().get(0).getAddressHash());

                                bookAppointment.setVisibility(View.GONE);
                                BookingManager.setBookingProfile(null);
                                BookingManager.setBookingProvider(provider);
                                BookingManager.setBookingOffice(currentOffice);

                                BookingSelectPersonFragment bookingFragment = BookingSelectPersonFragment.newInstance();
                                bookingFragment.setSelectPersonInterface(ProviderDetailsFragment.this);
                                bookingFragment.setRefreshInterface(ProviderDetailsFragment.this);
                                getChildFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.booking_frame, bookingFragment)
                                        .addToBackStack(null)
                                        .commit();
                                getChildFragmentManager().executePendingTransactions();

                                Map<String, Object> tealiumData = new HashMap<>();
                                tealiumData.put(Constants.FAD_PROVIDER_NPI, provider != null ? provider.getNpi() : providerNpi);
                                TealiumUtil.trackEvent(Constants.SCHEDULING_STARTED_EVENT, tealiumData);
                            }
                        });

                        if (ProfileManager.getFavoriteProviders() != null) {
                            for (ProviderResponse provider : ProfileManager.getFavoriteProviders()) {
                                if (ProviderDetailsFragment.this.provider.getNpi().contains(provider.getNpi())) {
                                    fav = true;
                                    CommonUtil.updateFavView(true, favProvider);
                                    break;
                                }
                            }
                        }
                    } else {
                        ApiErrorUtil.getInstance().getProviderDetailsError(getContext(), providerDetailsView, response);
                        detailsProgressBar.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), R.string.provider_details_error_msg, Toast.LENGTH_SHORT).show();
                        Timber.e("Response, but not successful?\n" + response);
                        showStatsUnavailable();
                    }

                    MapUtil.zoomMap(getContext(), providerMap, markers);
                    mHandler.sendEmptyMessageDelayed(1, 200);
                }
            }

            @Override
            public void onFailure(Call<ProviderDetails> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    ApiErrorUtil.getInstance().getProviderDetailsFailed(getContext(), providerDetailsView, t);

                    detailsProgressBar.setVisibility(View.GONE);
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

        if (null == provider)
            return;
        //Add markers
        markers = MapUtil.addMapMarkers(getActivity(), providerMap, provider.getOffices(),
                BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue), new GoogleMap.OnMarkerClickListener() {
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
        if (provider != null && provider.getOffices() != null) {
            for (ProviderDetailsOffice office : provider.getOffices()) {
                if (MapUtil.isOfficeSelected(office, marker)) {
                    currentOffice = office;
                    break;
                }
            }
        }

        bookAppointment.setVisibility(provider != null && provider.getSupportsOnlineBooking() ? View.VISIBLE : View.GONE);

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.booking_frame);
        if (fragment != null) {
            //Close book appointments and reset flow again
            restartSchedulingFlow();
            expandableLinearLayout.collapse();
            expandableLinearLayout.initLayout();
        }

        address.setText(marker.getSnippet());
        phone.setText(CommonUtil.constructPhoneNumber(currentOffice.getAddresses().get(0).getPhones().get(0)));
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

    private void updateStatsView(final ProviderDetailsResult providerDetailsResponse) {
        updateStatsViewProfile(providerDetailsResponse);
        updateStatsViewEducation(providerDetailsResponse);
        updateStatsViewExperience(providerDetailsResponse);
    }

    private void updateStatsViewProfile(final ProviderDetailsResult providerDetailsResult) {
        acceptingNewPatients = (TextView) statsProfileView.findViewById(R.id.accepting_new_patients);
        languages = (TextView) statsProfileView.findViewById(R.id.languages);
        gender = (TextView) statsProfileView.findViewById(R.id.gender);
        experience = (TextView) statsProfileView.findViewById(R.id.experience);
        philosophy = (TextView) statsProfileView.findViewById(R.id.philosophy);
        locations = (RecyclerView) statsProfileView.findViewById(R.id.locations_list);
        locationsLabel = (TextView) statsProfileView.findViewById(R.id.label_locations);

        acceptingNewPatients.setText(providerDetailsResult.getAcceptsNewPatients() != null && providerDetailsResult.getAcceptsNewPatients() ? getString(R.string.yes) : getString(R.string.no));
        languages.setText(providerDetailsResult.getLanguages() != null ? CommonUtil.prettyPrint(providerDetailsResult.getLanguages()) : getString(R.string.unknown));

        if (providerDetailsResult.getGender() != null && !providerDetailsResult.getGender().isEmpty()) {
            if (providerDetailsResult.getGender().equalsIgnoreCase("M") || providerDetailsResult.getGender().equalsIgnoreCase(getString(R.string.male))) {
                gender.setText(getString(R.string.male));
            } else if (providerDetailsResult.getGender().equalsIgnoreCase("F") || providerDetailsResult.getGender().equalsIgnoreCase(getString(R.string.female))) {
                gender.setText(getString(R.string.female));
            } else {
                gender.setText(getString(R.string.unknown));
            }
        }

        experience.setText(providerDetailsResult.getYearsOfExperience() != null ? providerDetailsResult.getYearsOfExperience() + " " + getString(R.string.years) : getString(R.string.unknown));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            philosophy.setText(providerDetailsResult.getPhilosophy() != null && !providerDetailsResult.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResult.getPhilosophy(), Html.FROM_HTML_MODE_COMPACT) : getString(R.string.unknown));
        } else {
            //noinspection deprecation
            philosophy.setText(providerDetailsResult.getPhilosophy() != null && !providerDetailsResult.getPhilosophy().isEmpty() ? Html.fromHtml(providerDetailsResult.getPhilosophy()) : getString(R.string.unknown));
        }

        //Adjust Margin to account for HTML paragraph break
        if (providerDetailsResult.getPhilosophy() != null && !providerDetailsResult.getPhilosophy().isEmpty()) {
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

        if (providerDetailsResult != null && providerDetailsResult.getOffices() != null) {
            locations.setAdapter(new ProviderDetailsLocationAdapter(getActivity(), providerDetailsResult.getOffices(), new RecyclerViewListener() {
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

    private void updateStatsViewEducation(final ProviderDetailsResult providerDetailsResult) {
        if ((providerDetailsResult.getMedicalSchools() == null || providerDetailsResult.getMedicalSchools().isEmpty()) &&
                (providerDetailsResult.getResidencies() == null || providerDetailsResult.getResidencies().isEmpty()) &&
                (providerDetailsResult.getFellowships() == null || providerDetailsResult.getFellowships().isEmpty()) &&
                (providerDetailsResult.getInternships() == null || providerDetailsResult.getInternships().isEmpty())) {
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

            if (providerDetailsResult != null) {
                List<String> curriculum = new ArrayList<String>() {{
                    addAll(providerDetailsResult.getMedicalSchools());
                    addAll(providerDetailsResult.getResidencies());
                    addAll(providerDetailsResult.getFellowships());
                    addAll(providerDetailsResult.getInternships());
                }};

                educationList.setAdapter(new ProviderDetailsEducationAdapter(getActivity(), curriculum));
            } else {
                educationList.setAdapter(new ProviderDetailsEducationAdapter(getActivity(), null));
            }
        }
    }

    private void updateStatsViewExperience(ProviderDetailsResult providerDetailsResult) {
        if ((providerDetailsResult.getAwards() == null || providerDetailsResult.getAwards().isEmpty()) &&
                (providerDetailsResult.getCertifications() == null || providerDetailsResult.getCertifications().isEmpty())) {
            statsExperienceView.setVisibility(View.GONE);
        } else {
            statsExperienceView.setVisibility(View.VISIBLE);
            certificationsLabel = (TextView) statsExperienceView.findViewById(R.id.certifications_label);
            certifications = (TextView) statsExperienceView.findViewById(R.id.certifications);
            awardsLabel = (TextView) statsExperienceView.findViewById(R.id.awards_label);
            awards = (TextView) statsExperienceView.findViewById(R.id.awards);

            if (providerDetailsResult.getCertifications() != null && !providerDetailsResult.getCertifications().isEmpty()) {
                certificationsLabel.setVisibility(View.VISIBLE);
                certifications.setVisibility(View.VISIBLE);
                CommonUtil.prettyPrint(providerDetailsResult.getCertifications());
            } else {
                certificationsLabel.setVisibility(View.GONE);
                certifications.setVisibility(View.GONE);

                //Remove Margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                awardsLabel.setLayoutParams(params);
            }

            if (providerDetailsResult.getAwards() != null && !providerDetailsResult.getAwards().isEmpty()) {
                awardsLabel.setVisibility(View.VISIBLE);
                awards.setVisibility(View.VISIBLE);
                CommonUtil.prettyPrint(providerDetailsResult.getAwards());
            } else {
                awardsLabel.setVisibility(View.GONE);
                awards.setVisibility(View.GONE);
            }
        }
    }

    //Simply clear the back stack and set the visibility back to 'normal'
    private void restartSchedulingFlow() {
        if (isAdded()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.booking_frame)).commit();
            }

            bookAppointment.setVisibility(provider != null && provider.getSupportsOnlineBooking() ? View.VISIBLE : View.GONE);
        }
    }

    //Launches the booking registration form
    private void launchRegistrationForms() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (BookingManager.getBookingProfile() != null) {
            bookingRegistrationDialog = BookingDialogFragment.newInstance(true);
        } else if (BookingManager.isBookingForMe()) {
            BookingManager.setBookingProfile(ProfileManager.getProfile());
            bookingRegistrationDialog = BookingDialogFragment.newInstance(false);
        } else {
            bookingRegistrationDialog = BookingDialogFragment.newInstance(true);
        }

        bookingRegistrationDialog.setBookingDialogInterface(this);
        bookingRegistrationDialog.setCancelable(false);
        bookingRegistrationDialog.show(getChildFragmentManager(), BookingDialogFragment.BOOKING_DIALOG_TAG);
    }

    @Override
    public void onPersonSelected(boolean isBookingForMe) {
        //If user selects different option, clear booking profile
        if (isBookingForMe != BookingManager.isBookingForMe()) {
            BookingManager.clearBookingData(true);
        }

        BookingManager.setIsBookingForMe(isBookingForMe);

        if (AppointmentManager.getInstance().getNumberOfMonths() > 0) {
            waitingForAppointmentTypes = false;
            BookingSelectStatusFragment bookingFragment = BookingSelectStatusFragment.newInstance(AppointmentManager.getInstance().getAppointmentTypes());
            bookingFragment.setSelectStatusInterface(this);
            bookingFragment.setRefreshInterface(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();
        } else {
            waitingForAppointmentTypes = true;
            //TODO Kevin, possibly add loading screen here???
        }
    }

    @Override
    public void onTypeSelected(AppointmentType appointmentType) {
        BookingManager.setBookingAppointmentType(appointmentType);
        BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance();
        bookingFragment.setSelectTimeInterface(this);
        bookingFragment.setRefreshInterface(this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(TIME_TAG)
                .commit();
        getChildFragmentManager().executePendingTransactions();
    }

    @Override
    public void onTimeSelected(AppointmentTime appointment) {
        BookingManager.setBookingAppointment(appointment);

        Fragment fragment = getChildFragmentManager().findFragmentByTag(BookingDialogFragment.BOOKING_DIALOG_TAG);
        if (fragment != null) {
            return;
        }

        launchRegistrationForms();
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
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    @Override
    public void onDateChanged(Date date) {
        BookingManager.setBookingDate(date);
    }

    @Override
    public void onPhoneNumberClicked() {
        Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + currentOffice.getAddresses().get(0).getPhones().get(0)));
        intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentPhone);
    }

    @Override
    public void onBookingDialogFinished(Profile bookingProfile) {
        BookingManager.setBookingProfile(bookingProfile);
        BookingConfirmationFragment bookingFragment = BookingConfirmationFragment.newInstance();
        bookingFragment.setConfirmationInterface(this);
        bookingFragment.setRefreshInterface(this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
    }

    @Override
    public void onValidationRulesError(Response response) {
        ApiErrorUtil.getInstance().getValidationRulesError(getContext(), providerDetailsView, response);
    }

    @Override
    public void onValidationRulesFailed(Throwable throwable) {
        ApiErrorUtil.getInstance().getValidationRulesFailed(getContext(), providerDetailsView, throwable);
    }

    @Override
    public void onClickBook() {
        BookingDoneFragment bookingFragment = BookingDoneFragment.newInstance(provider.getDisplayName(),
                provider.getNpi(), currentOffice.getName(), currentOffice.getAddresses().get(0).getPhones().get(0));
        bookingFragment.setDoneInterface(ProviderDetailsFragment.this);
        bookingFragment.setRefreshInterface(ProviderDetailsFragment.this);
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.booking_frame, bookingFragment)
                .addToBackStack(null)
                .commit();
        getChildFragmentManager().executePendingTransactions();
    }

    @Override
    public void onBookingSuccess() {
        //Booking Successful!
        Map<String, Object> tealiumData = new HashMap<>();
        tealiumData.put(Constants.FAD_PROVIDER_NPI, provider != null ? provider.getNpi() : providerNpi);
        TealiumUtil.trackEvent(Constants.SCHEDULING_ENDED_EVENT, tealiumData);
    }

    @Override
    public void onBookingFailed(String errorMessage) {
        Map<String, Object> tealiumData = new HashMap<>();
        tealiumData.put(Constants.FAD_PROVIDER_NPI, provider != null ? provider.getNpi() : providerNpi);
        TealiumUtil.trackEvent(Constants.SCHEDULING_FAILED_EVENT, tealiumData);

        if (isAdded()) {
            Toast.makeText(getActivity(), getString(R.string.booking_failed), Toast.LENGTH_LONG).show();

            //Go to Time Fragment, then open up the Registration Forms Again

            BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance();
            bookingFragment.setSelectTimeInterface(this);
            bookingFragment.setRefreshInterface(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.booking_frame, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            getChildFragmentManager().executePendingTransactions();

            launchRegistrationForms();
        }
    }

    /**
     * @return true if you wish to eat the back button action, false if you don't (calls the super)
     */
    @Override
    public boolean onBackButtonPressed() {
        ApiErrorUtil.getInstance().clearErrorMessage();
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.booking_frame);
        if (fragment != null && (fragment instanceof BookingSelectCalendarFragment || fragment instanceof BookingSelectTimeFragment)) {
            //If on Time or Calendar booking page, just popbackstack to before all the Time/Calendar fragments
            getChildFragmentManager().popBackStack(TIME_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        } else if (fragment != null && fragment instanceof BookingDoneFragment) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));
            BookingManager.clearBookingData(true);
            restartSchedulingFlow();
            expandableLinearLayout.collapse();
            expandableLinearLayout.initLayout();
            return true;
        } else if (fragment != null && fragment instanceof BookingConfirmationFragment) {
            //You should go back to the Time slot, but relaunch the Registration Forms
            getChildFragmentManager().popBackStack();
            launchRegistrationForms();
            return true;
        } else if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        } else if (getChildFragmentManager().getBackStackEntryCount() == 1) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));
            restartSchedulingFlow();
            expandableLinearLayout.collapse();
            expandableLinearLayout.initLayout();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRefreshView(boolean shouldExpand) {
        expandableLinearLayout.initLayout();

        if (shouldExpand) {
            expandableLinearLayout.expand();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.booking_frame);

                    if (fragment instanceof BookingSelectCalendarFragment) {
                        //You're on the calendar
                        BookingSelectTimeFragment bookingFragment = BookingSelectTimeFragment.newInstance(BookingManager.getBookingDate());
                        bookingFragment.setSelectTimeInterface(ProviderDetailsFragment.this);
                        bookingFragment.setRefreshInterface(ProviderDetailsFragment.this);
                        getChildFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                                .replace(R.id.booking_frame, bookingFragment)
                                .addToBackStack(null)
                                .commit();
                        getChildFragmentManager().executePendingTransactions();
                    } else if (fragment instanceof BookingSelectTimeFragment) {
                        //You were on the times
                        BookingSelectCalendarFragment bookingFragment = BookingSelectCalendarFragment.newInstance(BookingManager.getBookingDate());
                        bookingFragment.setSelectTimeInterface(ProviderDetailsFragment.this);
                        bookingFragment.setRefreshInterface(ProviderDetailsFragment.this);
                        getChildFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                                .replace(R.id.booking_frame, bookingFragment)
                                .addToBackStack(null)
                                .commit();
                        getChildFragmentManager().executePendingTransactions();
                    }
                    break;
                case 1:
                    coachmarkBooking();
                    break;
            }
        }
    };

    private ProviderResponse getSavedDoctor(ProviderDetailsResult providerDetailsResult) {
        ProviderResponse provider = new ProviderResponse();
        if (providerDetailsResult == null)
            return null;

        provider.setDisplayLastName(providerDetailsResult.getDisplayLastName());
        provider.setDisplayName(providerDetailsResult.getDisplayName());
        provider.setDisplayLastNamePlural(providerDetailsResult.getDisplayLastNamePlural());
        provider.setFirstName(providerDetailsResult.getFirstName());
        provider.setLastName(providerDetailsResult.getLastName());
        provider.setNpi(providerDetailsResult.getNpi());
        provider.setMiddleName(providerDetailsResult.getMiddleName());
        provider.setPhilosophy(providerDetailsResult.getPhilosophy());
        provider.setPrimarySpecialities(providerDetailsResult.getPrimarySpecialities());
        provider.setTitle(providerDetailsResult.getTitle());

        List<ImagesResponse> imageUrls = new ArrayList<>();
        for (ProviderDetailsImage image : providerDetailsResult.getImages()) {
            ImagesResponse response = new ImagesResponse();
            response.setUrl(image.getUrl());
            imageUrls.add(response);
        }

        provider.setImages(imageUrls);

        return provider;
    }

    private void coachmarkBooking() {
        if (bookAppointment.getVisibility() != View.VISIBLE) {
            coachmarkLocations();
            return;
        }
        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.PROVIDER_DETAILS_BOOK_SKIP_COACH_MARKS);
        if (skip)
            return;
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(bookAppointment, getString(R.string.coachmark_provider_details_book))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkLocations();
                        AppPreferences.getInstance().setBooleanPreference(Constants.PROVIDER_DETAILS_BOOK_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.PROVIDER_DETAILS_BOOK_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkLocations() {
        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.PROVIDER_DETAILS_LOCATION_SKIP_COACH_MARKS);
        if (skip)
            return;
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(myMap.getView(), getString(R.string.coachmarks_provider_details_location))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.PROVIDER_DETAILS_LOCATION_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.PROVIDER_DETAILS_LOCATION_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }
}