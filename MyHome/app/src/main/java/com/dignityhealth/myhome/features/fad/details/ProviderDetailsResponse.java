package com.dignityhealth.myhome.features.fad.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.dignityhealth.myhome.features.fad.Facility;
import com.dignityhealth.myhome.features.fad.Office;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 5/15/17.
 */


public class ProviderDetailsResponse implements Parcelable {

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
    public String Quote;
    public String Philosophy;
    public String InMyOwnWords;
    public ArrayList<String> Specialties;
    public ArrayList<String> Languages;
    public String Degree;
    public ArrayList<String> MedicalSchools;
    public ArrayList<String> Residencies;
    public ArrayList<String> Fellowships;
    public ArrayList<String> Internships;
    public ArrayList<String> Practicums; //These will probably not be strings, but addresses...
    public ArrayList<Facility> Facilities;
    public ArrayList<Office> Offices;
    public ArrayList<String> Memberships;
    public ArrayList<String> Certifications;
    public ArrayList<String> Awards;
    public Boolean HasAppointments;
    public ArrayList<String> ServiceErrors;


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

    public List<String> getPracticums() {
        return Practicums;
    }

    public List<Facility> getFacilities() {
        return Facilities;
    }

    public ArrayList<Office> getOffices() {
        return Offices;
    }

    public List<String> getMemberships() {
        return Memberships;
    }

    public List<String> getCertifications() {
        return Certifications;
    }

    public List<String> getAwards() {
        return Awards;
    }

    public Boolean getHasAppointments() {
        return HasAppointments;
    }

    public List<String> getServiceErrors() {
        return ServiceErrors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.RecordNumber);
        dest.writeString(this.ProviderId);
        dest.writeString(this.Npi);
        dest.writeString(this.Title);
        dest.writeString(this.FirstName);
        dest.writeString(this.MiddleName);
        dest.writeString(this.MiddleInitialDot);
        dest.writeString(this.LastName);
        dest.writeString(this.DisplayFullName);
        dest.writeString(this.DisplayLastName);
        dest.writeString(this.DisplayLastNamePlural);
        dest.writeString(this.DateOfBirth);
        dest.writeString(this.Gender);
        dest.writeString(this.YearsOfExperience);
        dest.writeValue(this.AcceptsNewPatients);
        dest.writeString(this.ImageUrl);
        dest.writeString(this.Quote);
        dest.writeString(this.Philosophy);
        dest.writeString(this.InMyOwnWords);
        dest.writeStringList(this.Specialties);
        dest.writeStringList(this.Languages);
        dest.writeString(this.Degree);
        dest.writeStringList(this.MedicalSchools);
        dest.writeStringList(this.Residencies);
        dest.writeStringList(this.Fellowships);
        dest.writeStringList(this.Internships);
        dest.writeStringList(this.Practicums);
        dest.writeTypedList(this.Facilities);
        dest.writeTypedList(this.Offices);
        dest.writeStringList(this.Memberships);
        dest.writeStringList(this.Certifications);
        dest.writeStringList(this.Awards);
        dest.writeValue(this.HasAppointments);
        dest.writeStringList(this.ServiceErrors);
    }

    public ProviderDetailsResponse() {
    }

    protected ProviderDetailsResponse(Parcel in) {
        this.RecordNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ProviderId = in.readString();
        this.Npi = in.readString();
        this.Title = in.readString();
        this.FirstName = in.readString();
        this.MiddleName = in.readString();
        this.MiddleInitialDot = in.readString();
        this.LastName = in.readString();
        this.DisplayFullName = in.readString();
        this.DisplayLastName = in.readString();
        this.DisplayLastNamePlural = in.readString();
        this.DateOfBirth = in.readString();
        this.Gender = in.readString();
        this.YearsOfExperience = in.readString();
        this.AcceptsNewPatients = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.ImageUrl = in.readString();
        this.Quote = in.readString();
        this.Philosophy = in.readString();
        this.InMyOwnWords = in.readString();
        this.Specialties = in.createStringArrayList();
        this.Languages = in.createStringArrayList();
        this.Degree = in.readString();
        this.MedicalSchools = in.createStringArrayList();
        this.Residencies = in.createStringArrayList();
        this.Fellowships = in.createStringArrayList();
        this.Internships = in.createStringArrayList();
        this.Practicums = in.createStringArrayList();
        this.Facilities = in.createTypedArrayList(Facility.CREATOR);
        this.Offices = in.createTypedArrayList(Office.CREATOR);
        this.Memberships = in.createStringArrayList();
        this.Certifications = in.createStringArrayList();
        this.Awards = in.createStringArrayList();
        this.HasAppointments = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.ServiceErrors = in.createStringArrayList();
    }

    public static final Parcelable.Creator<ProviderDetailsResponse> CREATOR = new Parcelable.Creator<ProviderDetailsResponse>() {
        @Override
        public ProviderDetailsResponse createFromParcel(Parcel source) {
            return new ProviderDetailsResponse(source);
        }

        @Override
        public ProviderDetailsResponse[] newArray(int size) {
            return new ProviderDetailsResponse[size];
        }
    };

    @Override
    public String toString() {
        return "ProviderDetailsResponse{" +
                "RecordNumber=" + RecordNumber +
                ", ProviderId='" + ProviderId + '\'' +
                ", Npi='" + Npi + '\'' +
                ", Title='" + Title + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", MiddleName='" + MiddleName + '\'' +
                ", MiddleInitialDot='" + MiddleInitialDot + '\'' +
                ", LastName='" + LastName + '\'' +
                ", DisplayFullName='" + DisplayFullName + '\'' +
                ", DisplayLastName='" + DisplayLastName + '\'' +
                ", DisplayLastNamePlural='" + DisplayLastNamePlural + '\'' +
                ", DateOfBirth='" + DateOfBirth + '\'' +
                ", Gender='" + Gender + '\'' +
                ", YearsOfExperience='" + YearsOfExperience + '\'' +
                ", AcceptsNewPatients=" + AcceptsNewPatients +
                ", ImageUrl='" + ImageUrl + '\'' +
                ", Quote='" + Quote + '\'' +
                ", Philosophy='" + Philosophy + '\'' +
                ", InMyOwnWords='" + InMyOwnWords + '\'' +
                ", Specialties=" + Specialties +
                ", Languages=" + Languages +
                ", Degree='" + Degree + '\'' +
                ", MedicalSchools=" + MedicalSchools +
                ", Residencies=" + Residencies +
                ", Fellowships=" + Fellowships +
                ", Internships=" + Internships +
                ", Practicums=" + Practicums +
                ", Facilities=" + Facilities +
                ", Offices=" + Offices +
                ", Memberships=" + Memberships +
                ", Certifications=" + Certifications +
                ", Awards=" + Awards +
                ", HasAppointments=" + HasAppointments +
                ", ServiceErrors=" + ServiceErrors +
                '}';
    }
}
