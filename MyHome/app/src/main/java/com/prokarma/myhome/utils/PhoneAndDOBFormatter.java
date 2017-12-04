package com.prokarma.myhome.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by stomar on 7/13/17.
 * Text watcher to auto format phone number in format xxx-xxx-xxxx.
 */

public class PhoneAndDOBFormatter implements TextWatcher {

    /**
     * enum for different formatter types and their properties.
     */
    public enum FormatterType {
        /**
         * FormatterType to format Date of Birth MM/DD/YYYY.
         */
        DOB("/", "\\d{2}", 2),
        /**
         * FormatterType to format Phone Number xxx-xxx-xxxx.
         */
        PHONE_NUMBER_HYPHENS("-", "\\d{3}", 2),
        /**
         * FormatterType to format Phone Number xxx.xxx.xxxx.
         */
        PHONE_NUMBER_DOTS("\\.", "\\d{3}", 2);

        /**
         * string to user as separator.
         */
        private String separator;
        /**
         * Regex to include separator.
         */
        private String regex;
        /**
         * Number of separator to include.
         */
        private int separatorCount;

        FormatterType(String separator, String regex, int separatorCount) {
            this.separator = separator;
            this.regex = regex;
            this.separatorCount = separatorCount;
        }

        public String getSeparator() {
            return separator;
        }

        public String getRegex() {
            return regex;
        }

        public int getSeparatorCount() {
            return separatorCount;
        }
    }

    private EditText editText;
    private FormatterType formatterType;

    public PhoneAndDOBFormatter(EditText editText, FormatterType formatterType) {
        this.editText = editText;
        this.formatterType = formatterType;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String unformatted = s.toString();
        if (count > 0 && before == 0) {
            int selectionIndex = start + count;
            unformatted = unformatted.replaceAll(formatterType.getSeparator(), "");
            selectionIndex -= (s.length() - unformatted.length());
            Pattern pattern = Pattern.compile(formatterType.getRegex());
            Matcher matcher = pattern.matcher(unformatted);
            int index = 0;
            StringBuilder sbFormatted = new StringBuilder("");
            int loopCount = 0;
            while (matcher.find(index)) {
                ++loopCount;
                sbFormatted.append(unformatted.substring(matcher.start(), matcher.end())).append(formatterType.getSeparator());
                ++selectionIndex;
                index = matcher.end();
                if (loopCount == formatterType.getSeparatorCount()) {
                    break;
                }
            }
            sbFormatted.append(unformatted.substring(index, unformatted.length()));
            String finalString = sbFormatted.toString();

            if (formatterType.equals(FormatterType.PHONE_NUMBER_DOTS)) {
                finalString = finalString.replaceAll("\\\\", "");
            }

            editText.removeTextChangedListener(this);
            editText.setText(finalString);

            try {
                if (selectionIndex < 0) selectionIndex = 0;
                if (selectionIndex > sbFormatted.toString().length())
                    selectionIndex = sbFormatted.toString().length();
                editText.setSelection(selectionIndex);
            } catch (IndexOutOfBoundsException ex) {
                Timber.w(ex);
            }

            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
