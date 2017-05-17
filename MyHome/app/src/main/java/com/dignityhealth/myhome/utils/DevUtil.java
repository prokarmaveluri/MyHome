package com.dignityhealth.myhome.utils;

import android.os.Build;

import com.dignityhealth.myhome.features.profile.Address;
import com.dignityhealth.myhome.features.profile.ProfileManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by kwelsh on 5/17/17.
 */

public class DevUtil {

    public static String getEmail() {
        if (ProfileManager.getProfile() != null) {
            return ProfileManager.getProfile().email;
        } else {
            return "jjonnalagadda@prokarma.com";
        }
    }

    public static int getRandomID() {
        return new Random().nextInt(500);
    }

    public static boolean getRandomBoolean() {
        if (new Random().nextBoolean()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomAppointmentDate() {
        Date date = new Date();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            date.setTime(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(100000, 100000000));
        } else {
            long random = (long) (new Random().nextLong() * 1.5);
            date.setTime(System.currentTimeMillis() + random);
        }

        String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_UTC, Locale.US);
        return sdf.format(date);
    }

    public static String getRandomDoctor() {
        if (new Random().nextBoolean()) {
            return "Dr.Seuss";
        } else {
            return "Dr.Phil";
        }
    }

    public static Address getRandomAddress() {
        if (new Random().nextBoolean()) {
            return new Address("1301 Shoreway Road", null, "Belmont", "CA", "94002", "US");
        } else {
            return new Address("8820 Saddlehorn Drive", null, "Irving", "TX", "75063", "US");
        }
    }

    public static String getRandomPhoneNumber() {
        if (new Random().nextBoolean()) {
            return "8005882300";
        } else {
            return "6168675309";
        }
    }

    public static String getRandomAppointmentReason(){
        if (new Random().nextBoolean()) {
            return "Make sure my skin is nice and silky smooth";
        } else {
            return "Need to get a check up because I am very stressed out about this Dignity Health project.";
        }
    }

    public static String getRandomFacilityName(){
        if (new Random().nextBoolean()) {
            return "Hotel California";
        } else {
            return "Facility ABC";
        }
    }
}
