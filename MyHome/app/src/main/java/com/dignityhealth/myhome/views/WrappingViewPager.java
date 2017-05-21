package com.dignityhealth.myhome.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kwelsh on 5/20/17.
 * This is a ViewPager that can use wrap_content as it's height (unlike the native ViewPager).
 * It does this by calculating the height of the children, finding the tallest, and using that as it's height.
 *
 * http://stackoverflow.com/a/24666987/2128921
 */

public class WrappingViewPager extends ViewPager {
    public WrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            // super has to be called in the beginning so the child views can be initialized.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) height = h;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
