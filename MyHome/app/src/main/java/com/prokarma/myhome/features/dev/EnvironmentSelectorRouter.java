package com.prokarma.myhome.features.dev;

import android.support.v4.app.DialogFragment;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorRouter implements EnvironmentSelectorContract.Router{
    final DialogFragment fragment;

    public EnvironmentSelectorRouter(DialogFragment fragment) {
        this.fragment = fragment;
    }


    @Override
    public void dismissEnvironmentPicker() {
        fragment.dismiss();
    }
}
