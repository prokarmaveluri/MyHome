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

/**
 * This is a helper "wrapper" of the two potential objects coming
 * back from a successful SDK response.
 * <p>
 * They are bundled to a single object
 * for the rx Observer pattern used here.
 *
 * @param <T>
 * @param <E>
 */
public class SDKResponse<T, E extends SDKError> {
    private T result;
    private E error;

    public SDKResponse(T result, E error) {
        this.result = result;
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public E getError() {
        return error;
    }
}