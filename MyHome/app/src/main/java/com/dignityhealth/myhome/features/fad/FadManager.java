package com.dignityhealth.myhome.features.fad;

/**
 * Created by cmajji on 5/13/17.
 */

public class FadManager {

    private LocationResponse location;

    private static final FadManager ourInstance = new FadManager();

    public static FadManager getInstance() {
        return ourInstance;
    }

    private FadManager() {
    }


    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }
}
