package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.ApiRecyclerViewListener;
import com.prokarma.myhome.entities.ApiOption;
import com.prokarma.myhome.utils.AppPreferences;

import java.util.ArrayList;

/**
 * Created by kwelsh on 11/17/17.
 */

public class ApiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<ApiOption> apiOptions;
    private final ApiRecyclerViewListener onPreferenceChangedListener;


    public ApiRecyclerViewAdapter(Context context, ArrayList<ApiOption> apiOptions, ApiRecyclerViewListener onPreferenceChangedListener) {
        this.context = context;
        this.apiOptions = apiOptions;
        this.onPreferenceChangedListener = onPreferenceChangedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(context, LayoutInflater.from(parent.getContext()).inflate(R.layout.api_switch_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;
        ApiOption apiOption = apiOptions.get(position);

        myHolder.title.setText(apiOption.getTitle());
        myHolder.description.setText(apiOption.getDescription());
        myHolder.warning.setVisibility(apiOption.getWarning() != null && !apiOption.getWarning().isEmpty() ? View.VISIBLE : View.GONE);
        myHolder.warning.setText(apiOption.getWarning() != null ? apiOption.getWarning() : "");
        myHolder.apiSwitch.setChecked(!AppPreferences.getInstance().getBooleanPreference(apiOption.getPreference()));
        myHolder.setOnPreferenceSwitchedListener(apiOption, onPreferenceChangedListener);
    }

    @Override
    public int getItemCount() {
        if (apiOptions == null || apiOptions.isEmpty()) {
            return 0;
        }

        return apiOptions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView description;
        public TextView warning;
        public SwitchCompat apiSwitch;

        public ViewHolder(final Context context, final View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            warning = (TextView) view.findViewById(R.id.warning);
            apiSwitch = (SwitchCompat) view.findViewById(R.id.api_switch);
        }

        public void setOnPreferenceSwitchedListener(final ApiOption apiOption, final ApiRecyclerViewListener onPreferenceChangedListener) {
            apiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    if (buttonView.isPressed()) {
                        onPreferenceChangedListener.onPreferenceChanged(apiOption, getAdapterPosition(), isChecked);
                    }
                }
            });
        }

    }
}
