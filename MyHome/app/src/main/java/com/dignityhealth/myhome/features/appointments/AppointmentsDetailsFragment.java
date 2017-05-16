package com.dignityhealth.myhome.features.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsDetailsFragment extends BaseFragment {
    public static final String APPOINTMENTS_DETAILS_TAG = "appointment_details_tag";

    private Appointment appointment;
    private View appointmentsView;

    public static AppointmentsDetailsFragment newInstance() {
        return new AppointmentsDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointment = getArguments().getParcelable(AppointmentsFragment.APPOINTMENT_KEY);
        appointmentsView = inflater.inflate(R.layout.appointments_details, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.details));

        TextView dateHeader = (TextView) appointmentsView.findViewById(R.id.date_header);
        TextView timeHeader = (TextView) appointmentsView.findViewById(R.id.time_header);
        TextView doctorName = (TextView) appointmentsView.findViewById(R.id.doctor_name);
        TextView facilityName = (TextView) appointmentsView.findViewById(R.id.facility_name);
        TextView facilityAddress = (TextView) appointmentsView.findViewById(R.id.facility_address);
        TextView reason = (TextView) appointmentsView.findViewById(R.id.reason);
        TextView phoneNumber = (TextView) appointmentsView.findViewById(R.id.phone_number);
        ImageView calendar = (ImageView) appointmentsView.findViewById(R.id.calendar);
        ImageView pin = (ImageView) appointmentsView.findViewById(R.id.pin_icon);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.addCalendarEvent(getActivity(), appointment);
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
            }
        });

        if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
            dateHeader.setText(DateUtil.getDateWordsFromUTC(appointment.appointmentStart));
            timeHeader.setText(DateUtil.getTimeFromUTC(appointment.appointmentStart));
        }

        if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
            doctorName.setText(appointment.doctorName);
        }

        if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
            facilityName.setText(appointment.facilityName);
        }

        if (appointment.facilityAddress != null) {
            facilityAddress.setText(CommonUtil.constructAddress(
                    appointment.facilityAddress.line1,
                    appointment.facilityAddress.line2,
                    appointment.facilityAddress.city,
                    appointment.facilityAddress.stateOrProvince,
                    appointment.facilityAddress.zipCode));
        }

        if (appointment.visitReason != null && !appointment.visitReason.isEmpty()) {
            reason.setText(appointment.visitReason);
        }

        if (appointment.facilityPhoneNumber != null && !appointment.facilityPhoneNumber.isEmpty()) {
            phoneNumber.setText(CommonUtil.constructPhoneNumber(appointment.facilityPhoneNumber));
        }

        return appointmentsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }
}
