package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Jsonapi {
    private String version;

    public Jsonapi(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}