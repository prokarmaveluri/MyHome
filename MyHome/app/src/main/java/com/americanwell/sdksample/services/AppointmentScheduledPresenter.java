/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;

import java.util.Date;

import icepick.State;

/**
 * Presenter for AppointmentScheduledActivity
 *
 * @since AWSDK 3.0
 */
public class AppointmentScheduledPresenter extends BaseSampleNucleusRxPresenter<AppointmentScheduledActivity> {

    @State
    ProviderInfo providerInfo;
    @State
    Date appointmentDate;

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    public void setProviderInfo(final ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    public void setAppointmentDate(final Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @Override
    public void onTakeView(AppointmentScheduledActivity view) {
        super.onTakeView(view);
        view.setConfirmationEmailAddress(consumer.getEmail());
        view.setProviderImage(providerInfo);
        view.setWith(providerInfo.getFullName());
        view.setSpecialty(providerInfo.getSpecialty().getName());
        view.setAppointmentDate(appointmentDate);
    }

    // there's a slight crossing of responsibilities here in that we're passing UI componentry into the presenter, but it's a necessary evil
    public void loadProviderImage(final ProviderInfo providerInfo,
                                  final ImageView imageView,
                                  final Drawable placeHolder) {
        // check image exists
        if (providerInfo.hasImage()) {
            awsdk.getPracticeProvidersManager()
                    .newImageLoader(providerInfo, imageView, ProviderImageSize.EXTRA_EXTRA_LARGE)
                    .placeholder(placeHolder)
                    .build()
                    .load();
        }
        else {
            imageView.setImageDrawable(placeHolder);
        }
    }
}
