/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.americanwell.sdk.entity.SDKEntity;
import com.prokarma.myhome.R;

import java.util.List;

import butterknife.BindView;
import nucleus.presenter.Presenter;

/**
 * Adds support for Activities that use "swipe to refresh" list views
 */
public abstract class BaseSwipeToRefreshActivity<P extends Presenter, L extends SDKEntity>
        extends BaseSampleNucleusActivity<P>
        implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_view)
    protected ListView listView;
    @BindView(R.id.empty_view)
    protected View emptyView;

    protected ArrayAdapter<L> adapter;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        adapter = createAdapter();
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        setTitleCount(0);
    }

    /**
     * this is an example of an overridden "busy" view that uses the spinner in the list refresh
     * instead of the progress dialog
     *
     * @param show
     */
    @Override
    protected void showBusy(final boolean show) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.postDelayed(new Runnable() { // http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(show);
                }
            }, PROGRESS_DIALOG_DELAY_MS);
        }
        else {
            super.showBusy(show);
        }
    }

    /**
     * the activity must create the adapter here
     *
     * @return
     */
    protected abstract ArrayAdapter createAdapter();

    /**
     * the activity must provide a string res id for the title
     *
     * @return
     */
    protected abstract
    @StringRes
    int getTitleRes(); // make this return 0 if not using titles with count

    /**
     * the presenter will call this when there are new items to add to the list
     *
     * @param items
     */
    public void setListItems(@NonNull final List<? extends L> items) {
        adapter.clear();
        adapter.addAll(items);
        listView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // set the title with a count.
    // the return value of getTitleRes() should be properly formatted to accept the count
    public void setTitleCount(final int count) {
        if (getTitleRes() != 0) {
            final String countString =
                    count <= 0
                            ? ""
                            : " (" + count + ")";
            setTitle(getString(getTitleRes(), countString));
        }
    }
}
