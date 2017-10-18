/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.util;

/**
 * Basic Object to represent {@link #mLower lower} and {@link #mUpper upper} bounds on a range of
 * int values.
 * <p>
 * Use {@link #contains(int)} to check whether a value is within this Range.
 */
public class Range {
    private int mLower, mUpper;


    private Range() {
    }


    public Range(int lower, int upper) {
        mLower = lower;
        mUpper = upper;
    }


    public int getLower() {
        return mLower;
    }


    public int getUpper() {
        return mUpper;
    }


    public boolean contains(int value) {
        return (mLower <= value && value <= mUpper);
    }


    public int getQuantity() {
        return mUpper - mLower + 1;
    }


    @Override
    public String toString() {
        return "Range{" +
                "mLower=" + mLower +
                ", mUpper=" + mUpper +
                '}';
    }
}
