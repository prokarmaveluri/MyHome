package com.prokarma.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by kwelsh on 4/28/17.
 */

public class Profile implements Parcelable {

    //New Values from API change 6-12-2017
    public String userId;  //DHOME ID is pointing to selfId now. Reference: user story 25349
    public String selfId;
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

    public boolean isTermsAccepted;
    public String termsAcceptedDate;
    public String securityQuestion;

    public int numPortalProofWizardAttempts;
    public boolean showPortalProofWizard;
    public int photoUploadCountThisPeriod;
    public String photoUploadStartOfThisPeriod;

    public ProfileSelf profileSelf;

    public Profile() {

    }

    public Profile(String userId, String selfId, ProfileSelf profileSelf, String userName,
                   boolean isVerified, String securityQuestion, String termsAcceptedDate, boolean isTermsAccepted,
                   String createdDate, String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName,
                   String clientID, String remoteID, String reasonForVisit,
                   boolean translationNeeded, String translatorLanguage, boolean assistanceNeeded) {
        this.userId = userId;
        this.selfId = selfId;
        this.userName = userName;

        this.isVerified = isVerified;
        this.securityQuestion = securityQuestion;
        this.termsAcceptedDate = termsAcceptedDate;
        this.isTermsAccepted = isTermsAccepted;

        this.createdDate = createdDate;
        this.phoneNumberType = phoneNumberType;
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.primaryCaregiverName = primaryCaregiverName;
        this.clientID = clientID;
        this.remoteID = remoteID;
        this.reasonForVisit = reasonForVisit;
        this.translationNeeded = translationNeeded;
        this.translatorLanguage = translatorLanguage;
        this.assistanceNeeded = assistanceNeeded;

        this.profileSelf = profileSelf;

        this.idLevel = this.profileSelf.idLevel;
        this.firstName = this.profileSelf.firstName;
        this.lastName = this.profileSelf.lastName;
        this.preferredName = this.profileSelf.preferredName;
        this.email = this.profileSelf.email;
        this.dateOfBirth = this.profileSelf.dateOfBirth;
        this.gender = this.profileSelf.gender;
        this.isPregnant = this.profileSelf.isPregnant;
        this.weeksPregnant = this.profileSelf.weeksPregnant;
        this.phoneNumber = this.profileSelf.phoneNumber;
        this.address = this.profileSelf.address;
        this.insuranceProvider = this.profileSelf.insuranceProvider;
    }

    public Profile(String phoneNumberType, String contactName, String contactPhoneNumber, String primaryCaregiverName, String clientID, String remoteID, String reasonForVisit, boolean translationNeeded, String translatorLanguage, boolean assistanceNeeded) {
        this.userId = null;
        this.selfId = null;
        this.userName = null;
        this.isVerified = false;
        this.securityQuestion = null;
        this.termsAcceptedDate = null;
        this.isTermsAccepted = false;

        this.createdDate = null;
        this.phoneNumberType = phoneNumberType;
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.primaryCaregiverName = primaryCaregiverName;
        this.clientID = clientID;
        this.remoteID = remoteID;
        this.reasonForVisit = reasonForVisit;
        this.translationNeeded = translationNeeded;
        this.translatorLanguage = translatorLanguage;
        this.assistanceNeeded = assistanceNeeded;

        this.profileSelf = profileSelf;

        this.idLevel = this.profileSelf.idLevel;
        this.firstName = this.profileSelf.firstName;
        this.lastName = this.profileSelf.lastName;
        this.preferredName = this.profileSelf.preferredName;
        this.email = this.profileSelf.email;
        this.dateOfBirth = this.profileSelf.dateOfBirth;
        this.gender = this.profileSelf.gender;
        this.isPregnant = this.profileSelf.isPregnant;
        this.weeksPregnant = this.profileSelf.weeksPregnant;
        this.phoneNumber = this.profileSelf.phoneNumber;
        this.address = this.profileSelf.address;
        this.insuranceProvider = this.profileSelf.insuranceProvider;
    }

    public void setEmail(String email) {
        if (this.profileSelf != null) {
            this.profileSelf.email = email;
        }
    }

    public void setZipCode(String zipCode) {
        this.profileSelf.address.setZipCode(zipCode);
    }

