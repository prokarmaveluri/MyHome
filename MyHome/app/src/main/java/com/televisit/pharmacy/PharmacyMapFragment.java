package com.televisit.pharmacy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.pharmacy.PharmacyType;
import com.americanwell.sdk.manager.SDKValidatedCallback;
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
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.MapClusterItem;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.MapUtil;
import com.prokarma.myhome.utils.RESTConstants;
import com.squareup.otto.Subscribe;
import com.televisit.AwsManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyMapFragment extends Fragment implements
        PharmacyListAdapter.IPharmacyClick,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        ClusterManager.OnClusterClickListener<MapClusterItem>,
        GoogleMap.OnInfoWindowClickListener,
        TextView.OnEditorActionListener {

    public static final String PHARMACIES_TAG = "pharmacies_tag";

    public final static int MAP_CLUSTER_LIST = 100;
    private final static int MAP_UPDATE_LOCATION = 200;
    private final static int MAP_PHARMACY_DETAILS = 300;
    private final static int MAP_ZOOM = 400;


    private EditText pharmacySearch;
    private ProgressBar progressBar;
    private GoogleMap map;
    private ClusterManager<MapClusterItem> mClusterManager;

    private Marker marker;
    private Pharmacy pharmacy;
    private Button searchThisArea;
    private LatLng latlon;
    private LocationResponse location = null;

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
        searchThisArea = (Button) view.findViewById(R.id.searchThisArea);
        progressBar = (ProgressBar) view.findViewById(R.id.search_progress);

        pharmacySearch = (EditText) view.findViewById(R.id.pharmacySearch);
        pharmacySearch.setOnEditorActionListener(this);
        searchCancelClickEvent();

        location = FadManager.getInstance().getLocation();

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

        if (null != map) {
            updateMap();
        }
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
    public void onDestroy() {
        super.onDestroy();
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentMapView);
        if (map != null) {
            getChildFragmentManager().beginTransaction().remove(map).commit();
        }
    }

    @Override
    public void onCameraMoveCanceled() {
        Timber.d("map. onCameraMoveCanceled ");
    }

    @Override
    public void onCameraMove() {
        if (map != null) {
            Timber.d("map. Zoom " + map.getCameraPosition().zoom);
            getHandler().removeMessages(MAP_UPDATE_LOCATION);
            getHandler().sendEmptyMessageDelayed(MAP_UPDATE_LOCATION, 200);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Timber.d("map. onInfoWindowClick " + marker.getTitle());
        this.marker = marker;

        if (null != marker) {
            getHandler().sendEmptyMessage(MAP_PHARMACY_DETAILS);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (null == map) {
            map = googleMap;
            updateMap();
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
        Timber.d("map. onClusterClick " + cluster.getItems().size());

        if (cluster.getSize() <= 0) {
            return false;
        }

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
        try {
            ArrayList<Pharmacy> list = new ArrayList<>();
            PharmacyListDialog dialog = new PharmacyListDialog();
            Bundle bundle = new Bundle();
            for (MapClusterItem item : cluster) {
                list.add((Pharmacy) item.getProvider());
            }

            bundle.putParcelableArrayList("PHARMACY_LIST", list);
            bundle.putBoolean("IS_MAP_CLUSTER", true);
            dialog.setArguments(bundle);

            //dialog.setTargetFragment(this, MAP_CLUSTER_LIST);
            dialog.show(getChildFragmentManager(), PharmacyListDialog.PHARMACY_LIST_DIALOG_TAG);

        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }
    }

    private boolean isClusterSameLocation(Collection<MapClusterItem> cluster) {
        Location locCurr = null;
        for (MapClusterItem item : cluster) {
            if (null == locCurr) {
                locCurr = new Location("curLocation");
                locCurr.setLatitude(item.getPosition().latitude);
                locCurr.setLongitude(item.getPosition().longitude);
            } else {
                Location loc = new Location("newLocation");
                loc.setLatitude(item.getPosition().latitude);
                loc.setLongitude(item.getPosition().longitude);
                float distance = locCurr.distanceTo(loc);
                if (distance > 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMap() {
        if (map == null) {
            return;
        }
        //map.setMyLocationEnabled(true);
        try {
            mClusterManager = new ClusterManager<>(getActivity(), map);
            map.setOnCameraIdleListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setRenderer(new MapClusterRenderer(getActivity(), map, mClusterManager));
            mClusterManager.setOnClusterClickListener(this);
            map.setOnCameraMoveListener(this);
            addMarkers();
        } catch (NullPointerException | IllegalStateException ex) {
            Timber.w(ex);
        }
    }

    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            handleMarkerClick(marker);
            marker.showInfoWindow(); //Won't fit with the zoom if states apart
            return true;
        }
    };

    //Set address text, then make sure to change selected icon
    private void handleMarkerClick(Marker marker) {

    }

    @Override
    public void pharmacyClick(Pharmacy pharmacy) {

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
                Timber.w(ex);
            }
        }
    }

    private LatLng currClusterPosition;

    private void addMarkers() {
        try {
            map.clear();
            mClusterManager.clearItems();

            if (AwsManager.getInstance().getPharmacies() == null || AwsManager.getInstance().getPharmacies().size() <= 0) {
                Timber.i("No Pharmacies");
                currClusterPosition = new LatLng(Double.valueOf(FadManager.getInstance().getLocation().getLat()),
                        Double.valueOf(FadManager.getInstance().getLocation().getLong()));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currClusterPosition, 12.0f);
                map.moveCamera(update);
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Pharmacy pharmacy : AwsManager.getInstance().getPharmacies()) {

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
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Timber.i("Dialog. onActivityResult. requestCode = " + requestCode + ". resultCode = " + resultCode);

        if (requestCode == MAP_CLUSTER_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                marker = null;
                pharmacy = data.getExtras().getParcelable("PHARMACY");

                if (pharmacy != null) {
                    launchPharmacyDetails(pharmacy);
                } else {
                    getHandler().sendEmptyMessage(MAP_PHARMACY_DETAILS);
                }
            }
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

    public void launchPharmacyDetails(Pharmacy pharmacy) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PharmacyDetailsFragment.PHARMACY_KEY, pharmacy);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_PHARMACY_DETAILS, bundle);
    }

    private static class MapViewHandler extends Handler {
        private final WeakReference<PharmacyMapFragment> mMapViewFragment;

        private MapViewHandler(PharmacyMapFragment mapViewFragment) {
            mMapViewFragment = new WeakReference<PharmacyMapFragment>(mapViewFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            PharmacyMapFragment mapViewFragment = mMapViewFragment.get();
            if (mapViewFragment != null) {
                switch (msg.what) {

                    case MAP_UPDATE_LOCATION:
                        if (mapViewFragment.map != null) {
                            if (mapViewFragment.isLocationSearchable()) {
                                mapViewFragment.latlon = mapViewFragment.map.getCameraPosition().target;
                                mapViewFragment.searchThisArea.setVisibility(View.GONE);
                            } else {
                                mapViewFragment.searchThisArea.setVisibility(View.GONE);
                            }
                        }
                        break;

                    case MAP_PHARMACY_DETAILS:
                        if (mapViewFragment.marker != null) {
                            Pharmacy pharmacy = MapUtil.getPharmacy(mapViewFragment.marker, AwsManager.getInstance().getPharmacies());
                            if (pharmacy != null) {
                                mapViewFragment.launchPharmacyDetails(pharmacy);
                            }
                        }
                        break;

                    case MAP_ZOOM:
                        if (mapViewFragment.map != null && mapViewFragment.currClusterPosition != null) {

                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                                    mapViewFragment.currClusterPosition,
                                    mapViewFragment.map.getCameraPosition().zoom + 1.0f);
                            mapViewFragment.map.animateCamera(update);
                        }
                        break;
                }
            }
        }
    }

    private Handler getHandler() {
        return new PharmacyMapFragment.MapViewHandler(this);
    }

    private void searchCancelClickEvent() {
        pharmacySearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if ((int) event.getRawX() >= (pharmacySearch.getRight() -
                            pharmacySearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        pharmacySearch.setText("");

                        CommonUtil.hideSoftKeyboard(getActivity());

                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void getPharmaciesByZip(final String zipCode) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().getPharmacies(
                AwsManager.getInstance().getConsumer(),
                PharmacyType.MAIL_ORDER,
                null,
                null,
                zipCode, new SDKValidatedCallback<List<Pharmacy>, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(List<Pharmacy> pharmacies, SDKError sdkError) {
                        if (sdkError == null) {

                            AwsManager.getInstance().setPharmacies(pharmacies);

                            PharmacyMapFragment.SearchPharmacies object = new PharmacyMapFragment.SearchPharmacies();
                            object.setPharmacies(pharmacies);
                            if (null != getActivity()) {
                                NavigationActivity.eventBus.post(object);
                                CommonUtil.hideSoftKeyboard(getActivity());
                            }

                            updateMap();
                        }

                        progressBar.setVisibility(View.GONE);

                        if (pharmacies == null || pharmacies.size() == 0) {
                            CommonUtil.showToast(getContext(), getString(R.string.no_pharmacies_found_for) + zipCode);
                        }

                        if (null != getActivity()) {
                            CommonUtil.hideSoftKeyboard(getActivity());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (pharmacySearch.getText().toString().length() == 5) {
                getPharmaciesByZip(pharmacySearch.getText().toString());
            } else {
                CommonUtil.showToast(getContext(),getContext().getString(R.string.zip_invalid));
            }
            return true;
        }
        return false;
    }

    public class SearchPharmacies {
        private List<Pharmacy> pharmacies;

        public List<Pharmacy> getPharmacies() {
            return pharmacies;
        }

        public void setPharmacies(List<Pharmacy> pharmacies) {
            this.pharmacies = pharmacies;
        }
    }

    static float DISTANCE_SEARCH_THIS_AREA =
            Float.valueOf(RESTConstants.PHARMACY_DISTANCE_MILES) * 1609.34f; // convert miles in meters

    private boolean isLocationSearchable() {
        try {
            Location locCurr = new Location("curLocation");
            locCurr.setLatitude(Double.valueOf(location.getLat()));
            locCurr.setLongitude(Double.valueOf(location.getLong()));

            Location newLoc = new Location("newLocation");
            newLoc.setLatitude(map.getCameraPosition().target.latitude);
            newLoc.setLongitude(map.getCameraPosition().target.longitude);
            float distance = locCurr.distanceTo(newLoc); // distance in meters

            return distance >= DISTANCE_SEARCH_THIS_AREA;
        } catch (NumberFormatException | NullPointerException | IllegalStateException ex) {
            return false;
        }
    }
}
