package com.prokarma.myhome.features.fad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.RESTConstants;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
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
    private ProviderDetailsResponse provider;
    private Button searchThisArea;
    private LatLng latlon;
    private LocationResponse location = null;
    private List<ProviderDetailsResponse> providerList = new ArrayList<>();
    private ClusterManager<MapClusterItem> mClusterManager;

    private final static int MAP_CLUSTER_LIST = 100;
    private final static int MAP_UPDATE_LOCATION = 200;
    private final static int MAP_PROVIDER_DETAILS = 300;
    private final static int MAP_ZOOM = 400;

    public static final String FAD_TAG = "fad_map_view";

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList.clear();
            ArrayList<ProviderDetailsResponse> list = getArguments().getParcelableArrayList("PROVIDER_LIST");
            location = FadManager.getInstance().getLocation();
            providerList.addAll(list);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        searchThisArea = (Button) view.findViewById(R.id.searchThisArea);

        final SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentMapView);
        map.getMapAsync(this);

        searchThisArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchThisArea.setVisibility(View.INVISIBLE);
                location = new LocationResponse();
                location.setLat(String.valueOf(latlon.latitude));
                location.setLon(String.valueOf(latlon.longitude));
                location.setDisplayName("Map Search Location");
                FadManager.getInstance().setLocation(location);
                NavigationActivity.eventBus.post(latlon);
            }
        });
        if (null != map)
            updateMap();
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
    public void providerClick(int position) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (null == map) {
            map = googleMap;
            updateMap();
        }
    }

    private void updateMap() {
        //        map.setMyLocationEnabled(true);
        try {
            mClusterManager = new ClusterManager<>(getActivity(), map);
            map.setOnCameraIdleListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setRenderer(new MapClusterRenderer(getActivity(), map, mClusterManager));
            mClusterManager.setOnClusterClickListener(this);
            map.setOnCameraMoveListener(this);
            addMarkers();
        } catch (NullPointerException | IllegalStateException ex) {

        }
    }

    private void addMarkers() {
        try {
            map.clear();
            mClusterManager.clearItems();

            if (providerList == null || providerList.size() <= 0) {
                Timber.i("No Providers");
                currClusterPosition = new LatLng(Double.valueOf(FadManager.getInstance().getLocation().getLat()),
                        Double.valueOf(FadManager.getInstance().getLocation().getLong()));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currClusterPosition, 12.0f);
                map.moveCamera(update);
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (ProviderDetailsResponse provider : providerList) {
                for (Office office : provider.getOffices()) {
                    LatLng position = new LatLng(Double.valueOf(office.getLat()), Double.valueOf(office.getLong()));
                    builder.include(position);

                    MapClusterItem item = new MapClusterItem(position,
                            provider.getDisplayFullName(),
                            office.getAddress(), provider);
                    mClusterManager.addItem(item);
                }
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
//        map.animateCamera(cu);
            map.setMaxZoomPreference(18);
            map.moveCamera(cu);
            map.setOnInfoWindowClickListener(this);
            map.setOnCameraMoveCanceledListener(this);
        } catch (NullPointerException | NumberFormatException | IllegalStateException ex) {
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

    @Override
    public void onCameraMove() {
        if (map != null) {
            Timber.i("Zoom " + map.getCameraPosition().zoom);
            getHandler().removeMessages(MAP_UPDATE_LOCATION);
            getHandler().sendEmptyMessageDelayed(MAP_UPDATE_LOCATION, 200);
        }
    }

    private LatLng currClusterPosition;

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
        Timber.i("onClusterClick " + cluster.getItems().size());

        if (cluster.getSize() <= 0)
            return false;

        if (isClusterSameLocation(cluster.getItems())) {
            startListDialog(cluster.getItems());
            return false;
        }
        //Zoom
        currClusterPosition = cluster.getPosition();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                map.getCameraPosition().zoom + 1.0f);
        map.animateCamera(update);
        getHandler().sendEmptyMessage(MAP_ZOOM);
        return false;
    }

    private void startListDialog(Collection<MapClusterItem> cluster) {

        ArrayList<ProviderDetailsResponse> list = new ArrayList<>();
        ProviderListDialog dialog = new ProviderListDialog();
        Bundle bundle = new Bundle();
        for (MapClusterItem item : cluster) {
            list.add((ProviderDetailsResponse) item.getProvider());
        }

        bundle.putParcelableArrayList("PROVIDER_LIST", list);
        bundle.putBoolean("IS_MAP_CLUSTER", true);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, MAP_CLUSTER_LIST);
        dialog.show(getChildFragmentManager(), "List Dialog");
    }

    private boolean isClusterSameLocation(Collection<MapClusterItem> cluster) {
        Location locCurr = null;
        for (MapClusterItem item : cluster) {
            if (null == locCurr) {
                locCurr = new Location("curLocation");
                locCurr.setLatitude(Double.valueOf(item.getPosition().latitude));
                locCurr.setLongitude(Double.valueOf(item.getPosition().longitude));
            } else {
                Location loc = new Location("newLocation");
                loc.setLatitude(Double.valueOf(item.getPosition().latitude));
                loc.setLongitude(Double.valueOf(item.getPosition().longitude));
                float distance = locCurr.distanceTo(loc);
                if (distance > 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void providerDetails(ProviderDetailsResponse provider) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, provider.convertToNewProviderDetails());
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Timber.i("onInfoWindowClick " + marker.getTitle());
        this.marker = marker;

        if (null != marker) {
            getHandler().sendEmptyMessage(MAP_PROVIDER_DETAILS);
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
                getHandler().sendEmptyMessage(MAP_PROVIDER_DETAILS);
            }
        }
    }

    private ProviderDetailsResponse getProvider(String title, LatLng position) {
        try {
            for (ProviderDetailsResponse provider : providerList) {
                for (Office office : provider.getOffices()) {
                    LatLng officePosition = new LatLng(Double.valueOf(office.getLat()),
                            Double.valueOf(office.getLong()));
                    if (provider.getDisplayFullName().contains(title) && officePosition.equals(position)) {
                        return provider;
                    }
                }
            }
            return null;
        } catch (NullPointerException | IllegalStateException | NumberFormatException ex) {
            return null;
        }
    }

    static float DISTANCE_SEARCH_THIS_AREA =
            Float.valueOf(RESTConstants.PROVIDER_DISTANCE) * 1609.34f; // PROVIDER_DISTANCE miles in meters

    private boolean isLocationSearchable() {
        try {
            Location locCurr = new Location("curLocation");
            locCurr.setLatitude(Double.valueOf(location.getLat()));
            locCurr.setLongitude(Double.valueOf(location.getLong()));

            Location newLoc = new Location("newLocation");
            newLoc.setLatitude(Double.valueOf(map.getCameraPosition().target.latitude));
            newLoc.setLongitude(Double.valueOf(map.getCameraPosition().target.longitude));
            float distance = locCurr.distanceTo(newLoc); // distance in meters
            Timber.i("Search Map Location " + location);

            return distance >= DISTANCE_SEARCH_THIS_AREA;
        } catch (NumberFormatException | NullPointerException | IllegalStateException ex) {
            return false;
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
                if (null != getActivity() && isAdded()) {
                    BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.mipmap.one_pin);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawable.getBitmap()));
                }
            } catch (NullPointerException ex) {
            }
        }
    }


    @Subscribe
    public void updateNewPageList(FadFragment.NewPageData pageData) {
        try {
            Timber.i("update new page list " + pageData.getList().size());
            this.providerList.addAll(pageData.getList());
            updateMap();
        } catch (NullPointerException ex) {
        }
    }

    private static class MapViewHandler extends Handler {
        private final WeakReference<MapViewFragment> mMapViewFragment;

        private MapViewHandler(MapViewFragment mapViewFragment) {
            mMapViewFragment = new WeakReference<MapViewFragment>(mapViewFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MapViewFragment mapViewFragment = mMapViewFragment.get();
            if (mapViewFragment != null) {
                switch (msg.what) {
                    case MAP_UPDATE_LOCATION:
                        Timber.i("Update Map Location " + mapViewFragment.map.getCameraPosition().target);
                        if (mapViewFragment.isLocationSearchable()) {
                            mapViewFragment.latlon = mapViewFragment.map.getCameraPosition().target;
                            mapViewFragment.searchThisArea.setVisibility(View.VISIBLE);
                        } else {
                            mapViewFragment.searchThisArea.setVisibility(View.GONE);
                        }
                        break;

                    case MAP_PROVIDER_DETAILS:
                        if (mapViewFragment.marker != null){
                            mapViewFragment.provider = mapViewFragment.getProvider(mapViewFragment.marker.getTitle(), mapViewFragment.marker.getPosition());
                        }

                        if (null != mapViewFragment.provider){
                            mapViewFragment.providerDetails(mapViewFragment.provider);
                        }

                        break;

                    case MAP_ZOOM:
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mapViewFragment.currClusterPosition,
                                mapViewFragment.map.getCameraPosition().zoom + 1.0f);
                        mapViewFragment.map.animateCamera(update);
                        break;
                }
            }
        }
    }

    private Handler getHandler() {
        return new MapViewFragment.MapViewHandler(this);
    }
}
