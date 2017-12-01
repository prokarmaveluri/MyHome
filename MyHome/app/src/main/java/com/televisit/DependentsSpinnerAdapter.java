package com.televisit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.prokarma.myhome.R;

import java.util.List;

/**
 * Created by kwelsh on 12/1/17.
 * Inspiration from https://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
 */
public class DependentsSpinnerAdapter extends ArrayAdapter<Consumer> {
    private Context context;
    private List<Consumer> consumers;
    private int textViewResourceId;

    public DependentsSpinnerAdapter(Context context, int textViewResourceId, List<Consumer> consumers) {
        super(context, textViewResourceId, consumers);
        this.context = context;
        this.consumers = consumers;
        this.textViewResourceId = textViewResourceId;
    }

    public int getCount() {
        return consumers.size();
    }

    public Consumer getItem(int position) {
        return consumers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        TextView name = (TextView) view.findViewById(R.id.consumer_name);
        name.setText(consumers.get(position).getFullName());
        return name;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        TextView name = (TextView) view.findViewById(R.id.consumer_name);
        name.setText(consumers.get(position).getFullName());
        return name;
    }

}
