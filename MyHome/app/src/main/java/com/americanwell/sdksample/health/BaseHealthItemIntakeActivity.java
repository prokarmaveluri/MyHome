/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.widget.ListView;

import com.americanwell.sdk.entity.health.ConsumerHealthItem;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.util.HealthItemAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Base activity for intake activities for ConsumerHealthItem collection
 */
public abstract class BaseHealthItemIntakeActivity<P extends BaseHealthItemIntakePresenter, I extends ConsumerHealthItem> extends BaseSampleNucleusActivity<P> {

    @BindView(R.id.list_view)
    ListView listView;

    private HealthItemAdapter<I> adapter;

    public void setConsumerHealthItems(final List<I> items) {
        adapter = new HealthItemAdapter<>(this, items);
        listView.setAdapter(adapter);
    }

    @SuppressWarnings("unchecked")
    @OnItemClick(R.id.list_view)
    public void setItemSelected(int position) {
        final I item = adapter.getItem(position);
        getPresenter().toggleItemCurrent(item);
        adapter.notifyDataSetChanged();
    }

}
