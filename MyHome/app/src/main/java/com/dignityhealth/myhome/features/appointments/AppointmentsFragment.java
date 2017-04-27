package com.dignityhealth.myhome.features.appointments;

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

public class AppointmentsFragment extends BaseFragment{

    public static final String APPOINTMENTS_TAG = "appointment_tag";
    View appointmentsView;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointmentsView = inflater.inflate(R.layout.appointments, container, false);
        return appointmentsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
