package com.prokarma.myhome.utils;

import com.americanwell.sdk.entity.SDKEntity;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.ProviderDetailsAddress;
import com.prokarma.myhome.features.profile.Address;

import java.util.HashMap;

/**
 * Created by veluri on 12/20/17.
 */

public class AddressUtil {

    public static final HashMap<String, String> STATE_MAP;

    static {
        STATE_MAP = new HashMap<String, String>();
        STATE_MAP.put("AL", "Alabama");
        STATE_MAP.put("AK", "Alaska");
        STATE_MAP.put("AB", "Alberta");
        STATE_MAP.put("AZ", "Arizona");
        STATE_MAP.put("AR", "Arkansas");
        STATE_MAP.put("BC", "British Columbia");
        STATE_MAP.put("CA", "California");
        STATE_MAP.put("CO", "Colorado");
        STATE_MAP.put("CT", "Connecticut");
        STATE_MAP.put("DE", "Delaware");
        STATE_MAP.put("DC", "District Of Columbia");
        STATE_MAP.put("FL", "Florida");
        STATE_MAP.put("GA", "Georgia");
        STATE_MAP.put("GU", "Guam");
        STATE_MAP.put("HI", "Hawaii");
        STATE_MAP.put("ID", "Idaho");
        STATE_MAP.put("IL", "Illinois");
        STATE_MAP.put("IN", "Indiana");
        STATE_MAP.put("IA", "Iowa");
        STATE_MAP.put("KS", "Kansas");
        STATE_MAP.put("KY", "Kentucky");
        STATE_MAP.put("LA", "Louisiana");
        STATE_MAP.put("ME", "Maine");
        STATE_MAP.put("MB", "Manitoba");
        STATE_MAP.put("MD", "Maryland");
        STATE_MAP.put("MA", "Massachusetts");
        STATE_MAP.put("MI", "Michigan");
        STATE_MAP.put("MN", "Minnesota");
        STATE_MAP.put("MS", "Mississippi");
        STATE_MAP.put("MO", "Missouri");
        STATE_MAP.put("MT", "Montana");
        STATE_MAP.put("NE", "Nebraska");
        STATE_MAP.put("NV", "Nevada");
        STATE_MAP.put("NB", "New Brunswick");
        STATE_MAP.put("NH", "New Hampshire");
        STATE_MAP.put("NJ", "New Jersey");
        STATE_MAP.put("NM", "New Mexico");
        STATE_MAP.put("NY", "New York");
        STATE_MAP.put("NF", "Newfoundland");
        STATE_MAP.put("NC", "North Carolina");
        STATE_MAP.put("ND", "North Dakota");
        STATE_MAP.put("NT", "Northwest Territories");
        STATE_MAP.put("NS", "Nova Scotia");
        STATE_MAP.put("NU", "Nunavut");
        STATE_MAP.put("OH", "Ohio");
        STATE_MAP.put("OK", "Oklahoma");
        STATE_MAP.put("ON", "Ontario");
        STATE_MAP.put("OR", "Oregon");
        STATE_MAP.put("PA", "Pennsylvania");
        STATE_MAP.put("PE", "Prince Edward Island");
        STATE_MAP.put("PR", "Puerto Rico");
        STATE_MAP.put("QC", "Quebec");
        STATE_MAP.put("RI", "Rhode Island");
        STATE_MAP.put("SK", "Saskatchewan");
        STATE_MAP.put("SC", "South Carolina");
        STATE_MAP.put("SD", "South Dakota");
        STATE_MAP.put("TN", "Tennessee");
        STATE_MAP.put("TX", "Texas");
        STATE_MAP.put("UT", "Utah");
        STATE_MAP.put("VT", "Vermont");
        STATE_MAP.put("VI", "Virgin Islands");
        STATE_MAP.put("VA", "Virginia");
        STATE_MAP.put("WA", "Washington");
        STATE_MAP.put("WV", "West Virginia");
        STATE_MAP.put("WI", "Wisconsin");
        STATE_MAP.put("WY", "Wyoming");
        STATE_MAP.put("YT", "Yukon Territory");
    }


    public static final HashMap<String, String> STATE_MAP_LONG;

