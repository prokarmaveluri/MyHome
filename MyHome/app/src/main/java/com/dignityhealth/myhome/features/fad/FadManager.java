package com.dignityhealth.myhome.features.fad;

/**
 * Created by cmajji on 5/13/17.
 */

public class FadManager {

    private LocationResponse location;
    private LocationResponse currentLocation;

    private static final FadManager ourInstance = new FadManager();

    public static FadManager getInstance() {
        return ourInstance;
    }

    private FadManager() {
    }

    /**
     * get user location fetched from REST API
     *
     * @return
     */
    public LocationResponse getLocation() {
        //return new LocationResponse("Belmont", "CA", "", "37.518341", "-122.276207", "Belmont, CA");
        return location;
    }

    /**
     * set user location from REST API
     *
     * @param location
     */
    public void setLocation(LocationResponse location) {
        this.location = location;
    }

    public LocationResponse getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationResponse currentLocation) {
        this.currentLocation = currentLocation;
    }
}
