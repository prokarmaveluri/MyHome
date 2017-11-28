/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.util;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.daimajia.swipe.SwipeLayout;

/**
 * standard listener for use in pull-to-refresh activites with
 * item-swipe list views.  disables the pull-to-refresh control when the item swiper
 * is doing something
 */
public class SampleSwipeListener implements SwipeLayout.SwipeListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    public SampleSwipeListener(@NonNull final SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onStartOpen(SwipeLayout layout) {
        // disable full list pull-down refresh while the item menu is open
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onOpen(SwipeLayout layout) {

    }

    @Override
    public void onStartClose(SwipeLayout layout) {

    }

    @Override
    public void onClose(SwipeLayout layout) {

    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
        // enable full list pull-down refresh once the item menu has closed
        swipeRefreshLayout.setEnabled(true);
    }
}
