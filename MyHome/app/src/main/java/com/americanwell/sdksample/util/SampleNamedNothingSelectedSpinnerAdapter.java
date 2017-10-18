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
import android.support.annotation.LayoutRes;

import com.americanwell.sdk.entity.NamedSDKEntity;

import java.util.List;

/**
 * "standard" nothing selected spinner adapter wrapping an NamedArrayAdapter
 */
public class SampleNamedNothingSelectedSpinnerAdapter<T extends NamedSDKEntity> extends NothingSelectedSpinnerAdapter {

    public SampleNamedNothingSelectedSpinnerAdapter(final Context context,
                                                    final List<T> list,
                                                    @LayoutRes final int rowLayoutId) {
        super(new NamedArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_item,
                        list),
                rowLayoutId,
                context);
    }

    public SampleNamedNothingSelectedSpinnerAdapter(final Context context,
                                                    final List<T> list,
                                                    @LayoutRes final int rowLayoutId,
                                                    boolean displayNothingSelectedOnEmpty) {
        super(new NamedArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_item,
                        list),
                rowLayoutId,
                context,
                displayNothingSelectedOnEmpty);
    }


}