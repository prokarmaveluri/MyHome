package com.prokarma.myhome.utils;

import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;

/**
 * Created by svenugopal on 30/01/18.
 */

public class AccessibilityDelegate extends AccessibilityDelegateCompat {
    private String contentDescription = "";

    public AccessibilityDelegate(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(View host,
                                                  AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        info.setText(contentDescription);
    }
}
