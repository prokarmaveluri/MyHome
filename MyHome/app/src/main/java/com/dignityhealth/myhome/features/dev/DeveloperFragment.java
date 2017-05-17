package com.dignityhealth.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/17/17.
 */

public class DeveloperFragment extends BaseFragment{
    public static final String DEVELOPER_TAG = "dev_tag";
    View developerView;

    public static DeveloperFragment newInstance(){
        return new DeveloperFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        developerView = inflater.inflate(R.layout.developer, container, false);
        return developerView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.DEVELOPER;
    }
}
