package com.dignityhealth.myhome.features.profile;

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
}
