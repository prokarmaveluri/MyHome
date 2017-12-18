package com.prokarma.myhome.features.profile;

/**
 * Created by kwelsh on 12/18/17.
 */

public interface ProfileUpdateInterface {
    void profileUpdateComplete(Profile profile);

    void profileUpdateFailed(String errorMessage);
}
