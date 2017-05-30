package com.dignityhealth.myhome.features.fad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsFragment;
import com.dignityhealth.myhome.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for providers map view, display doctors with search feature.
 */

public class MapViewFragment extends Fragment implements
        ProvidersAdapter.IProviderClick, OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener,
        ClusterManager.OnClusterClickListener<MapClusterItem>,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap map;
    private Marker marker;
    private Provider provider;
    private List<Provider> providerList = new ArrayList<>();
    private ClusterManager<MapClusterItem> mClusterManager;

    private final static int MAP_CLUSTER_LIST = 100;
    private final static int MAP_UPDATE_LOCATION = 200;
    private final static int MAP_PROVIDER_DETAILS = 300;

    public static final String FAD_TAG = "fad_map_view";

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList = getArguments().getParcelableArrayList("PROVIDER_LIST");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.providersMap);
        map.getMapAsync(this);
        return view;
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
//        map.setMyLocationEnabled(true);

        mClusterManager = new ClusterManager<>(getActivity(), map);
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        map.setOnCameraMoveListener(this);
        addMarkers();
    }

    private void addMarkers() {
        if (providerList == null || providerList.size() <= 0) {
            Toast.makeText(getActivity(), "No Providers", Toast.LENGTH_LONG).show();
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Provider provider : providerList) {
            for (Office office : provider.getOffices()) {
                LatLng position = new LatLng(Double.valueOf(office.getLat()),
                        Double.valueOf(office.getLong()));
                builder.include(position);

                MapClusterItem item = new MapClusterItem(Double.valueOf(office.getLat()),
                        Double.valueOf(office.getLong()),
                        provider.getDisplayFullName(),
                        office.getAddress(), provider);
                mClusterManager.addItem(item);
            }
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);
//        map.animateCamera(cu);
        map.moveCamera(cu);
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraMoveCanceledListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.providersMap);
        if (map != null) {
            getChildFragmentManager().beginTransaction().remove(map).commit();
        }
    }

    @Override
    public void onCameraMove() {
        if (map != null) {
            mapHandler.removeMessages(MAP_UPDATE_LOCATION);
            mapHandler.sendEmptyMessageDelayed(MAP_UPDATE_LOCATION, 3000);
        }
    }

    private Handler mapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAP_UPDATE_LOCATION:
                    Timber.i("Update Map Location " + map.getCameraPosition().target);
                    break;
                case MAP_PROVIDER_DETAILS:
                    if (marker != null)
                        provider = getProvider(marker.getTitle(), marker.getPosition());
                    if (null != provider)
                        providerDetails(provider);
                    break;
            }
        }
    };

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
        Timber.i("onClusterClick " + cluster.getItems().size());

        startListDialog(cluster.getItems());
        return false;
    }

    private void startListDialog(Collection<MapClusterItem> cluster) {

        ArrayList<Provider> list = new ArrayList<>();
        ProviderListDialog dialog = new ProviderListDialog();
        Bundle bundle = new Bundle();
        for (MapClusterItem item : cluster) {
            list.add(item.getProvider());
        }

        bundle.putParcelableArrayList("PROVIDER_LIST", list);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, MAP_CLUSTER_LIST);
        dialog.show(getChildFragmentManager(), "Filter Dialog");
    }

    private void providerDetails(Provider provider) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, provider);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Timber.i("onInfoWindowClick " + marker.getTitle());
        this.marker = marker;

        if (null != marker) {
            mapHandler.sendEmptyMessage(MAP_PROVIDER_DETAILS);
        }
    }

    @Override
    public void onCameraMoveCanceled() {
        Timber.i("onCameraMoveCanceled ");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_CLUSTER_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                marker = null;
                provider = data.getExtras().getParcelable("PROVIDER");
                mapHandler.sendEmptyMessage(MAP_PROVIDER_DETAILS);
            }
        }
    }

    private Provider getProvider(String title, LatLng position) {
        for (Provider provider : providerList) {
            for (Office office : provider.getOffices()) {
                LatLng officePosition = new LatLng(Double.valueOf(office.getLat()),
                        Double.valueOf(office.getLong()));
                if (provider.getDisplayFullName().contains(title) && officePosition.equals(position)) {
                    return provider;
                }
            }
        }
        return null;
    }
}
