package com.prokarma.myhome.features.televisit.visitreports;

import android.os.Bundle;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.Constants;

import static com.prokarma.myhome.features.televisit.summary.VisitSummaryFragment.VISIT_LIST_POSITION;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsRouter implements MCNReportsContract.Router {
    final BaseFragment fragment;

    public MCNReportsRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void goToSummaryDetail(int position) {
        if (position >= 0 && position < AwsManager.getInstance().getVisitReports().size()) {
            Bundle bundle = new Bundle();
            bundle.putInt(VISIT_LIST_POSITION, position);
            ((NavigationActivity) fragment.getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY, bundle);
        }
    }

    @Override
    public void goToMcnDashboard() {
        //in this specific case, we donot have to skip an entry in back stack and so handled by default.
    }
}
