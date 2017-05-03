package com.dignityhealth.myhome.features.profile;

/**
 * Created by kwelsh on 5/2/17.
 */

public class Address {
    public String line1;
    public String line2;
    public String city;
    public String stateOrProvince;
    public String zipCode;
    public String countryCode;

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
