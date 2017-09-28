package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by kwelsh on 9/28/17.
 * Tests for DignityHealth APIs
 */

public class ProfileTest {

    @Before
    public void setup() {

    }

    @Test
    public void getProfile_Dev() {
        SignInRequest loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
        EnviHandler.initEnv(EnviHandler.EnvType.DEV);
        NetworkManager.getInstance().initService();

        TestUtil.getProfile(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void getProfile_Stage() {
        SignInRequest loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
        EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
        NetworkManager.getInstance().initService();

        TestUtil.getProfile(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void getProfile_Prod() {
        SignInRequest loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
        EnviHandler.initEnv(EnviHandler.EnvType.PROD);
        NetworkManager.getInstance().initService();

        TestUtil.getProfile(TestUtil.getLogin(loginRequest));
    }

}
