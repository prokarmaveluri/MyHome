package com.prokarma.myhome.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

import com.prokarma.myhome.features.fad.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/15/17.
 */

public class DateUtil {

    //Date formats
    public static final String DATE_FORMAT = "MMMM dd, yyyy";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    public static final String DATE_HYPHEN_FORMAT = "yyyy-MM-dd";
    public static final SimpleDateFormat SIMPLE_DATE_HYPHEN_FORMAT = new SimpleDateFormat(DATE_HYPHEN_FORMAT, Locale.getDefault());
    public static final String DATE_SLASH_FORMAT = "MM/dd/yyyy";
    public static final SimpleDateFormat SIMPLE_DATE_SLASH_FORMAT = new SimpleDateFormat(DATE_SLASH_FORMAT, Locale.getDefault());
    public static final String DATE_SHORT_WORDS_FORMAT = "EEE, MMM dd";
    public static final SimpleDateFormat SIMPLE_DATE_SHORT_WORDS_FORMAT = new SimpleDateFormat(DATE_SHORT_WORDS_FORMAT, Locale.getDefault());
    public static final String DATE_WORDS_1st_FORMAT = "MMMM d'st,' yyyy";   //Adds a "st" to the day (like 1st)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_1st_FORMAT = new SimpleDateFormat(DATE_WORDS_1st_FORMAT, Locale.getDefault());
    public static final String DATE_WORDS_2nd_FORMAT = "MMMM d'nd,' yyyy";   //Adds a "nd" to the day (like 2nd)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_2nd_FORMAT = new SimpleDateFormat(DATE_WORDS_2nd_FORMAT, Locale.getDefault());
    public static final String DATE_WORDS_3rd_FORMAT = "MMMM d'rd,' yyyy";   //Adds a "rd" to the day (like 3rd)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_3rd_FORMAT = new SimpleDateFormat(DATE_WORDS_3rd_FORMAT, Locale.getDefault());
    public static final String DATE_WORDS_4th_FORMAT = "MMMM d'th,' yyyy";   //Adds a "th" to the day (like 4th)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_4th_FORMAT = new SimpleDateFormat(DATE_WORDS_4th_FORMAT, Locale.getDefault());
    public static final String TIME_FORMAT = "h:mm a";
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    public static final String DATE_FORMAT_UTC_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_UTC_TIMEZONE = new SimpleDateFormat(DATE_FORMAT_UTC_TIMEZONE, Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_UTC_NO_TIMEZONE = new SimpleDateFormat(DATE_FORMAT_UTC_TIMEZONE, Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_UTC = new SimpleDateFormat(DATE_FORMAT_UTC, Locale.getDefault());

    /**
     * Gets the date given a UTC-formatted string
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return Date object representing the UTC date
     */
    public static Date getDateTimeZone(String utcDate) throws ParseException {
        SimpleDateFormat sdf = SIMPLE_DATE_FORMAT_UTC_TIMEZONE;
        sdf.setTimeZone(TimeZone.getTimeZone("GMT" + getTimeZone(utcDate)));

        return sdf.parse(utcDate);
    }

    /**
     * Gets the date given a UTC-formatted string
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return Date object representing the UTC date
     */
    public static Date getDateNoTimeZone(String utcDate) throws ParseException {
        SimpleDateFormat sdf = SIMPLE_DATE_FORMAT_UTC_NO_TIMEZONE;
        return sdf.parse(utcDate);
    }

    /**
     * Gets the date given a UTC-formatted string
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss'Z'", has a trailing 'Z')
     * @return Date object representing the UTC date
     */
    public static Date getDateZ(String utcDate) throws ParseException {
        SimpleDateFormat sdf = SIMPLE_DATE_FORMAT_UTC;
        sdf.setTimeZone(TimeZone.getDefault()); //We don't need to care about timezone's for date of birth, so leave this as is

        return sdf.parse(utcDate);
    }

    /**
     * Gets the Timezone of a utc date.
     * This assumes that the timezone is the last six digits of the date (we can simply append "GMT" for setting the timezone)
     *
     * @param utcDate
     * @return
     */
    public static String getTimeZone(String utcDate) {
        return utcDate.substring(utcDate.length() - 6, utcDate.length());
    }

    /**
     * Convert a UTC date to a more "human-friendly" format.
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a String representing the UTC Date (formatted like such: "yyyy-MM--dd")
     */
    public static String convertUTCtoHyphen(String utcDate) {
        try {
            return SIMPLE_DATE_HYPHEN_FORMAT.format(getDateNoTimeZone(utcDate));
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Convert a UTC date to a more "human-friendly" format.
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a String representing the UTC Date (formatted like such: "MM/dd/yyyy")
     */
    public static String convertUTCtoReadable(String utcDate) {
        try {
            return SIMPLE_DATE_SLASH_FORMAT.format(getDateNoTimeZone(utcDate));
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Convert a human-friendly date to UTC format.
     *
     * @param readableDate human-friendly date in a String format (formatted like such: "MM/dd/yyyy")
     * @return a String representing the UTC Date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss'Z'")
     */
    public static String convertReadableToUTC(String readableDate) {
        try {
            Date date = SIMPLE_DATE_SLASH_FORMAT.parse(readableDate);
            return SIMPLE_DATE_FORMAT_UTC.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format readable date " + readableDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return readableDate;
    }

    /**
     * Convert a Date object into a human-friendly format.
     *
     * @param date the date object. This object's date should already be set and finalized before making this call.
     * @return a String representing the UTC Date (formatted like such: "MM/dd/yy")
     */
    public static String convertDateToReadable(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

    /**
     * Checks to see if date is valid and the age is reasonable for a birth date (must be year 1800 or later, but older than 0)
     *
     * @param readableDate
     * @return
     */
    public static boolean isValidDateOfBirth(String readableDate) {
        if(CommonUtil.isEmptyString(readableDate)){
            return false;
        }
        try {
            SIMPLE_DATE_SLASH_FORMAT.setLenient(false);
            Date date = SIMPLE_DATE_SLASH_FORMAT.parse(readableDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) < 1800)
                return false;

            return !(getAge(date) <= 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } finally {
            SIMPLE_DATE_FORMAT.setLenient(true);
        }
    }

    /**
     * Calculates the age based on milliseconds of Dates.
     * Inspired from here: https://stackoverflow.com/a/32106993/2128921
     *
     * @param date
     * @return
     */
    public static int getAge(Date date) {
        Date now = new Date();
        long timeBetween = now.getTime() - date.getTime();
        double yearsBetween = timeBetween / 3.156e+10;
        int age = (int) Math.floor(yearsBetween);
        return age;
    }

    /**
     * Convert a Date object into a human-friendly format.
     *
     * @param date the date object. This object's date should already be set and finalized before making this call.
     * @return a String representing the UTC Date (formatted like such: "EEE, MMM dd")
     */
    public static String convertDateToReadableShortWords(Date date) {
        return SIMPLE_DATE_SHORT_WORDS_FORMAT.format(date);
    }

    /**
     * Convert a Date object into a UTC format.
     *
     * @param date the date object. This object's date should already be set and finalized before making this call.
     * @return a String representing the UTC Date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss'Z'")
     */
    public static String convertDateToUTC(Date date) {
        return SIMPLE_DATE_FORMAT_UTC.format(date);
    }

    /**
     * Gets a string of the date.
     * Formatted as such: "Tue Jun 06"
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a string representation of the date similar to this format "Tue Jun 06"
     */
    public static String getDateWordsFromUTC(String utcDate) {
        try {
            return SIMPLE_DATE_SHORT_WORDS_FORMAT.format(getDateNoTimeZone(utcDate));
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Gets a string of the date.
     * Formatted as such: "January 28th, 2017"
     * Inspired: https://stackoverflow.com/a/33540720/2128921
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a string representation of the date similar to this format "January 28th, 2017"
     */
    public static String getDateWords2FromUTC(String utcDate) {
        try {
            Date date = getDateNoTimeZone(utcDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DATE);
            if (!((day > 10) && (day < 19))) {
                switch (day % 10) {
                    case 1:
                        return SIMPLE_DATE_WORDS_1st_FORMAT.format(date);
                    case 2:
                        return SIMPLE_DATE_WORDS_2nd_FORMAT.format(date);
                    case 3:
                        return SIMPLE_DATE_WORDS_3rd_FORMAT.format(date);
                    default:
                        return SIMPLE_DATE_WORDS_4th_FORMAT.format(date);
                }
            } else {
                return SIMPLE_DATE_WORDS_4th_FORMAT.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Gets a string of the date.
     * Formatted as such: "January 28th, 2017"
     * Inspired: https://stackoverflow.com/a/33540720/2128921
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a string representation of the date similar to this format "January 28th, 2017"
     */
    public static String getDayOfWeek(String utcDate) {
        try {
            Date date = getDateNoTimeZone(utcDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    return "Sunday";
                case Calendar.MONDAY:
                    return "Monday";
                case Calendar.TUESDAY:
                    return "Tuesday";
                case Calendar.WEDNESDAY:
                    return "Wednesday";
                case Calendar.THURSDAY:
                    return "Thursday";
                case Calendar.FRIDAY:
                    return "Friday";
                case Calendar.SATURDAY:
                    return "Saturday";
            }
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Gets the Time from a UTC Date
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return a String representing the time (formatted like such: "h:mm a")
     */
    public static String getTime(String utcDate) {
        try {
            return SIMPLE_TIME_FORMAT.format(getDateNoTimeZone(utcDate));
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Gets milliseconds of a UTC date. This is needed for Calendar Event times.
     *
     * @param utcDate the UTC date (formatted like such: "yyyy-MM-dd'T'HH:mm:ss" with +- for TimeZone)
     * @return the milliseconds since epoch of the UTC date
     */
    public static long getMilliseconds(String utcDate) {
        try {
            return getDateTimeZone(utcDate).getTime();
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Method to move a certain date a certain amount of days
     *
     * @param date       the date to be modified
     * @param daysToMove the amount of days we wish to move the date. Can be negative to go back in time.
     * @return the new date after the move
     */
    public static Date moveDate(Date date, int daysToMove) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, daysToMove);

        return calendar.getTime();
    }

    /**
     * Compares two dates to see if they are on the same day.
     * We compare year, month, and day (not time)
     *
     * @param date1
     * @param date2
     * @return whether or not the dates are on the same day
     */
    public static boolean isOnSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            cal1.setTimeZone(TimeZone.getDefault());

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            cal2.setTimeZone(TimeZone.getDefault());

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        } else {
            return false;
        }
    }

    /**
     * Compares if one date is before another.
     * We compare year, month, and day (not time)
     *
     * @param date1
     * @param date2
     * @return whether or not date1 is before date2
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            cal1.setTimeZone(TimeZone.getDefault());

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            cal2.setTimeZone(TimeZone.getDefault());

            return cal1.get(Calendar.YEAR) <= cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) <= cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) <= cal2.get(Calendar.DAY_OF_MONTH);
        } else {
            return false;
        }
    }

    /**
     * Compares if one date is after another.
     * We compare year, month, and day (not time)
     *
     * @param date1
     * @param date2
     * @return whether or not date1 is after date2
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            cal1.setTimeZone(TimeZone.getDefault());

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            cal2.setTimeZone(TimeZone.getDefault());

            return cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) >= cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) >= cal2.get(Calendar.DAY_OF_MONTH);
        } else {
            return false;
        }
    }

    /**
     * Checks to see if the given date is today.
     * We compare year, month, and day (not time).
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        return isOnSameDay(date, new Date());
    }

    /**
     * Gets the first appointment's date from a list.
     * Assumes that the list is sorted.
     *
     * @param appointments
     * @return the date of the first appointment in the list
     */
    @Nullable
    public static Date findFirstAppointmentDate(ArrayList<Appointment> appointments) {
        try {
            return DateUtil.getDateNoTimeZone(appointments.get(0).Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the last appointment's date from a list.
     * Assumes that the list is sorted.
     *
     * @param appointments
     * @return the date of the last appointment in the list
     */
    @Nullable
    public static Date findLastAppointmentDate(ArrayList<Appointment> appointments) {
        try {
            return DateUtil.getDateNoTimeZone(appointments.get(appointments.size() - 1).Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Determine's if date is older than 18 (legal age of adults)
     *
     * @return true if an adult, false if a minor
     */
    public static boolean isOlderThan18(Date userBirthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        Date youngestEligibleDate = calendar.getTime();

        return userBirthday.before(youngestEligibleDate);
    }

    /**
     * Determine's if date is older than 18 (legal age of adults)
     *
     * @return true if an adult, false if a minor
     */
    public static boolean isOlderThan18(String utcDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        Date youngestEligibleDate = calendar.getTime();

        Date userBirthday = null;
        try {
            userBirthday = getDateNoTimeZone(utcDate);
            return userBirthday.before(youngestEligibleDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
