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

        final MaterialCalendarView calendar = (MaterialCalendarView) bookingView.findViewById(R.id.calendar);
        calendar.setTopbarVisible(false);
        calendar.setPagingEnabled(false);
        calendar.state().edit().setMinimumDate(Calendar.getInstance()).commit();
        calendar.setDateSelected(Calendar.getInstance(), true);

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

                Calendar cal = calendar.getCurrentDate().getCalendar();
                cal.add(Calendar.DATE, -1);
                CalendarDay calendarDay = CalendarDay.from(cal);
                calendar.setSelectedDate((Date) null);
                calendar.setDateSelected(calendarDay, true);
                calendar.setCurrentDate(calendarDay, true);
                setMonthHeader(calendarDay);
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeInterface != null) {
                    selectTimeInterface.onFrontArrowClicked();
                }

                Calendar cal = calendar.getCurrentDate().getCalendar();
                cal.add(Calendar.DATE, 1);
                CalendarDay calendarDay = CalendarDay.from(cal);
                calendar.setSelectedDate((Date) null);
                calendar.setDateSelected(calendarDay, true);
                calendar.setCurrentDate(calendarDay, true);
                setMonthHeader(calendarDay);
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

    public void setMonthHeader(CalendarDay calendarDay){
        monthLabel.setText(DateUtil.convertDateToReadable(calendarDay.getDate()));
    }

//    /**
//     * Handles setting up the layout for selecting the calendar of the appointment
//     */
//    private void setupDate() {
//        MaterialCalendarView materialCalendarView = (MaterialCalendarView) selectDateLayout.findViewById(R.id.calendar);
//        materialCalendarView.setPagingEnabled(false);
//        materialCalendarView.state().edit().setMinimumDate(Calendar.getInstance()).commit();
//
//        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                selectedDateHeader = DateUtil.convertDateToReadable(date.getDate());
//                isDateSelected = true;
//                refreshSelectTime();
//                refreshSelectReason();
//            }
//        });
//    }


    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
