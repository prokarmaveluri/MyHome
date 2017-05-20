package com.dignityhealth.myhome.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.dignityhealth.myhome.features.fad.Office;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
            PackageInfo info = context.getPackageManager().getPackageInfo("com.dignityhealth.myhome",
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
        if (googleMap != null) {
            for (Office office : offices) {
                markers.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(office.getLat()), Double.parseDouble(office.getLong())))
                        .title(office.getName() != null ? office.getName() : office.getAddress1())
                        .snippet(office.getAddress1())
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
     * Used to calculate the zoom if multiple markers need to fit on the map
     * Inspired From: http://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
     *
     * @param markers the markers that need to fit on the map
     * @return
     */
    public static CameraUpdate calculateZoom(Context context, ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        return cu;
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
        if (markers.size() <= 1) {
            googleMap.setMaxZoomPreference(15f);
        }

        googleMap.animateCamera(MapUtil.calculateZoom(context, markers));
    }
}
