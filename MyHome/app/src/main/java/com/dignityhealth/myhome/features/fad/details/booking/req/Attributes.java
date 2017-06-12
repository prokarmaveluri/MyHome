package com.dignityhealth.myhome.features.fad.details.booking.req;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kwelsh on 6/12/17.
 */

public class Attributes {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("attributes")
    @Expose
    private List<String> attributes = null;
    @SerializedName("allow-blank")
    @Expose
    private Boolean allowBlank;
    @SerializedName("with")
    @Expose
    private String with;
    @SerializedName("allow-nil")
    @Expose
    private Boolean allowNil;
    @SerializedName("accept")
    @Expose
    private String accept;
    @SerializedName("is")
    @Expose
    private String is;
    @SerializedName("fields")
    @Expose
    private List<String> fields = null;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("age-limit-threshold")
    @Expose
    private Integer ageLimitThreshold;

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