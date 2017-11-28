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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.americanwell.sdk.entity.NamedSDKEntity;

import java.util.List;

/**
 * Adapter used for spinners for any objects that extend NamedSDKEntity
 */
public class NamedArrayAdapter<T extends NamedSDKEntity> extends ArrayAdapter<T> {
    public NamedArrayAdapter(final Context context, @LayoutRes int resource, final List<T> list) {
        super(context, resource, list);
    }

    private class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view;
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final T entity = getItem(position);
        viewHolder.textView.setText(entity.getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}