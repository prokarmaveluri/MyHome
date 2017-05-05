package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 4/28/17.
 */

public class Profile {

    public String firstName;
    public String middleInitial;
    public String lastName;
    public String preferredName;
    public String gender;
    public String dateOfBirth;
    public Address address;
    public String phoneNumber;
    public String phoneNumberType;
    public String contactName;
    public String contactPhoneNumber;
    public String primaryCaregiverName;
    public boolean isPregnant;
    public String weeksPregnant;
    public InsuranceProvider insuranceProvider;
    public String clientID;
    public String remoteID;
    public String email;

    public Profile(String firstName, String middleInitial, String lastName, String preferredName, String gender, String dateOfBirth, Address address, String phoneNumber, String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName, boolean isPregnant, String weeksPregnant, InsuranceProvider insuranceProvider, String clientID, String remoteID, String email) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.phoneNumberType = phoneNumberType;
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.primaryCaregiverName = primaryCaregiverName;
        this.isPregnant = isPregnant;
        this.weeksPregnant = weeksPregnant;
        this.insuranceProvider = insuranceProvider;
        this.clientID = clientID;
        this.remoteID = remoteID;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredName='" + preferredName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", phoneNumberType='" + phoneNumberType + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhoneNumber='" + contactPhoneNumber + '\'' +
                ", primaryCaregiverName='" + primaryCaregiverName + '\'' +
                ", isPregnant=" + isPregnant +
                ", weeksPregnant='" + weeksPregnant + '\'' +
                ", insuranceProvider=" + insuranceProvider +
                ", clientID='" + clientID + '\'' +
                ", remoteID='" + remoteID + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}