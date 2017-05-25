package com.dignityhealth.myhome.features.fad.details;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.views.FlowLayout;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingAdapter extends PagerAdapter {
    private Context context;
    private static int NUM_ITEMS = 4;

    ArrayList<String> times = new ArrayList<>();

    public BookingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout;

        switch (position) {
            case 0:
                layout = (ViewGroup) inflater.inflate(R.layout.book_select_person, container, false);
                setupSelectPerson(layout);
                break;

            case 1:
                layout = (ViewGroup) inflater.inflate(R.layout.book_calendar, container, false);
                setupCalendar(layout);
                break;

            case 2:
                layout = (ViewGroup) inflater.inflate(R.layout.book_select_time, container, false);
                setupSelectTime(layout);
                break;

            case 3:
                layout = (ViewGroup) inflater.inflate(R.layout.book_reason, container, false);
                setupSelectTime(layout);
                break;

            default:
                layout = null;
                break;
        }

        container.addView(layout, position);
        return layout;
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

    /**
     * Handles setting up the layout for selecting the calendar of the appointment
     *
     * @param layout
     */
    private void setupCalendar(ViewGroup layout) {
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) layout.findViewById(R.id.calendar);
        materialCalendarView.setPagingEnabled(false);
        materialCalendarView.state().edit().setMinimumDate(Calendar.getInstance()).commit();
    }

    /**
     * Handles setting up the layout for selecting the person the appointment is for
     *
     * @param layout
     */
    private void setupSelectPerson(ViewGroup layout) {
        final ToggleButton buttonMe = (ToggleButton) layout.findViewById(R.id.book_me);
        final ToggleButton buttonOther = (ToggleButton) layout.findViewById(R.id.book_other);
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
     *
     * @param layout
     */
    private void setupSelectTime(ViewGroup layout) {
        times.add("9:15am");
        times.add("10:30am");
        times.add("11:45am");
        times.add("1:00pm");
        times.add("3:00pm");
        times.add("3:15pm");
        times.add("3:30pm");

        setAppointmentTimes((FlowLayout) layout.findViewById(R.id.time_group), times);
    }

    private void setupSelectReason(ViewGroup layout){

    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup Flow Layout where Times will be added
     * @param times the times being added to the Flow Layout
     */
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<String> times) {
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4), DeviceDisplayManager.dpToPx(context, 4));

        for (String time : times) {
            final ToggleButton timeToggle = new ToggleButton(new ContextThemeWrapper(context, R.style.selectableButtonStyle), null, R.style.selectableButtonStyle);
            timeToggle.setPadding(DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12), DeviceDisplayManager.dpToPx(context, 12));
            timeToggle.setGravity(Gravity.CENTER);
            timeToggle.setLayoutParams(layoutParams);
            timeToggle.setTextOn(time);
            timeToggle.setTextOff(time);
            timeToggle.setChecked(false);
            timeToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeGroup.setCurrentCheckedChild(timeToggle);
                }
            });

            timeGroup.addView(timeToggle, layoutParams);
        }
    }
}
