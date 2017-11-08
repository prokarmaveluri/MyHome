package com.prokarma.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.DateUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectCalendarFragment extends Fragment {
    public static final String BOOKING_SELECT_CALENDAR_TAG = "booking_select_calendar_tag";
    public static final String DATE_KEY = "date";

    private Date bookingDate;
    private BookingDateHeaderInterface selectTimeInterface;
    private BookingRefreshInterface refreshInterface;

    View bookingView;
    MaterialCalendarView calendar;
    TextView monthLabel;

    public static BookingSelectCalendarFragment newInstance() {
        return new BookingSelectCalendarFragment();
    }

    public static BookingSelectCalendarFragment newInstance(Date date) {
        BookingSelectCalendarFragment bookingFragment = new BookingSelectCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATE_KEY, date);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        bookingView = inflater.inflate(R.layout.book_calendar, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));

        final Calendar cal = Calendar.getInstance();

        calendar = (MaterialCalendarView) bookingView.findViewById(R.id.calendar);
        calendar.setTopbarVisible(false);
        calendar.setPagingEnabled(false);

        if (args != null && args.getSerializable(DATE_KEY) != null) {
            bookingDate = (Date) args.getSerializable(DATE_KEY);
            calendar.setSelectedDate(bookingDate);
            calendar.setDateSelected(bookingDate, true);
            calendar.setCurrentDate(CalendarDay.from(bookingDate), true);
            calendar.state().edit().setMinimumDate(cal).commit();
        } else {
            calendar.setSelectedDate(cal);
            calendar.setDateSelected(cal, true);
            calendar.setCurrentDate(CalendarDay.from(cal), true);
            calendar.state().edit().setMinimumDate(cal).commit();
        }

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

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    setMonthHeader(date);
                }
            }
        });

        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setMonthHeader(CalendarDay calendarDay) {
        monthLabel.setText(DateUtil.convertDateToReadableShortWords(calendarDay.getDate()));

        if (selectTimeInterface != null) {
            selectTimeInterface.onDateChanged(calendarDay.getDate());
        }
    }

    public void moveSelectedDay(int daysToMove) {
        Date date = calendar.getSelectedDate().getDate();
        date = DateUtil.moveDate(date, daysToMove);
        CalendarDay calendarDay = CalendarDay.from(date);

        if (calendarDay.isInRange(calendar.getMinimumDate(), calendar.getMaximumDate())) {
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

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }
}
