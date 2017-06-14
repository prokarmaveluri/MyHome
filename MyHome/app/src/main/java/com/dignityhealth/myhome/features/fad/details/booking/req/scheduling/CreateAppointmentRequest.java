package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

import com.dignityhealth.myhome.features.profile.Profile;

/**
 * Created by cmajji on 6/8/17.
 */

public class CreateAppointmentRequest {

    private Jsonapi jsonapi;
    private Data data;

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


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

    public class Data {

        private String type;
        private Attributes attributes;
        private Relationships relationships;

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

    public class AppointmentDetails {

        private String id;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


    public class Jsonapi {

        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

    }

    public class Relationships {

        private Schedule schedule;

        public Schedule getSchedule() {
            return schedule;
        }

        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
        }

    }

    public class Schedule {

        private AppointmentDetails data;

        public AppointmentDetails getData() {
            return data;
        }

        public void setData(AppointmentDetails data) {
            this.data = data;
        }

    }
}