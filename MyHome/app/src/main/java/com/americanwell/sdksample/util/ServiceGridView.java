/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Used in the home screen, a non-scrollable grid so it can live peacefully inside
 * a nestedscrollview. this isn't a great solution if you have a huge number of items
 */
public class ServiceGridView extends GridView {
    public ServiceGridView(Context context) {
        super(context);
    }

    public ServiceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ServiceGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
