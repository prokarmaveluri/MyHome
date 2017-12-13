package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.utils.EnviHandler;

/**
 * Created by kwelsh on 12/13/17.
 */

public interface EnvironmentSelectorInterface {
    void envAmWellSelected(EnviHandler.AmWellEnvType amWellType);

    void envMyHomeSelected(EnviHandler.EnvType envType);
}
