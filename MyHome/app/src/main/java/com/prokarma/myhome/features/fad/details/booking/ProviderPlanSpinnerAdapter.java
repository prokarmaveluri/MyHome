package com.prokarma.myhome.features.fad.details.booking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegIncluded;
import com.prokarma.myhome.utils.CommonUtil;
import java.util.List;

/**
 * Created by kwelsh on 6/22/17.
 * Inspiration from https://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
 */
public class ProviderPlanSpinnerAdapter extends ArrayAdapter<RegIncluded> {
    private Context context;
    private List<RegIncluded> values;
    private int textViewResourceId;

    public ProviderPlanSpinnerAdapter(Context context, int textViewResourceId, List<RegIncluded> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        this.textViewResourceId = textViewResourceId;
    }

    public int getCount() {
        return values.size();
    }

    public RegIncluded getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        TextView name = (TextView) view.findViewById(R.id.provider_plan_name);
        name.setText(values.get(position).getAttributes().getName());
        if (CommonUtil.isAccessibilityEnabled(context)) {
            name.setContentDescription(CommonUtil.capitalContent(name.getText().toString()));
        }
        return name;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        TextView name = (TextView) view.findViewById(R.id.provider_plan_name);
        name.setText(values.get(position).getAttributes().getName());
        return name;
    }

}
