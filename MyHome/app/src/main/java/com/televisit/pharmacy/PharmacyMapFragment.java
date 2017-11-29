package com.televisit.pharmacy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.MapClusterItem;
import com.squareup.otto.Subscribe;
import com.televisit.SDKUtils;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener,
        ClusterManager.OnClusterClickListener<MapClusterItem>,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap map;
    private ClusterManager<MapClusterItem> mClusterManager;

    public static final String PHARMACIES_TAG = "pharmacies_tag";

    public PharmacyMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static PharmacyMapFragment newInstance() {
        PharmacyMapFragment fragment = new PharmacyMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.pharmacy));
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        final SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentMapView);
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationActivity.eventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        NavigationActivity.eventBus.unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (null == map) {
            map = googleMap;
        }
        updateMap();
    }

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
        Timber.i("onClusterClick " + cluster.getItems().size());

        if (cluster.getSize() <= 0)
            return false;

        //Zoom
        currClusterPosition = cluster.getPosition();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                map.getCameraPosition().zoom + 1.0f);
        map.animateCamera(update);
        return false;
    }

    private void updateMap() {
        //        map.setMyLocationEnabled(true);
        try {
            mClusterManager = new ClusterManager<>(getActivity(), map);
            map.setOnCameraIdleListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setRenderer(new PharmacyMapFragment.MapClusterRenderer(getActivity(), map, mClusterManager));
            mClusterManager.setOnClusterClickListener(this);
            map.setOnCameraMoveListener(this);
            addMarkers();
        } catch (NullPointerException | IllegalStateException ex) {
            Timber.e(ex);
        }
    }


    public class MapClusterRenderer extends DefaultClusterRenderer<MapClusterItem> {

        public MapClusterRenderer(Context context, GoogleMap map,
                                  ClusterManager<MapClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected int getColor(int clusterSize) {
            return Color.parseColor("#097288");
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MapClusterItem> cluster) {
            return cluster.getSize() > 1;
        }

        @Override
        protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            try {
                if (isAdded()) {
                    BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.mipmap.one_pin);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawable.getBitmap()));
                }
            } catch (NullPointerException ex) {
                Timber.e(ex);
            }
        }
    }

    private LatLng currClusterPosition;

    private void addMarkers() {
        try {
            map.clear();
            mClusterManager.clearItems();

            if (SDKUtils.getInstance().getPharmacies() == null || SDKUtils.getInstance().getPharmacies().size() <= 0) {
                Timber.i("No Pharmacies");
                currClusterPosition = new LatLng(Double.valueOf(FadManager.getInstance().getLocation().getLat()),
                        Double.valueOf(FadManager.getInstance().getLocation().getLong()));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currClusterPosition, 12.0f);
                map.moveCamera(update);
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Pharmacy pharmacy : SDKUtils.getInstance().getPharmacies()) {

                LatLng position = new LatLng(Double.valueOf(pharmacy.getLatitude()),
                        Double.valueOf(pharmacy.getLongitude()));
                builder.include(position);

                MapClusterItem item = new MapClusterItem(position,
                        pharmacy.getName(),
                        getPharmacyAddress(pharmacy), pharmacy);
                mClusterManager.addItem(item);
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
            map.setMaxZoomPreference(18);
            map.moveCamera(cu);
            map.setOnInfoWindowClickListener(this);
            map.setOnCameraMoveCanceledListener(this);
        } catch (NullPointerException | NumberFormatException | IllegalStateException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentMapView);
        if (map != null) {
            getChildFragmentManager().beginTransaction().remove(map).commit();
        }
    }

    @Subscribe
    public void searchPharmacies(PharmacyListFragment.SearchPharmacies object) {
        updateMap();
    }

    private String getPharmacyAddress(Pharmacy pharmacy) {
        return pharmacy.getAddress().getAddress1() + ", " + pharmacy.getAddress().getCity() + ", "
                + pharmacy.getAddress().getState().getCode() + " " + pharmacy.getAddress().getZipCode();
    }
}
