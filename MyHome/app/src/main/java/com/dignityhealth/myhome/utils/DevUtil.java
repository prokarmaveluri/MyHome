package com.dignityhealth.myhome.utils;

import android.os.Build;

import com.dignityhealth.myhome.features.profile.Address;
import com.dignityhealth.myhome.features.profile.ProfileManager;

import java.util.Date;
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
            date.setTime(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(10000000, 1000000000));
        } else {
            long random = (long) (new Random().nextLong() * 1.5);
            date.setTime(System.currentTimeMillis() + random);
        }

        return DateUtil.SIMPLE_DATE_FORMAT_UTC.format(date);
    }

    public static String getRandomDoctor() {
        int random = new Random().nextInt(5);
        switch (random) {
            case 0:
                return "Dr. Gregory House";
            case 1:
                return "Dr.Seuss";
            case 2:
                return "Dr.Phil";
            case 3:
                return "Dr.Leonard 'Bones' McCoy";
            case 4:
                return "Dr.Evil";
            default:
                return "Grey";
        }
    }

    public static Address getRandomAddress() {
        int random = new Random().nextInt(5);
        switch (random) {
            case 0:
                return new Address("1301 Shoreway Road", "Floor 4", "Belmont", "CA", "94002", "US");
            case 1:
                return new Address("8820 Saddlehorn Drive", null, "Irving", "TX", "75063", "US");
            case 2:
                return new Address("7171 S. 51st Ave", null, "Laveen", "AZ", "85339", "US");
            case 3:
                return new Address("2905 W Warner Road", "Suite 100", "Chandler", "AZ", "85224", "US");
            case 4:
                return new Address("8280 W. Warm Springs Rd.", null, "Las Vegas", "NV", "89113", "US");
            default:
                return new Address("155 Glasson Way", "Room 321", "Grass Valley", "CA", "95945", "US");
        }
    }

    public static String getRandomPhoneNumber() {
        int random = new Random().nextInt(5);
        switch (random) {
            case 0:
                return "8005882300";
            case 1:
                return "6168675309";
            case 2:
                return "5555555555";
            case 3:
                return "6168261635";
            case 4:
                return "6165605016";
            default:
                return "8006060842";
        }
    }

    public static String getRandomAppointmentReason() {
        int random = new Random().nextInt(5);
        switch (random) {
            case 0:
                return "Make sure my skin is nice and silky smooth.";
            case 1:
                return "Need to get a check up because I am very stressed out about this Dignity Health project.";
            case 2:
                return "I should get my blood pressure checked.";
            case 3:
                return "I need a routine check up because the last time I was at the doctor, I was still in high school...";
            case 4:
                return "Make sure my heart still beats.";
            default:
                return "Does this look infectious to you?";
        }
    }

    public static String getRandomFacilityName() {
        int random = new Random().nextInt(5);
        switch (random) {
            case 0:
                return "Hotel California";
            case 1:
                return "Facility ABC";
            case 2:
                return "Johns Hopkins Hospital";
            case 3:
                return "UCSF Medical Center";
            case 4:
                return "Northwestern Memorial Hospital";
            default:
                return "Mayo Clinic";
        }
    }
}
