package com.dignityhealth.myhome.features.fad.details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cmajji on 5/15/17.
 */


public class ProviderDetailsResponse {

    public Integer RecordNumber;
    public String ProviderId;
    public String Npi;
    public String Title;
    public String FirstName;
    public String MiddleName;
    public String MiddleInitialDot;
    public String LastName;
    public String DisplayFullName;
    public String DisplayLastName;
    public String DisplayLastNamePlural;
    public String DateOfBirth;
    public String Gender;
    public String YearsOfExperience;
    public Boolean AcceptsNewPatients;
    public String ImageUrl;
    public Object Quote;
    public String Philosophy;
    public String InMyOwnWords;
    public List<String> Specialties;
    public List<String> Languages;
    public String Degree;
    public List<String> MedicalSchools;
    public List<String> Residencies;
    public List<String> Fellowships;
    public List<String> Internships;
    public List<Object> Practicums;
    public List<Object> Facilities;
    public List<Office> Offices;
    public List<Object> Memberships;
    public List<Object> Certifications;
    public List<String> Awards;
    public Boolean HasAppointments;
    public List<Object> ServiceErrors;


    public Integer getRecordNumber() {
        return RecordNumber;
    }

    public String getProviderId() {
        return ProviderId;
    }

    public String getNpi() {
        return Npi;
    }

    public String getTitle() {
        return Title;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public String getMiddleInitialDot() {
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

    public String getDisplayLastNamePlural() {
        return DisplayLastNamePlural;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public String getYearsOfExperience() {
        return YearsOfExperience;
    }

    public Boolean getAcceptsNewPatients() {
        return AcceptsNewPatients;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public Object getQuote() {
        return Quote;
    }

    public String getPhilosophy() {
        return Philosophy;
    }

    public String getInMyOwnWords() {
        return InMyOwnWords;
    }

    public List<String> getSpecialties() {
        return Specialties;
    }

    public List<String> getLanguages() {
        return Languages;
    }

    public String getDegree() {
        return Degree;
    }

    public List<String> getMedicalSchools() {
        return MedicalSchools;
    }

    public List<String> getResidencies() {
        return Residencies;
    }

    public List<String> getFellowships() {
        return Fellowships;
    }

    public List<String> getInternships() {
        return Internships;
    }

    public List<Object> getPracticums() {
        return Practicums;
    }

    public List<Object> getFacilities() {
        return Facilities;
    }

    public List<Office> getOffices() {
        return Offices;
    }

    public List<Object> getMemberships() {
        return Memberships;
    }

    public List<Object> getCertifications() {
        return Certifications;
    }

    public List<String> getAwards() {
        return Awards;
    }

    public Boolean getHasAppointments() {
        return HasAppointments;
    }

    public List<Object> getServiceErrors() {
        return ServiceErrors;
    }


    public class Office {

        public String Name;
        public String Address1;
        public Object Address2;
        public String City;
        public String State;
        public String ZipCode;
        public String Phone;
        public String Fax;
        public String Url;
        public String Lat;
        @SerializedName(value = "Long")
        public String lon;
        public Integer SortRank;
        public Object DistanceMilesFromSearch;
        public String DirectionsLink;
        public String Hash;
        public String LatLongHash;
        public List<Object> Appointments;
        public Boolean LocationMatch;

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

        public String getFax() {
            return Fax;
        }

        public String getUrl() {
            return Url;
        }

        public String getLat() {
            return Lat;
        }

        public String getLon() {
            return lon;
        }

        public Integer getSortRank() {
            return SortRank;
        }

        public Object getDistanceMilesFromSearch() {
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

        public List<Object> getAppointments() {
            return Appointments;
        }

        public Boolean getLocationMatch() {
            return LocationMatch;
        }

    }
}