    static {
        STATE_MAP_LONG = new HashMap<String, String>();
        STATE_MAP_LONG.put("Alabama", "AL");
        STATE_MAP_LONG.put("Alaska", "AK");
        STATE_MAP_LONG.put("Alberta", "AB");
        STATE_MAP_LONG.put("Arizona", "AZ");
        STATE_MAP_LONG.put("Arkansas", "AR");
        STATE_MAP_LONG.put("British Columbia", "BC");
        STATE_MAP_LONG.put("California", "CA");
        STATE_MAP_LONG.put("Colorado", "CO");
        STATE_MAP_LONG.put("Connecticut", "CT");
        STATE_MAP_LONG.put("Delaware", "DE");
        STATE_MAP_LONG.put("District Of Columbia", "DC");
        STATE_MAP_LONG.put("Florida", "FL");
        STATE_MAP_LONG.put("Georgia", "GA");
        STATE_MAP_LONG.put("Guam", "GU");
        STATE_MAP_LONG.put("Hawaii", "HI");
        STATE_MAP_LONG.put("Idaho", "ID");
        STATE_MAP_LONG.put("Illinois", "IL");
        STATE_MAP_LONG.put("Indiana", "IN");
        STATE_MAP_LONG.put("Iowa", "IA");
        STATE_MAP_LONG.put("Kansas", "KS");
        STATE_MAP_LONG.put("Kentucky", "KY");
        STATE_MAP_LONG.put("Louisiana", "LA");
        STATE_MAP_LONG.put("Maine", "ME");
        STATE_MAP_LONG.put("Manitoba", "MB");
        STATE_MAP_LONG.put("Maryland", "MD");
        STATE_MAP_LONG.put("Massachusetts", "MA");
        STATE_MAP_LONG.put("Michigan", "MI");
        STATE_MAP_LONG.put("Minnesota", "MN");
        STATE_MAP_LONG.put("Mississippi", "MS");
        STATE_MAP_LONG.put("Missouri", "MO");
        STATE_MAP_LONG.put("Montana", "MT");
        STATE_MAP_LONG.put("Nebraska", "NE");
        STATE_MAP_LONG.put("Nevada", "NV");
        STATE_MAP_LONG.put("New Brunswick", "NB");
        STATE_MAP_LONG.put("New Hampshire", "NH");
        STATE_MAP_LONG.put("New Jersey", "NJ");
        STATE_MAP_LONG.put("New Mexico", "NM");
        STATE_MAP_LONG.put("New York", "NY");
        STATE_MAP_LONG.put("Newfoundland", "NF");
        STATE_MAP_LONG.put("North Carolina", "NC");
        STATE_MAP_LONG.put("North Dakota", "ND");
        STATE_MAP_LONG.put("Northwest Territories", "NT");
        STATE_MAP_LONG.put("Nova Scotia", "NS");
        STATE_MAP_LONG.put("Nunavut", "NU");
        STATE_MAP_LONG.put("Ohio", "OH");
        STATE_MAP_LONG.put("Oklahoma", "OK");
        STATE_MAP_LONG.put("Ontario", "ON");
        STATE_MAP_LONG.put("Oregon", "OR");
        STATE_MAP_LONG.put("Pennsylvania", "PA");
        STATE_MAP_LONG.put("Prince Edward Island", "PE");
        STATE_MAP_LONG.put("Puerto Rico", "PR");
        STATE_MAP_LONG.put("Quebec", "QC");
        STATE_MAP_LONG.put("Rhode Island", "RI");
        STATE_MAP_LONG.put("Saskatchewan", "SK");
        STATE_MAP_LONG.put("South Carolina", "SC");
        STATE_MAP_LONG.put("South Dakota", "SD");
        STATE_MAP_LONG.put("Tennessee", "TN");
        STATE_MAP_LONG.put("Texas", "TX");
        STATE_MAP_LONG.put("Utah", "UT");
        STATE_MAP_LONG.put("Vermont", "VT");
        STATE_MAP_LONG.put("Virgin Islands", "VI");
        STATE_MAP_LONG.put("Virginia", "VA");
        STATE_MAP_LONG.put("Washington", "WA");
        STATE_MAP_LONG.put("West Virginia", "WV");
        STATE_MAP_LONG.put("Wisconsin", "WI");
        STATE_MAP_LONG.put("Wyoming", "WY");
        STATE_MAP_LONG.put("Yukon Territory", "YT");
    }

    //https://wiki.acstechnologies.com/display/ACSDOC/Common+Approved+Address+Abbreviations
    //https://www.expertmarket.com/USPS-street-suffix

