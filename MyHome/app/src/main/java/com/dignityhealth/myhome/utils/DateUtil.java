package com.dignityhealth.myhome.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/15/17.
 */

public class DateUtil {

    //Date formats
    public static final String DATE_FORMAT = "MM/dd/yy";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    public static final String DATE_SHORT_WORDS_FORMAT = "EEE MMM dd";
    public static final SimpleDateFormat SIMPLE_DATE_SHORT_WORDS_FORMAT = new SimpleDateFormat(DATE_SHORT_WORDS_FORMAT, Locale.US);
    public static final String DATE_WORDS_1st_FORMAT = "MMMM d'st,' yyyy";   //Adds a "st" to the day (like 1st)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_1st_FORMAT = new SimpleDateFormat(DATE_WORDS_1st_FORMAT, Locale.US);
    public static final String DATE_WORDS_2nd_FORMAT = "MMMM d'nd,' yyyy";   //Adds a "nd" to the day (like 2nd)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_2nd_FORMAT = new SimpleDateFormat(DATE_WORDS_2nd_FORMAT, Locale.US);
    public static final String DATE_WORDS_3rd_FORMAT = "MMMM d'rd,' yyyy";   //Adds a "rd" to the day (like 3rd)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_3rd_FORMAT = new SimpleDateFormat(DATE_WORDS_3rd_FORMAT, Locale.US);
    public static final String DATE_WORDS_4th_FORMAT = "MMMM d'th,' yyyy";   //Adds a "th" to the day (like 4th)
    public static final SimpleDateFormat SIMPLE_DATE_WORDS_4th_FORMAT = new SimpleDateFormat(DATE_WORDS_4th_FORMAT, Locale.US);
    public static final String TIME_FORMAT = "h:mm a";
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.US);
    //public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";    //For Profile to work, need to remove milliseconds
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";    //Some appointments will appear ugly without milliseconds...
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_UTC = new SimpleDateFormat(DATE_FORMAT_UTC, Locale.US);
    public static final String DATE_RANGE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss-HH:mm";    //For appointments with time slots
    public static final SimpleDateFormat SIMPLE_DATE_RANGE_FORMAT_UTC = new SimpleDateFormat(DATE_RANGE_FORMAT_UTC, Locale.US);

    /**
     * Convert a UTC date to a more "human-friendly" format.
     *
     * @param utcDate a String representation of a UTC date
     * @return a human-friendly string of the given date
     */
    public static String convertUTCtoReadable(String utcDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return SIMPLE_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Convert a human-friendly date to UTC format.
     *
     * @param readableDate human-friendly date in a String format
     * @return a UTC date
     */
    public static String convertReadableToUTC(String readableDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT.parse(readableDate);
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
     * @return a human-friendly string of the given date
     */
    public static String convertDateToReadable(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

    /**
     * Convert a Date object into a UTC format.
     *
     * @param date the date object. This object's date should already be set and finalized before making this call.
     * @return a UTC string of the given date
     */
    public static String convertDateToUTC(Date date) {
        return SIMPLE_DATE_FORMAT_UTC.format(date);
    }

    /**
     * Gets a string of the date.
     * Formatted as such: "Tue Jun 06"
     *
     * @param utcDate
     * @return a string representation of the date similar to this format "Tue Jun 06"
     */
    public static String getDateWordsFromUTC(String utcDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return SIMPLE_DATE_SHORT_WORDS_FORMAT.format(date);
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
     * @param utcDate
     * @return a string representation of the date similar to this format "January 28th, 2017"
     */
    public static String getDateWords2FromUTC(String utcDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT_UTC.parse(utcDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DATE);
            if (!((day > 10) && (day < 19)))
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
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    public static String getTimeFromUTC(String utcDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return SIMPLE_TIME_FORMAT.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    public static String getTimeRangeFromUTC(String utcDate) {
        try {
            Date date = SIMPLE_DATE_RANGE_FORMAT_UTC.parse(utcDate);
            Timber.i(date.toString());
            return SIMPLE_TIME_FORMAT.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    /**
     * Gets milliseconds of a UTC date. This is needed for Calendar Event times.
     *
     * @param utcDate the utc date
     * @return the milliseconds since epoch of the UTC date
     */
    public static long getMilliseconds(String utcDate) {
        try {
            Date date = SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return date.getTime();
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
}
