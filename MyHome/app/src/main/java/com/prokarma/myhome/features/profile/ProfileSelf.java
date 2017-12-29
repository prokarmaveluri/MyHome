package com.prokarma.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by veluri on 12/28/17.
 */

public class ProfileSelf {

    //implements Parcelable

    public String personId;
    public String idLevel;

    public String firstName;
    public String lastName;
    public String preferredName;
    public String email;
    public String dateOfBirth;
    public String gender;
    public String degreeCredential;
    public String specialty;

    public String phoneNumber;
    public String mobilePhoneNumber;
    public String officePhoneNumber;

    public boolean isPregnant;
    public String weeksPregnant;
    public String numKbaAttempts;
    public String nextKbaAttempt;
    public Address address;
    public InsuranceProvider insuranceProvider;

    public ProfileSelf(String personId, String idLevel,
                       String firstName, String lastName, String preferredName,
                       String email, String dateOfBirth, String gender,
                       String degreeCredential, String specialty, String phoneNumber,
                       String mobilePhoneNumber, String officePhoneNumber,
                       boolean isPregnant, String weeksPregnant,
                       String numKbaAttempts, String nextKbaAttempt,
                       Address address, InsuranceProvider insuranceProvider) {
        this.personId = personId;
        this.idLevel = idLevel;

        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;

        this.degreeCredential = degreeCredential;
        this.specialty = specialty;
        this.phoneNumber = phoneNumber;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.officePhoneNumber = officePhoneNumber;
        this.isPregnant = isPregnant;
        this.weeksPregnant = weeksPregnant;
        this.numKbaAttempts = numKbaAttempts;
        this.nextKbaAttempt = nextKbaAttempt;
        this.address = address;
        this.insuranceProvider = insuranceProvider;
    }

    /*public ProfileSelf(String firstName, String middleInitial, String lastName, String preferredName, String gender, String dateOfBirth, Address address, String phoneNumber, String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName, boolean isPregnant, String weeksPregnant, InsuranceProvider insuranceProvider, String clientID, String remoteID, String email, String reasonForVisit, boolean translationNeeded, String translatorLanguage, boolean assistanceNeeded) {
        this.userId = null;
        this.userName = null;
        //this.idLevel = null;
        this.isVerified = false;
        this.createdDate = null;
        *//*this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;*//*
        this.phoneNumber = phoneNumber;
        this.phoneNumberType = phoneNumberType;
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.primaryCaregiverName = primaryCaregiverName;
        *//*this.isPregnant = isPregnant;
        this.weeksPregnant = weeksPregnant;
        this.insuranceProvider = insuranceProvider;*//*
        this.clientID = clientID;
        this.remoteID = remoteID;
        //this.email = email;
        this.reasonForVisit = reasonForVisit;
        this.translationNeeded = translationNeeded;
        this.translatorLanguage = translatorLanguage;
        this.assistanceNeeded = assistanceNeeded;
    }*/

    public void setEmail(String email) {
        this.email = email;
    }

    /*
    "self": {
            "personId": "4aec306f-a189-43ed-a837-0d7291e1b112",
            "idLevel": "level2",
            "firstName": "Test13",
            "lastName": "Jonnalagadda",
            "preferredName": "",
            "email": "jjjj@pk.com",
            "dateOfBirth": "1973-01-16T00:00:00Z",
            "gender": "male",
            "degreeCredential": null,
            "specialty": null,

            "phoneNumber": "2222222222",
            "mobilePhoneNumber": null,
            "officePhoneNumber": null,

            "isPregnant": false,
            "weeksPregnant": 0,
            "numKbaAttempts": 0,
            "nextKbaAttempt": null,

            "address": {
                "line1": "Dev test233",
                "line2": "Test address Dev234",
                "city": "Redding",
                "stateOrProvince": "CA",
                "zipCode": "58785",
                "countryCode": "US"
            },
            "insuranceProvider": {
                "providerName": null,
                "insurancePlan": "AARP Medicare Complete",
                "groupNumber": "4243243243243242",
                "memberNumber": "32423423423424"
            }
        } */

    /*protected ProfileSelf(Parcel in) {
        this.personId = in.readString();
        this.idLevel = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.preferredName = in.readString();
        this.email = in.readString();
        this.preferredName = in.readString();
        this.preferredName = in.readString();
        this.preferredName = in.readString();
        this.preferredName = in.readString();
        this.gender = in.readString();
        this.dateOfBirth = in.readString();
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.phoneNumber = in.readString();
        this.phoneNumberType = in.readString();
        this.contactName = in.readString();
        this.contactPhoneNumber = in.readString();
        this.primaryCaregiverName = in.readString();
        this.isPregnant = in.readByte() != 0;
        this.weeksPregnant = in.readString();
        this.insuranceProvider = in.readParcelable(InsuranceProvider.class.getClassLoader());
        this.clientID = in.readString();
        this.remoteID = in.readString();
        this.email = in.readString();
        this.reasonForVisit = in.readString();
        this.translationNeeded = in.readByte() != 0;
        this.translatorLanguage = in.readString();
        this.assistanceNeeded = in.readByte() != 0;
        this.isTermsAccepted = in.readByte() != 0;
        this.termsAcceptedDate = in.readString();
        this.securityQuestion = in.readString();
    }*/

   /* public static final Creator<ProfileSelf> CREATOR = new Creator<ProfileSelf>() {
        @Override
        public ProfileSelf createFromParcel(Parcel source) {
            return new ProfileSelf(source);
        }

        @Override
        public ProfileSelf[] newArray(int size) {
            return new ProfileSelf[size];
        }
    };*/
}
