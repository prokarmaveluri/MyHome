package com.prokarma.myhome.features.televisit.feedback;

import android.support.v4.app.FragmentManager;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by veluri on 2/16/18.
 */

public class MCNFeedbackRouter implements MCNFeedbackContract.Router {
    final BaseFragment fragment;

    public MCNFeedbackRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void goToMcnDashboard() {
        ((NavigationActivity) fragment.getActivity()).getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((NavigationActivity) fragment.getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_NOW, null);
    }
}
