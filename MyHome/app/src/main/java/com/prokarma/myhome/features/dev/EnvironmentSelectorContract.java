package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorContract {
    interface View {

    }

    interface Interactor {
        void attemptMutualAuth(boolean isMutualAuthEnabled);

        void hardcodedUser(String username, String password);

        void envAmWellSelected(EnviHandler.AmWellEnvType amWellEnvType);

        void envMyHomeSelected(EnviHandler.EnvType envType);

        void finishedSelecting(EnviHandler.EnvType envType);
    }

    interface Presentor extends BasePresenter {
        void onFinishedSelecting(int environmentId, int amwellEnvironmentId, boolean isMutualAuthEnabled, String username, String password);
    }

    interface Router {
        void dismissEnvironmentPicker();
    }
}
