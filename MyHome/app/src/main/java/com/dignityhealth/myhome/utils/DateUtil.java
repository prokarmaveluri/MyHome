package com.dignityhealth.myhome.utils;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/15/17.
 */

public class DateUtil {

    /**
     * Convert a UTC date to a more "human-friendly" format.
     *
     * @param utcDate a String representation of a UTC date
     * @return a human-friendly string of the given date
     */
    public static String convertUTCtoReadable(String utcDate) {
        try {
            Date date = Constants.SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return Constants.SIMPLE_DATE_FORMAT.format(date);
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
            Date date = Constants.SIMPLE_DATE_FORMAT.parse(readableDate);
            return Constants.SIMPLE_DATE_FORMAT_UTC.format(date);
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
        return Constants.SIMPLE_DATE_FORMAT.format(date);
    }

    public static String getDateFromUTC(String utcDate){
        try {
            Date date = Constants.SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return Constants.SIMPLE_TIME_FORMAT.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }

    public static String getTimeFromUTC(String utcDate){
        try {
            Date date = Constants.SIMPLE_DATE_FORMAT_UTC.parse(utcDate);
            return Constants.SIMPLE_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            Timber.e("Could not format UTC date " + utcDate + " correctly!\n" + e);
            e.printStackTrace();
        }

        return utcDate;
    }
}
