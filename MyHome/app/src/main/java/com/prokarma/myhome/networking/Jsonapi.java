package com.prokarma.myhome.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kwelsh on 6/12/17.
 */

public class Jsonapi {

    @SerializedName("version")
    @Expose
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