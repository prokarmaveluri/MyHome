package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.ApiRecyclerViewListener;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class ApiFragment extends BaseFragment implements ApiContract.View {
    ApiContract.Presenter presenter = new ApiPresenter(this);

    public static final String API_TAG = "api_tag";

    ArrayList<ApiOption> apiOptions;

    View apiView;
    RecyclerView apiRecyclerView;
    ApiRecyclerViewAdapter apiRecyclerViewAdapter;

    public static ApiFragment newInstance() {
        return new ApiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        apiView = inflater.inflate(R.layout.api, container, false);
        getActivity().setTitle(getString(R.string.api_settings));

        apiRecyclerView = (RecyclerView) apiView.findViewById(R.id.list_apis);
        presenter.requestingApiOptions();

        Button save = (Button) apiView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSignoutButtonPressed(ApiFragment.this);
            }
        });

        return apiView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.API;
    }

    @Override
    public void showApiOptions(ArrayList<ApiOption> apiOptions) {
        apiRecyclerViewAdapter = new ApiRecyclerViewAdapter(getContext(), apiOptions, new ApiRecyclerViewListener() {
            @Override
            public void onPreferenceChanged(Object model, int position, boolean isChecked) {
                final ApiOption apiOption = (ApiOption) model;
                AppPreferences.getInstance().setBooleanPreference(apiOption.getPreference(), !isChecked);
            }
        });

        apiRecyclerView.setHasFixedSize(true);
        apiRecyclerView.setAdapter(apiRecyclerViewAdapter);
        apiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        apiRecyclerView.addItemDecoration(itemDecoration);
    }
}
