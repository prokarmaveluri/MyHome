package com.prokarma.myhome.features.fad.details;

/**
 * Created by kwelsh on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProviderDetailsResult {

    @SerializedName("Gender")
    @Expose
    private String gender;
    @SerializedName("YearsOfExperience")
    @Expose
    private Integer yearsOfExperience;
    @SerializedName("AcceptsNewPatients")
    @Expose
    private Boolean acceptsNewPatients;
    @SerializedName("InMyOwnWords")
    @Expose
    private String inMyOwnWords;
    @SerializedName("Languages")
    @Expose
    private List<String> languages = null;
    @SerializedName("Degree")
    @Expose
    private String degree;
    @SerializedName("MedicalSchools")
    @Expose
    private List<String> medicalSchools = null;
    @SerializedName("Residencies")
    @Expose
    private List<String> residencies = null;
    @SerializedName("Fellowships")
    @Expose
    private List<String> fellowships = null;
    @SerializedName("Internships")
    @Expose
    private List<String> internships = null;
    @SerializedName("Practicums")
    @Expose
    private List<String> practicums = null;
    @SerializedName("Memberships")
    @Expose
    private List<String> memberships = null;
    @SerializedName("Certifications")
    @Expose
    private List<String> certifications = null;
    @SerializedName("Awards")
    @Expose
    private List<String> awards = null;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Npi")
    @Expose
    private String npi;
    @SerializedName("DisplayName")
    @Expose
    private String displayName;
    @SerializedName("DisplayLastName")
    @Expose
    private String displayLastName;
    @SerializedName("DisplayLastNamePlural")
    @Expose
    private String displayLastNamePlural;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("MiddleName")
    @Expose
    private String middleName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Suffix")
    @Expose
    private String suffix;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Images")
    @Expose
    private List<ProviderDetailsImage> images = null;
    @SerializedName("Philosophy")
    @Expose
    private String philosophy;
    @SerializedName("Offices")
    @Expose
    private List<ProviderDetailsOffice> offices = null;
    @SerializedName("Facilities")
    @Expose
    private List<ProviderDetailsFacility> facilities = null;
    @SerializedName("PrimarySpecialities")
    @Expose
    private List<String> primarySpecialities = null;
    @SerializedName("SupportsOnlineBooking")
    @Expose
    private Boolean supportsOnlineBooking;
    @SerializedName("ProviderDetailsUrl")
    @Expose
    private String providerDetailsUrl;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Boolean getAcceptsNewPatients() {
        return acceptsNewPatients;
    }

    public void setAcceptsNewPatients(Boolean acceptsNewPatients) {
        this.acceptsNewPatients = acceptsNewPatients;
    }

    public String getInMyOwnWords() {
        return inMyOwnWords;
    }

    public void setInMyOwnWords(String inMyOwnWords) {
        this.inMyOwnWords = inMyOwnWords;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<String> getMedicalSchools() {
        return medicalSchools;
    }

    public void setMedicalSchools(List<String> medicalSchools) {
        this.medicalSchools = medicalSchools;
    }

    public List<String> getResidencies() {
        return residencies;
    }

    public void setResidencies(List<String> residencies) {
        this.residencies = residencies;
    }

    public List<String> getFellowships() {
        return fellowships;
    }

    public void setFellowships(List<String> fellowships) {
        this.fellowships = fellowships;
    }

    public List<String> getInternships() {
        return internships;
    }

    public void setInternships(List<String> internships) {
        this.internships = internships;
    }

    public List<String> getPracticums() {
        return practicums;
    }

    public void setPracticums(List<String> practicums) {
        this.practicums = practicums;
    }

    public List<String> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<String> memberships) {
        this.memberships = memberships;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayLastName() {
        return displayLastName;
    }

    public void setDisplayLastName(String displayLastName) {
        this.displayLastName = displayLastName;
    }

    public String getDisplayLastNamePlural() {
        return displayLastNamePlural;
    }

    public void setDisplayLastNamePlural(String displayLastNamePlural) {
        this.displayLastNamePlural = displayLastNamePlural;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProviderDetailsImage> getImages() {
        return images;
    }

    public void setImages(List<ProviderDetailsImage> images) {
        this.images = images;
    }

    public String getPhilosophy() {
        return philosophy;
    }

    public void setPhilosophy(String philosophy) {
        this.philosophy = philosophy;
    }

    public List<ProviderDetailsOffice> getOffices() {
        return offices;
    }

    public void setOffices(List<ProviderDetailsOffice> offices) {
        this.offices = offices;
    }

    public List<ProviderDetailsFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<ProviderDetailsFacility> facilities) {
        this.facilities = facilities;
    }

    public List<String> getPrimarySpecialities() {
        return primarySpecialities;
    }

    public void setPrimarySpecialities(List<String> primarySpecialities) {
        this.primarySpecialities = primarySpecialities;
    }

    public Boolean getSupportsOnlineBooking() {
        return supportsOnlineBooking;
    }

    public void setSupportsOnlineBooking(Boolean supportsOnlineBooking) {
        this.supportsOnlineBooking = supportsOnlineBooking;
    }

    public String getProviderDetailsUrl() {
        return providerDetailsUrl;
    }

    public void setProviderDetailsUrl(String providerDetailsUrl) {
        this.providerDetailsUrl = providerDetailsUrl;
    }
}