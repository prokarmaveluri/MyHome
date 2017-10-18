/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import android.content.Intent;

import com.americanwell.sdk.entity.SDKError;

/**
 * This is a custom variation of SDKResponse used to handle the responses from
 * StartConferenceCallback
 */
public class SDKStartConferenceResponse<T, E extends SDKError> extends SDKResponse<T, E> {

    private Intent intent;
    private boolean conferenceCanceled = false;
    private boolean conferenceEnded = false;
    private boolean conferenceDisabled = false;

    public SDKStartConferenceResponse(T result, E error) {
        super(result, error);
    }

    public SDKStartConferenceResponse() {
        super(null, null);
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public boolean isConferenceCanceled() {
        return conferenceCanceled;
    }

    public void setConferenceCanceled(boolean conferenceCanceled) {
        this.conferenceCanceled = conferenceCanceled;
    }

    public boolean isConferenceEnded() {
        return conferenceEnded;
    }

    public void setConferenceEnded(boolean conferenceEnded) {
        this.conferenceEnded = conferenceEnded;
    }

    public boolean isConferenceDisabled() {
        return conferenceDisabled;
    }

    public void setConferenceDisabled(boolean conferenceDisabled) {
        this.conferenceDisabled = conferenceDisabled;
    }
}
