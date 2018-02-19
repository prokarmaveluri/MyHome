package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorInteractor implements EnvironmentSelectorContract.Interactor {
    private final Context context;

    public EnvironmentSelectorInteractor(Context context) {
        this.context = context;
    }

    @Override
    public void attemptMutualAuth(boolean isMutualAuthEnabled) {
        EnviHandler.setAttemptMutualAuth(isMutualAuthEnabled);
    }

    @Override
    public void hardcodedUser(String username, String password) {
        EnviHandler.setAmwellUsername(username);
        EnviHandler.setAmwellPassword(password);
    }

    @Override
    public void envAmWellSelected(EnviHandler.AmWellEnvType amWellEnvType) {
        switch (amWellEnvType) {
            case DEV:
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.DEV);
                break;
            case STAGE:
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.STAGE);
                break;
            case IOT:
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.IOT);
                break;
            case PROD:
                EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.PROD);
                break;
        }
    }

    @Override
    public void envMyHomeSelected(EnviHandler.EnvType envType) {
        switch (envType) {
            case DEMO:
                EnviHandler.initEnv(EnviHandler.EnvType.DEMO);
                break;
            case DEV:
                EnviHandler.initEnv(EnviHandler.EnvType.DEV);
                break;
            case TEST:
                EnviHandler.initEnv(EnviHandler.EnvType.TEST);
                break;
            case SLOT1:
                EnviHandler.initEnv(EnviHandler.EnvType.SLOT1);
                break;
            case STAGE:
                EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
                break;
            case PROD:
                EnviHandler.initEnv(EnviHandler.EnvType.PROD);
                break;
        }
    }

    @Override
    public void finishedSelecting(EnviHandler.EnvType envType) {
        Intent intent = new Intent(EnvironmentSelectorFragment.ENVIRONMENT_BROADCAST_RECEIVER_INTENT);
        intent.putExtra(EnvironmentSelectorFragment.INTENT_EXTRA_ENVIRONMENT, envType);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
