package com.prokarma.myhome.utils;

import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

/**
 * Created by svenugopal on 02/08/18.
 */

public class AccessibilityCapitalTextChangedListener implements TextWatcher {

    View view;

    public AccessibilityCapitalTextChangedListener(TextView view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null && s.length() > 0) {
            ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegate(CommonUtil.capitalContent(s.toString())));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
