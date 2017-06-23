package com.prokarma.myhome.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.Office;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/19/17.
 */

public class MapUtil {

    /**
     * Can be used to check key hashes
     *
     * @param context
     */
    public static void checkMapsKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.prokarma.myhome",
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

    /**
     * Adds Map Markers for Offices
     *
     * @param context
     * @param googleMap        the google map object we're adding markers to
     * @param offices          an ArrayList of Offices
     * @param bitmapDescriptor the icon of the map markers
     * @param listener         the clickListener of the map markers
     * @return an ArrayList of all the markers that were added
     */
    public static ArrayList<Marker> addMapMarkers(Context context, @NonNull GoogleMap googleMap, @NonNull ArrayList<Office> offices, @NonNull BitmapDescriptor bitmapDescriptor, GoogleMap.OnMarkerClickListener listener) {
        ArrayList<Marker> markers = new ArrayList<>();
        if (googleMap != null && offices != null) {
            for (Office office : offices) {
                markers.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(office.getLat()), Double.parseDouble(office.getLong())))
                        .title(office.getName() != null ? office.getName() : office.getAddress1())
                        .snippet(office.getAddress1() != null ? office.getAddress1() + "\n" + office.getAddress() : context.getString(R.string.address_unknown))
                        .icon(bitmapDescriptor)));

                if (listener != null) {
                    googleMap.setOnMarkerClickListener(listener);
                }
            }
        } else {
            Timber.e("GoogleMap object provided was null. Returning empty set of markers...");
        }

        return markers;
    }

    /**
     * Compares address for office versus the address we formatted for the marker in MapUtil.addMapMarker
     *
     * @param office
     * @param marker
     * @return if the office has the same address as the marker, return true. Otherwise, return false.
     */
    public static boolean isOfficeSelected(Office office, Marker marker) {
        if ((office.getAddress1() + "\n" + office.getAddress()).equalsIgnoreCase(marker.getSnippet())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Used to calculate the zoom if multiple markers need to fit on the map
     * Inspired From: http://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
     *
     * @param markers the markers that need to fit on the map
     * @return
     */
    @Nullable
    public static CameraUpdate calculateZoom(Context context, ArrayList<Marker> markers) {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            //the include method will calculate the min and max bound.
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }

            LatLngBounds bounds = builder.build();

            int width = context.getResources().getDisplayMetrics().widthPixels;
            //int height = context.getResources().getDisplayMetrics().heightPixels; //only if map is using entire height
            int height = DeviceDisplayManager.dpToPx(context, 160);     //The Height of the Map View
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            return cu;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    /**
     * Handles the zooming logic for maps.
     * Maps with one or fewer markers need to make sure max zoom is set or the zoom will be way too close
     *
     * @param context   the context
     * @param googleMap the map needing zoom help
     * @param markers   the markers the need to be zoomed in on
     */
    public static void zoomMap(Context context, GoogleMap googleMap, ArrayList<Marker> markers) {
        try {
            if (markers.size() <= 1) {
                googleMap.setMaxZoomPreference(15f);
            }

//        googleMap.animateCamera(MapUtil.calculateZoom(context, markers));
            googleMap.moveCamera(MapUtil.calculateZoom(context, markers));
        } catch (NullPointerException ex) {

        }
    }

    public static void setMarkerSelectedIcon(Context context, ArrayList<Marker> markers, String address) {
        try {
            for (Marker marker : markers) {
                if (marker.getSnippet().equalsIgnoreCase(address)) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_blue));
                }
            }
        } catch (NullPointerException ex) {

        }
    }

    public static void clearMarkers(Context context, GoogleMap googleMap) {
        if (null != googleMap)
            googleMap.clear();
    }
}
