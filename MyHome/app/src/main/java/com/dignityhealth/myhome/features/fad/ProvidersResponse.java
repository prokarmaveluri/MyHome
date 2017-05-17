package com.dignityhealth.myhome.features.fad;

import com.google.gson.annotations.SerializedName;

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

    public class Office {

        private String Name;
        private String Address1;
        private Object Address2;
        private String City;
        private String State;
        private String ZipCode;
        private String Phone;
        private Object Fax;
        private Object Url;
        private String Lat;
        @SerializedName(value = "Long")
        private String lon;
        private Integer SortRank;
        private String DistanceMilesFromSearch;
        private String DirectionsLink;
        private String Hash;
        private String LatLongHash;
        private Object Appointments;
        private Boolean LocationMatch;

        public String getName() {
            return Name;
        }

        public String getAddress1() {
            return Address1;
        }

        public Object getAddress2() {
            return Address2;
        }

        public String getCity() {
            return City;
        }

        public String getState() {
            return State;
        }

        public String getZipCode() {
            return ZipCode;
        }

        public String getPhone() {
            return Phone;
        }

        public Object getFax() {
            return Fax;
        }

        public Object getUrl() {
            return Url;
        }

        public String getLat() {
            return Lat;
        }

        public String getLong() {
            return lon;
        }

        public Integer getSortRank() {
            return SortRank;
        }

        public String getDistanceMilesFromSearch() {
            return DistanceMilesFromSearch;
        }

        public String getDirectionsLink() {
            return DirectionsLink;
        }

        public String getHash() {
            return Hash;
        }

        public String getLatLongHash() {
            return LatLongHash;
        }

        public Object getAppointments() {
            return Appointments;
        }

        public Boolean getLocationMatch() {
            return LocationMatch;
        }

        public String getAddress() {
            StringBuilder fullAddress = new StringBuilder();
            if (null != City)
                fullAddress.append(City);
            if (null != State)
                fullAddress.append(", " + State);
            if (null != ZipCode)
                fullAddress.append(" " + ZipCode);
            return fullAddress.toString();
        }
    }

    public class Provider {

        private Integer RecordNumber;
        private String ProviderId;
        private String Npi;
        private Object Title;
        private String FirstName;
        private Object MiddleName;
        private Object MiddleInitialDot;
        private String LastName;
        private String DisplayFullName;
        private String DisplayLastName;
        private Object DisplayLastNamePlural;
        private String DateOfBirth;
        private Object CommonModel;
        private Object YearsOfExperience;
        private Boolean CommonModels;
        private String ImageUrl;
        private Object Quote;
        private Object Philosophy;
        private Object InMyOwnWords;
        private List<String> Specialties;
        private List<String> Languages;
        private Object Degree;
        private Object MedicalSchools;
        private Object Residencies;
        private Object Fellowships;
        private Object Internships;
        private Object Practicums;
        private Object Facilities;
        private List<Office> Offices;
        private Object Memberships;
        private Object Certifications;
        private Object Awards;
        private Boolean HasAppointments;
        private Object ServiceErrors;

        public Integer getRecordNumber() {
            return RecordNumber;
        }

        public String getProviderId() {
            return ProviderId;
        }

        public String getNpi() {
            return Npi;
        }

        public Object getTitle() {
            return Title;
        }

        public String getFirstName() {
            return FirstName;
        }

        public Object getMiddleName() {
            return MiddleName;
        }

        public Object getMiddleInitialDot() {
            return MiddleInitialDot;
        }

        public String getLastName() {
            return LastName;
        }

        public String getDisplayFullName() {
            return DisplayFullName;
        }

        public String getDisplayLastName() {
            return DisplayLastName;
        }

        public Object getDisplayLastNamePlural() {
            return DisplayLastNamePlural;
        }

        public String getDateOfBirth() {
            return DateOfBirth;
        }

        public Object getCommonModel() {
            return CommonModel;
        }

        public Object getYearsOfExperience() {
            return YearsOfExperience;
        }

        public Boolean getCommonModels() {
            return CommonModels;
        }

        public String getImageUrl() {
            return ImageUrl;
        }

        public Object getQuote() {
            return Quote;
        }

        public Object getPhilosophy() {
            return Philosophy;
        }

        public Object getInMyOwnWords() {
            return InMyOwnWords;
        }

        public List<String> getSpecialties() {
            return Specialties;
        }

        public List<String> getLanguages() {
            return Languages;
        }

        public Object getDegree() {
            return Degree;
        }

        public Object getMedicalSchools() {
            return MedicalSchools;
        }

        public Object getResidencies() {
            return Residencies;
        }

        public Object getFellowships() {
            return Fellowships;
        }

        public Object getInternships() {
            return Internships;
        }

        public Object getPracticums() {
            return Practicums;
        }

        public Object getFacilities() {
            return Facilities;
        }

        public List<Office> getOffices() {
            return Offices;
        }

        public Object getMemberships() {
            return Memberships;
        }

        public Object getCertifications() {
            return Certifications;
        }

        public Object getAwards() {
            return Awards;
        }

        public Boolean getHasAppointments() {
            return HasAppointments;
        }

        public Object getServiceErrors() {
            return ServiceErrors;
        }

    }

}