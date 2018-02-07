package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class ApiFragment extends BaseFragment {
    ApiContract.Presenter presenter;

    public static final String API_TAG = "api_tag";

    public static ApiFragment newInstance() {
        return new ApiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View apiView = inflater.inflate(R.layout.api, container, false);
        getActivity().setTitle(getString(R.string.api_settings));

        presenter = new ApiPresenter(getContext(), this, apiView);
        presenter.onCreate();

        return apiView;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.API;
    }
}
