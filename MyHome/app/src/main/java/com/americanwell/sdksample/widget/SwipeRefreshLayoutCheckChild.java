/**
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Sample implementation of Swipe Refresh with inner AbsListView in layout.
 */

public class SwipeRefreshLayoutCheckChild extends SwipeRefreshLayout {

    View mChildView;

    public SwipeRefreshLayoutCheckChild(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutCheckChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        return (mChildView != null) ? mChildView.canScrollVertically(-1) : super.canChildScrollUp();
    }

    public void setChildView(View childView) {
        mChildView = childView;
    }
}
