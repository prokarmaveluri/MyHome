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
        //in this specific case, we donot have to skip an entry in back stack and so handled by default.
    }
}
