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
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdk.manager.VisitTransferCallback;

import java.util.Map;

/**
 * This is a custom variation of SDKResponse used to handle the responses from
 * {@link VisitTransferCallback}
 */
public class SDKVisitTransferResponse<T, E extends SDKError> extends SDKValidatedResponse<T, E> {

    private Visit visitRedirect;
    private VisitContext visitContext;
    private Visit newVisit;
    private boolean providerUnavailable = false;

    public SDKVisitTransferResponse(T result, E error, Map<String, ValidationReason> validationReasons) {
        super(result, error, validationReasons);
    }

    public SDKVisitTransferResponse() {
        super(null, null, null);
    }

    public Visit getVisitRedirect() {
        return visitRedirect;
    }

    public void setVisitRedirect(Visit visitRedirect) {
        this.visitRedirect = visitRedirect;
    }

    public VisitContext getVisitContext() {
        return visitContext;
    }

    public void setVisitContext(VisitContext visitContext) {
        this.visitContext = visitContext;
    }

    public Visit getNewVisit() {
        return newVisit;
    }

    public void setNewVisit(Visit newVisit) {
        this.newVisit = newVisit;
    }

    public boolean isProviderUnavailable() {
        return providerUnavailable;
    }

    public void setProviderUnavailable(boolean providerUnavailable) {
        this.providerUnavailable = providerUnavailable;
    }
}
