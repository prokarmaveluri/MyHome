package com.televisit.visitreports.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.visitreports.ReportsContract;
import com.televisit.visitreports.ReportsPresenter;

/**
 * Created by veluri on 2/8/18.
 */

public class ReportsFragment extends BaseFragment {

    public static final String PREVIOUS_VISITS_TAG = "previous_visits_tag";
    ReportsContract.Presenter presenter;

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_visits, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.previous_visit_summaries));

        setHasOptionsMenu(false);

        presenter = new ReportsPresenter(getContext(), this, view);
        presenter.onCreate();

        return view;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PREVIOUS_VISITS_SUMMARIES;
    }
}

