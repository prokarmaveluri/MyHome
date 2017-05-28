package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.Appointment;
import com.dignityhealth.myhome.utils.DateUtil;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.views.FlowLayout;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectTimeFragment extends Fragment {
    public static final String BOOKING_SELECT_TIME_TAG = "booking_select_time_tag";
    public static final String APPOINTMENTS = "appointments";

    public ArrayList<Appointment> appointments;
    public BookingDateHeaderInterface selectTimeInterface;

    View bookingView;

    public static BookingSelectTimeFragment newInstance() {
        return new BookingSelectTimeFragment();
    }

    public static BookingSelectTimeFragment newInstance(ArrayList<Appointment> appointments) {
        BookingSelectTimeFragment bookingFragment = new BookingSelectTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(APPOINTMENTS, appointments);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        appointments = args.getParcelableArrayList(APPOINTMENTS);
        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        setAppointmentTimes((FlowLayout) bookingView.findViewById(R.id.time_group), appointments);

        RelativeLayout dateHeader = (RelativeLayout) bookingView.findViewById(R.id.date_header);
        ImageView leftArrow = (ImageView) dateHeader.findViewById(R.id.left_date_arrow);
        ImageView rightArrow = (ImageView) dateHeader.findViewById(R.id.left_date_arrow);
        TextView monthLabel = (TextView) dateHeader.findViewById(R.id.date);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onBackArrowClicked();
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onFrontArrowClicked();
                }
            }
        });

        monthLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onMonthHeaderClicked();
                }
            }
        });

        return bookingView;
    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup    Flow Layout where Times will be added
     * @param appointments the appointments being added to the Flow Layout
     */
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<Appointment> appointments) {
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4));

        View.OnClickListener timeClickedListener;

        for (final Appointment appointment : appointments) {
            final ToggleButton timeToggle = new ToggleButton(new ContextThemeWrapper(getContext(), R.style.selectableButtonStyle), null, R.style.selectableButtonStyle);
            timeToggle.setPadding(DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12));
            timeToggle.setGravity(Gravity.CENTER);
            timeToggle.setLayoutParams(layoutParams);
            timeToggle.setTextOn(DateUtil.getTimeRangeFromUTC(appointment.Time));
            timeToggle.setTextOff(DateUtil.getTimeRangeFromUTC(appointment.Time));
            timeToggle.setChecked(appointment.Selected);

            timeClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectTimeInterface != null) {
                        selectTimeInterface.onTimeSelected(appointment);
                    }
                }
            };
            timeToggle.setOnClickListener(timeClickedListener);

            timeGroup.addView(timeToggle, layoutParams);
        }
    }

//    /**
//     * Gets the top three choices of dates
//     *
//     * @return an array with the top times depending on the index selected
//     */
//    private ArrayList<BookingTimeSlot> getTopTimeChoices() {
//        ArrayList<BookingTimeSlot> topTimes = new ArrayList<>();
//
//        if (times.size() <= 2) {
//            //Less than three choices for time
//            topTimes.addAll(times);
//        } else if (timeIndex <= 0) {
//            //Selected time is the first choice or no timeIndex set
//            topTimes.addAll(times.subList(0, 3));
//        } else if (timeIndex >= times.size() - 1) {
//            //Selected time is the last choice
//            topTimes.addAll(times.subList(times.size() - 3, times.size()));
//        } else {
//            //Normal, middle time picked
//            topTimes.addAll(times.subList(timeIndex - 1, timeIndex + 2));
//        }
//
//        return topTimes;
//    }


    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
