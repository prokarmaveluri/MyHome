package com.dignityhealth.myhome.features.profile;

import java.util.Objects;

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

    public Profile() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return isPregnant == profile.isPregnant &&
                Objects.equals(firstName, profile.firstName) &&
                Objects.equals(middleInitial, profile.middleInitial) &&
                Objects.equals(lastName, profile.lastName) &&
                Objects.equals(preferredName, profile.preferredName) &&
                Objects.equals(gender, profile.gender) &&
                Objects.equals(dateOfBirth, profile.dateOfBirth) &&
                Objects.equals(address, profile.address) &&
                Objects.equals(phoneNumber, profile.phoneNumber) &&
                Objects.equals(phoneNumberType, profile.phoneNumberType) &&
                Objects.equals(contactName, profile.contactName) &&
                Objects.equals(contactPhoneNumber, profile.contactPhoneNumber) &&
                Objects.equals(primaryCaregiverName, profile.primaryCaregiverName) &&
                Objects.equals(weeksPregnant, profile.weeksPregnant) &&
                Objects.equals(insuranceProvider, profile.insuranceProvider) &&
                Objects.equals(clientID, profile.clientID) &&
                Objects.equals(remoteID, profile.remoteID) &&
                Objects.equals(email, profile.email);
    }

    /**
     * Method to see if two Profile objects are the same.
     * We don't care about email in this method, and ignore casing for gender
     *
     * @param o
     * @return
     */
    public boolean equalsSansEmails(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return isPregnant == profile.isPregnant &&
                Objects.equals(firstName, profile.firstName) &&
                Objects.equals(middleInitial, profile.middleInitial) &&
                Objects.equals(lastName, profile.lastName) &&
                Objects.equals(preferredName, profile.preferredName) &&
                Objects.equals(gender.toUpperCase(), profile.gender.toUpperCase()) &&
                Objects.equals(dateOfBirth, profile.dateOfBirth) &&
                Objects.equals(address, profile.address) &&
                Objects.equals(phoneNumber, profile.phoneNumber) &&
                Objects.equals(phoneNumberType, profile.phoneNumberType) &&
                Objects.equals(contactName, profile.contactName) &&
                Objects.equals(contactPhoneNumber, profile.contactPhoneNumber) &&
                Objects.equals(primaryCaregiverName, profile.primaryCaregiverName) &&
                Objects.equals(weeksPregnant, profile.weeksPregnant) &&
                Objects.equals(insuranceProvider, profile.insuranceProvider) &&
                Objects.equals(clientID, profile.clientID) &&
                Objects.equals(remoteID, profile.remoteID);
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton
     *
     * @param otherProfile
     * @return
     */
    public static Profile copy(Profile otherProfile) {
        Profile profile = new Profile();
        profile.firstName = otherProfile.firstName;
        profile.middleInitial = otherProfile.middleInitial;
        profile.lastName = otherProfile.lastName;
        profile.preferredName = otherProfile.preferredName;
        profile.gender = otherProfile.gender;
        profile.dateOfBirth = otherProfile.dateOfBirth;
        profile.address = Address.copy(otherProfile.address);
        profile.phoneNumber = otherProfile.phoneNumber;
        profile.phoneNumberType = otherProfile.phoneNumberType;
        profile.contactName = otherProfile.contactName;
        profile.contactPhoneNumber = otherProfile.contactPhoneNumber;
        profile.primaryCaregiverName = otherProfile.primaryCaregiverName;
        profile.weeksPregnant = otherProfile.weeksPregnant;
        profile.insuranceProvider = InsuranceProvider.copy(otherProfile.insuranceProvider);
        profile.clientID = otherProfile.clientID;
        profile.remoteID = otherProfile.remoteID;

        return profile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleInitial, lastName, preferredName, gender, dateOfBirth, address, phoneNumber, phoneNumberType, contactName, contactPhoneNumber, primaryCaregiverName, isPregnant, weeksPregnant, insuranceProvider, clientID, remoteID, email);
    }
}