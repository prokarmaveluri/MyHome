package com.prokarma.myhome.features.mycare;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
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

        View divider = (View) view.findViewById(R.id.dividerLine);
        if (divider != null) {
            divider.setVisibility(View.VISIBLE);
        }

        TextView name = (TextView) view.findViewById(R.id.consumer_name);

        //when the list is created and bound to the adapter, since Me is always added as position zero, we are taking object at position zero, to be as Me.
        if (position == 0) {
            name.setText("Me");
        } else {
            name.setText(consumers.get(position).getFullName());
        }

        //getContext().getColor() API is available only for API Level 23 and above, at this point.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            name.setTextColor(getContext().getColor(R.color.teal));
        } else {
            name.setTextColor(Color.parseColor("#097288"));
        }
        name.setTypeface(name.getTypeface(), Typeface.BOLD);

        return name;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        View divider = (View) view.findViewById(R.id.dividerLine);
        if (divider != null) {
            divider.setVisibility(View.GONE);
        }

        TextView name = (TextView) view.findViewById(R.id.consumer_name);
        //when the list is created and bound to the adapter, since Me is always added as position zero, we are taking object at position zero, to be as Me.
        if (position == 0) {
            name.setText("Me");
        } else {
            name.setText(consumers.get(position).getFullName());
        }
        return name;
    }

}
