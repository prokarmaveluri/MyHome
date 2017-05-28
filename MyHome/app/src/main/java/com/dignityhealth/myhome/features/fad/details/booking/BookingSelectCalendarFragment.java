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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectCalendarFragment extends Fragment {
    public static final String BOOKING_SELECT_CALENDAR_TAG = "booking_select_calendar_tag";
    public static final String PROVIDER_DETAILS_RESPONSE_KEY = "provider_details_response";

    public ProviderDetailsResponse providerDetailsResponse;
    public BookingDateHeaderInterface selectTimeInterface;

    View bookingView;

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

        MaterialCalendarView calendar = (MaterialCalendarView) bookingView.findViewById(R.id.calendar);
        calendar.setTopbarVisible(false);

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
