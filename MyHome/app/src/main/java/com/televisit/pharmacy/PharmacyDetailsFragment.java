package com.televisit.pharmacy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.MapUtil;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsUpdatePharmacy;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyDetailsFragment extends BaseFragment implements OnMapReadyCallback, AwsUpdatePharmacy {
    public static final String PHARMACY_DETAILS_TAG = "pharmacy_details_tag";
    public static final String PHARMACY_KEY = "pharmacy_key";

    private Pharmacy pharmacy;
    private Consumer patient;

    public PharmacyDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static PharmacyDetailsFragment newInstance() {
        return new PharmacyDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pharmacy = getArguments().getParcelable(PHARMACY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.pharmacy));
        } else {
            getActivity().setTitle(getString(R.string.pharmacy));
        }
        View view = inflater.inflate(R.layout.fragment_pharmacy_details, container, false);

        SupportMapFragment myMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.pharmacy_map));
        myMap.getMapAsync(this);

        TextView name = view.findViewById(R.id.pharmacy_name);
        name.setText(pharmacy.getName());

        TextView address = view.findViewById(R.id.pharmacy_address);
        address.setText(CommonUtil.getPharmacyAddress(pharmacy));

        String addressContentDescription = pharmacy != null && pharmacy.getAddress() != null ?
                AddressUtil.getAddressForAccessibilityUser(pharmacy.getAddress())
                : getString(R.string.address_unknown);

        address.setContentDescription(getString(R.string.location) + addressContentDescription);

        final TextView phone = view.findViewById(R.id.phone);
        ImageView phoneIcon = view.findViewById(R.id.phone_icon);
        phone.setText(CommonUtil.constructPhoneNumberDots(pharmacy.getPhone()));
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL + phone.getText().toString()));
                intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentPhone);
            }
        });

        ImageView mapIcon = view.findViewById(R.id.map_icon);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.getDirections(getActivity(), pharmacy.getAddress().getAddress1(), pharmacy.getAddress().getCity(), pharmacy.getAddress().getState().getCode());
            }
        });

        patient = AwsManager.getInstance().getPatient() != null ? AwsManager.getInstance().getPatient() : AwsManager.getInstance().getConsumer();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.pharmacy_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.save_pharmacy:
                AwsNetworkManager.getInstance().updatePharmacy(patient, pharmacy, this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<Marker> markers = MapUtil.addMapMarkers(getActivity(), googleMap, pharmacy,
                BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue), new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow(); //Won't fit with the zoom if states apart
                        return true;
                    }
                });

        MapUtil.zoomMap(getContext(), googleMap, markers);
    }

    @Override
    public void pharmacyUpdateComplete(Pharmacy pharmacy) {
        if (isAdded()) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void pharmacyUpdateFailed(String errorMessage) {
        CommonUtil.showToast(getContext(), getContext().getString(R.string.error_saving_pharmacy)+ errorMessage);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_PHARMACY_DETAILS;
    }
}
