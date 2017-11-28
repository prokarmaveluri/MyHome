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

import com.americanwell.sdksample.util.Range;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * RecyclerView adapter used for calculating and controlling multiple sub adapters
 */
public class RangedAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    private static int TITLE_ID = -1;
    private int mLastViewedId = 1;


    private LinkedHashMap<Integer, BaseSubAdapter> mAdapterMap;
    private HashMap<Integer, Integer> mViewTypeMap;
    private HashMap<Integer, Range> mRangeMap;
    private int count = -1;
    private int mIDCount = 0;

    private OnDataChangedListener mListener = new OnDataChangedListener() {
        @Override
        public void OnDataChanged() {
            notifyDataSetChanged();
        }
    };

    public RangedAdapter() {
        mAdapterMap = new LinkedHashMap<>();
        mViewTypeMap = new HashMap<>();
        mRangeMap = new HashMap<>();
    }

    public void addAdapter(BaseSubAdapter<H> adapter) {
        int id = getNextID();
        adapter.setID(id);
        adapter.setTitleId(TITLE_ID);
        adapter.setOnDataChangedListener(mListener);
        mAdapterMap.put(id, adapter);
        registerViewTypes(id, adapter.getViewTypes());

        notifyDataSetChanged();
    }

    protected void resetData() {
        mLastViewedId = 1;
        count = -1;
        mIDCount = 0;
    }

    private int getNextID() {
        if (mIDCount != Integer.MAX_VALUE) {
            mIDCount++;
        }
        else {
            throw new ArithmeticException("Integer overflow: Cannot generate any more ID's. Please reduce the number of Adapters");
        }
        return mIDCount;
    }

    private boolean registerViewTypes(int id, int[] viewTypes) {
        boolean hasDuplicate = false;
        for (int viewType : viewTypes) {
            hasDuplicate = (hasDuplicate == true || mViewTypeMap.containsKey(viewType));
            mViewTypeMap.put(viewType, id);
        }
        return hasDuplicate;
    }

    @Override
    public int getItemCount() {
        if (count == -1) {
            for (Integer key : mAdapterMap.keySet()) {
                BaseSubAdapter subAdapter = mAdapterMap.get(key);
                int rangeDiff = subAdapter.getCount();
                if (subAdapter.isTitleShowing()) {
                    rangeDiff++;
                }
                if (rangeDiff > 0) {
                    int lower = count + 1;
                    count += rangeDiff;
                    Range adapterRange = new Range(lower, count);
                    mRangeMap.put(subAdapter.getID(), adapterRange);
                }
            }
            count++;//offset for a zero value;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        BaseSubAdapter adapter = getAdapterByPosition(position);
        Range adapterRange = mRangeMap.get(adapter.getID());
        int index = getIndex(position, adapterRange, adapter.isTitleShowing());
        int viewType;
        if (index != -1) {
            viewType = adapter.getItemViewType(index); //subtract one to remove the title
        }
        else {
            viewType = VisitAdapterConstants.TYPE_SUB_HEADER;
        }
        return viewType;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        H holder;
        if (viewType != VisitAdapterConstants.TYPE_SUB_HEADER) {
            BaseSubAdapter<H> adapter = getAdapterByViewType(viewType);
            holder = adapter.onCreateViewHolder(parent, viewType);
        }
        else {
            holder = getTitleViewHolder(parent, viewType);
        }
        return holder;
    }

    public H getTitleViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        BaseSubAdapter adapter = getAdapterByPosition(position);
        Range adapterRange = mRangeMap.get(adapter.getID());
        if (holder.getItemViewType() != VisitAdapterConstants.TYPE_SUB_HEADER) {
            int index = getIndex(position, adapterRange, adapter.isTitleShowing());
            adapter.onBindViewHolder(holder, index);
        }
        else {
            bindTitleHolder(holder, adapter);
        }
    }

    protected void bindTitleHolder(H holder, BaseSubAdapter adapter) {
    }

    private BaseSubAdapter getAdapterByPosition(int position) {
        if (!mRangeMap.get(mLastViewedId).contains(position)) {
            for (int key : mAdapterMap.keySet()) {
                if (key != mLastViewedId) {
                    Range range = mRangeMap.get(key);
                    if (range.contains(position)) {
                        mLastViewedId = key;
                        break;
                    }
                }
            }
        }
        return mAdapterMap.get(mLastViewedId);
    }

    private int getIndex(int position, Range range, boolean hasTitle) {
        int offSet = range.getLower();
        if (hasTitle) {
            offSet++;
        }
        return position - offSet;
    }

    private BaseSubAdapter getAdapterByViewType(int viewType) {
        BaseSubAdapter adapter = null;
        Integer id = mViewTypeMap.get(viewType);
        if (id != null) {
            adapter = mAdapterMap.get(id);
        }
        return adapter;
    }


    public interface OnDataChangedListener {
        void OnDataChanged();
    }
}