    public static final HashMap<String, String> ADDRESS_ABBREVIATIONS_MAP;

    static {
        ADDRESS_ABBREVIATIONS_MAP = new HashMap<String, String>();

        ADDRESS_ABBREVIATIONS_MAP.put("ALLY", "Alley");
        ADDRESS_ABBREVIATIONS_MAP.put("ARC", "Arcade");
        ADDRESS_ABBREVIATIONS_MAP.put("AVE", "Avenue");

        ADDRESS_ABBREVIATIONS_MAP.put("BVD", "Boulevard");
        ADDRESS_ABBREVIATIONS_MAP.put("BLVD", "Boulevard");
        ADDRESS_ABBREVIATIONS_MAP.put("BYPA", "Bypass");

        ADDRESS_ABBREVIATIONS_MAP.put("CTR", "Center");
        ADDRESS_ABBREVIATIONS_MAP.put("CCT", "Circuit");
        ADDRESS_ABBREVIATIONS_MAP.put("CL", "Close");
        ADDRESS_ABBREVIATIONS_MAP.put("CRN", "Corner");
        ADDRESS_ABBREVIATIONS_MAP.put("CIR", "Circle");
        ADDRESS_ABBREVIATIONS_MAP.put("CR", "Circle");
        ADDRESS_ABBREVIATIONS_MAP.put("CT", "Court");
        ADDRESS_ABBREVIATIONS_MAP.put("CRES", "Crescent");
        ADDRESS_ABBREVIATIONS_MAP.put("CDS", "Cul-de-sac");

        ADDRESS_ABBREVIATIONS_MAP.put("DR", "Drive");

        ADDRESS_ABBREVIATIONS_MAP.put("ESP", "Esplanade");
        ADDRESS_ABBREVIATIONS_MAP.put("EXPY", "Expressway");

        ADDRESS_ABBREVIATIONS_MAP.put("GRN", "Green");
        ADDRESS_ABBREVIATIONS_MAP.put("GR", "Grove");

        ADDRESS_ABBREVIATIONS_MAP.put("HWY", "Highway");
        ADDRESS_ABBREVIATIONS_MAP.put("HTS", "Heights");

        ADDRESS_ABBREVIATIONS_MAP.put("IS", "Island");

        ADDRESS_ABBREVIATIONS_MAP.put("JCT", "Junction");
        ADDRESS_ABBREVIATIONS_MAP.put("JNC", "Junction");

        ADDRESS_ABBREVIATIONS_MAP.put("LN", "Lane");
        ADDRESS_ABBREVIATIONS_MAP.put("LK", "Lake");

        ADDRESS_ABBREVIATIONS_MAP.put("MTN", "Mountain");

        ADDRESS_ABBREVIATIONS_MAP.put("PDE", "Parade");
        ADDRESS_ABBREVIATIONS_MAP.put("PL", "Place");
        ADDRESS_ABBREVIATIONS_MAP.put("PKWY", "Parkway");
        ADDRESS_ABBREVIATIONS_MAP.put("PLZ", "Plaza");

        ADDRESS_ABBREVIATIONS_MAP.put("RD", "Road");
        ADDRESS_ABBREVIATIONS_MAP.put("RDGE", "Ridge");
        ADDRESS_ABBREVIATIONS_MAP.put("RDG", "Ridge");

        ADDRESS_ABBREVIATIONS_MAP.put("SQ", "Square");
        ADDRESS_ABBREVIATIONS_MAP.put("ST", "Street");
        ADDRESS_ABBREVIATIONS_MAP.put("STA", "Station");

        ADDRESS_ABBREVIATIONS_MAP.put("TCE", "Terrace");
        ADDRESS_ABBREVIATIONS_MAP.put("TER", "Terrace");
        ADDRESS_ABBREVIATIONS_MAP.put("TRL", "Trail");
        ADDRESS_ABBREVIATIONS_MAP.put("TPKE", "Turnpike");

        ADDRESS_ABBREVIATIONS_MAP.put("VLY", "Valley");


        ADDRESS_ABBREVIATIONS_MAP.put("ALY", "Alley");
        ADDRESS_ABBREVIATIONS_MAP.put("ANX", "Anex");
        ADDRESS_ABBREVIATIONS_MAP.put("ARC", "Arcade");
        ADDRESS_ABBREVIATIONS_MAP.put("BYU", "Bayou");
        ADDRESS_ABBREVIATIONS_MAP.put("BCH", "Beach");
        ADDRESS_ABBREVIATIONS_MAP.put("APT", "Apartment");
        ADDRESS_ABBREVIATIONS_MAP.put("BSMT", "Basement");
        ADDRESS_ABBREVIATIONS_MAP.put("BLDG", "Building");
        ADDRESS_ABBREVIATIONS_MAP.put("DEPT", "Department");
        ADDRESS_ABBREVIATIONS_MAP.put("FL", "Floor");
        ADDRESS_ABBREVIATIONS_MAP.put("FRNT", "Front");
        ADDRESS_ABBREVIATIONS_MAP.put("HNGR", "Hanger");
        ADDRESS_ABBREVIATIONS_MAP.put("LBBY", "Lobby");
        ADDRESS_ABBREVIATIONS_MAP.put("LOWR", "Lower");
        ADDRESS_ABBREVIATIONS_MAP.put("PH", "Penthouse");
        ADDRESS_ABBREVIATIONS_MAP.put("RM", "Room");
        ADDRESS_ABBREVIATIONS_MAP.put("SPC", "Space");
        ADDRESS_ABBREVIATIONS_MAP.put("STE", "Suite");
        ADDRESS_ABBREVIATIONS_MAP.put("TRLR", "Trailer");
        ADDRESS_ABBREVIATIONS_MAP.put("UPPR", "Upper");
    }

