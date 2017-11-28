/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.Provider;

/**
 * This is a custom variation of SDKResponse used to handle the responses from
 * MatchmakerCallback
 */
public class SDKMatchmakerResponse<T, E extends SDKError> extends SDKResponse<T, E> {

    private Provider provider;
    private boolean providerListExhausted = false;
    private boolean requestGone = false;

    public SDKMatchmakerResponse(T result, E error) {
        super(result, error);
    }

    public SDKMatchmakerResponse() {
        super(null, null);
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public boolean isProviderListExhausted() {
        return providerListExhausted;
    }

    public void setProviderListExhausted(boolean providerListExhausted) {
        this.providerListExhausted = providerListExhausted;
    }

    public boolean isRequestGone() {
        return requestGone;
    }

    public void setRequestGone(boolean requestGone) {
        this.requestGone = requestGone;
    }
}
