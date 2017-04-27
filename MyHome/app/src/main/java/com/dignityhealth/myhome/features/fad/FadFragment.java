package com.dignityhealth.myhome.features.fad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 4/26/17.
 */

public class FadFragment extends BaseFragment {
    public static final String FAD_TAG = "fad_tag";
    View fadView;

    public static FadFragment newInstance() {
        return new FadFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        fadView = inflater.inflate(R.layout.fad, container, false);
        return fadView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD;
    }
}
