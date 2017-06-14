package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

import com.dignityhealth.myhome.features.profile.Profile;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Attributes extends Profile {
    private String address;
    private String address2;
    private String appointmentAt;
    private String appointmentType;
    private String birthdate;
    private String caregiverName;
    private String city;
    private String email;
    private String firstName;
    private String gender;
    private Boolean hasPhysician;
    private String insuranceGroupNumber;
    private String insuranceMemberNumber;
    private String insurancePlanName;
    private String insurancePlanPermalink;
    private String insurancePlanPhoneNumber;
    private String lastName;
    private String middleInitial;
    private Boolean newPatient;
    private String patientComplaint;
    private String phoneNumber;
    private Boolean pregnant;
    private Boolean requiresStandingAssistance;
    private Boolean requiresTranslator;
    private String state;
    private Boolean termsTos;
    private String translatorLanguage;
    private String weeksPregnant;
    private String zip;
    private String doctorName;
    private String facilityName;
    private String facilityPhoneNumber;
    private String facilityAddressLine1;
    private String facilityCity;
    private String facilityState;
    private String facilityZip;
    private Boolean isCreatedByCaregiver;

    public Attributes(){

    }

    public Attributes(String address, String address2, String appointmentAt, String appointmentType, String birthdate, String caregiverName, String city, String email, String firstName, String gender, Boolean hasPhysician, String insuranceGroupNumber, String insuranceMemberNumber, String insurancePlanName, String insurancePlanPermalink, String insurancePlanPhoneNumber, String lastName, String middleInitial, Boolean newPatient, String patientComplaint, String phoneNumber, Boolean pregnant, Boolean requiresStandingAssistance, Boolean requiresTranslator, String state, Boolean termsTos, String translatorLanguage, String weeksPregnant, String zip, String doctorName, String facilityName, String facilityPhoneNumber, String facilityAddressLine1, String facilityCity, String facilityState, String facilityZip, Boolean isCreatedByCaregiver) {
        this.address = address;
        this.address2 = address2;
        this.appointmentAt = appointmentAt;
        this.appointmentType = appointmentType;
        this.birthdate = birthdate;
        this.caregiverName = caregiverName;
        this.city = city;
        this.email = email;
        this.firstName = firstName;
        this.gender = gender;
        this.hasPhysician = hasPhysician;
        this.insuranceGroupNumber = insuranceGroupNumber;
        this.insuranceMemberNumber = insuranceMemberNumber;
        this.insurancePlanName = insurancePlanName;
        this.insurancePlanPermalink = insurancePlanPermalink;
        this.insurancePlanPhoneNumber = insurancePlanPhoneNumber;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.newPatient = newPatient;
        this.patientComplaint = patientComplaint;
        this.phoneNumber = phoneNumber;
        this.pregnant = pregnant;
        this.requiresStandingAssistance = requiresStandingAssistance;
        this.requiresTranslator = requiresTranslator;
        this.state = state;
        this.termsTos = termsTos;
        this.translatorLanguage = translatorLanguage;
        this.weeksPregnant = weeksPregnant;
        this.zip = zip;
        this.doctorName = doctorName;
        this.facilityName = facilityName;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.facilityAddressLine1 = facilityAddressLine1;
        this.facilityCity = facilityCity;
        this.facilityState = facilityState;
        this.facilityZip = facilityZip;
        this.isCreatedByCaregiver = isCreatedByCaregiver;
    }

    public Attributes(Profile profile){
        super(
                profile.userId,
                profile.userName,
                profile.idLevel,
                profile.isVerified,
                profile.createdDate,
                profile.firstName,
                profile.middleInitial,
                profile.lastName,
                profile.preferredName,
                profile.gender,
                profile.dateOfBirth,
                profile.address,
                profile.phoneNumber,
                profile.phoneNumberType,
                profile.contactName,
                profile.contactPhoneNumber,
                profile.primaryCaregiverName,
                profile.isPregnant,
                profile.weeksPregnant,
                profile.insuranceProvider,
                profile.clientID,
                profile.remoteID,
                profile.email,
                profile.reasonForVisit,
                profile.translationNeeded,
                profile.translatorLanguage,
                profile.assistanceNeeded
        );
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(String appointmentAt) {
        this.appointmentAt = appointmentAt;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Object getCaregiverName() {
        return caregiverName;
    }

    public void setCaregiverName(String caregiverName) {
        this.caregiverName = caregiverName;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getHasPhysician() {
        return hasPhysician;
    }

    public void setHasPhysician(Boolean hasPhysician) {
        this.hasPhysician = hasPhysician;
    }

    public Object getInsuranceGroupNumber() {
        return insuranceGroupNumber;
    }

    public void setInsuranceGroupNumber(String insuranceGroupNumber) {
        this.insuranceGroupNumber = insuranceGroupNumber;
    }

    public Object getInsuranceMemberNumber() {
        return insuranceMemberNumber;
    }

    public void setInsuranceMemberNumber(String insuranceMemberNumber) {
        this.insuranceMemberNumber = insuranceMemberNumber;
    }

    public String getInsurancePlanName() {
        return insurancePlanName;
    }

    public void setInsurancePlanName(String insurancePlanName) {
        this.insurancePlanName = insurancePlanName;
    }

    public String getInsurancePlanPermalink() {
        return insurancePlanPermalink;
    }

    public void setInsurancePlanPermalink(String insurancePlanPermalink) {
        this.insurancePlanPermalink = insurancePlanPermalink;
    }

    public Object getInsurancePlanPhoneNumber() {
        return insurancePlanPhoneNumber;
    }

    public void setInsurancePlanPhoneNumber(String insurancePlanPhoneNumber) {
        this.insurancePlanPhoneNumber = insurancePlanPhoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Object getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public Boolean getNewPatient() {
        return newPatient;
    }

    public void setNewPatient(Boolean newPatient) {
        this.newPatient = newPatient;
    }

    public String getPatientComplaint() {
        return patientComplaint;
    }

    public void setPatientComplaint(String patientComplaint) {
        this.patientComplaint = patientComplaint;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPregnant() {
        return pregnant;
    }

    public void setPregnant(Boolean pregnant) {
        this.pregnant = pregnant;
    }

    public Boolean getRequiresStandingAssistance() {
        return requiresStandingAssistance;
    }

    public void setRequiresStandingAssistance(Boolean requiresStandingAssistance) {
        this.requiresStandingAssistance = requiresStandingAssistance;
    }

    public Boolean getRequiresTranslator() {
        return requiresTranslator;
    }

    public void setRequiresTranslator(Boolean requiresTranslator) {
        this.requiresTranslator = requiresTranslator;
    }

    public Object getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getTermsTos() {
        return termsTos;
    }

    public void setTermsTos(Boolean termsTos) {
        this.termsTos = termsTos;
    }

    public Object getTranslatorLanguage() {
        return translatorLanguage;
    }

    public void setTranslatorLanguage(String translatorLanguage) {
        this.translatorLanguage = translatorLanguage;
    }

    public Object getWeeksPregnant() {
        return weeksPregnant;
    }

    public void setWeeksPregnant(String weeksPregnant) {
        this.weeksPregnant = weeksPregnant;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityPhoneNumber() {
        return facilityPhoneNumber;
    }

    public void setFacilityPhoneNumber(String facilityPhoneNumber) {
        this.facilityPhoneNumber = facilityPhoneNumber;
    }

    public String getFacilityAddressLine1() {
        return facilityAddressLine1;
    }

    public void setFacilityAddressLine1(String facilityAddressLine1) {
        this.facilityAddressLine1 = facilityAddressLine1;
    }

    public String getFacilityCity() {
        return facilityCity;
    }

    public void setFacilityCity(String facilityCity) {
        this.facilityCity = facilityCity;
    }

    public String getFacilityState() {
        return facilityState;
    }

    public void setFacilityState(String facilityState) {
        this.facilityState = facilityState;
    }

    public String getFacilityZip() {
        return facilityZip;
    }

    public void setFacilityZip(String facilityZip) {
        this.facilityZip = facilityZip;
    }

    public Boolean getIsCreatedByCaregiver() {
        return isCreatedByCaregiver;
    }

    public void setIsCreatedByCaregiver(Boolean isCreatedByCaregiver) {
        this.isCreatedByCaregiver = isCreatedByCaregiver;
    }
}
