package com.prokarma.myhome.features.televisit.medications;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.televisit.visitreports.MCNReportsContract;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsRouter implements MCNMedicationsContract.Router {
    final BaseFragment fragment;

    public MCNMedicationsRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void goToMcnDashboard() {
        fragment.getActivity().getSupportFragmentManager().popBackStack();
    }
}
