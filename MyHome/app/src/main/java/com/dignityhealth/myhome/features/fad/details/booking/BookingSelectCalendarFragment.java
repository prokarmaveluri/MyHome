package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.utils.DateUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectCalendarFragment extends Fragment {
    public static final String BOOKING_SELECT_CALENDAR_TAG = "booking_select_calendar_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingDateHeaderInterface selectTimeInterface;

    View bookingView;
    MaterialCalendarView calendar;
    TextView monthLabel;

    public static BookingSelectCalendarFragment newInstance() {
        return new BookingSelectCalendarFragment();
    }

    public static BookingSelectCalendarFragment newInstance(ProviderDetailsResponse providerDetailsResponse) {
        BookingSelectCalendarFragment bookingFragment = new BookingSelectCalendarFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROVIDER_DETAILS_RESPONSE_KEY, providerDetailsResponse);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        bookingView = inflater.inflate(R.layout.book_calendar, container, false);

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);

        calendar = (MaterialCalendarView) bookingView.findViewById(R.id.calendar);
        calendar.setTopbarVisible(false);
        calendar.setPagingEnabled(false);
        calendar.setSelectedDate(cal);
        calendar.setDateSelected(cal, true);
        calendar.setCurrentDate(CalendarDay.from(cal), true);
        calendar.state().edit().setMinimumDate(cal).commit();

        RelativeLayout dateHeader = (RelativeLayout) bookingView.findViewById(R.id.date_header);
        ImageView leftArrow = (ImageView) dateHeader.findViewById(R.id.left_date_arrow);
        ImageView rightArrow = (ImageView) dateHeader.findViewById(R.id.right_date_arrow);
        monthLabel = (TextView) dateHeader.findViewById(R.id.date);
        setMonthHeader(calendar.getSelectedDate());

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onBackArrowClicked();
                }

                moveSelectedDay(-1);
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onFrontArrowClicked();
                }

                moveSelectedDay(1);
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

    public void setMonthHeader(CalendarDay calendarDay) {
        monthLabel.setText(DateUtil.convertDateToReadable(calendarDay.getDate()));
    }

    public void moveSelectedDay(int daysToMove) {
        Date date = calendar.getSelectedDate().getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysToMove);
        CalendarDay calendarDay = CalendarDay.from(cal);

        if(calendarDay.isInRange(calendar.getMinimumDate(), calendar.getMaximumDate())){
            calendar.clearSelection();
            calendar.setSelectedDate(calendarDay);
            calendar.setDateSelected(calendarDay, true);
            calendar.setCurrentDate(calendarDay, true);
            setMonthHeader(calendarDay);
        }
    }

    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
