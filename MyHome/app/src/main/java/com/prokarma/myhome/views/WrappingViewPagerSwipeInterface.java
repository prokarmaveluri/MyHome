package com.prokarma.myhome.views;

/**
 * Created by kwelsh on 5/24/17.
 * Used in conjunction with WrappingViewPager to limit swipes to the left/right
 * Return true to allow the swipe, else return false
 */

public interface WrappingViewPagerSwipeInterface {
    boolean onSwipeRight();

    boolean onSwipeLeft();
}
