package com.dignityhealth.myhome.features.profile;

import java.util.Objects;

/**
 * Created by kwelsh on 5/2/17.
 */

public class InsuranceProvider {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceProvider that = (InsuranceProvider) o;
        return Objects.equals(providerName, that.providerName) &&
                Objects.equals(insurancePlan, that.insurancePlan) &&
                Objects.equals(groupNumber, that.groupNumber) &&
                Objects.equals(memberNumber, that.memberNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, insurancePlan, groupNumber, memberNumber);
    }
}
