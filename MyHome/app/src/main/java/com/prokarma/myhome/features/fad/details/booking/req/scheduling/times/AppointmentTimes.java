package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.Jsonapi;

import java.util.List;

public class AppointmentTimes {

    @SerializedName("jsonapi")
    @Expose
    private Jsonapi jsonapi;
    @SerializedName("data")
    @Expose
    private List<AppointmentData> data = null;

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public List<AppointmentData> getData() {
        return data;
    }

    public void setData(List<AppointmentData> data) {
        this.data = data;
    }
}
