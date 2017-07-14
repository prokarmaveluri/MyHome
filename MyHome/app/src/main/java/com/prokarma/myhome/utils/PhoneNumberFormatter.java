package com.prokarma.myhome.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stomar on 7/13/17.
 * Text watcher to auto format phone number in format xxx-xxx-xxxx.
 */

public class PhoneNumberFormatter implements TextWatcher {

    private EditText phone;

    public PhoneNumberFormatter(EditText phone) {
        this.phone = phone;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String string = s.toString();
        if (count > 0 || before > 0) {
            String hyphen = "-";
            int selectionIndex = start + count;
            string = string.replaceAll(hyphen, "");
            selectionIndex -= (s.length() - string.length());
            Pattern pattern = Pattern.compile("\\d{3}");
            Matcher matcher = pattern.matcher(string);
            int index = 0;
            StringBuffer sbFormattedNumber = new StringBuffer("");
            int loopCount = 0;
            while (matcher.find(index)) {
                ++loopCount;
                sbFormattedNumber.append(string.substring(matcher.start(), matcher.end()) + hyphen);
                ++selectionIndex;
                index = matcher.end();
                if (loopCount == 2) {
                    break;
                }
            }
            sbFormattedNumber.append(string.substring(index, string.length()));
            phone.removeTextChangedListener(this);
            phone.setText(sbFormattedNumber.toString());
            phone.setSelection(selectionIndex);
            phone.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
