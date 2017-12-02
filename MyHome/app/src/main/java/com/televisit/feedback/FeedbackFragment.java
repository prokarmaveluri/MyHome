package com.televisit.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 12/1/17.
 */

public class FeedbackFragment extends BaseFragment{

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.feedback));
        View view = inflater.inflate(R.layout.visit_summary, container, false);


        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.VIDEO_VISIT_FEEDBACK;
    }
}
