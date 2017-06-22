package com.dignityhealth.myhome.features.fad.details.booking.req.validation;

import java.util.List;

/**
 * Created by cmajji on 6/12/17.
 */

public class RegAttributes {

    private String kind;
    private List<String> attributes = null;
    private Boolean allowBlank;
    private String with;
    private Boolean allowNil;
    private String accept;
    private String is;
    private List<String> fields = null;
    private String company;
    private String name;
    private Integer ageLimitThreshold;

    public RegAttributes(String company, String name) {
        this.company = company;
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public Boolean getAllowBlank() {
        return allowBlank;
    }

    public void setAllowBlank(Boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public Boolean getAllowNil() {
        return allowNil;
    }

    public void setAllowNil(Boolean allowNil) {
        this.allowNil = allowNil;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getIs() {
        return is;
    }

    public void setIs(String is) {
        this.is = is;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAgeLimitThreshold() {
        return ageLimitThreshold;
    }

    public void setAgeLimitThreshold(Integer ageLimitThreshold) {
        this.ageLimitThreshold = ageLimitThreshold;
    }

}
