package com.prokarma.myhome.features.fad;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;

/**
 * Created by cmajji on 5/24/17.
 */

public class MapClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private ProviderDetailsResponse provider;

    public MapClusterItem(LatLng position) {
        mPosition = position;
        mTitle = "";
        mSnippet = "";
    }

    public MapClusterItem(LatLng position, String title, String snippet,
                          ProviderDetailsResponse provider) {
        mPosition = position;
        mTitle = title;
        mSnippet = snippet;
        this.provider = provider;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public ProviderDetailsResponse getProvider() {
        return provider;
    }
}
