/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.services;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.SampleUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import icepick.Icepick;
import icepick.State;

/**
 * View for Provider details
 */
public class ProviderView extends LinearLayout {

    @Inject
    SampleUtils sampleUtils;

    @Inject
    LocaleUtils localeUtils;

    @BindView(R.id.image_view)
    CircleImageView imageView;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.specialty_text_view)
    TextView specialtyTextView;
    @BindView(R.id.availability_text_view)
    TextView availabilityTextView;
    @BindView(R.id.get_started_button)
    Button getStartedButton;
    @BindView(R.id.greeting_text_view)
    TextView greetingTextView;
    @BindView(R.id.average_rating_bar)
    RatingBar averageRatingBar;
    @BindView(R.id.professional_education_text_view)
    TextView professionalEducationTextView;
    @BindView(R.id.professional_experience_text_view)
    TextView professionalExperienceTextView;
    @BindView(R.id.languages_spoken_text_view)
    TextView languagesSpokenTextView;
    @BindView(R.id.appointments_layout)
    View appointmentsLayout;
    @BindView(R.id.appointment_date)
    TextView appointmentDateTextView;
    @BindView(R.id.appointments_grid)
    RecyclerView appointmentsGrid;
    @BindView(R.id.empty_view)
    View emptyView;

    @BindString(R.string.provider_details_appointments_show_all)
    String showAll;

    @State
    boolean showGetStartedButton = true;
    @State
    ArrayList<Date> appointmentDates;

    private ProviderViewListener listener;

    public interface ProviderViewListener {
        void onScheduleAppointment(Date date);
        ProviderPresenter getPresenter();
    }

    private OnClickListener getStartedOnClickListener;

    public ProviderView(Context context) {
        super(context);
        init(context);
    }

    public ProviderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setListener(@NonNull final ProviderViewListener listener) {
        this.listener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_provider, this, true);
        ButterKnife.bind(this);
        SampleApplication.getActivityComponent().inject(this);
    }

    public void setGetStartedOnClickListener(final OnClickListener listener) {
        getStartedOnClickListener = listener;
    }

    @OnClick(R.id.get_started_button)
    public void onGetStartedClick() {
        if (getStartedOnClickListener != null) {
            getStartedOnClickListener.onClick(getStartedButton);
        }
    }

    public boolean canGetStarted() {
        return getStartedButton.getVisibility() == View.VISIBLE;
    }

    public void setProvider(final Provider provider) {
        if (provider != null) {
            setProviderImage(provider);
            setProviderName(provider.getFullName());
            setGreeting(provider.getTextGreeting());
            setAvailability(provider.getVisibility(), provider.getWaitingRoomCount());
            setSpecialty(provider.getSpecialty().getName());
            setRating(provider.getRating());
            setLanguagesSpoken(provider.getSpokenLanguages());
            setProfessionalEducation(provider.getSchoolName(), provider.getGraduatingYear().getYear());
            setExperience(provider.getYearsExperience());
            setCanGetStarted(ProviderVisibility.isVideoAvailable(provider.getVisibility()) ||
                    ProviderVisibility.isVideoBusy(provider.getVisibility()));
        }
    }

    private void setProviderImage(final Provider provider) {
        if (getContext() instanceof BaseSampleNucleusActivity) {
            // this is getting the instance of PracticeProvidersManager from the presenter, by casting the context to the activity.
            // this should really have a special base class of Presenter that's used by all Activites that have this view included
            final PracticeProvidersManager practiceProvidersManager =
                    ((BaseSampleNucleusRxPresenter) ((BaseSampleNucleusActivity) getContext()).getPresenter()).getPracticeProvidersManager();

            // check image exists
            if (provider.hasImage()) {
                practiceProvidersManager
                        .newImageLoader(provider, imageView, ProviderImageSize.EXTRA_EXTRA_LARGE)
                        .placeholder(getResources().getDrawable(R.drawable.img_provider_photo_placeholder))
                        .build()
                        .load();
            }
            // skip request, set placeholder
            else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_provider_photo_placeholder));
            }

        }
    }

    private void setProviderName(final String name) {
        nameTextView.setText(name);
    }

    private void setGreeting(final String greeting) {
        greetingTextView.setText(greeting);
    }

    private void setAvailability(final ProviderVisibility availability, final Integer waitingRoomCount) {
        String text = sampleUtils.getProviderAvailabilityString(
                getResources(),
                availability,
                waitingRoomCount);
        availabilityTextView.setText(text);
    }

    private void setSpecialty(final String specialty) {
        specialtyTextView.setText(specialty);
    }

    private void setRating(final int rating) {
        averageRatingBar.setNumStars(5);
        averageRatingBar.setRating(rating);
    }

    private void setLanguagesSpoken(final List<Language> languages) {
        final StringBuilder languagesSpokenBuilder = new StringBuilder();
        for (int i = 0, n = languages.size(); i < n; i++) {
            final String language = languages.get(i).getName();
            if (i > 0) {
                languagesSpokenBuilder.append(", ");
            }
            languagesSpokenBuilder.append(language);
        }
        languagesSpokenTextView.setText(languagesSpokenBuilder.toString());
    }

    private void setProfessionalEducation(final String schoolName, final int graduatingYear) {
        professionalEducationTextView.setText(String.format("%s, %s", schoolName, graduatingYear));
    }

    private void setExperience(final int experience) {
        professionalExperienceTextView.setText(String.valueOf(experience));
    }

    private void setCanGetStarted(final boolean bCan) {
        getStartedButton.setVisibility((showGetStartedButton && bCan) ? View.VISIBLE : View.GONE);
    }

    public boolean showGetStartedButton() {
        return showGetStartedButton;
    }

    public void setShowGetStartedButton(boolean showGetStartedButton) {
        this.showGetStartedButton = showGetStartedButton;
        setCanGetStarted(false);
    }

    public void setShowAppointmentsLayout(boolean bShow) {
        appointmentsLayout.setVisibility(bShow ? VISIBLE : GONE);
    }

    public void setAppointmentDate(@NonNull final Date appointmentDate) {
        appointmentDateTextView.setText(getContext()
                .getString(R.string.provider_details_appointment_date,
                        localeUtils.formatAppointmentDate(appointmentDate)));
    }

    public void setAvailableAppointments(final List<Date> appointmentDates) {
        this.appointmentDates = (ArrayList<Date>) appointmentDates;
        if (appointmentDates != null && !appointmentDates.isEmpty()) {
            appointmentsGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
            appointmentsGrid.setAdapter(new AppointmentDateAdapter());
            emptyView.setVisibility(GONE);
        }
        else {
            appointmentsGrid.setVisibility(GONE);
            emptyView.setVisibility(VISIBLE);
        }
    }

    class AppointmentTimeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.provider_appointment_time_button)
        Button button;

        AppointmentTimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class AppointmentDateAdapter extends RecyclerView.Adapter<AppointmentTimeHolder> {

        // initial state is to show a max 3x3 grid of appointments, which is 9 total items
        private static final int MAX_WHEN_NOT_SHOWING_ALL = 9;
        private boolean bShowAll = false;

        @Override
        public AppointmentTimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_provider_appointment, parent, false);

            return new AppointmentTimeHolder(view);
        }

        @Override
        public void onBindViewHolder(final AppointmentTimeHolder holder, final int position) {

            // it not showing all and we're at position 9 and have more than 9 items... show a "show all" button
            if (!bShowAll && (holder.getAdapterPosition() == MAX_WHEN_NOT_SHOWING_ALL - 1) && appointmentDates.size() > MAX_WHEN_NOT_SHOWING_ALL) {
                holder.button.setText(showAll);
                holder.button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bShowAll = true; // showing all
                        notifyDataSetChanged(); // refresh
                    }
                });
            }
            else {
                final Date slot = appointmentDates.get(holder.getAdapterPosition());
                holder.button.setText(localeUtils.formatAppointmentSlot(slot));
                holder.button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onScheduleAppointment(appointmentDates.get(holder.getAdapterPosition()));
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            final int total = appointmentDates.size();
            if (!bShowAll && total > MAX_WHEN_NOT_SHOWING_ALL) {
                return MAX_WHEN_NOT_SHOWING_ALL; // limit when not showing all
            }
            return total;
        }

    }
}

