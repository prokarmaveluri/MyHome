package com.prokarma.myhome.views;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.prokarma.myhome.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by prokarma on 11/24/2017.
 */
public class EventDecorator implements DayViewDecorator {
    Context context;
    int drawableId;
    HashSet<CalendarDay> calendarDayList;

    public EventDecorator(Context context, @DrawableRes int drawableId, Collection<CalendarDay> calendarDayList) {
        this.drawableId = drawableId;
        this.context = context;
        this.calendarDayList = new HashSet<>(calendarDayList);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        //Log.d(TAG, "shouldDecorate: day:"+day.toString()+" calendarDayList.contains(day):"+calendarDayList.contains(day));
        return calendarDayList.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (drawableId == R.drawable.circle_calendar_day_event){
            view.setDaysDisabled(true);
        }else{
            view.setDaysDisabled(false);
        }
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, drawableId));

    }
}
