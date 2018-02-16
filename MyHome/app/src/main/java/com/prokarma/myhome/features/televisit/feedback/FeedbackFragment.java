package com.prokarma.myhome.features.televisit.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 12/1/17.
 */

public class FeedbackFragment extends BaseFragment {
    public static final String FEEDBACK_TAG = "feedback_tag";
    MCNFeedbackContract.Presenter presenter;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.feedback));
        View view = inflater.inflate(R.layout.visit_feedback, container, false);

        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.feedback));
        } else {
            getActivity().setTitle(getString(R.string.feedback));
        }

        setHasOptionsMenu(true);

        presenter = new MCNFeedbackPresenter(getContext(), this.getActivity(), this, view);
        presenter.onCreate();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.onActivityCreated();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.finish_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_FEEDBACK_SCREEN, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                presenter.saveFeedback();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.VIDEO_VISIT_FEEDBACK;
    }
}