    @Override
    public String toString() {
        return "Profile{" +
                ", phoneNumberType='" + phoneNumberType + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhoneNumber='" + contactPhoneNumber + '\'' +
                ", primaryCaregiverName='" + primaryCaregiverName + '\'' +
                ", clientID='" + clientID + '\'' +
                ", remoteID='" + remoteID + '\'' +
                ", reasonForVisit='" + reasonForVisit + '\'' +
                ", translationNeeded=" + translationNeeded +
                ", translatorLanguage='" + translatorLanguage + '\'' +
                ", assistanceNeeded=" + assistanceNeeded +
                ", profileSelf=" + profileSelf.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return translationNeeded == profile.translationNeeded &&
                assistanceNeeded == profile.assistanceNeeded &&
                Objects.equals(phoneNumberType, profile.phoneNumberType) &&
                Objects.equals(contactName, profile.contactName) &&
                Objects.equals(contactPhoneNumber, profile.contactPhoneNumber) &&
                Objects.equals(primaryCaregiverName, profile.primaryCaregiverName) &&
                Objects.equals(clientID, profile.clientID) &&
                Objects.equals(remoteID, profile.remoteID) &&
                Objects.equals(reasonForVisit, profile.reasonForVisit) &&
                Objects.equals(translatorLanguage, profile.translatorLanguage) &&
                Objects.equals(profileSelf, profile.profileSelf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumberType, contactName, contactPhoneNumber, primaryCaregiverName, clientID, remoteID, reasonForVisit, translationNeeded, translatorLanguage, assistanceNeeded, profileSelf);
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

        if (!Objects.equals(clientID, profile.clientID) && (profile.clientID != null && !profile.clientID.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(remoteID, profile.remoteID) && (profile.remoteID != null && !profile.remoteID.isEmpty())) {
            askToSave = true;
        }

        boolean shouldAskToSaveForSelf = false;
        if (this.profileSelf != null) {
            shouldAskToSaveForSelf = this.profileSelf.shouldAskToSave(profile);
        }
        if (shouldAskToSaveForSelf) {
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
            profile.phoneNumberType = otherProfile.phoneNumberType;
            profile.contactName = otherProfile.contactName;
            profile.contactPhoneNumber = otherProfile.contactPhoneNumber;
            profile.primaryCaregiverName = otherProfile.primaryCaregiverName;
            profile.clientID = otherProfile.clientID;
            profile.remoteID = otherProfile.remoteID;
            profile.reasonForVisit = otherProfile.reasonForVisit;
            profile.translationNeeded = otherProfile.translationNeeded;
            profile.translatorLanguage = otherProfile.translatorLanguage;
            profile.assistanceNeeded = otherProfile.assistanceNeeded;

            profile.profileSelf = otherProfile.profileSelf; //ProfileSelf.copy(otherProfile.profileSelf);
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
            profile.phoneNumberType = otherProfile.phoneNumberType;
            profile.clientID = otherProfile.clientID;
            profile.remoteID = otherProfile.remoteID;

            profile.profileSelf = otherProfile.profileSelf; //ProfileSelf.copy(otherProfile.profileSelf);
        }

        return profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.selfId);
        dest.writeString(this.userName);
        dest.writeByte(this.isVerified ? (byte) 1 : (byte) 0);
        dest.writeString(this.createdDate);
        dest.writeString(this.phoneNumberType);
        dest.writeString(this.contactName);
        dest.writeString(this.contactPhoneNumber);
        dest.writeString(this.primaryCaregiverName);
        dest.writeString(this.clientID);
        dest.writeString(this.remoteID);
        dest.writeString(this.reasonForVisit);
        dest.writeByte(this.translationNeeded ? (byte) 1 : (byte) 0);
        dest.writeString(this.translatorLanguage);
        dest.writeByte(this.assistanceNeeded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTermsAccepted ? (byte) 1 : (byte) 0);
        dest.writeString(this.termsAcceptedDate);
        dest.writeString(this.securityQuestion);

        dest.writeParcelable(this.profileSelf, flags);
    }

    protected Profile(Parcel in) {
        this.userId = in.readString();
        this.selfId = in.readString();
        this.userName = in.readString();
        this.isVerified = in.readByte() != 0;
        this.createdDate = in.readString();
        this.phoneNumberType = in.readString();
        this.contactName = in.readString();
        this.contactPhoneNumber = in.readString();
        this.primaryCaregiverName = in.readString();
        this.clientID = in.readString();
        this.remoteID = in.readString();
        this.reasonForVisit = in.readString();
        this.translationNeeded = in.readByte() != 0;
        this.translatorLanguage = in.readString();
        this.assistanceNeeded = in.readByte() != 0;
        this.isTermsAccepted = in.readByte() != 0;
        this.termsAcceptedDate = in.readString();
        this.securityQuestion = in.readString();
        this.profileSelf = in.readParcelable(ProfileSelf.class.getClassLoader());

        this.idLevel = this.profileSelf.idLevel;
        this.firstName = this.profileSelf.firstName;
        this.lastName = this.profileSelf.lastName;
        this.preferredName = this.profileSelf.preferredName;
        this.email = this.profileSelf.email;
        this.dateOfBirth = this.profileSelf.dateOfBirth;
        this.gender = this.profileSelf.gender;
        this.isPregnant = this.profileSelf.isPregnant;
        this.weeksPregnant = this.profileSelf.weeksPregnant;
        this.phoneNumber = this.profileSelf.phoneNumber;
        this.address = this.profileSelf.address;
        this.insuranceProvider = this.profileSelf.insuranceProvider;
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
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