    public static String getStateNameShorter(String state) {
        if (STATE_MAP_LONG != null && STATE_MAP_LONG.containsKey(state)) {
            return STATE_MAP_LONG.get(state);
        }
        return state;
    }

    public static String getStateNameLonger(String stateAbbreviation) {
        if (STATE_MAP != null && STATE_MAP.containsKey(stateAbbreviation)) {
            return STATE_MAP.get(stateAbbreviation);
        }
        return stateAbbreviation;
    }

    public static String getAddressFieldLonger(String abbreviation) {
        if (ADDRESS_ABBREVIATIONS_MAP != null && ADDRESS_ABBREVIATIONS_MAP.containsKey(abbreviation)) {
            return ADDRESS_ABBREVIATIONS_MAP.get(abbreviation);
        }
        return abbreviation;
    }

    public static String getAddressUnAbbreveiated(String address) {
        String[] splited = null;

        if (address != null) {
            splited = address.split("\\s+");
        }
        if (splited != null && splited.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String splitString : splited) {
                sb.append(getAddressFieldLonger(splitString.toUpperCase()) + ", ");
            }
            return sb.toString();
        }
        return address;
    }

    public static String getAddressForAccessibilityUser(Address address) {
        String stringLine = "";
        if (address.line1 != null && address.line2 != null) {
            stringLine = address.line1 + " , " + address.line2;
        }
        else if (address.line1 != null) {
            stringLine = address.line1;
        }
        else if (address.line2 != null) {
            stringLine = address.line2;
        }

        String addressContentDescription =
                AddressUtil.getAddressUnAbbreveiated(stringLine) + "\n" +
                        address.city + ", " +
                        AddressUtil.getStateNameLonger(address.stateOrProvince) + " " +
                        CommonUtil.stringToSpacesString(address.zipCode);

        return addressContentDescription;
    }

    public static String getAddressForAccessibilityUser(ProviderDetailsAddress address) {

        String addressContentDescription =
                AddressUtil.getAddressUnAbbreveiated(address.getAddress()) + "\n" +
                        address.getCity() + ", " +
                        AddressUtil.getStateNameLonger(address.getState()) + " " +
                        CommonUtil.stringToSpacesString(address.getZip());

        return addressContentDescription;
    }

    public static String getAddressForAccessibilityUser(com.americanwell.sdk.entity.Address address) {
        String stringLine = "";
        if (address.getAddress1() != null && address.getAddress2() != null) {
            stringLine = address.getAddress1() + " , " + address.getAddress2();
        }
        else if (address.getAddress1() != null) {
            stringLine = address.getAddress1();
        }
        else if (address.getAddress2() != null) {
            stringLine = address.getAddress2();
        }

        String addressContentDescription =
                AddressUtil.getAddressUnAbbreveiated(stringLine) + "\n" +
                        address.getCity() + ", " +
                        AddressUtil.getStateNameLonger(address.getState().getCode()) + " " +
                        CommonUtil.stringToSpacesString(address.getZipCode());

        return addressContentDescription;
    }

}
