package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 4/28/17.
 */

public class ProfileResponse {

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
    public String email;   //Is this in there...

    public ProfileResponse(){

    }

    public ProfileResponse(String firstName, String middleInitial, String lastName, String preferredName, String gender, String dateOfBirth, Address address, String phoneNumber, String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName, boolean isPregnant, String weeksPregnant, InsuranceProvider insuranceProvider, String clientID, String remoteID, String email) {
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
        return "ProfileResponse{" +
                "firstName='" + firstName + '\'' +
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

    public class Address {
        public String line1;
        public String line2;
        public String city;
        public String stateOrProvince;
        public String zipCode;
        public String countryCode;

        public Address(String line1, String line2, String city, String stateOrProvince, String zipCode, String countryCode) {
            this.line1 = line1;
            this.line2 = line2;
            this.city = city;
            this.stateOrProvince = stateOrProvince;
            this.zipCode = zipCode;
            this.countryCode = countryCode;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "line1='" + line1 + '\'' +
                    ", line2='" + line2 + '\'' +
                    ", city='" + city + '\'' +
                    ", stateOrProvince='" + stateOrProvince + '\'' +
                    ", zipCode='" + zipCode + '\'' +
                    ", countryCode='" + countryCode + '\'' +
                    '}';
        }
    }

    public class InsuranceProvider{
        public String providerName;
        public String insurancePlan;
        public String groupNumber;
        public String memberNumber;

        @Override
        public String toString() {
            return "InsuranceProvider{" +
                    "providerName='" + providerName + '\'' +
                    ", insurancePlan='" + insurancePlan + '\'' +
                    ", groupNumber='" + groupNumber + '\'' +
                    ", memberNumber='" + memberNumber + '\'' +
                    '}';
        }
    }
}

