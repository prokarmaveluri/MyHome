package com.prokarma.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by veluri on 12/28/17.
 */

public class ProfileSelf implements Parcelable {

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

    public ProfileSelf() {

    }

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

    public ProfileSelf(
            String firstName, String lastName, String preferredName,
            String email, String dateOfBirth, String gender,
            String degreeCredential, String specialty, String phoneNumber,
            String mobilePhoneNumber, String officePhoneNumber,
            boolean isPregnant, String weeksPregnant,
            String numKbaAttempts, String nextKbaAttempt,
            Address address, InsuranceProvider insuranceProvider) {
        this.personId = null;
        this.idLevel = null;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipCode(String zipCode) {
        this.address.setZipCode(zipCode);
    }

    @Override
    public String toString() {

        return "Profile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preferredName='" + preferredName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", degreeCredential='" + degreeCredential + '\'' +
                ", specialty='" + specialty + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mobilePhoneNumber='" + mobilePhoneNumber + '\'' +
                ", officePhoneNumber='" + officePhoneNumber + '\'' +
                ", isPregnant=" + isPregnant +
                ", weeksPregnant='" + weeksPregnant + '\'' +
                ", numKbaAttempts='" + numKbaAttempts + '\'' +
                ", nextKbaAttempt='" + nextKbaAttempt + '\'' +
                ", address=" + address +
                ", insuranceProvider=" + insuranceProvider +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileSelf profileSelf = (ProfileSelf) o;
        return Objects.equals(firstName, profileSelf.firstName) &&
                Objects.equals(lastName, profileSelf.lastName) &&
                Objects.equals(preferredName, profileSelf.preferredName) &&
                Objects.equals(email, profileSelf.email) &&
                Objects.equals(dateOfBirth, profileSelf.dateOfBirth) &&
                Objects.equals(gender, profileSelf.gender) &&
                Objects.equals(degreeCredential, profileSelf.degreeCredential) &&
                Objects.equals(specialty, profileSelf.specialty) &&
                Objects.equals(phoneNumber, profileSelf.phoneNumber) &&
                Objects.equals(mobilePhoneNumber, profileSelf.mobilePhoneNumber) &&
                Objects.equals(officePhoneNumber, profileSelf.officePhoneNumber) &&
                isPregnant == profileSelf.isPregnant &&
                Objects.equals(weeksPregnant, profileSelf.weeksPregnant) &&
                Objects.equals(numKbaAttempts, profileSelf.numKbaAttempts) &&
                Objects.equals(nextKbaAttempt, profileSelf.nextKbaAttempt) &&
                Objects.equals(address, profileSelf.address) &&
                Objects.equals(insuranceProvider, profileSelf.insuranceProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, preferredName,
                email, dateOfBirth, gender, degreeCredential, specialty,
                phoneNumber, mobilePhoneNumber, officePhoneNumber,
                isPregnant, weeksPregnant, numKbaAttempts, nextKbaAttempt, address, insuranceProvider);
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
        ProfileSelf profileSelf = (ProfileSelf) o;
        boolean askToSave = false;

        if (!Objects.equals(firstName, profileSelf.firstName) && (profileSelf.firstName != null && !profileSelf.firstName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(lastName, profileSelf.lastName) && (profileSelf.lastName != null && !profileSelf.lastName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(preferredName, profileSelf.preferredName) && (profileSelf.preferredName != null && !profileSelf.preferredName.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(email, profileSelf.email) && (profileSelf.email != null && !profileSelf.email.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(dateOfBirth, profileSelf.dateOfBirth) && (profileSelf.dateOfBirth != null && !profileSelf.dateOfBirth.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(gender.toUpperCase(), profileSelf.gender.toUpperCase()) && !profileSelf.gender.equalsIgnoreCase("Unknown")) {
            askToSave = true;
        }

        if (!Objects.equals(degreeCredential, profileSelf.degreeCredential) && (profileSelf.degreeCredential != null && !profileSelf.degreeCredential.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(specialty, profileSelf.specialty) && (profileSelf.specialty != null && !profileSelf.specialty.isEmpty())) {
            askToSave = true;
        }


        if (!Objects.equals(phoneNumber, profileSelf.phoneNumber) && (profileSelf.phoneNumber != null && !profileSelf.phoneNumber.isEmpty())) {
            askToSave = true;
        }
        if (!Objects.equals(mobilePhoneNumber, profileSelf.mobilePhoneNumber) && (profileSelf.mobilePhoneNumber != null && !profileSelf.mobilePhoneNumber.isEmpty())) {
            askToSave = true;
        }
        if (!Objects.equals(officePhoneNumber, profileSelf.officePhoneNumber) && (profileSelf.officePhoneNumber != null && !profileSelf.officePhoneNumber.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(isPregnant, profileSelf.isPregnant)) {
            askToSave = true;
        }
        if (!Objects.equals(weeksPregnant, profileSelf.weeksPregnant) && (profileSelf.weeksPregnant != null && !profileSelf.weeksPregnant.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(numKbaAttempts, profileSelf.numKbaAttempts) && (profileSelf.numKbaAttempts != null && !profileSelf.numKbaAttempts.isEmpty())) {
            askToSave = true;
        }
        if (!Objects.equals(nextKbaAttempt, profileSelf.nextKbaAttempt) && (profileSelf.nextKbaAttempt != null && !profileSelf.nextKbaAttempt.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.line1, profileSelf.address.line1) && (profileSelf.address.line1 != null && !profileSelf.address.line1.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.line2, profileSelf.address.line2) && (profileSelf.address.line2 != null && !profileSelf.address.line2.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.city, profileSelf.address.city) && (profileSelf.address.city != null && !profileSelf.address.city.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.stateOrProvince, profileSelf.address.stateOrProvince) && (profileSelf.address.stateOrProvince != null && !profileSelf.address.stateOrProvince.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.zipCode, profileSelf.address.zipCode) && (profileSelf.address.zipCode != null && !profileSelf.address.zipCode.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(address.countryCode, profileSelf.address.countryCode) && (profileSelf.address.countryCode != null && !profileSelf.address.countryCode.isEmpty())) {
            askToSave = true;
        }


        if (!Objects.equals(insuranceProvider.insurancePlan, profileSelf.insuranceProvider.insurancePlan) && (profileSelf.insuranceProvider.insurancePlan != null && !profileSelf.insuranceProvider.insurancePlan.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(insuranceProvider.memberNumber, profileSelf.insuranceProvider.memberNumber) && (profileSelf.insuranceProvider.memberNumber != null && !profileSelf.insuranceProvider.memberNumber.isEmpty())) {
            askToSave = true;
        }

        if (!Objects.equals(insuranceProvider.groupNumber, profileSelf.insuranceProvider.groupNumber) && (profileSelf.insuranceProvider.groupNumber != null && !profileSelf.insuranceProvider.groupNumber.isEmpty())) {
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
    public static ProfileSelf copy(ProfileSelf otherProfile) {
        ProfileSelf profileSelf = new ProfileSelf();

        if (otherProfile != null) {
            profileSelf.firstName = otherProfile.firstName;
            profileSelf.lastName = otherProfile.lastName;
            profileSelf.preferredName = otherProfile.preferredName;
            profileSelf.email = otherProfile.email;
            profileSelf.dateOfBirth = otherProfile.dateOfBirth;
            profileSelf.gender = otherProfile.gender;

            profileSelf.degreeCredential = otherProfile.degreeCredential;
            profileSelf.specialty = otherProfile.specialty;
            profileSelf.phoneNumber = otherProfile.phoneNumber;

            profileSelf.mobilePhoneNumber = otherProfile.mobilePhoneNumber;
            profileSelf.officePhoneNumber = otherProfile.officePhoneNumber;
            profileSelf.isPregnant = otherProfile.isPregnant;
            profileSelf.weeksPregnant = otherProfile.weeksPregnant;
            profileSelf.numKbaAttempts = otherProfile.numKbaAttempts;
            profileSelf.nextKbaAttempt = otherProfile.nextKbaAttempt;

            profileSelf.address = otherProfile.address; //Address.copy(otherProfile.address);
            profileSelf.insuranceProvider = otherProfile.insuranceProvider; //InsuranceProvider.copy(otherProfile.insuranceProvider);
        }

        return profileSelf;
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton.
     * This method does not save Booking-specfic info.
     *
     * @param otherProfile
     * @return
     */
    public static ProfileSelf copySansBookingInfo(ProfileSelf otherProfile) {
        ProfileSelf profile = new ProfileSelf();

        if (otherProfile != null) {
            profile.firstName = otherProfile.firstName;
            profile.lastName = otherProfile.lastName;
            profile.preferredName = otherProfile.preferredName;
            profile.email = otherProfile.email;
            profile.dateOfBirth = otherProfile.dateOfBirth;
            profile.gender = otherProfile.gender;

            profile.degreeCredential = otherProfile.degreeCredential;
            profile.specialty = otherProfile.specialty;
            profile.phoneNumber = otherProfile.phoneNumber;
            profile.mobilePhoneNumber = otherProfile.mobilePhoneNumber;
            profile.officePhoneNumber = otherProfile.officePhoneNumber;

            profile.isPregnant = otherProfile.isPregnant;
            profile.weeksPregnant = otherProfile.weeksPregnant;
            profile.numKbaAttempts = otherProfile.numKbaAttempts;
            profile.nextKbaAttempt = otherProfile.nextKbaAttempt;

            profile.address = otherProfile.address; //Address.copy(otherProfile.address);
            profile.insuranceProvider = otherProfile.insuranceProvider; //InsuranceProvider.copySansBookingInfo(otherProfile.insuranceProvider);
        }

        return profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.personId);
        dest.writeString(this.idLevel);

        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.preferredName);
        dest.writeString(this.email);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.gender);

        dest.writeString(this.degreeCredential);
        dest.writeString(this.specialty);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.mobilePhoneNumber);
        dest.writeString(this.officePhoneNumber);

        dest.writeByte(this.isPregnant ? (byte) 1 : (byte) 0);
        dest.writeString(this.weeksPregnant);
        dest.writeString(this.numKbaAttempts);
        dest.writeString(this.nextKbaAttempt);

        dest.writeParcelable(this.address, flags);
        dest.writeParcelable(this.insuranceProvider, flags);
    }

    protected ProfileSelf(Parcel in) {
        this.personId = in.readString();
        this.idLevel = in.readString();

        this.firstName = in.readString();
        this.lastName = in.readString();
        this.preferredName = in.readString();
        this.email = in.readString();
        this.dateOfBirth = in.readString();
        this.gender = in.readString();

        this.degreeCredential = in.readString();
        this.specialty = in.readString();
        this.phoneNumber = in.readString();
        this.mobilePhoneNumber = in.readString();
        this.officePhoneNumber = in.readString();

        this.isPregnant = in.readByte() != 0;
        this.weeksPregnant = in.readString();
        this.numKbaAttempts = in.readString();
        this.nextKbaAttempt = in.readString();

        this.address = in.readParcelable(Address.class.getClassLoader());
        this.insuranceProvider = in.readParcelable(InsuranceProvider.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProfileSelf> CREATOR = new Parcelable.Creator<ProfileSelf>() {
        @Override
        public ProfileSelf createFromParcel(Parcel source) {
            return new ProfileSelf(source);
        }

        @Override
        public ProfileSelf[] newArray(int size) {
            return new ProfileSelf[size];
        }
    };
}
