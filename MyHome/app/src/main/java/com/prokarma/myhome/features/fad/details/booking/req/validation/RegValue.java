package com.prokarma.myhome.features.fad.details.booking.req.validation;

import com.prokarma.myhome.networking.Jsonapi;

import java.util.List;

/**
 * Created by cmajji on 6/12/17.
 */

public class RegValue {

    private Jsonapi jsonapi;
    private RegData data;
    private List<RegIncluded> included = null;

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public RegData getData() {
        return data;
    }

    public void setData(RegData data) {
        this.data = data;
    }

    public List<RegIncluded> getIncluded() {
        return included;
    }

    public void setIncluded(List<RegIncluded> included) {
        this.included = included;
    }

}