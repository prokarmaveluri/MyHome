package com.dignityhealth.myhome.features.fad.details;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.features.fad.Provider;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.views.CircularImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProviderDetailsFragment extends BaseFragment {
    public static final String PROVIDER_KEY = "PROVIDER_KEY";
    public static final String PROVIDER_DETAILS_TAG = "provider_details_tag";

    private Provider provider;

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
        getProviderDetails();
        checkMapsKey();

        doctorImage = (CircularImageView) providerDetailsView.findViewById(R.id.doctor_image);
        name = (TextView) providerDetailsView.findViewById(R.id.doctor_name);
        speciality = (TextView) providerDetailsView.findViewById(R.id.speciality);
        address = (TextView) providerDetailsView.findViewById(R.id.facility_address);

        viewPager = (ViewPager) providerDetailsView.findViewById(R.id.view_pager);
        FragmentStatePagerAdapter pagerAdapter = new ProviderDetailsAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) providerDetailsView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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

            SupportMapFragment myMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
            if (myMap != null) {
                myMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        Timber.d("Map is ready\n" + googleMap.toString());
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}