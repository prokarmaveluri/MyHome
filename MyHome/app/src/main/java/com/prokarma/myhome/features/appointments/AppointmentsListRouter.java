package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Appointment;
import com.prokarma.myhome.utils.CommonUtil;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsListRouter implements AppointmentsListContract.Router{
    private Context context;

    public AppointmentsListRouter(final Context context) {
        this.context = context;
    }

    @Override
    public void goToAppointmentDetails(Appointment appointment, boolean isPastAppointment) {
        Intent intent = new Intent(AppointmentsListFragment.APPOINTMENT_DETAILS_INTENT);
        intent.putExtra(AppointmentsListFragment.APPOINTMENT_KEY, appointment);
        intent.putExtra(AppointmentsListFragment.PAST_APPOINTMENT_KEY, isPastAppointment);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void goToAppointmentDirections(Appointment appointment) {
        if (appointment == null || appointment.facilityAddress == null) {
            CommonUtil.showToast(context, context.getString(R.string.directions_not_found));
        } else {
            CommonUtil.getDirections(context, appointment.facilityAddress);
        }
    }
}
