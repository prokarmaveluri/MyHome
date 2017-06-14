package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by cmajji on 6/8/17.
 */

public class CreateAppointmentRequest {
    private Jsonapi jsonapi;
    private Data data;

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Jsonapi {
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

    }
}