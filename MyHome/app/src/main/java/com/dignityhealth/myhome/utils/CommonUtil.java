package com.dignityhealth.myhome.utils;

import android.widget.TextView;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean isValidEmail(String email) {

        return EmailValidator.getInstance().isValid(email);
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
}
