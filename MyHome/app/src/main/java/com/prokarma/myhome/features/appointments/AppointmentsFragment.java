package com.prokarma.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsFragment extends BaseFragment {
    public static final String APPOINTMENTS_TAG = "appointment_tag";

    AppointmentsPresenter presenter;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View appointmentsView = inflater.inflate(R.layout.appointments, container, false);

        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.appointments));
        ((NavigationActivity) getActivity()).setActionBarLineVisibility(false);

        presenter = new AppointmentsPresenter(getContext(), this, appointmentsView, getChildFragmentManager());
        presenter.onCreate();

        return appointmentsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.APPOINTMENTS_SCREEN, null);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
