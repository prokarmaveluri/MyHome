package com.dignityhealth.myhome.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kwelsh on 5/20/17.
 * This is a ViewPager that can use wrap_content as it's height (unlike the native ViewPager).
 * It does this by calculating the height of the children, finding the tallest, and using that as it's height.
 * <p>
 * http://stackoverflow.com/a/24666987/2128921
 * https://stackoverflow.com/questions/19602369/how-to-disable-viewpager-from-swiping-in-one-direction
 */

public class WrappingViewPager extends ViewPager {
    private float initialXValue;
    private WrappingViewPagerSwipeInterface swipeInterface;
    private boolean swipeRightAllowed = true;
    private boolean swipeLeftAllowed = true;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.IsSwipeAllowed(event) && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.IsSwipeAllowed(event) && super.onInterceptTouchEvent(event);
    }


    private boolean IsSwipeAllowed(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0) {
                    // swipe from left to right detected
                    if (swipeInterface != null) {
                        return swipeInterface.onSwipeRight();
                    } else {
                        return swipeRightAllowed;
                    }

                } else if (diffX < 0) {
                    // swipe from right to left detected
                    if (swipeInterface != null) {
                        return swipeInterface.onSwipeLeft();
                    } else {
                        return swipeLeftAllowed;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }

    public WrappingViewPagerSwipeInterface getSwipeInterface() {
        return swipeInterface;
    }

    public void setSwipeInterface(WrappingViewPagerSwipeInterface swipeInterface) {
        this.swipeInterface = swipeInterface;
    }

    public void setSwipeRightAllowed(boolean swipeRightAllowed) {
        this.swipeRightAllowed = swipeRightAllowed;
    }

    public void setSwipeLeftAllowed(boolean swipeLeftAllowed) {
        this.swipeLeftAllowed = swipeLeftAllowed;
    }

    public void setSwipeAllowed(boolean swipeAllowed) {
        this.swipeLeftAllowed = swipeAllowed;
        this.swipeRightAllowed = swipeAllowed;
    }
}
