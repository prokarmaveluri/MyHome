package com.prokarma.myhome.features.profile;

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
    public String insurancePlanPermaLink;

    public InsuranceProvider() {
    }

    public InsuranceProvider(String providerName, String insurancePlan, String groupNumber, String memberNumber, String insurancePhoneNumber, String insurancePlanPermaLink) {
        this.providerName = providerName;
        this.insurancePlan = insurancePlan;
        this.groupNumber = groupNumber;
        this.memberNumber = memberNumber;
        this.insurancePhoneNumber = insurancePhoneNumber;
        this.insurancePlanPermaLink = insurancePlanPermaLink;
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
                Objects.equals(insurancePhoneNumber, that.insurancePhoneNumber) &&
                Objects.equals(insurancePlanPermaLink, that.insurancePlanPermaLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, insurancePlan, groupNumber, memberNumber, insurancePhoneNumber, insurancePlanPermaLink);
    }

    @Override
    public String toString() {
        return "InsuranceProvider{" +
                "providerName='" + providerName + '\'' +
                ", insurancePlan='" + insurancePlan + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", memberNumber='" + memberNumber + '\'' +
                ", insurancePhoneNumber='" + insurancePhoneNumber + '\'' +
                ", insurancePlanPermaLink='" + insurancePlanPermaLink + '\'' +
                '}';

    }

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton
     *
     * @param otherInsuranceProvider
     * @return
     */
    /*public static InsuranceProvider copy(InsuranceProvider otherInsuranceProvider) {
        InsuranceProvider insuranceProvider = new InsuranceProvider();

        if (otherInsuranceProvider != null) {
            insuranceProvider.providerName = otherInsuranceProvider.providerName;
            insuranceProvider.insurancePlan = otherInsuranceProvider.insurancePlan;
            insuranceProvider.groupNumber = otherInsuranceProvider.groupNumber;
            insuranceProvider.memberNumber = otherInsuranceProvider.memberNumber;
            insuranceProvider.insurancePhoneNumber = otherInsuranceProvider.insurancePhoneNumber;
            insuranceProvider.insurancePlanPermaLink = otherInsuranceProvider.insurancePlanPermaLink;
        }

        return insuranceProvider;
    }*/

    /**
     * Simple Copy-By-Value method to avoid copy by reference problems when using a Singleton.
     * This method does not save Booking-specfic info.
     *
     * @param otherInsuranceProvider
     * @return
     */
    /*public static InsuranceProvider copySansBookingInfo(InsuranceProvider otherInsuranceProvider) {
        InsuranceProvider insuranceProvider = new InsuranceProvider();

        if (otherInsuranceProvider != null) {
            insuranceProvider.insurancePlan = otherInsuranceProvider.insurancePlan;
            insuranceProvider.groupNumber = otherInsuranceProvider.groupNumber;
            insuranceProvider.memberNumber = otherInsuranceProvider.memberNumber;
        }

        return insuranceProvider;
    }*/

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
        dest.writeString(this.insurancePlanPermaLink);
    }

    protected InsuranceProvider(Parcel in) {
        this.providerName = in.readString();
        this.insurancePlan = in.readString();
        this.groupNumber = in.readString();
        this.memberNumber = in.readString();
        this.insurancePhoneNumber = in.readString();
        this.insurancePlanPermaLink = in.readString();
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
