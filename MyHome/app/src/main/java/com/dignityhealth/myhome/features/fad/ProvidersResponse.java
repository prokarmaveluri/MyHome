package com.dignityhealth.myhome.features.fad;

import java.util.List;


/**
 * Created by cmajji on 5/12/17.
 */


public class ProvidersResponse {

    private List<Provider> Providers;
    private List<CommonModel> AcceptsNewPatients;
    private List<CommonModel> Specialties;
    private List<CommonModel> Genders;
    private List<CommonModel> Languages;
    private List<CommonModel> Hospitals;
    private List<CommonModel> Practices;
    private Integer SearchDistanceMiles;
    private Integer CurrentPageNum;
    private Integer CurrentPageResults;
    private Integer NumResults;
    private String SearchTerm;
    private List<Object> NearbyCities;
    private String DataUrl;
    private String SearchLocation;
    private List<Object> ServiceErrors;
    private Object LocationError;

    public List<Provider> getProviders() {
        return Providers;
    }

    public void setProviders(List<Provider> providers) {
        this.Providers = providers;
    }

    public List<CommonModel> getAcceptsNewPatients() {
        return AcceptsNewPatients;
    }

    public List<CommonModel> getSpecialties() {
        return Specialties;
    }

    public List<CommonModel> getGenders() {
        return Genders;
    }

    public List<CommonModel> getLanguages() {
        return Languages;
    }

    public List<CommonModel> getHospitals() {
        return Hospitals;
    }

    public List<CommonModel> getPractices() {
        return Practices;
    }

    public Integer getSearchDistanceMiles() {
        return SearchDistanceMiles;
    }

    public Integer getCurrentPageNum() {
        return CurrentPageNum;
    }

    public Integer getCurrentPageResults() {
        return CurrentPageResults;
    }

    public Integer getNumResults() {
        return NumResults;
    }

    public String getSearchTerm() {
        return SearchTerm;
    }

    public List<Object> getNearbyCities() {
        return NearbyCities;
    }

    public String getDataUrl() {
        return DataUrl;
    }

    public String getSearchLocation() {
        return SearchLocation;
    }

    public List<Object> getServiceErrors() {
        return ServiceErrors;
    }

    public Object getLocationError() {
        return LocationError;
    }
}