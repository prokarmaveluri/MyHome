package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.app.BasePresenter;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorContract {
    interface View {

    }

    interface Interactor {

    }

    interface Presentor extends BasePresenter {
        void onFinishedSelecting(int environmentId, int amwellEnvironmentId, boolean isMutualAuthEnabled, String username, String password);
    }

    interface Router {
        void dismissEnvironmentPicker();
    }
}
