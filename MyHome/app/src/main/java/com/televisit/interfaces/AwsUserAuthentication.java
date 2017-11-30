package com.televisit.interfaces;

import com.americanwell.sdk.entity.Authentication;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsUserAuthentication {
    void authenticationComplete(Authentication authentication);

    void authentciationFailed(String errorMessage);
}
