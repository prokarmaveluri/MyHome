/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import com.americanwell.sdk.entity.health.ConsumerHealthItem;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

/**
 * Base presenter for BaseHealthItemIntakeActivity instances
 */
public abstract class BaseHealthItemIntakePresenter<V extends BaseHealthItemIntakeActivity, I extends ConsumerHealthItem> extends BaseSampleNucleusRxPresenter<V> {

    @State
    protected ArrayList<I> consumerHealthItems;

    public void onTakeView(V view) {
        super.onTakeView(view);
        if (consumerHealthItems == null) {
            startGetItems();
        }
        else {
            setItems(consumerHealthItems);
        }
    }

    public abstract void startGetItems();

    @SuppressWarnings("unchecked")
    public void setItems(final List<I> items) {
        this.consumerHealthItems = (ArrayList<I>) items;
        view.setConsumerHealthItems(items);
    }

    public void toggleItemCurrent(final I item) {
        item.setCurrent(!item.isCurrent());
    }

}
