package com.prokarma.myhome.features.tos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/9/17.
 */

public class TosFragment extends BaseFragment {
    public static final String TOS_TAG = "tos_tag";

    TosPresenter presenter;

    public static TosFragment newInstance() {
        return new TosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View tosView = inflater.inflate(R.layout.tos, container, false);
        getActivity().setTitle(getString(R.string.terms_of_service));
        presenter = new TosPresenter(getContext(), this, tosView);
        presenter.onCreate();
        return tosView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.TERMS_OF_SERVICE;
    }
}
