package com.dignityhealth.myhome.features.fad.details.booking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.DateUtil;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.views.FlowLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingAdapter extends PagerAdapter {
    private Context context;
    private static int NUM_ITEMS = 4;

    private ViewGroup selectPersonLayout;
    private ViewGroup selectDateLayout;
    private ViewGroup selectTimeLayout;
    private ViewGroup selectReasonLayout;

    private String selectedDateHeader;

    ArrayList<BookingTimeSlot> times = new ArrayList<>();
    int timeIndex = -1;

    public BookingAdapter(Context context) {
        this.context = context;
        populateDummyData();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (position) {
            case 0:
                selectPersonLayout = (ViewGroup) inflater.inflate(R.layout.book_select_person, container, false);
                setupSelectPerson();
                container.addView(selectPersonLayout, position);
                return selectPersonLayout;

            case 1:
                selectDateLayout = (ViewGroup) inflater.inflate(R.layout.book_calendar, container, false);
                setupDate();
                container.addView(selectDateLayout, position);
                return selectDateLayout;

            case 2:
                selectTimeLayout = (ViewGroup) inflater.inflate(R.layout.book_select_time, container, false);
                setupSelectTime();
                container.addView(selectTimeLayout, position);
                return selectTimeLayout;

            case 3:
                selectReasonLayout = (ViewGroup) inflater.inflate(R.layout.book_select_reason, container, false);
                setupSelectReason();
                container.addView(selectReasonLayout, position);
                return selectReasonLayout;

            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeViewAt(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    private void populateDummyData(){
        times.add(new BookingTimeSlot("9:15am", false));
        times.add(new BookingTimeSlot("10:30am", false));
        times.add(new BookingTimeSlot("11:45am", false));
        times.add(new BookingTimeSlot("1:00pm", false));
        times.add(new BookingTimeSlot("3:00pm", false));
        times.add(new BookingTimeSlot("3:15pm", false));
        times.add(new BookingTimeSlot("3:30pm", false));
    }

    /**
     * Handles setting up the layout for selecting the calendar of the appointment
     */
    private void setupDate() {
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) selectDateLayout.findViewById(R.id.calendar);
        materialCalendarView.setPagingEnabled(false);
        materialCalendarView.state().edit().setMinimumDate(Calendar.getInstance()).commit();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDateHeader = DateUtil.convertDateToReadable(date.getDate());
                refreshSelectTime();
                refreshSelectReason();
            }
        });
    }

    /**
     * Handles setting up the layout for selecting the person the appointment is for
     */
    private void setupSelectPerson() {
        final ToggleButton buttonMe = (ToggleButton) selectPersonLayout.findViewById(R.id.book_me);
        final ToggleButton buttonOther = (ToggleButton) selectPersonLayout.findViewById(R.id.book_other);
        buttonMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOther.setChecked(false);
            }
        });

        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonMe.setChecked(false);
            }
        });
    }

    /**
     * Handles setting up the layout for selecting the time of the appointment
     */
    private void setupSelectTime() {
        ((TextView) selectTimeLayout.findViewById(R.id.date_header)).setText(selectedDateHeader);
        setAppointmentTimes((FlowLayout) selectTimeLayout.findViewById(R.id.time_group), times);
    }

    /**
     * Handles setting up the layout for the select reason of the appointment
     */
    private void setupSelectReason() {
        ((TextView) selectReasonLayout.findViewById(R.id.date_header)).setText(selectedDateHeader);
        setAppointmentTimes((FlowLayout) selectReasonLayout.findViewById(R.id.time_group), getTopTimeChoices());
    }

    /**
     * Clears the Time Group of SelectReasonLayout and recreates it again
     */
    private void refreshSelectTime() {
        FlowLayout timeGroup = (FlowLayout) selectTimeLayout.findViewById(R.id.time_group);
        timeGroup.removeAllViews();

        notifyDataSetChanged();
        setupSelectTime();
    }

    /**
     * Clears the Time Group of SelectReasonLayout and recreates it again
     */
    private void refreshSelectReason() {
        FlowLayout timeGroup = (FlowLayout) selectReasonLayout.findViewById(R.id.time_group);
        timeGroup.removeAllViews();

        setupSelectReason();
    }

    /**
     * Gets the top three choices of dates
     *
     * @return an array with the top times depending on the index selected
     */
    private ArrayList<BookingTimeSlot> getTopTimeChoices() {
        ArrayList<BookingTimeSlot> topTimes = new ArrayList<>();

        if (times.size() <= 2) {
            //Less than three choices for time
            topTimes.addAll(times);
        } else if (timeIndex <= 0) {
            //Selected time is the first choice or no timeIndex set
            topTimes.addAll(times.subList(0, 3));
        } else if (timeIndex >= times.size() - 1) {
            //Selected time is the last choice
            topTimes.addAll(times.subList(times.size() - 3, times.size()));
        } else {
            //Normal, middle time picked
            topTimes.addAll(times.subList(timeIndex - 1, timeIndex + 2));
        }

        return topTimes;
    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup Flow Layout where Times will be added
     * @param times     the times being added to the Flow Layout
     */
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<BookingTimeSlot> times) {
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4));

        for (final BookingTimeSlot time : times) {
            final ToggleButton timeToggle = new ToggleButton(new ContextThemeWrapper(context, R.style.selectableButtonStyle), null, R.style.selectableButtonStyle);
            timeToggle.setPadding(DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12));
            timeToggle.setGravity(Gravity.CENTER);
            timeToggle.setLayoutParams(layoutParams);
            timeToggle.setTextOn(time.time);
            timeToggle.setTextOff(time.time);
            timeToggle.setChecked(time.isPicked);
            timeToggle.setOnClickListener(getAppointmentTimeClickListener(time));

            timeGroup.addView(timeToggle, layoutParams);
        }
    }

    private View.OnClickListener getAppointmentTimeClickListener(final BookingTimeSlot time){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BookingTimeSlot bookingTimeSlot : times) {
                    bookingTimeSlot.isPicked = false;
                }

                time.isPicked = true;
                timeIndex = times.indexOf(time);
                refreshSelectReason();
                refreshSelectTime();
            }
        };
    }
}
