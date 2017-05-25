package com.dignityhealth.myhome.features.fad.details;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.features.fad.Provider;
import com.dignityhealth.myhome.features.fad.details.booking.BookingAdapter;
import com.dignityhealth.myhome.features.fad.recently.viewed.RecentlyViewedDataSourceDB;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.MapUtil;
import com.dignityhealth.myhome.views.CircularImageView;
import com.dignityhealth.myhome.views.WrappingViewPager;
import com.dignityhealth.myhome.views.WrappingViewPagerSwipeInterface;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProviderDetailsFragment extends BaseFragment implements OnMapReadyCallback {
    public static final String PROVIDER_KEY = "PROVIDER_KEY";
    public static final String PROVIDER_DETAILS_TAG = "provider_details_tag";

    private Provider provider;

    private SupportMapFragment myMap;
    private View providerDetailsView;
    private WrappingViewPager statsViewPager;
    private CircularImageView doctorImage;
    private TextView name;
    private TextView speciality;
    private TextView address;
    private Button bookAppointment;
    private ExpandableLinearLayout expandableLinearLayout;

    private WrappingViewPager bookingViewPager;

    private GoogleMap providerMap;
    private ArrayList<Marker> markers = new ArrayList<>();

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
        RecentlyViewedDataSourceDB.getInstance().createEntry(provider.getProviderId());
        ((NavigationActivity) getActivity()).setActionBarTitle("Find Care");
        ((NavigationActivity) getActivity()).getNavigationActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        providerDetailsView = inflater.inflate(R.layout.fragment_provider_details, container, false);

        myMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.provider_map));
        myMap.getMapAsync(this);
        getProviderDetails();

        doctorImage = (CircularImageView) providerDetailsView.findViewById(R.id.doctor_image);
        name = (TextView) providerDetailsView.findViewById(R.id.doctor_name);
        speciality = (TextView) providerDetailsView.findViewById(R.id.speciality);
        address = (TextView) providerDetailsView.findViewById(R.id.facility_address);

        bookAppointment = (Button) providerDetailsView.findViewById(R.id.book_appointment);
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLinearLayout.toggle();
            }
        });
        expandableLinearLayout = (ExpandableLinearLayout) providerDetailsView.findViewById(R.id.expandable_layout);

        statsViewPager = (WrappingViewPager) providerDetailsView.findViewById(R.id.stats_view_pager);
        statsViewPager.setOffscreenPageLimit(2); //Let's us load all three of the fragments for the pager and keep them in memory
        FragmentStatePagerAdapter pagerAdapter = new ProviderDetailsAdapter(getChildFragmentManager());
        statsViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) providerDetailsView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(statsViewPager);

        bookingViewPager = (WrappingViewPager) providerDetailsView.findViewById(R.id.booking_view_pager);
        bookingViewPager.setOffscreenPageLimit(5);
        bookingViewPager.setAdapter(new BookingAdapter(getContext()));
        bookingViewPager.setSwipeInterface(new WrappingViewPagerSwipeInterface() {
            @Override
            public boolean onSwipeRight() {
                return true;
            }

            @Override
            public boolean onSwipeLeft() {
                if (bookingViewPager.getCurrentItem() == 2 && ((BookingAdapter)bookingViewPager.getAdapter()).getTimeIndex() != -1){
                    //If you're on Time page and have selected a time, allow swipe
                    return true;
                } else if (bookingViewPager.getCurrentItem() == 1 && ((BookingAdapter)bookingViewPager.getAdapter()).isDateSelected()){
                    //If you're on Calendar page and have selected a date, allow swipe
                    return true;
                } else if(bookingViewPager.getCurrentItem() == 0 && ((BookingAdapter)bookingViewPager.getAdapter()).getPerson() != -1){
                    //If you're on Select Person page and have selected a person, allow swipe
                    return true;
                }

                return false;
            }
        });

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

        String url = provider.getImageUrl();
        url = url.replace("w60h80", "w120h160");
        Picasso.with(getActivity())
                .load(url)
                .into(doctorImage);

        name.setText(provider.getDisplayFullName() != null ? provider.getDisplayFullName() : "Name Unknown");
        speciality.setText(provider.getSpecialties() != null ? provider.getSpecialties().get(0) : "Specialities Unknown");
        address.setText(provider.getOffices() != null ? provider.getOffices().get(0).getAddress1() + "\n" + provider.getOffices().get(0).getAddress() : "Address Unknown");
    }

    private void getProviderDetails() {
        NetworkManager.getInstance().getProviderDetails(provider.getProviderId()).enqueue(new Callback<ProviderDetailsResponse>() {
            @Override
            public void onResponse(Call<ProviderDetailsResponse> call, Response<ProviderDetailsResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        final ProviderDetailsResponse providerDetailsResponse = response.body();
                        FragmentStatePagerAdapter pagerAdapter = new ProviderDetailsAdapter(getChildFragmentManager(), providerDetailsResponse);
                        statsViewPager.setAdapter(pagerAdapter);

                        MapUtil.clearMarkers(getContext(), providerMap);
                        markers = MapUtil.addMapMarkers(getActivity(), providerMap, providerDetailsResponse.getOffices(), BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue), new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                handleMarkerClick(marker);
                                //marker.showInfoWindow(); Won't fit with the zoom if states apart
                                return true;
                            }
                        });

                        MapUtil.setMarkerSelectedIcon(getContext(), markers, address.getText().toString());

                    } else {
                        Timber.e("Response, but not successful?\n" + response);
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
        address.setText(marker.getSnippet());
        MapUtil.setMarkerSelectedIcon(getContext(), markers, address.getText().toString());
    }

}