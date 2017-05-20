package com.dignityhealth.myhome.features.fad.details;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.features.fad.Office;
import com.dignityhealth.myhome.features.fad.Provider;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.views.CircularImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private ViewPager viewPager;
    private CircularImageView doctorImage;
    private TextView name;
    private TextView speciality;
    private TextView address;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        providerDetailsView = inflater.inflate(R.layout.fragment_provider_details, container, false);

        myMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.provider_map));
        getProviderDetails();

        doctorImage = (CircularImageView) providerDetailsView.findViewById(R.id.doctor_image);
        name = (TextView) providerDetailsView.findViewById(R.id.doctor_name);
        speciality = (TextView) providerDetailsView.findViewById(R.id.speciality);
        address = (TextView) providerDetailsView.findViewById(R.id.facility_address);

        viewPager = (ViewPager) providerDetailsView.findViewById(R.id.view_pager);
        FragmentStatePagerAdapter pagerAdapter = new ProviderDetailsAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) providerDetailsView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        checkMapsKey();
        setupInitialView();
        return providerDetailsView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myMap.getMapAsync(this);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROVIDER_DETAILS;
    }

    private void setupInitialView() {
        if (providerDetailsView == null) {
            return;
        }

        Picasso.with(getActivity()).load(provider.getImageUrl()).into(doctorImage);

        name.setText(provider.getDisplayFullName() != null ? provider.getDisplayFullName() : "Name Unknown");
        speciality.setText(provider.getSpecialties() != null ? provider.getSpecialties().get(0) : "Specialities Unknown");
        address.setText(provider.getOffices() != null ? provider.getOffices().get(0).getAddress1() + "\n" + provider.getOffices().get(0).getAddress() : "Address Unknown");
    }

    private void getProviderDetails() {
        NetworkManager.getInstance().getProviderDetails(provider.getProviderId()).enqueue(new Callback<ProviderDetailsResponse>() {
            @Override
            public void onResponse(Call<ProviderDetailsResponse> call, Response<ProviderDetailsResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProviderDetailsResponse providerDetailsResponse = response.body();
                    FragmentStatePagerAdapter pagerAdapter = new ProviderDetailsAdapter(getActivity().getSupportFragmentManager(), providerDetailsResponse);
                    viewPager.setAdapter(pagerAdapter);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<ProviderDetailsResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }


    private void checkMapsKey() {
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo("com.dignityhealth.myhome",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Timber.v("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.d("Map is ready\n" + googleMap);

        //Add markers
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue);
        ArrayList<Marker> markers = new ArrayList<>();
        for (Office office : provider.getOffices()) {
            markers.add(googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(office.getLat()), Double.parseDouble(office.getLong())))
                    .title("Some title here")
                    .snippet("Some description here")
                    .icon(icon)));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(getContext(), "Clicked on marker " + marker, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        //The higher the float, the more zoom allowed
        googleMap.setMaxZoomPreference(15f);
        googleMap.animateCamera(calculateZoom(markers));
    }

    /**
     * Used to calculate the zoom if multiple markers need to fit on the map
     *
     * @param markers
     * @return
     */
    private CameraUpdate calculateZoom(ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        return cu;
    }
}