package com.dignityhealth.myhome.features.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by kwelsh on 5/2/17.
 */

public class InsuranceProvider implements Parcelable {
    public String providerName;
    public String insurancePlan;
    public String groupNumber;
    public String memberNumber;
    public String insurancePhoneNumber;

    @Override
    public String toString() {
        return "InsuranceProvider{" +
                "providerName='" + providerName + '\'' +
                ", insurancePlan='" + insurancePlan + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", memberNumber='" + memberNumber + '\'' +
                ", insurancePhoneNumber='" + insurancePhoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceProvider that = (InsuranceProvider) o;
        return Objects.equals(providerName, that.providerName) &&
                Objects.equals(insurancePlan, that.insurancePlan) &&
                Objects.equals(groupNumber, that.groupNumber) &&
                Objects.equals(memberNumber, that.memberNumber) &&
                Objects.equals(insurancePhoneNumber, that.insurancePhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, insurancePlan, groupNumber, memberNumber, insurancePhoneNumber);
    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton
     *
     * @param otherInsuranceProvider
     * @return
     */
    public static InsuranceProvider copy(InsuranceProvider otherInsuranceProvider) {
        InsuranceProvider insuranceProvider = new InsuranceProvider();

        if (otherInsuranceProvider != null) {
            insuranceProvider.providerName = otherInsuranceProvider.providerName;
            insuranceProvider.insurancePlan = otherInsuranceProvider.insurancePlan;
            insuranceProvider.groupNumber = otherInsuranceProvider.groupNumber;
            insuranceProvider.memberNumber = otherInsuranceProvider.memberNumber;
            insuranceProvider.insurancePhoneNumber = otherInsuranceProvider.insurancePhoneNumber;
        }

        return insuranceProvider;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.providerName);
        dest.writeString(this.insurancePlan);
        dest.writeString(this.groupNumber);
        dest.writeString(this.memberNumber);
        dest.writeString(this.insurancePhoneNumber);
    }

    public InsuranceProvider() {
    }

    protected InsuranceProvider(Parcel in) {
        this.providerName = in.readString();
        this.insurancePlan = in.readString();
        this.groupNumber = in.readString();
        this.memberNumber = in.readString();
        this.insurancePhoneNumber = in.readString();
    }

    public static final Parcelable.Creator<InsuranceProvider> CREATOR = new Parcelable.Creator<InsuranceProvider>() {
        @Override
        public InsuranceProvider createFromParcel(Parcel source) {
            return new InsuranceProvider(source);
        }

        @Override
        public InsuranceProvider[] newArray(int size) {
            return new InsuranceProvider[size];
        }
    };
}
