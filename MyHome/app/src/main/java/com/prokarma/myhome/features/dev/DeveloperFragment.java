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
public class DeveloperFragment extends BaseFragment {
    DevContract.Presenter presenter;

    public static final String DEVELOPER_TAG = "dev_tag";
    public static final String DEVELOPER = "developer";

    public static DeveloperFragment newInstance() {
        return new DeveloperFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View developerView = inflater.inflate(R.layout.developer, container, false);
        getActivity().setTitle(getString(R.string.developer_settings));

        presenter = new DevPresenter(getContext(), this, developerView);
        presenter.onCreate();

        return developerView;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.DEVELOPER;
    }
}
