package com.dignityhealth.myhome.utils;

import android.widget.TextView;

import org.apache.commons.validator.routines.EmailValidator;

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

        if (view.getText().toString().isEmpty() || view.getText().toString().length() < 1) {
            return false;
        } else {
            return true;
        }
    }
}
