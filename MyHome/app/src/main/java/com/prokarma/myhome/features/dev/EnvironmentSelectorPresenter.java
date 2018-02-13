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

    public EnvironmentSelectorPresenter(Context context, DialogFragment fragment, View view) {
        this.context = context;
        this.view = new EnvironmentSelectorView(view, this);
        this.interactor = new EnvironmentSelectorInteractor(context);
        this.router = new EnvironmentSelectorRouter(fragment);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
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

        interactor.attemptMutualAuth(isMutualAuthEnabled);
        interactor.hardcodedUser(username, password);

        switch (amwellEnvironmentId) {
            case R.id.radio_amwell_dev:
                interactor.envAmWellSelected(EnviHandler.AmWellEnvType.DEV);
                break;
            case R.id.radio_amwell_stage:
                interactor.envAmWellSelected(EnviHandler.AmWellEnvType.STAGE);
                break;
            case R.id.radio_amwell_iot:
                interactor.envAmWellSelected(EnviHandler.AmWellEnvType.IOT);
                break;
            case R.id.radio_amwell_prod:
                interactor.envAmWellSelected(EnviHandler.AmWellEnvType.PROD);
                break;
        }

        switch (environmentId) {
            case R.id.radio_demo:
                interactor.envMyHomeSelected(EnviHandler.EnvType.DEMO);
                interactor.finishedSelecting(EnviHandler.EnvType.DEMO);
                break;
            case R.id.radio_dev:
                interactor.envMyHomeSelected(EnviHandler.EnvType.DEV);
                interactor.finishedSelecting(EnviHandler.EnvType.DEV);
                break;
            case R.id.radio_test:
                interactor.envMyHomeSelected(EnviHandler.EnvType.TEST);
                interactor.finishedSelecting(EnviHandler.EnvType.TEST);
                break;
            case R.id.radio_slot1:
                interactor.envMyHomeSelected(EnviHandler.EnvType.SLOT1);
                interactor.finishedSelecting(EnviHandler.EnvType.SLOT1);
                break;
            case R.id.radio_stage:
                interactor.envMyHomeSelected(EnviHandler.EnvType.STAGE);
                interactor.finishedSelecting(EnviHandler.EnvType.STAGE);
                break;
            case R.id.radio_prod:
                interactor.envMyHomeSelected(EnviHandler.EnvType.PROD);
                interactor.finishedSelecting(EnviHandler.EnvType.PROD);
                break;
            default:
                interactor.envMyHomeSelected(EnviHandler.EnvType.PROD);
                interactor.finishedSelecting(EnviHandler.EnvType.PROD);
                break;
        }

        router.dismissEnvironmentPicker();
    }
}
