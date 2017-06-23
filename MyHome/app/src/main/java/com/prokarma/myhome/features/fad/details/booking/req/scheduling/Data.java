package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Data {

    private String type;
    private Attributes attributes;
    private Relationships relationships;

    public Data(String type, Attributes attributes, Relationships relationships) {
        this.type = type;
        this.attributes = attributes;
        this.relationships = relationships;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }



}
