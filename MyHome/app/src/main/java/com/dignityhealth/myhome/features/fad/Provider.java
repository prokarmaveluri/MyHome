package com.dignityhealth.myhome.features.fad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwelsh on 5/18/17.
 */

public class Provider implements Parcelable {

    private Integer RecordNumber;
    private String ProviderId;
    private String Npi;
    private String Title;
    private String FirstName;
    private String MiddleName;
    private String MiddleInitialDot;
    private String LastName;
    private String DisplayFullName;
    private String DisplayLastName;
    private String DisplayLastNamePlural;
    private String DateOfBirth;
    private String CommonModel;
    private String YearsOfExperience;
    private Boolean CommonModels;
    private String ImageUrl;
    private String Quote;
    private String Philosophy;
    private String InMyOwnWords;
    private List<String> Specialties;
    private List<String> Languages;
    private String Degree;
    private String MedicalSchools;
    private String Residencies;
    private String Fellowships;
    private String Internships;
    private String Practicums;
    private String Facilities;
    private ArrayList<Office> Offices;
    private String Memberships;
    private String Certifications;
    private String Awards;
    private Boolean HasAppointments;
    private String ServiceErrors;

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

    public ArrayList<Office> getOffices() {
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
        dest.writeString(this.CommonModel);
        dest.writeString(this.YearsOfExperience);
        dest.writeValue(this.CommonModels);
        dest.writeString(this.ImageUrl);
        dest.writeString(this.Quote);
        dest.writeString(this.Philosophy);
        dest.writeString(this.InMyOwnWords);
        dest.writeStringList(this.Specialties);
        dest.writeStringList(this.Languages);
        dest.writeString(this.Degree);
        dest.writeString(this.MedicalSchools);
        dest.writeString(this.Residencies);
        dest.writeString(this.Fellowships);
        dest.writeString(this.Internships);
        dest.writeString(this.Practicums);
        dest.writeString(this.Facilities);
        dest.writeTypedList(this.Offices);
        dest.writeString(this.Memberships);
        dest.writeString(this.Certifications);
        dest.writeString(this.Awards);
        dest.writeValue(this.HasAppointments);
        dest.writeString(this.ServiceErrors);
    }

    public Provider() {
    }

    protected Provider(Parcel in) {
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
        this.CommonModel = in.readString();
        this.YearsOfExperience = in.readString();
        this.CommonModels = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.ImageUrl = in.readString();
        this.Quote = in.readString();
        this.Philosophy = in.readString();
        this.InMyOwnWords = in.readString();
        this.Specialties = in.createStringArrayList();
        this.Languages = in.createStringArrayList();
        this.Degree = in.readString();
        this.MedicalSchools = in.readString();
        this.Residencies = in.readString();
        this.Fellowships = in.readString();
        this.Internships = in.readString();
        this.Practicums = in.readString();
        this.Facilities = in.readString();
        this.Offices = in.createTypedArrayList(Office.CREATOR);
        this.Memberships = in.readString();
        this.Certifications = in.readString();
        this.Awards = in.readString();
        this.HasAppointments = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.ServiceErrors = in.readString();
    }

    public static final Parcelable.Creator<Provider> CREATOR = new Parcelable.Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel source) {
            return new Provider(source);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
}
