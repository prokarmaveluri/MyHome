package com.prokarma.myhome.features.appointments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsDetailsFragment extends BaseFragment {
    public static final String APPOINTMENTS_DETAILS_TAG = "appointment_details_tag";

    private Appointment appointment;
    private View appointmentsView;
    private boolean favDoc = false;
    private ImageView favProvider;

    public static AppointmentsDetailsFragment newInstance() {
        return new AppointmentsDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointment = getArguments().getParcelable(AppointmentsFragment.APPOINTMENT_KEY);
        appointmentsView = inflater.inflate(R.layout.appointments_details, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.details));

        try {
            TextView dateHeader = (TextView) appointmentsView.findViewById(R.id.date_header);
            TextView timeHeader = (TextView) appointmentsView.findViewById(R.id.time_header);
            TextView doctorName = (TextView) appointmentsView.findViewById(R.id.doctor_name);
            TextView facilityName = (TextView) appointmentsView.findViewById(R.id.facility_name);
            TextView facilityAddress = (TextView) appointmentsView.findViewById(R.id.facility_address);
            TextView reason = (TextView) appointmentsView.findViewById(R.id.reason);
            final TextView phoneNumber = (TextView) appointmentsView.findViewById(R.id.phone_number);
            ImageView phoneIcon = (ImageView) appointmentsView.findViewById(R.id.phone_icon);
            ImageView calendar = (ImageView) appointmentsView.findViewById(R.id.calendar);
            ImageView pin = (ImageView) appointmentsView.findViewById(R.id.pin_icon);
            TextView shareText = (TextView) appointmentsView.findViewById(R.id.share_text);
            ImageView shareIcon = (ImageView) appointmentsView.findViewById(R.id.share_icon);
            favProvider = (ImageView) appointmentsView.findViewById(R.id.heart_icon);

            favProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favDoc = !favDoc;
                    Toast.makeText(getActivity(), "Dev In Progress", Toast.LENGTH_SHORT).show();
                    if (null != appointment && null != appointment.provider.getNpi()) {
                        NetworkManager.getInstance().updateFavDoctor(favDoc, appointment.provider.getNpi(),
                                favProvider, appointment.provider, false, getActivity());
                    }
                }
            });

            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.shareAppointment(getActivity(), appointment);
                }
            });

            shareText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.shareAppointment(getActivity(), appointment);
                }
            });

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

            phoneIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL +
                            phoneNumber.getText().toString()));
                    intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentPhone);
                }
            });

            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL +
                            phoneNumber.getText().toString()));
                    intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentPhone);
                }
            });

            if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
                dateHeader.setText(DateUtil.getDateWords2FromUTC(appointment.appointmentStart));
                timeHeader.setText(DateUtil.getTime(appointment.appointmentStart));
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

            phoneIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL +
                            phoneNumber.getText().toString()));
                    intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentPhone);
                }
            });

            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.TEL +
                            phoneNumber.getText().toString()));
                    intentPhone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentPhone);
                }
            });

            if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
                dateHeader.setText(DateUtil.getDateWords2FromUTC(appointment.appointmentStart));
                timeHeader.setText(DateUtil.getTime(appointment.appointmentStart));
            }

            if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
                doctorName.setText(appointment.doctorName);
            }

            if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
                facilityName.setText(appointment.facilityName);
            }

            if (appointment.visitReason != null && !appointment.visitReason.isEmpty()) {
                reason.setText(appointment.visitReason);
            }

            if (appointment.facilityPhoneNumber != null && !appointment.facilityPhoneNumber.isEmpty()) {
                phoneNumber.setText(CommonUtil.constructPhoneNumber(appointment.facilityPhoneNumber));
            }
            CommonUtil.updateFavView(false, favProvider);
            for (ProviderResponse provider : ProfileManager.getFavoriteProviders())
                if (appointment.provider.getNpi().contains(provider.getNpi())) {
                    favDoc = true;
                    CommonUtil.updateFavView(true, favProvider);
                    break;
                }
        } catch (NullPointerException ex) {
        }
        return appointmentsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS;
    }

}
