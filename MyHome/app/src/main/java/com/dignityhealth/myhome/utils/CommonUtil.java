package com.dignityhealth.myhome.utils;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by cmajji on 4/27/17.
 */

public class CommonUtil {

    public static boolean isValidPassword(String password) {

        Pattern p = Pattern.compile(Constants.REGEX_PASSWORD);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean isValidPassword(String password, String firstName, String lastName) {

        if (password.toLowerCase().contains(firstName.toLowerCase()) |
                password.toLowerCase().contains(lastName.toLowerCase())) {
            return false;
        }

        Pattern p = Pattern.compile(Constants.REGEX_PASSWORD);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidTextInput(TextView view) {
        checkNotNull(view);

        if (view.getText().toString().isEmpty() || view.getText().toString().length() < 1 ||
                view.getText().toString().length() > 35) {
            return false;
        } else {
            return true;
        }
    }


    public static String getBulletPoints() {
        String points = "";
        for (int index = 0; index < getCriteria().size(); index++) {
            if (index != getCriteria().size() - 1) {
                points = points.concat("\u2022 " + getCriteria().get(index) + "\n");
            } else {
                points = points.concat("\u2022 " + getCriteria().get(index));
            }
        }
        return points;
    }

    public static List<String> getCriteria() {
        List<String> criteria = new ArrayList<>();
        criteria.add("Password must be at least 8 characters");
        criteria.add("Password must not contain part of your name");
        criteria.add("At least one UPPERCASE letter");
        criteria.add("At least one lowercase letter");
        criteria.add("At least one number");
        criteria.add("At least one special character: !@#$%^&*");
        return criteria;
    }

    /**
     * Upper cases a word.
     * This also lower cases the rest of the word if it isn't already.
     *
     * @param word the string to upper case
     * @return word, now upper cased
     */
    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Sees if a phone number entered in is valid.
     *
     * @param phone1 the region code (can be either zero digits or three)
     * @param phone2 the first part of the phone number (needs to be three digits)
     * @param phone3 the last part of the phone number (needs to be four digits)
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean validPhoneNumber(String phone1, String phone2, String phone3) {
        if (phone3.length() == 4 && phone2.length() == 3 && (phone1.length() == 0 || phone1.length() == 3)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simply returns a concatenation of first and last name
     *
     * @param firstName first name of user
     * @param lastName  last name of user
     * @return a proper representation of the user's name
     */
    public static String constructName(String firstName, String lastName) {
        if (firstName == null) {
            firstName = "";
        }

        if (lastName == null) {
            lastName = "";
        }

        return firstName + " " + lastName;
    }

    /**
     * Construct a concatenation of all available address fields to provide a full formed address string
     *
     * @param address  the first address of the user
     * @param address2 the second address of the user
     * @param city     the city of the user
     * @param state    the two character code of the state of the user
     * @param zip      the five digit zip code of the user
     * @return a proper representation of the user's address
     */
    public static String constructAddress(String address, String address2, String city, String state, String zip) {
        String fullAddress = "";

        if (address != null && !address.isEmpty()) {
            fullAddress = fullAddress + address;
        }

        if (address2 != null && !address2.isEmpty()) {
            fullAddress = fullAddress + "\n" + address2;
        }

        if (address != null && !address.isEmpty() && address2 != null && !address2.isEmpty()) {
            fullAddress = fullAddress + "\n";
        }

        if (city != null && !city.isEmpty()) {
            fullAddress = fullAddress + city;
        }

        if (city != null && !city.isEmpty() && state != null && !state.isEmpty()) {
            fullAddress = fullAddress + ", ";
        }

        if (state != null && !state.isEmpty()) {
            fullAddress = fullAddress + state + " ";
        }

        if (zip != null && !zip.isEmpty()) {
            fullAddress = fullAddress + zip;
        }

        return fullAddress;
    }

    public static String constructPhoneNumber(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
        } else {
            //Deprecated method
            return PhoneNumberUtils.formatNumber(number);
        }
    }

    /**
     * Strip the phone number of any hyphens or parenthesis
     *
     * @param number the phone number being stripped
     * @return a 10 digit representation of the phone number without dashes
     */
    public static String stripPhoneNumber(String number) {
        return number.replaceAll("\\D", "").trim();
    }
}
