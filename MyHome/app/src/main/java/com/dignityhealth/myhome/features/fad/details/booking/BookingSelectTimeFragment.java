package com.dignityhealth.myhome.features.fad.details.booking;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.Appointment;
import com.dignityhealth.myhome.utils.DateUtil;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.views.FlowLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectTimeFragment extends Fragment {
    public static final String BOOKING_SELECT_TIME_TAG = "booking_select_time_tag";
    public static final String APPOINTMENTS_KEY = "appointments";
    public static final String DATE_KEY = "date";

    public ArrayList<Appointment> allAppointments;
    public ArrayList<Appointment> todaysAppointments = new ArrayList<>();
    public Date bookingDate;
    public Date firstAppointmentDate;
    public Date lastAppointmentDate;
    public BookingDateHeaderInterface selectTimeInterface;

    View bookingView;
    TextView monthLabel;
    ImageView rightArrow;
    ImageView leftArrow;
    FlowLayout timeLayout;
    Button noAppointments;
    Button callForAppointments;

    Appointment nextAppointment;

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
        allAppointments = args.getParcelableArrayList(APPOINTMENTS_KEY);

        Collections.sort(allAppointments);

        if (args != null && args.getSerializable(DATE_KEY) != null) {
            bookingDate = (Date) args.getSerializable(DATE_KEY);
        } else {
            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.DATE, 1);
            bookingDate = calendar.getTime();
        }

        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        timeLayout = (FlowLayout) bookingView.findViewById(R.id.time_group);
        noAppointments = (Button) bookingView.findViewById(R.id.empty_appointments);
        callForAppointments = (Button) bookingView.findViewById(R.id.call_for_appointment);

        RelativeLayout dateHeader = (RelativeLayout) bookingView.findViewById(R.id.date_header);
        leftArrow = (ImageView) dateHeader.findViewById(R.id.left_date_arrow);
        rightArrow = (ImageView) dateHeader.findViewById(R.id.right_date_arrow);
        monthLabel = (TextView) dateHeader.findViewById(R.id.date);
        setMonthHeader(bookingDate);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adjust left arrow color and register click if in range
                if (!DateUtil.isOnSameDay(bookingDate, firstAppointmentDate) && !DateUtil.isBefore(bookingDate, firstAppointmentDate)) {
                    bookingDate = DateUtil.moveDate(bookingDate, -1);
                    setMonthHeader(bookingDate);
                    setupView();
                }

                adjustArrowColors();

                if (selectTimeInterface != null) {
                    selectTimeInterface.onBackArrowClicked();
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adjust right arrow color and register click if in range
                if (!DateUtil.isOnSameDay(bookingDate, lastAppointmentDate) && !DateUtil.isAfter(bookingDate, lastAppointmentDate)) {
                    bookingDate = DateUtil.moveDate(bookingDate, 1);
                    setMonthHeader(bookingDate);
                    setupView();
                }

                adjustArrowColors();

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

        setupView();

        return bookingView;
    }

    private void setupView() {
        todaysAppointments.clear();
        todaysAppointments = getTodaysAppointments(bookingDate, allAppointments);
        firstAppointmentDate = DateUtil.findFirstAppointmentDate(allAppointments);
        lastAppointmentDate = DateUtil.findLastAppointmentDate(allAppointments);
        adjustArrowColors();

        if (todaysAppointments != null && !todaysAppointments.isEmpty() && !DateUtil.isToday(bookingDate)) {
            timeLayout.setVisibility(View.VISIBLE);
            noAppointments.setVisibility(View.GONE);
            callForAppointments.setVisibility(View.GONE);
            setAppointmentTimes(timeLayout, todaysAppointments);
        } else {
            timeLayout.setVisibility(View.GONE);
            noAppointments.setVisibility(View.VISIBLE);

            if (DateUtil.isToday(bookingDate)) {
                nextAppointment = findNextAppointment(DateUtil.moveDate(bookingDate, 1), allAppointments);
            } else {
                nextAppointment = findNextAppointment(bookingDate, allAppointments);
            }

            if (nextAppointment != null) {

                //Bold just the Date part
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    noAppointments.setText(Html.fromHtml(getString(R.string.next_available) + ": " + "<b>" + DateUtil.getDateWordsFromUTC(nextAppointment.Time) + "</b>", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    noAppointments.setText(Html.fromHtml(getString(R.string.next_available) + ": " + "<b>" + DateUtil.getDateWordsFromUTC(nextAppointment.Time) + "</b>"));
                }

                noAppointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            bookingDate = DateUtil.getDateTimeZone(nextAppointment.Time);
                            setMonthHeader(bookingDate);
                            setupView();

                            //You're moving forward, so we can piggy back on this interface call for now. Might need it's own in the future...
                            if (selectTimeInterface != null) {
                                selectTimeInterface.onFrontArrowClicked();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                noAppointments.setText(getString(R.string.no_appointments_available));
            }

            if (DateUtil.isToday(bookingDate)) {
                callForAppointments.setVisibility(View.VISIBLE);
                callForAppointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectTimeInterface != null) {
                            selectTimeInterface.onPhoneNumberClicked();
                        }
                    }
                });
            } else {
                callForAppointments.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup    Flow Layout where Times will be added
     * @param appointments the appointments being added to the Flow Layout
     */
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<Appointment> appointments) {
        timeGroup.removeAllViews();
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DeviceDisplayManager.dpToPx(getContext(), 35));
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5));
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

        View.OnClickListener timeClickedListener;

        for (final Appointment appointment : appointments) {
            final Button timeButton = new Button(new ContextThemeWrapper(getContext(), R.style.selectableButtonStyle), null, R.style.selectableButtonStyle);
            timeButton.setPadding(DeviceDisplayManager.dpToPx(getContext(), 10), DeviceDisplayManager.dpToPx(getContext(), 2), DeviceDisplayManager.dpToPx(getContext(), 10), DeviceDisplayManager.dpToPx(getContext(), 2));
            timeButton.setGravity(Gravity.CENTER);
            timeButton.setTextSize(18);
            timeButton.setTypeface(boldTypeface);
            timeButton.setMinimumWidth(DeviceDisplayManager.dpToPx(getContext(), 100)); //Make sure it's at least 100dp, though it's allowed to stretch to wrap_content
            timeButton.setLayoutParams(layoutParams);
            timeButton.setText(DateUtil.getTime(appointment.Time));

            timeClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectTimeInterface != null) {
                        selectTimeInterface.onTimeSelected(appointment);
                    }
                }
            };
            timeButton.setOnClickListener(timeClickedListener);

            timeGroup.addView(timeButton, layoutParams);
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

    @Nullable
    private Appointment findNextAppointment(final Date todaysDate, final ArrayList<Appointment> allAppointments) {
        Appointment nextAppointment = null;

        Date appointmentDate = new Date();
        for (Appointment appointment : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateTimeZone(appointment.Time);
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (appointmentDate.after(todaysDate)) {
                nextAppointment = appointment;
                break;
            }
        }

        return nextAppointment;
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

    private void adjustArrowColors() {
        //Adjust left arrow color and register click if in range
        if (DateUtil.isOnSameDay(bookingDate, firstAppointmentDate) || DateUtil.isBefore(bookingDate, firstAppointmentDate)) {
            leftArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_darker));
        } else {
            leftArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
        }

        //Adjust right arrow color
        if (DateUtil.isOnSameDay(bookingDate, lastAppointmentDate) || DateUtil.isAfter(bookingDate, lastAppointmentDate)) {
            rightArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_darker), PorterDuff.Mode.MULTIPLY);
        } else {
            rightArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
        }
    }

    public void setMonthHeader(Date date) {
        monthLabel.setText(DateUtil.convertDateToReadableShortWords(date));

        if (selectTimeInterface != null) {
            selectTimeInterface.onDateChanged(bookingDate);
        }
    }

    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
