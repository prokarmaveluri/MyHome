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

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.entities.Appointment;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.DeviceDisplayManager;
import com.prokarma.myhome.views.CircularImageView;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/11/17.
 */

public class AppointmentsDetailsFragment extends BaseFragment {
    public static final String APPOINTMENTS_DETAILS_TAG = "appointment_details_tag";

    private Appointment appointment;
    private View appointmentsView;
    private boolean favDoc = false;
    private boolean isPastAppointment;
    private ImageView favProvider;
    private ImageView phoneIcon;
    private ImageView calendarIcon;
    private ImageView docImage;

    public static AppointmentsDetailsFragment newInstance() {
        return new AppointmentsDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        appointment = getArguments().getParcelable(AppointmentsListFragment.APPOINTMENT_KEY);
        isPastAppointment = getArguments().getBoolean(AppointmentsListFragment.PAST_APPOINTMENT_KEY);

        Timber.i("AppointDetailsFragment\n" + "appointment=" + appointment + "\nisPastAppointment=" + isPastAppointment);

        appointmentsView = inflater.inflate(R.layout.appointments_details, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.details));
        CommonUtil.setTitle(getActivity(), getString(R.string.details), true);

        TextView dateHeader = (TextView) appointmentsView.findViewById(R.id.date_header);
        TextView timeHeader = (TextView) appointmentsView.findViewById(R.id.time_header);
        TextView doctorName = (TextView) appointmentsView.findViewById(R.id.doctor_name);
        TextView facilityName = (TextView) appointmentsView.findViewById(R.id.facility_name);
        TextView facilityAddress = (TextView) appointmentsView.findViewById(R.id.facility_address);
        TextView reason = (TextView) appointmentsView.findViewById(R.id.reason);
        final TextView phoneNumber = (TextView) appointmentsView.findViewById(R.id.phone_number);
        phoneIcon = (ImageView) appointmentsView.findViewById(R.id.phone_icon);
        calendarIcon = (ImageView) appointmentsView.findViewById(R.id.calendar);
        ImageView pin = (ImageView) appointmentsView.findViewById(R.id.pin_icon);
        TextView shareText = (TextView) appointmentsView.findViewById(R.id.share_text);
        ImageView shareIcon = (ImageView) appointmentsView.findViewById(R.id.share_icon);
        TextView rescheduleText = (TextView) appointmentsView.findViewById(R.id.reschedule_text);
        favProvider = (ImageView) appointmentsView.findViewById(R.id.heart_icon);
        docImage = (CircularImageView) appointmentsView.findViewById(R.id.doctor_image);

        if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
            dateHeader.setText(DateUtil.getDateWords2FromUTC(appointment.appointmentStart));
            timeHeader.setText(DateUtil.getTime(appointment.appointmentStart) + " " + DateUtil.getReadableTimeZone(appointment));
            dateHeader.setContentDescription(getString(R.string.appointment_date) + ", " + dateHeader.getText().toString());
            timeHeader.setContentDescription(getString(R.string.appointment_time) + ", " + timeHeader.getText().toString());
        }

        if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
            doctorName.setText(appointment.doctorName);
            doctorName.setContentDescription(getString(R.string.doctor_name) + ", " + appointment.doctorName);
            pin.setContentDescription(appointment.doctorName + ", " + getString(R.string.location) + getString(R.string.show_in_map));
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

            String addressContentDescription = appointment.facilityAddress != null ?
                    AddressUtil.getAddressForAccessibilityUser(appointment.facilityAddress)
                    : getString(R.string.address_unknown);
            facilityAddress.setContentDescription(getString(R.string.location) + addressContentDescription);
        } else {
            facilityAddress.setText(null);
        }

        if (appointment.visitReason != null && !appointment.visitReason.isEmpty()) {
            reason.setText(appointment.visitReason);
            reason.setContentDescription(getString(R.string.appointment_reason) + ", " + appointment.visitReason);
        }

        if (appointment.facilityPhoneNumber != null && !appointment.facilityPhoneNumber.isEmpty()) {
            phoneNumber.setText(CommonUtil.constructPhoneNumberDots(appointment.facilityPhoneNumber));

            String phoneContentDescription = CommonUtil.stringToSpacesString(phoneNumber.getText().toString());
            phoneNumber.setContentDescription(getString(R.string.phone_number_des) + phoneContentDescription + getString(R.string.phone_number_open_dialer));
        }

        favProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favDoc = !favDoc;
                if (null != appointment && null != appointment.provider.getNpi()) {
                    NetworkManager.getInstance().updateFavDoctor(favDoc, appointment.provider.getNpi(),
                            favProvider, appointment.provider, false, getActivity(), appointmentsView);
                } else {
                    CommonUtil.showToast(getActivity(),getString(R.string.unable_to_add_message));
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

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.addCalendarEvent(getActivity(), appointment);
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointment == null || appointment.facilityAddress == null) {
                    CommonUtil.showToast(getContext(), getString(R.string.directions_not_found));
                } else {
                    CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
                }
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

        if (ProfileManager.getFavoriteProviders() != null) {
            CommonUtil.updateFavView(getActivity(), false, favProvider);
            for (ProviderResponse provider : ProfileManager.getFavoriteProviders()) {
                if (appointment.provider.getNpi() != null && appointment.provider.getNpi().contains(provider.getNpi())) {
                    favDoc = true;
                    CommonUtil.updateFavView(getActivity(), true, favProvider);
                    break;
                }
            }
        }

        if (isPastAppointment) {
            calendarIcon.setVisibility(View.GONE);
            rescheduleText.setVisibility(View.GONE);
            coachmarkHeart();
        } else {
            coachmarkCalendar();
        }
        try {
            if (null != appointment.provider.getImages()) {
                String url = appointment.provider.getImages().get(2).getUrl();
                url = url.replace(DeviceDisplayManager.W60H80, DeviceDisplayManager.W120H160);

                Picasso.with(getActivity())
                        .load(url)
                        .into(docImage);
            }
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Timber.w(ex);
        }

        return appointmentsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.APPOINTMENTS_DETAILS;
    }

    private void coachmarkHeart() {
        boolean skip;
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        if (isPastAppointment)
            skip = AppPreferences.getInstance().getBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS);
        else
            skip = AppPreferences.getInstance().getBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS);
        if (skip)
            return;
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(favProvider, getString(R.string.coachmark_appointments_favorite_doctor))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        if (isPastAppointment)
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS, true);
                        else
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        if (isPastAppointment)
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS, true);
                        else
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkCalendar() {
        if (CommonUtil.isAccessibilityEnabled(getActivity())) {
            return;
        }

        boolean skip;
        if (isPastAppointment)
            skip = AppPreferences.getInstance().getBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS);
        else
            skip = AppPreferences.getInstance().getBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS);
        if (skip)
            return;
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(calendarIcon, getString(R.string.coachmark_appointments_calendar))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkHeart();
                        if (isPastAppointment)
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS, true);
                        else
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        if (isPastAppointment)
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_PAST_SKIP_COACH_MARKS, true);
                        else
                            AppPreferences.getInstance().setBooleanPreference(Constants.APT_DETAILS_UPCOMING_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }
}
