package com.televisit.pharmacy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
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
import com.prokarma.myhome.features.fad.MapClusterItem;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.MapUtil;
import com.squareup.otto.Subscribe;
import com.televisit.AwsManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        ClusterManager.OnClusterClickListener<MapClusterItem>,
        GoogleMap.OnInfoWindowClickListener,
        TextView.OnEditorActionListener {

    private EditText pharmacySearch;
    private ProgressBar progressBar;
    private GoogleMap map;
    private ClusterManager<MapClusterItem> mClusterManager;

    public static final String PHARMACIES_TAG = "pharmacies_tag";

    private final static int MAP_CLUSTER_LIST = 100;
    private final static int MAP_UPDATE_LOCATION = 200;
    private final static int MAP_PROVIDER_DETAILS = 300;
    private final static int MAP_ZOOM = 400;

    private Marker marker;

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
        progressBar = (ProgressBar) view.findViewById(R.id.search_progress);

        pharmacySearch = (EditText) view.findViewById(R.id.pharmacySearch);
        pharmacySearch.setOnEditorActionListener(this);
        searchCancelClickEvent();

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
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

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
    public void onMapReady(GoogleMap googleMap) {
        if (null == map) {
            map = googleMap;
        }
        updateMap();
    }

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
        Timber.i("onClusterClick " + cluster.getItems().size());

        if (cluster.getSize() <= 0) {
            return false;
        }

        //Zoom
        currClusterPosition = cluster.getPosition();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                map.getCameraPosition().zoom + 1.0f);
        map.animateCamera(update);
        return false;
    }

    private void updateMap() {
        try {
            //map.setMyLocationEnabled(true);
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

    private void launchPharmacyDetails(Pharmacy pharmacy) {
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
                    case MAP_PROVIDER_DETAILS:
                        if (mapViewFragment.marker != null) {
                            Pharmacy pharmacy = MapUtil.getPharmacy(mapViewFragment.marker, AwsManager.getInstance().getPharmacies());
                            if (pharmacy != null) {
                                mapViewFragment.launchPharmacyDetails(pharmacy);
                            }
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

    private void getPharmaciesByZip(String zipCode) {

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().getPharmacies(
                AwsManager.getInstance().getConsumer(),
                null,
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
                Toast.makeText(getContext(), "Invalid zip code", Toast.LENGTH_SHORT).show();
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
}
