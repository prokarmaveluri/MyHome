package com.prokarma.myhome.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.fad.Office;
import com.prokarma.myhome.features.fad.details.Image;
import com.prokarma.myhome.features.fad.details.ProviderDetailsAddress;
import com.prokarma.myhome.features.fad.details.ProviderDetailsImage;
import com.prokarma.myhome.features.fad.details.ProviderDetailsOffice;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentAvailableTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.features.fad.filter.FilterExpandableList;
import com.prokarma.myhome.features.profile.Address;
import com.televisit.history.HistoryExpandableList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by cmajji on 4/27/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class CommonUtil {
    private static final String TYPE_PLAIN = "text/plain";

    public static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    /**
     * Regular expression for a domain label, as per RFC 3490.
     * Its total length must not exceed 63 octets, according to RFC 5890.
     */
    private static final String LABEL_REGEXP =
            "([" + GOOD_IRI_CHAR + "\\-]{2,61})?";

    /**
     * Expression that matches a domain name, including international domain names in Punycode or
     * Unicode.
     */
    private static final String DOMAIN_REGEXP =
            "(" + LABEL_REGEXP + "\\.)+"                 // Subdomains and domain
                    // Top-level domain must be at least 2 chars
                    + "[" + GOOD_IRI_CHAR + "\\-]{2,61}";


    public static boolean isValidPassword(String password) {

        Pattern p = Pattern.compile(Constants.REGEX_PASSWORD);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean isValidPassword(@NonNull String password, @NonNull String firstName, @NonNull String lastName) {

        if (password.toLowerCase().contains(firstName.toLowerCase()) |
                password.toLowerCase().contains(lastName.toLowerCase())) {
            return false;
        }

        Pattern p = Pattern.compile(Constants.REGEX_PASSWORD);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean isValidEmail(String email) {

        if (null == email)
            return false;
        if (email.isEmpty() || !email.contains("@"))
            return false;
        String[] tokens = email.split("\\@");

        if (null == tokens)
            return false;
        if (tokens.length != 2)
            return false;

        if (tokens[0].length() > 49 || tokens[1].length() > 50)
            return false;

        Pattern domainRegEx = Pattern.compile(DOMAIN_REGEXP);

        if (!domainRegEx.matcher(tokens[1]).matches())
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidTextInput(@Nullable TextView view) {
        checkNotNull(view);

        if (view.getText().toString().trim().isEmpty() ||
                view.getText().toString().trim().length() < 1 ||
                view.getText().toString().length() > 35) {
            return false;
        } else {
            return true;
        }
    }

    static public final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    public static boolean isValidMobile(String phone) {
        return PHONE_NUMBER_PATTERN.matcher(phone).find();
    }

    public static void displayPopupWindow(Activity activity, View anchorView, String points) {
        PopupWindow popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_content, null);
        TextView textView = (TextView) layout.findViewById(R.id.criteria_text);
        textView.setText(points);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

    }

    public static String getBulletPoints(List<String> list) {
        String points = "";
        for (int index = 0; index < list.size(); index++) {
            if (index != list.size() - 1) {
                points = points.concat("\u2022 " + list.get(index) + "\n");
            } else {
                points = points.concat("\u2022 " + list.get(index));
            }
        }
        return points;
    }

    /**
     * The list of criteria for passwords
     *
     * @param context
     * @return a list of all the criteria for passwords
     */
    public static List<String> getCriteria(Context context) {
        List<String> criteria = new ArrayList<>();
        criteria.add(context.getString(R.string.password_criteria_8char));
        criteria.add(context.getString(R.string.password_criteria_name));
        criteria.add(context.getString(R.string.password_criteria_uppercase));
        criteria.add(context.getString(R.string.password_criteria_lowercase));
        criteria.add(context.getString(R.string.password_criteria_number));
        criteria.add(context.getString(R.string.password_criteria_special_char));
        return criteria;
    }

    /**
     * The list of criteria for security question answer
     *
     * @param context
     * @return a list of all the criteria for security question answer
     */
    public static List<String> getSecurityAnswerCriteria(Context context) {
        List<String> criteria = new ArrayList<>();
        criteria.add(context.getString(R.string.sq_criteria_4char));
        criteria.add(context.getString(R.string.sq_criteria_question));
        criteria.add(context.getString(R.string.sq_criteria_username));
        criteria.add(context.getString(R.string.sq_criteria_password));
        return criteria;
    }

    /**
     * Upper cases a word.
     * This also lower cases the rest of the word if it isn't already.
     *
     * @param word the string to upper case
     * @return word, now upper cased
     */
    public static String capitalize(@NonNull String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Simply returns a concatenation of first and last name
     *
     * @param firstName first name of user
     * @param lastName  last name of user
     * @return a proper representation of the user's name
     */
    public static String constructName(@Nullable String firstName, @Nullable String lastName) {
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
    public static String constructAddress(@Nullable String address, @Nullable String address2, @Nullable String city, @Nullable String state, @Nullable String zip) {
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

    /**
     * Adds dashes and parentheses to a phone number
     *
     * @param number the number being formatted
     * @return a String representation of the phone number, formatted with dashes and parentheses
     */
    @SuppressWarnings("deprecation")
    public static String constructPhoneNumber(@NonNull String number) {
        String phoneNumber = "";

        if (null == number || number.trim().isEmpty())
            return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
        } else {
            //Deprecated method
            phoneNumber = PhoneNumberUtils.formatNumber(number);
        }
        phoneNumber = phoneNumber.replace("(", "");
        phoneNumber = phoneNumber.replace(")", "-");
        phoneNumber = phoneNumber.replace(" ", "");
        if (!phoneNumber.contains("-") && phoneNumber.length() == 10)
            phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
        return phoneNumber.trim();
    }

    /**
     * Strip the phone number of any hyphens or parenthesis
     *
     * @param number the phone number being stripped
     * @return a 10 digit representation of the phone number without dashes
     */
    public static String stripPhoneNumber(@NonNull String number) {
        return number.replaceAll("\\D", "").trim();
    }

    /**
     * Create a calendar event for an appointment. Uses intents to call the system's calendar with a pre-populated event
     *
     * @param context
     * @param appointment the appointment being created in the calendar
     */
    public static void addCalendarEvent(Context context, @Nullable Appointment appointment) {
        if (appointment != null) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

            if (appointment.appointmentStart != null) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateUtil.getMilliseconds(appointment.appointmentStart));
            }

            if (appointment.doctorName != null) {
                intent.putExtra(CalendarContract.Events.TITLE, context.getString(R.string.appointment_with) + " " + appointment.doctorName);
            }

            if (appointment.facilityPhoneNumber != null || appointment.visitReason != null) {
                intent.putExtra(CalendarContract.Events.DESCRIPTION, constructPhoneNumber(appointment.facilityPhoneNumber) + "\n" + appointment.visitReason);
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
                Toast.makeText(context, context.getString(R.string.please_install_calendar_app), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Create a calendar event for an appointment. Uses intents to call the system's calendar with a pre-populated event
     *
     * @param context
     * @param appointmentStart    the time of the appointment
     * @param doctorName          the doctor overseeing the appointment
     * @param facilityAddress     the address where the appointment is taking place
     * @param facilityPhoneNumber the phone where the appointment is taking place
     * @param visitReason         the reason for the appointment
     */
    public static void addCalendarEvent(Context context, @Nullable String appointmentStart, @Nullable String doctorName, @Nullable Address facilityAddress, @Nullable String facilityPhoneNumber, @Nullable String visitReason) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

        if (appointmentStart != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateUtil.getMilliseconds(appointmentStart));
        }

        if (doctorName != null) {
            intent.putExtra(CalendarContract.Events.TITLE, context.getString(R.string.appointment_with) + " " + doctorName);
        }

        if (facilityPhoneNumber != null || visitReason != null) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, constructPhoneNumber(facilityPhoneNumber) + "\n" + visitReason);
        }

        if (facilityAddress != null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, CommonUtil.constructAddress(
                    facilityAddress.line1,
                    facilityAddress.line2,
                    facilityAddress.city,
                    facilityAddress.stateOrProvince,
                    facilityAddress.zipCode));
        }

        intent.setType("vnd.android.cursor.item/event");

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            Toast.makeText(context, context.getString(R.string.please_install_calendar_app), Toast.LENGTH_LONG).show();
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
        }
    }

    /**
     * Launch Google Maps with address given
     *
     * @param context
     * @param address1        line 1 of the address of the destination
     * @param city            city of destination
     * @param stateOrProvince the state of the destination
     */
    public static void getDirections(Context context, String address1, String city, String stateOrProvince) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + Uri.encode(address1 + "," + city + "," + stateOrProvince)));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
        }
    }

    /**
     * Constructs the Intent for Sharing an Appointment.
     * We format the appointment values appropriately.
     *
     * @param context
     * @param appointment the appointment object being shared
     */
    public static void shareAppointment(Context context, @Nullable Appointment appointment) {
        String message = "";

        if (appointment == null) {
            return;
        }

        if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
            message = message + DateUtil.getDayOfWeek(appointment.appointmentStart) + ", " +
                    DateUtil.getDateWords2FromUTC(appointment.appointmentStart) + " " +
                    DateUtil.getTime(appointment.appointmentStart);
        }

        if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
            message = message + "\n" + appointment.doctorName;
        }

        if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
            message = message + "\n" + appointment.facilityName;
        }

        if (appointment.facilityAddress != null) {
            message = message + "\n" + CommonUtil.constructAddress(
                    appointment.facilityAddress.line1,
                    appointment.facilityAddress.line2,
                    appointment.facilityAddress.city,
                    appointment.facilityAddress.stateOrProvince,
                    appointment.facilityAddress.zipCode);
        }

        if (appointment.facilityPhoneNumber != null && !appointment.facilityPhoneNumber.isEmpty()) {
            message = message + "\n" + constructPhoneNumber(appointment.facilityPhoneNumber);
        }

        if (appointment.visitReason != null && !appointment.visitReason.isEmpty()) {
            message = message + "\n\nReason for visit: " + appointment.visitReason;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            Toast.makeText(context, context.getString(R.string.no_app_share_appointments), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructs the Intent for Sharing an Appointment.
     * We format the appointment values appropriately.
     *
     * @param context
     * @param startTime           starting time of appointment (in UTC format)
     * @param doctorName          doctor handling the appointment
     * @param facilityName        the name of the facility where the appointment is taking place
     * @param facilityAddress     the address of the facility where the appointment is taking place
     * @param facilityPhoneNumber the phone number of the facility where the appointment is taking place
     * @param visitReason         the reason for the appointment
     */
    public static void shareAppointment(Context context, @Nullable String startTime, @Nullable String doctorName, @Nullable String facilityName, @Nullable Address facilityAddress, @Nullable String facilityPhoneNumber, @Nullable String visitReason) {
        String message = "";

        if (startTime != null && !startTime.isEmpty()) {
            message = message + DateUtil.getDateWords2FromUTC(startTime) + " " + DateUtil.getTime(startTime);
        }

        if (doctorName != null && !doctorName.isEmpty()) {
            message = message + "\n" + doctorName;
        }

        if (facilityName != null && !facilityName.isEmpty()) {
            message = message + "\n" + facilityName;
        }

        if (facilityAddress != null) {
            message = message + "\n" + CommonUtil.constructAddress(
                    facilityAddress.line1,
                    facilityAddress.line2,
                    facilityAddress.city,
                    facilityAddress.stateOrProvince,
                    facilityAddress.zipCode);
        }

        if (facilityPhoneNumber != null && !facilityPhoneNumber.isEmpty()) {
            message = message + "\n" + constructPhoneNumber(facilityPhoneNumber);
        }

        if (visitReason != null && !visitReason.isEmpty()) {
            message = message + "\n" + visitReason;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex);
            Toast.makeText(context, context.getString(R.string.no_app_share_appointments), Toast.LENGTH_LONG).show();
        }
    }

    public static void setOneGroupExpandedListViewHeight(Context context, ExpandableListView listView,
                                                         int group, int lastGroup) {
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
        int height = totalHeight + 5;
        if (height < 325)
            height = 325;
        params.height = (int) (height * DeviceDisplayManager.getInstance().getDeviceDensity(context));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setExpandedListViewHeight(Context context, ExpandableListView listView, int group, int lastGroup) {
        int totalHeight = 0;
        FilterExpandableList listAdapter = (FilterExpandableList) listView.getExpandableListAdapter();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += 64;
            for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                View listItem = listAdapter.getChildView(i, j, false, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += 64;
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + 5;
        if (height < 325)
            height = 325;
        params.height = (int) (height * DeviceDisplayManager.getInstance().getDeviceDensity(context));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setExpandedListViewHeight(Context context, ExpandableListView listView) {
        int totalHeight = 0;
        HistoryExpandableList listAdapter = (HistoryExpandableList) listView.getExpandableListAdapter();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += 64;
            for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                View listItem = listAdapter.getChildView(i, j, false, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += 64;
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + 5;
        if (height < 133)
            height = 133;
        params.height = (int) (height * DeviceDisplayManager.getInstance().getDeviceDensity(context));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Merely prints all the objects in a list using their toString methods.
     *
     * @param objects the objects we wish to print
     * @return the string representing all the objects in the list
     */
    public static String prettyPrint(List<?> objects) {
        String prettyString = "";
        for (Object object : objects) {
            prettyString = prettyString + object.toString() + "\n";
        }

        return prettyString.trim();
    }

    /**
     * Merely prints all the objects in a list using their toString methods and break lines.
     *
     * @param objects the objects we wish to print
     * @return the string representing all the objects in the list
     */
    public static String prettyPrintLineBreak(List<?> objects) {
        String prettyString = "";
        for (Object object : objects) {
            prettyString = prettyString + object.toString() + "\n\n";
        }

        return prettyString.trim();
    }

    /**
     * Toggle Keyboard
     *
     * @param activity the activtity that we are currently on at the time we want to hide the keyboard.
     */
    public static void toggleSoftKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    /**
     * Show Keyboard
     *
     * @param activity the activtity that we are currently on at the time we want to hide the keyboard.
     */
    public static void showSoftKeyboard(View view, Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    /**
     * Hides Keyboard
     *
     * @param activity the activtity that we are currently on at the time we want to hide the keyboard.
     */
    public static void hideSoftKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    /**
     * Hides Keyboard
     *
     * @param context
     * @param currentFocusView the view that has the current focus (probably a Spinner or EditText that launches a Calendar)
     */
    public static void hideSoftKeyboard(Context context, View currentFocusView) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    /**
     * This method check whether strong is non null and null empty.
     *
     * @param string, sting value to check.
     * @return @true if string is non null and non empty otherwise @false.
     */
    public static boolean isEmptyString(String string) {
        return null == string || string.isEmpty();
    }


    /**
     * update imageview fav icon
     *
     * @param isFav
     * @param favProvider
     */
    public static void updateFavView(boolean isFav, ImageView favProvider) {
        if (!isFav) {
            favProvider.setImageResource(R.drawable.ic_favorite_stroke);
        } else {
            favProvider.setImageResource(R.drawable.ic_favorite_filled);
        }
    }

    /**
     * @param allAppointments
     * @return
     */
    public static ArrayList<Appointment> getFutureAppointments(ArrayList<Appointment> allAppointments) {
        if (allAppointments == null) {
            return null;
        }
        ArrayList<Appointment> futureAppointments = new ArrayList<>();
        Date todaysDate = new Date();
        Date appointmentDate = new Date();

        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateNoTimeZone(appointment.appointmentStart);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }
            if (appointmentDate.after(todaysDate)) {
                futureAppointments.add(appointment);
            }
        }
        return futureAppointments;
    }

    public static ArrayList<Appointment> getPastAppointments(ArrayList<Appointment> allAppointments) {
        if (allAppointments == null) {
            return null;
        }

        ArrayList<Appointment> pastAppointments = new ArrayList<>();
        Date todaysDate = new Date();
        Date appointmentDate = new Date();

        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateNoTimeZone(appointment.appointmentStart);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (appointmentDate.before(todaysDate)) {
                pastAppointments.add(0, appointment);
            }
        }

        return pastAppointments;
    }

    /**
     * Find the highest resolution image available (for a Provider)
     *
     * @param images the list of provider images
     * @return the image with the largest width & largest height
     */
    @Nullable
    public static ProviderDetailsImage getBestImage(List<ProviderDetailsImage> images) {
        int width = 0;
        int height = 0;
        ProviderDetailsImage bestImage = null;

        for (ProviderDetailsImage image : images) {
            if (image.getHeight() > height && image.getWidth() > width) {
                bestImage = image;
                width = image.getWidth();
                height = image.getHeight();
            }
        }

        return bestImage;
    }

    public static List<ProviderDetailsImage> convertImagesToProviderImages(List<Image> images) {
        List<ProviderDetailsImage> providerImages = new ArrayList<>();
        ProviderDetailsImage providerImage = new ProviderDetailsImage();
        for (Image image : images) {
            providerImage = new ProviderDetailsImage("imageType", image.getUrl(), 120, 160);
            providerImages.add(providerImage);
        }

        return providerImages;
    }

    public static ArrayList<Image> convertProviderImagesToImages(List<ProviderDetailsImage> providerImages) {
        ArrayList<Image> images = new ArrayList<>();
        Image image = new Image();
        for (ProviderDetailsImage providerImage : providerImages) {
            image = new Image(providerImage.getUrl(), providerImage.getImageType());
            images.add(image);
        }

        return images;
    }

    public static List<ProviderDetailsOffice> convertOfficeToProviderOffice(List<Office> offices) {
        List<ProviderDetailsOffice> providerOffices = new ArrayList<>();
        ProviderDetailsOffice providerOffice = new ProviderDetailsOffice();

        List<ProviderDetailsAddress> providerOfficeAddresses = new ArrayList<>();
        ProviderDetailsAddress providerOfficeAddress = new ProviderDetailsAddress();

        List<String> providerOfficePhones = new ArrayList<>();
        String providerOfficePhone = new String();

        for (Office office : offices) {
            providerOffice = new ProviderDetailsOffice();

            providerOfficeAddress = new ProviderDetailsAddress();
            providerOfficeAddress.setName(office.getName());
            providerOfficeAddress.setAddress(office.getAddress());
            providerOfficeAddress.setCity(office.getCity());
            providerOfficeAddress.setState(office.getState());
            providerOfficeAddress.setZip(office.getZipCode());
            providerOfficeAddress.setLatitude(Double.parseDouble(office.getLat()));
            providerOfficeAddress.setLongitude(Double.parseDouble(office.getLong()));

            providerOfficePhone = office.getPhone();
            providerOfficePhones.add(providerOfficePhone);

            providerOfficeAddress.setPhones(providerOfficePhones);

            providerOfficeAddresses.add(providerOfficeAddress);

            providerOffice.setAddresses(providerOfficeAddresses);

            providerOffices.add(providerOffice);
        }

        return providerOffices;
    }

    public static ArrayList<Office> convertProviderOfficeToOffice(List<ProviderDetailsOffice> providerOffices) {
        ArrayList<Office> offices = new ArrayList<>();
        Office office = new Office();

        for (ProviderDetailsOffice providerOffice : providerOffices) {
            office.setName(providerOffice.getAddresses().get(0).getName());
            office.setAddress1(providerOffice.getAddresses().get(0).getAddress());
            office.setCity(providerOffice.getAddresses().get(0).getCity());
            office.setState(providerOffice.getAddresses().get(0).getState());
            office.setZipCode(providerOffice.getAddresses().get(0).getZip());
            office.setLat(providerOffice.getAddresses().get(0).getLatitude().toString());
            office.setLon(providerOffice.getAddresses().get(0).getLongitude().toString());
            office.setLatLongHash(providerOffice.getAddresses().get(0).getLatLongHash());
            office.setPhone(providerOffice.getAddresses().get(0).getPhones().get(0));

            offices.add(office);
        }

        return offices;
    }

    public static ArrayList<AppointmentAvailableTime> filterAppointmentsToType(AppointmentTimeSlots appointmentTimeSlots, final AppointmentType appointmentType) {
        ArrayList<AppointmentAvailableTime> appointmentTimes = new ArrayList<>();
        AppointmentAvailableTime appointmentAvailableTime = new AppointmentAvailableTime();

        for (AppointmentAvailableTime availableTime : appointmentTimeSlots.getData().get(0).getAttributes().getAvailableTimes()) {
            appointmentAvailableTime = new AppointmentAvailableTime();
            appointmentAvailableTime.setDate(availableTime.getDate());
            appointmentAvailableTime.setTimes(new ArrayList<AppointmentTime>());

            for (AppointmentTime appointmentTime : availableTime.getTimes()) {
                for (AppointmentType type : appointmentTime.getAppointmentTypes()) {
                    if (type.getId().equals(appointmentType.getId())) {
                        appointmentAvailableTime.getTimes().add(appointmentTime);
                        break;
                    }
                }
            }

            if(!appointmentAvailableTime.getTimes().isEmpty()){
                appointmentTimes.add(appointmentAvailableTime);
            }
        }

        return appointmentTimes;
    }

    public static void exitApp(Context context, Activity activity) {
        Intent i = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
