package com.dignityhealth.myhome.features.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 4/27/17.
 */

public class MoreFragment extends BaseFragment {
    public static final String MORE_TAG = "more_tag";
    View moreView;

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        moreView = inflater.inflate(R.layout.more, container, false);
        return moreView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MORE;
    }
}
