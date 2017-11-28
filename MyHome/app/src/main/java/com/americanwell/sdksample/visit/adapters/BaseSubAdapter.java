/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Base abstract class used for adapters that want to be added to a {@link RangedAdapter}.
 */
public abstract class BaseSubAdapter<H extends RecyclerView.ViewHolder> {
    private RangedAdapter.OnDataChangedListener mListener;
    private int mTitleId;
    private boolean mHasTitle = true;
    private int mID;


    abstract int getCount();

    abstract int getItemViewType(int position);

    abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    abstract void onBindViewHolder(H holder, int position);

    abstract int[] getViewTypes();

    abstract boolean isViewTypeSupported(int viewType);

    abstract int getTitleRes();

    public void setID(int id) {
        mID = id;
    }

    public int getID() {
        return mID;
    }

    public void setTitleId(int id) {
        mTitleId = id;
    }

    public int getTitleID() {
        return mTitleId;
    }

    public void setOnDataChangedListener(RangedAdapter.OnDataChangedListener listener) {
        mListener = listener;
    }

    public void setHasTitle(boolean hasTitle) {
        mHasTitle = hasTitle;
    }

    public boolean isTitleShowing() {
        return mHasTitle;
    }


    public final void notifyDataSetChanged() {


        if (mListener != null) {
            mListener.OnDataChanged();
        }
    }

}
