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
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsDetailsFragment extends BaseFragment {
    public static final String APPOINTMENTS_DETAILS_TAG = "appointment_details_tag";

    private View appointmentsView;

    public static AppointmentsDetailsFragment newInstance() {
        return new AppointmentsDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointmentsView = inflater.inflate(R.layout.appointments_details, container, false);
        return appointmentsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
