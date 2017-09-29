package com.prokarma.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by kwelsh on 4/28/17.
 */

public class Profile implements Parcelable {

    //New Values from API change 6-12-2017
    public String userId;
    public String userName;
    public String idLevel;
    public boolean isVerified;
    public String createdDate;

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
    public String reasonForVisit;
    public boolean translationNeeded;
    public String translatorLanguage;
    public boolean assistanceNeeded;

    public Profile() {

    }

    public Profile(String userId, String userName, String idLevel, boolean isVerified, String createdDate, String firstName, String middleInitial, String lastName, String preferredName, String gender, String dateOfBirth, Address address, String phoneNumber, String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName, boolean isPregnant, String weeksPregnant, InsuranceProvider insuranceProvider, String clientID, String remoteID, String email, String reasonForVisit, boolean translationNeeded, String translatorLanguage, boolean assistanceNeeded) {
        this.userId = userId;
        this.userName = userName;
        this.idLevel = idLevel;
        this.isVerified = isVerified;
        this.createdDate = createdDate;
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
        this.reasonForVisit = reasonForVisit;
        this.translationNeeded = translationNeeded;
        this.translatorLanguage = translatorLanguage;
        this.assistanceNeeded = assistanceNeeded;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipCode(String zipCode){
        this.address.setZipCode(zipCode);
    }

    @Override
    public String toString() {
        return "Profile{" +
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
                ", insurancePlan=" + insuranceProvider +
                ", clientID='" + clientID + '\'' +
                ", remoteID='" + remoteID + '\'' +
                ", email='" + email + '\'' +
                ", reasonForVisit='" + reasonForVisit + '\'' +
                ", translationNeeded=" + translationNeeded +
                ", translatorLanguage='" + translatorLanguage + '\'' +
                ", assistanceNeeded=" + assistanceNeeded +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return isPregnant == profile.isPregnant &&
                translationNeeded == profile.translationNeeded &&
                assistanceNeeded == profile.assistanceNeeded &&
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
                Objects.equals(email, profile.email) &&
                Objects.equals(reasonForVisit, profile.reasonForVisit) &&
                Objects.equals(translatorLanguage, profile.translatorLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleInitial, lastName, preferredName, gender, dateOfBirth, address, phoneNumber, phoneNumberType, contactName, contactPhoneNumber, primaryCaregiverName, isPregnant, weeksPregnant, insuranceProvider, clientID, remoteID, email, reasonForVisit, translationNeeded, translatorLanguage, assistanceNeeded);
    }

    /**
     * Method to see if we should ask before saving.
     * We only need to ask before saving if any profile field was changed (not added).
     * We don't care about any booking fields (email, caregiver name, reason for visit, translator language...)
     *
     * @param o
     * @return
     */
    public boolean shouldAskToSave(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        boolean askToSave = false;

        if (!Objects.equals(firstName, profile.firstName) && (profile.firstName != null && !profile.firstName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(lastName, profile.lastName) && (profile.lastName != null && !profile.lastName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(preferredName, profile.preferredName) && (profile.preferredName != null && !profile.preferredName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(gender.toUpperCase(), profile.gender.toUpperCase()) && !profile.gender.equalsIgnoreCase("Unknown")) {
            askToSave = true;
        }

        if (!Objects.equals(dateOfBirth, profile.dateOfBirth) && (profile.dateOfBirth != null && !profile.dateOfBirth.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.line1, profile.address.line1) && (profile.address.line1 != null && !profile.address.line1.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.line2, profile.address.line2) && (profile.address.line2 != null && !profile.address.line2.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.city, profile.address.city) && (profile.address.city != null && !profile.address.city.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.stateOrProvince, profile.address.stateOrProvince) && (profile.address.stateOrProvince != null && !profile.address.stateOrProvince.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.zipCode, profile.address.zipCode) && (profile.address.zipCode != null && !profile.address.zipCode.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.countryCode, profile.address.countryCode) && (profile.address.countryCode != null && !profile.address.countryCode.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(phoneNumber, profile.phoneNumber) && (profile.phoneNumber != null && !profile.phoneNumber.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(insuranceProvider.insurancePlan, profile.insuranceProvider.insurancePlan) && (profile.insuranceProvider.insurancePlan != null && !profile.insuranceProvider.insurancePlan.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(insuranceProvider.memberNumber, profile.insuranceProvider.memberNumber) && (profile.insuranceProvider.memberNumber != null && !profile.insuranceProvider.memberNumber.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(insuranceProvider.groupNumber, profile.insuranceProvider.groupNumber) && (profile.insuranceProvider.groupNumber != null && !profile.insuranceProvider.groupNumber.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(clientID, profile.clientID) && (profile.clientID != null && !profile.clientID.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(remoteID, profile.remoteID) && (profile.remoteID != null && !profile.remoteID.isEmpty())) {
            askToSave = true;
        }

        return askToSave;
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton
     *
     * @param otherProfile
     * @return
     */
    public static Profile copy(Profile otherProfile) {
        Profile profile = new Profile();

        if (otherProfile != null) {
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
            profile.isPregnant = otherProfile.isPregnant;
            profile.weeksPregnant = otherProfile.weeksPregnant;
            profile.insuranceProvider = InsuranceProvider.copy(otherProfile.insuranceProvider);
            profile.clientID = otherProfile.clientID;
            profile.remoteID = otherProfile.remoteID;
            profile.email = otherProfile.email;
            profile.reasonForVisit = otherProfile.reasonForVisit;
            profile.translationNeeded = otherProfile.translationNeeded;
            profile.translatorLanguage = otherProfile.translatorLanguage;
            profile.assistanceNeeded = otherProfile.assistanceNeeded;
        }

        return profile;
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton.
     * This method does not save Booking-specfic info.
     *
     * @param otherProfile
     * @return
     */
    public static Profile copySansBookingInfo(Profile otherProfile) {
        Profile profile = new Profile();

        if (otherProfile != null) {
            profile.firstName = otherProfile.firstName;
            profile.middleInitial = otherProfile.middleInitial;
            profile.lastName = otherProfile.lastName;
            profile.preferredName = otherProfile.preferredName;
            profile.gender = otherProfile.gender;
            profile.dateOfBirth = otherProfile.dateOfBirth;
            profile.address = Address.copy(otherProfile.address);
            profile.phoneNumber = otherProfile.phoneNumber;
            profile.phoneNumberType = otherProfile.phoneNumberType;
            profile.insuranceProvider = InsuranceProvider.copySansBookingInfo(otherProfile.insuranceProvider);
            profile.clientID = otherProfile.clientID;
            profile.remoteID = otherProfile.remoteID;
        }

        return profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.middleInitial);
        dest.writeString(this.lastName);
        dest.writeString(this.preferredName);
        dest.writeString(this.gender);
        dest.writeString(this.dateOfBirth);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.phoneNumberType);
        dest.writeString(this.contactName);
        dest.writeString(this.contactPhoneNumber);
        dest.writeString(this.primaryCaregiverName);
        dest.writeByte(this.isPregnant ? (byte) 1 : (byte) 0);
        dest.writeString(this.weeksPregnant);
        dest.writeParcelable(this.insuranceProvider, flags);
        dest.writeString(this.clientID);
        dest.writeString(this.remoteID);
        dest.writeString(this.email);
        dest.writeString(this.reasonForVisit);
    }

    protected Profile(Parcel in) {
        this.firstName = in.readString();
        this.middleInitial = in.readString();
        this.lastName = in.readString();
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
    }

    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}