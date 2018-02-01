package com.prokarma.myhome.utils;

import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by svenugopal on 01/02/18.
 */

public class AccessibilityTextChangedLister implements TextWatcher {

    View view;

    public AccessibilityTextChangedLister(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null && s.length() > 0) {
            ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegate(CommonUtil.stringToSpacesString(s.toString())));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
