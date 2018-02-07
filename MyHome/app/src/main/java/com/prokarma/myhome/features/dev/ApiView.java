package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.ApiRecyclerViewListener;
import com.prokarma.myhome.entities.ApiOption;
import com.prokarma.myhome.utils.AppPreferences;

import java.util.ArrayList;

/**
 * Created by kwelsh on 2/7/18.
 */

public class ApiView implements ApiContract.View {
    private RecyclerView apiRecyclerView;
    private final Context context;

    public ApiView(final Context context, final View masterView, final ApiPresenter presenter) {
        this.context = context;

        apiRecyclerView = (RecyclerView) masterView.findViewById(R.id.list_apis);

        Button save = (Button) masterView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSignoutButtonPressed();
            }
        });
    }

    @Override
    public void showApiOptions(ArrayList<ApiOption> apiOptions) {
        ApiRecyclerViewAdapter apiRecyclerViewAdapter = new ApiRecyclerViewAdapter(context, apiOptions, new ApiRecyclerViewListener() {
            @Override
            public void onPreferenceChanged(Object model, int position, boolean isChecked) {
                final ApiOption apiOption = (ApiOption) model;
                AppPreferences.getInstance().setBooleanPreference(apiOption.getPreference(), !isChecked);
            }
        });

        apiRecyclerView.setHasFixedSize(true);
        apiRecyclerView.setAdapter(apiRecyclerViewAdapter);
        apiRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        apiRecyclerView.addItemDecoration(itemDecoration);
    }
}
