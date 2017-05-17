package com.dignityhealth.myhome.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.features.appointments.Appointment;
import com.dignityhealth.myhome.features.fad.filter.FilterExpandableList;
import com.dignityhealth.myhome.features.profile.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

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
            fullAddress = fullAddress + address + "\n";
        }

        if (address2 != null && !address2.isEmpty()) {
            fullAddress = fullAddress + address2;
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

    /**
     * Create a calendar event for an appointment. Uses intents to call the system's calendar with a pre-populated event
     *
     * @param context
     * @param appointment the appointment being created in the calendar
     */
    public static void addCalendarEvent(Context context, Appointment appointment) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

        if (appointment.appointmentStart != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateUtil.getMilliseconds(appointment.appointmentStart));
        }

        if (appointment.doctorName != null) {
            intent.putExtra(CalendarContract.Events.TITLE, "Appointment with " + appointment.doctorName);
        }

        if (appointment.visitReason != null) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, appointment.visitReason);
        }

        if (appointment.facilityAddress != null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, CommonUtil.constructAddress(
                    appointment.facilityAddress.line1,
                    appointment.facilityAddress.line2,
                    appointment.facilityAddress.city,
                    appointment.facilityAddress.stateOrProvince,
                    appointment.facilityAddress.zipCode));
        }

        intent.setType("vnd.android.cursor.item/event");

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            Toast.makeText(context, "Please install a calendar application", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Launch Google Maps with address given
     *
     * @param context
     * @param address the address where we are getting directions to
     */
    public static void getDirections(Context context, Address address) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + Uri.encode(address.line1 + "," + address.city + "," + address.stateOrProvince)));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=an+" + address.line1 + address.city));
                context.startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Timber.e(innerEx);
                Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void shareAppointment(Context context, Appointment appointment) {
        String message = "";

        if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
            message = message + DateUtil.getDateWordsFromUTC(appointment.appointmentStart) + ".\n" + DateUtil.getTimeFromUTC(appointment.appointmentStart);
        }

        if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
            message = message + ".\n" + appointment.doctorName;
        }

        if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
            message = message + ".\n" + appointment.facilityName;
        }

        if (appointment.facilityAddress != null) {
            message = message + ".\n" + CommonUtil.constructAddress(
                    appointment.facilityAddress.line1,
                    appointment.facilityAddress.line2,
                    appointment.facilityAddress.city,
                    appointment.facilityAddress.stateOrProvince,
                    appointment.facilityAddress.zipCode);
        }

        if (appointment.visitReason != null && !appointment.visitReason.isEmpty()) {
            message = message + ".\n" + appointment.visitReason;
        }

        if (appointment.facilityPhoneNumber != null && !appointment.facilityPhoneNumber.isEmpty()) {
            message = message + ".\n" + appointment.facilityPhoneNumber;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            Toast.makeText(context, "Couldn't find an app that can share appointments", Toast.LENGTH_LONG).show();
        }
    }

    public static void setListViewHeight(ExpandableListView listView, int group, int lastGroup) {
        int totalHeight = 0;
        FilterExpandableList listAdapter = (FilterExpandableList) listView.getExpandableListAdapter();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

//            totalHeight += groupItem.getMeasuredHeight();
            totalHeight += 64;

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    if (lastGroup != i || lastGroup == group) {
                        View listItem = listAdapter.getChildView(i, j, false, null, listView);
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//                        totalHeight += listItem.getMeasuredHeight();
                        totalHeight += 64;
                    }
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight;
        if (height < 320)
            height = 320;
        params.height = (int) (height * DeviceDisplayManager.getInstance().getDeviceDensity());
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
