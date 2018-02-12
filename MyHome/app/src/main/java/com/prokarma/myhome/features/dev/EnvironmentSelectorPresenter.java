package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorPresenter implements EnvironmentSelectorContract.Presentor {
    private Context context;
    private EnvironmentSelectorView view;
    private EnvironmentSelectorInteractor interactor;
    private EnvironmentSelectorRouter router;
    private EnvironmentSelectorInterface environmentSelectorInterface;

    public EnvironmentSelectorPresenter(Context context, DialogFragment fragment, View view, EnvironmentSelectorInterface environmentSelectorInterface) {
        this.context = context;
        this.view = new EnvironmentSelectorView(view, this);
        this.interactor = new EnvironmentSelectorInteractor();
        this.router = new EnvironmentSelectorRouter(fragment);
        this.environmentSelectorInterface = environmentSelectorInterface;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        environmentSelectorInterface = null;
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onFinishedSelecting(int environmentId, int amwellEnvironmentId, boolean isMutualAuthEnabled, String username, String password) {
        if (environmentId == -1 || amwellEnvironmentId == -1) {
            CommonUtil.showToast(context, context.getString(R.string.you_must_chose_an_environment));
            return;
        }

        if (!isMutualAuthEnabled) {
            if ((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
                CommonUtil.showToast(context, context.getString(R.string.please_provide_user_for_AmWell));
                return;
            }
        }

        if (environmentSelectorInterface != null) {
            environmentSelectorInterface.attemptMutualAuth(isMutualAuthEnabled);
            environmentSelectorInterface.hardcodedUser(username, password);

            switch (amwellEnvironmentId) {
                case R.id.radio_amwell_dev:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.DEV);
                    break;
                case R.id.radio_amwell_stage:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.STAGE);
                    break;
                case R.id.radio_amwell_iot:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.IOT);
                    break;
                case R.id.radio_amwell_prod:
                    environmentSelectorInterface.envAmWellSelected(EnviHandler.AmWellEnvType.PROD);
                    break;
            }

            switch (environmentId) {
                case R.id.radio_demo:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.DEMO);
                    break;
                case R.id.radio_dev:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.DEV);
                    break;
                case R.id.radio_test:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.TEST);
                    break;
                case R.id.radio_slot1:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.SLOT1);
                    break;
                case R.id.radio_stage:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.STAGE);
                    break;
                case R.id.radio_prod:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.PROD);
                    break;
                default:
                    environmentSelectorInterface.envMyHomeSelected(EnviHandler.EnvType.PROD);
                    break;
            }

            router.dismissEnvironmentPicker();
        }
    }
}
