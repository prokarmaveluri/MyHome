package com.prokarma.myhome.features.fad.details.booking;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentAvailableTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.DeviceDisplayManager;
import com.prokarma.myhome.views.FlowLayout;

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
    public static final String DATE_KEY = "date";

    public ArrayList<AppointmentAvailableTime> allAppointments;
    public ArrayList<AppointmentTime> todaysAppointments = new ArrayList<>();
    public Date bookingDate;
    public Date firstAppointmentDate;
    public BookingDateHeaderInterface selectTimeInterface;
    public BookingRefreshInterface refreshInterface;

    View bookingView;
    LinearLayout normalLayout;
    TextView monthLabel;
    TextView timeZoneWarning;
    ImageView rightArrow;
    ImageView leftArrow;
    FlowLayout timeLayout;
    Button noAppointments;
    Button callForAppointments;
    ProgressBar progressBar;

    AppointmentAvailableTime nextAppointment;

    public static BookingSelectTimeFragment newInstance() {
        return new BookingSelectTimeFragment();
    }

    public static BookingSelectTimeFragment newInstance(Date date) {
        BookingSelectTimeFragment bookingFragment = new BookingSelectTimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATE_KEY, date);
        bookingFragment.setArguments(args);
        return bookingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        getAllAppointments();

        if (args != null && args.getSerializable(DATE_KEY) != null) {
            bookingDate = (Date) args.getSerializable(DATE_KEY);
        } else {
            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.DATE, 1);
            bookingDate = calendar.getTime();
        }

        bookingView = inflater.inflate(R.layout.book_select_time, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.fad_title));

        normalLayout = (LinearLayout) bookingView.findViewById(R.id.normal_layout);
        timeLayout = (FlowLayout) bookingView.findViewById(R.id.time_group);
        timeLayout.setGravity(Gravity.CENTER);
        noAppointments = (Button) bookingView.findViewById(R.id.empty_appointments);
        callForAppointments = (Button) bookingView.findViewById(R.id.call_for_appointment);
        timeZoneWarning = (TextView) bookingView.findViewById(R.id.timezone_warning);
        progressBar = (ProgressBar) bookingView.findViewById(R.id.loading_layout);

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
                bookingDate = DateUtil.moveDate(bookingDate, 1);
                setMonthHeader(bookingDate);
                setupView();

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
        coachmarkTimeSlots(dateHeader);
        return bookingView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    private void getAllAppointments() {
        allAppointments = CommonUtil.filterAppointmentsToType(AppointmentManager.getInstance().getAppointmentTimeSlots(), BookingManager.getBookingAppointmentType());
        Collections.sort(allAppointments);
    }

    private void setupView() {
        todaysAppointments.clear();
        todaysAppointments = getTodaysAppointments(bookingDate, allAppointments);

        if (!allAppointments.isEmpty()) {
            firstAppointmentDate = DateUtil.findFirstAppointmentDate(allAppointments);
        }

        adjustArrowColors();

        if (todaysAppointments != null && !todaysAppointments.isEmpty() && !DateUtil.isToday(bookingDate)) {
            timeLayout.setVisibility(View.VISIBLE);
            noAppointments.setVisibility(View.GONE);
            callForAppointments.setVisibility(View.GONE);
            timeZoneWarning.setVisibility(View.VISIBLE);
            timeZoneWarning.setText(String.format(getResources().getString(R.string.booking_timezone_warning),
                    DateUtil.getReadableTimeZone(AppointmentManager.getInstance().getAppointmentTimeSlots())));

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
                timeZoneWarning.setVisibility(View.VISIBLE);
                timeZoneWarning.setText(String.format(getResources().getString(R.string.booking_timezone_warning),
                        DateUtil.getReadableTimeZone(AppointmentManager.getInstance().getAppointmentTimeSlots())));

                //Bold just the Date part
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    noAppointments.setText(Html.fromHtml(getString(R.string.next_available) + ": " + "<b>" + DateUtil.getDateWordsFromDateHyphen(nextAppointment.getDate()) + "</b>", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    //noinspection deprecation
                    noAppointments.setText(Html.fromHtml(getString(R.string.next_available) + ": " + "<b>" + DateUtil.getDateWordsFromDateHyphen(nextAppointment.getDate()) + "</b>"));
                }

                noAppointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            bookingDate = DateUtil.getDateFromHyphens(nextAppointment.getDate());
                            setMonthHeader(bookingDate);
                            setupView();

                            //You're moving forward, so we can piggy back on this interface call for now. Might need it's own in the future...
                            if (selectTimeInterface != null) {
                                selectTimeInterface.onFrontArrowClicked();
                            }
                        } catch (ParseException e) {
                            Timber.e(e);
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                noAppointments.setText(getString(R.string.no_appointments_available));
                noAppointments.setOnClickListener(null);
                timeZoneWarning.setVisibility(View.GONE);
            }

            if (DateUtil.isToday(bookingDate)) {
                callForAppointments.setVisibility(View.VISIBLE);
                callForAppointments.setText(getString(R.string.call_for_todays_appointmentss));
                callForAppointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectTimeInterface != null) {
                            selectTimeInterface.onPhoneNumberClicked();
                        }
                    }
                });
            } else if (nextAppointment == null) {
                callForAppointments.setVisibility(View.VISIBLE);
                callForAppointments.setText(getString(R.string.call_for_more_appointments));
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
                callForAppointments.setOnClickListener(null);
            }
        }
    }

    /**
     * Method for adding times to a Flow Layout
     *
     * @param timeGroup    Flow Layout where Times will be added
     * @param appointments the appointments being added to the Flow Layout
     */
    @SuppressWarnings("ObjectAllocationInLoop")
    private void setAppointmentTimes(final FlowLayout timeGroup, final ArrayList<AppointmentTime> appointments) {
        timeGroup.removeAllViews();
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(DeviceDisplayManager.dpToPx(getContext(), 110), DeviceDisplayManager.dpToPx(getContext(), 40));
        layoutParams.setMargins(DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5), DeviceDisplayManager.dpToPx(getContext(), 5));
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

        View.OnClickListener timeClickedListener;
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.selectableButtonStyle);

        for (final AppointmentTime appointment : appointments) {
            final Button timeButton = new Button(contextThemeWrapper, null, R.style.selectableButtonStyle);
//            timeButton.setPadding(DeviceDisplayManager.dpToPx(getContext(), 10), DeviceDisplayManager.dpToPx(getContext(), 4), DeviceDisplayManager.dpToPx(getContext(), 10), DeviceDisplayManager.dpToPx(getContext(), 4));
            timeButton.setGravity(Gravity.CENTER);
            layoutParams.gravity = Gravity.CENTER;
            timeButton.setTextSize(18);
            timeButton.setTypeface(boldTypeface);
//            timeButton.setMinimumWidth(DeviceDisplayManager.dpToPx(getContext(), 100)); //Make sure it's at least 100dp, though it's allowed to stretch to wrap_content
            timeButton.setLayoutParams(layoutParams);
            timeButton.setText(DateUtil.getTime(appointment.getTime()));

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

    private ArrayList<AppointmentTime> getTodaysAppointments(final Date todaysDate, final ArrayList<AppointmentAvailableTime> allAppointments) {
        ArrayList<AppointmentTime> todaysAppointments = new ArrayList<>();

        Date appointmentDate = new Date();
        for (AppointmentAvailableTime appointmentDetails : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateFromHyphens(appointmentDetails.getDate());
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (DateUtil.isOnSameDay(todaysDate, appointmentDate)) {
                for (AppointmentTime appointmentTime : appointmentDetails.getTimes()) {
                    todaysAppointments.add(appointmentTime);
                }
            }
        }

        return todaysAppointments;
    }

    @Nullable
    private AppointmentAvailableTime findNextAppointment(final Date todaysDate, final ArrayList<AppointmentAvailableTime> allAppointments) {
        AppointmentAvailableTime nextAppointment = null;

        Date appointmentDate = new Date();
        for (AppointmentAvailableTime appointmentDetails : allAppointments) {
            try {
                appointmentDate = DateUtil.getDateFromHyphens(appointmentDetails.getDate());
            } catch (ParseException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            if (DateUtil.isOnSameDay(appointmentDate, todaysDate) || appointmentDate.after(todaysDate)) {
                nextAppointment = appointmentDetails;
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

        rightArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
    }

    public void setMonthHeader(Date date) {
        monthLabel.setText(DateUtil.convertDateToReadableShortWords(date));

        if (selectTimeInterface != null) {
            selectTimeInterface.onDateChanged(bookingDate);
        }
    }

    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        normalLayout.setVisibility(View.GONE);

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        normalLayout.setVisibility(View.VISIBLE);

        //Refresh view with the latest appointment info
        getAllAppointments();
        setupView();

        if (refreshInterface != null) {
            refreshInterface.onRefreshView(true);
        }
    }

    public void setSelectTimeInterface(BookingDateHeaderInterface selectTimeInterface) {
        this.selectTimeInterface = selectTimeInterface;
    }

    public void setRefreshInterface(BookingRefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    private void coachmarkTimeSlots(View view) {
        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.BOOKING_DATE_SKIP_COACH_MARKS);
        if (skip)
            return;

        if (view != null && view.getVisibility() != View.VISIBLE)
            return;
        TapTargetView.showFor(getActivity(),
                TapTarget.forView(view, getString(R.string.coachmark_time_slots))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.BOOKING_DATE_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.BOOKING_DATE_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }
}
