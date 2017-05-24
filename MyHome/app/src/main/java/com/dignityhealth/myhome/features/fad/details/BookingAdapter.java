package com.dignityhealth.myhome.features.fad.details;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingAdapter extends PagerAdapter {
    private Context context;
    private static int NUM_ITEMS = 2;

    public BookingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout;
        MaterialCalendarView materialCalendarView;

        switch (position) {
            case 0:
                layout = (ViewGroup) inflater.inflate(R.layout.book_select_person, container, false);
                setupSelectPerson(layout);
                break;

            case 1:
                layout = (ViewGroup) inflater.inflate(R.layout.book_calendar, container, false);
                materialCalendarView = (MaterialCalendarView) layout.findViewById(R.id.calendar);
                materialCalendarView.setPagingEnabled(false);
                materialCalendarView.state().edit().setMinimumDate(Calendar.getInstance()).commit();
                break;

            default:
                layout = (ViewGroup) inflater.inflate(R.layout.book_calendar, container, false);
                materialCalendarView = (MaterialCalendarView) layout.findViewById(R.id.calendar);
                materialCalendarView.setPagingEnabled(false);
                materialCalendarView.state().edit().setMinimumDate(Calendar.getInstance()).commit();
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
     * Handles setting up the layout for Booking Select Person
     */
    private void setupSelectPerson(ViewGroup layout){
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
}
