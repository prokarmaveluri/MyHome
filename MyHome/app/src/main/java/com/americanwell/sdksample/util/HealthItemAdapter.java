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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.americanwell.sdk.entity.health.ConsumerHealthItem;

import java.util.List;

/**
 * Adapter used to build a list of health items.
 */
public class HealthItemAdapter<T extends ConsumerHealthItem> extends ArrayAdapter<T> {
    public HealthItemAdapter(final Context context, final List<T> list) {
        super(context, 0, list);
    }

    private class ViewHolder {
        CheckedTextView checkedTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = View.inflate(getContext(), android.R.layout.simple_list_item_multiple_choice, null);
            viewHolder = new ViewHolder();
            viewHolder.checkedTextView = (CheckedTextView) view;
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final ConsumerHealthItem healthItem = getItem(position);
        viewHolder.checkedTextView.setText(healthItem.getName());
        viewHolder.checkedTextView.setChecked(healthItem.isCurrent());
        return view;
    }
}