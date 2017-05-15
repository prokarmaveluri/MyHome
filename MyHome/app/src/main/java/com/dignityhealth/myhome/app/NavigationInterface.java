package com.dignityhealth.myhome.app;

import android.os.Bundle;

import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 4/27/17.
 */

public interface NavigationInterface {
    void goToPage(Constants.ActivityTag activityTag);
    void loadFragment(Constants.ActivityTag activityTag, Bundle bundle);
}