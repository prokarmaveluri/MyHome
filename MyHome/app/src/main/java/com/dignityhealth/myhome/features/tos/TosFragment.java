package com.dignityhealth.myhome.features.tos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/9/17.
 */

public class TosFragment extends BaseFragment{
    public static final String TOS_TAG = "settings_tag";
    View tosView;

    public static TosFragment newInstance() {
        return new TosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        tosView = inflater.inflate(R.layout.tos, container, false);
        return tosView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.TERMS_OF_SERVICE;
    }

}
