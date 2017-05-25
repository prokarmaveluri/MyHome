package com.dignityhealth.myhome.features.fad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class ProvidersMapFragment extends BaseFragment implements
        ProvidersAdapter.IProviderClick, OnMapReadyCallback {

    private GoogleMap map;
    private LocationResponse location = null;
    private List<Provider> providerList = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ClusterManager<MapClusterItem> mClusterManager;

    public static final String FAD_TAG = "fad_map";

    public static ProvidersMapFragment newInstance() {
        return new ProvidersMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList = getArguments().getParcelableArrayList("PROVIDER_LIST");
            location = FadManager.getInstance().getCurrentLocation();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_providers_maps, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.providers_map);
        map.getMapAsync(this);
        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD_MAP;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void providerClick(int position) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mClusterManager = new ClusterManager<>(getActivity(), map);
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        addMarkers();
    }

    private void addMarkers() {
        if (providerList == null || providerList.size() <= 0) {
            Toast.makeText(getActivity(), getString(R.string.no_providers), Toast.LENGTH_LONG).show();
            return;
        }
        int count = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Provider provider : providerList) {
            for (Office office : provider.getOffices()) {
                LatLng position = new LatLng(Double.valueOf(office.getLat()),
                        Double.valueOf(office.getLong()));
                builder.include(position);
//                MarkerOptions marker = new MarkerOptions().position(position).title(provider.getDisplayFullName())
//                        .snippet(office.getAddress())
////                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue));
//                count++;
//                Timber.i("markers "+position.toString());
//                map.addMarker(marker);
                MapClusterItem item = new MapClusterItem(Double.valueOf(office.getLat()),
                        Double.valueOf(office.getLong()),
                        provider.getDisplayFullName(),
                        office.getAddress());
                mClusterManager.addItem(item);
            }
        }
        Timber.i("No. Of markers "+count);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);
        map.animateCamera(cu);
    }
}
