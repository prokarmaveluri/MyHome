package com.dignityhealth.myhome.features.fad.details.booking.req.validation;

/**
 * Created by cmajji on 6/12/17.
 */

public class RegIncluded {

    private String type;
    private String id;
    private RegAttributes attributes;

    public RegIncluded(String type, String id, RegAttributes attributes) {
        this.type = type;
        this.id = id;
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RegAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(RegAttributes attributes) {
        this.attributes = attributes;
    }
}
