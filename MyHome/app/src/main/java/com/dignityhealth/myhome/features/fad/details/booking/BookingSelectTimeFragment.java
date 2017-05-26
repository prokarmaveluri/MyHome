package com.dignityhealth.myhome.features.fad.details.booking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.dignityhealth.myhome.views.FlowLayout;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/25/17.
 */

public class BookingSelectTimeFragment extends Fragment {
    public static final String BOOKING_SELECT_TIME_TAG = "booking_select_time_tag";
    public static final String TIMES_KEY = "booking_times";

    public ArrayList<BookingTimeSlot> times;
    public BookingSelectTimeInterface selectTimeInterface;

    View bookingView;

    public static BookingSelectTimeFragment newInstance() {
        return new BookingSelectTimeFragment();
    }

    public static BookingSelectTimeFragment newInstance(ArrayList<BookingTimeSlot> times) {
        BookingSelectTimeFragment bookingFragment = new BookingSelectTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TIMES_KEY, times);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        times = args.getParcelableArrayList(TIMES_KEY);
        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        setAppointmentTimes((FlowLayout) bookingView.findViewById(R.id.time_group), times);
        return bookingView;
    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup Flow Layout where Times will be added
     * @param times     the times being added to the Flow Layout
     */
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<BookingTimeSlot> times) {
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 4));

        View.OnClickListener timeClickedListener;

        for (final BookingTimeSlot time : times) {
            final ToggleButton timeToggle = new ToggleButton(new ContextThemeWrapper(getContext(), R.style.selectableButtonStyle), null, R.style.selectableButtonStyle);
            timeToggle.setPadding(DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12), DeviceDisplayManager.dpToPx(getContext(), 12));
            timeToggle.setGravity(Gravity.CENTER);
            timeToggle.setLayoutParams(layoutParams);
            timeToggle.setTextOn(time.time);
            timeToggle.setTextOff(time.time);
            timeToggle.setChecked(time.isPicked);

            timeClickedListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectTimeInterface != null) {
                        selectTimeInterface.onTimeSelected(time);
                    }
                }
            };
            timeToggle.setOnClickListener(timeClickedListener);

            timeGroup.addView(timeToggle, layoutParams);
        }
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


    public void setSelectTimeInterface(BookingSelectTimeInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }
}
