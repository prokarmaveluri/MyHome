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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectTimeFragment extends Fragment {
    public static final String BOOKING_SELECT_TIME_TAG = "booking_select_time_tag";
    public static final String APPOINTMENTS_KEY = "appointments";
    public static final String DATE_KEY = "date";

    public ArrayList<Appointment> appointments;
    public Date bookingDate;
    public BookingDateHeaderInterface selectTimeInterface;

    View bookingView;
    TextView monthLabel;


    public static BookingSelectTimeFragment newInstance() {
        return new BookingSelectTimeFragment();
    }

    public static BookingSelectTimeFragment newInstance(ArrayList<Appointment> appointments) {
        BookingSelectTimeFragment bookingFragment = new BookingSelectTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(APPOINTMENTS_KEY, appointments);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    public static BookingSelectTimeFragment newInstance(ArrayList<Appointment> appointments, Date date) {
        BookingSelectTimeFragment bookingFragment = new BookingSelectTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(APPOINTMENTS_KEY, appointments);
        args.putSerializable(DATE_KEY, date);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        appointments = args.getParcelableArrayList(APPOINTMENTS_KEY);

        if (args != null && args.getSerializable(DATE_KEY) != null) {
            bookingDate = (Date) args.getSerializable(DATE_KEY);
        } else {
            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.DATE, 1);
            bookingDate = calendar.getTime();
        }

        ArrayList<Appointment> todaysAppointments = getTodaysAppointments(bookingDate, appointments);

        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        setAppointmentTimes((FlowLayout) bookingView.findViewById(R.id.time_group), todaysAppointments);

        RelativeLayout dateHeader = (RelativeLayout) bookingView.findViewById(R.id.date_header);
        ImageView leftArrow = (ImageView) dateHeader.findViewById(R.id.left_date_arrow);
        ImageView rightArrow = (ImageView) dateHeader.findViewById(R.id.right_date_arrow);
        monthLabel = (TextView) dateHeader.findViewById(R.id.date);
        setMonthHeader(bookingDate);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onBackArrowClicked();
                }

                bookingDate = DateUtil.moveDate(bookingDate, -1);
                setMonthHeader(bookingDate);
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onFrontArrowClicked();
                }

                bookingDate = DateUtil.moveDate(bookingDate, 1);
                setMonthHeader(bookingDate);
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
            timeToggle.setTextOn(DateUtil.getTime(appointment.Time));
            timeToggle.setTextOff(DateUtil.getTime(appointment.Time));
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

    private ArrayList<Appointment> getTodaysAppointments(final Date todaysDate, final ArrayList<Appointment> allAppointments) {
        ArrayList<Appointment> todaysAppointments = new ArrayList<>();

        Date appointmentDate = new Date();
        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateTimeZone(appointment.Time);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (DateUtil.isOnSameDay(todaysDate, appointmentDate)) {
                todaysAppointments.add(appointment);
            }
        }

        return todaysAppointments;
    }


//    private ArrayList<Appointment> getTodaysAppointments(final Date todaysDate, final ArrayList<Appointment> allAppointments) {
//        Calendar todayCalendar = Calendar.getInstance();
//        todayCalendar.setTime(todaysDate);
//
//        ArrayList<Appointment> todaysAppointments = new ArrayList<>();
//
//        Calendar appointmentCalendar = Calendar.getInstance();
//        for (Appointment appointment : allAppointments) {
//            try {
//                appointmentCalendar.setTime(DateUtil.getDateTimeZone(appointment.Time));
//            } catch (ParseException e) {
//                Timber.e(e);
//                e.printStackTrace();
//            }
//
//            if(appointmentCalendar != null && appointmentCalendar.compareTo(todayCalendar) == 0){
//                todaysAppointments.add(appointment);
//            }
//        }
//
//        return todaysAppointments;
//    }

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

    public void setMonthHeader(Date date) {
        monthLabel.setText(DateUtil.convertDateToReadable(date));

        if (selectTimeInterface != null) {
            selectTimeInterface.onDateChanged(bookingDate);
        }
    }

    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
