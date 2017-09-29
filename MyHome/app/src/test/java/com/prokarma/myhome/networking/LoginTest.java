package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by kwelsh on 5/4/17.
 * Tests for DignityHealth APIs
 */

public class LoginTest {

    @Before
    public void setup() {

    }

    @Test
    public void getLogin_Dev() {
        TestUtil.setDevEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
        TestUtil.getLogin(loginRequest);
    }

//    @Test
//    public void getLogin_Test() {
//        SignInRequest loginRequest = new SignInRequest("sam@mailinator.com", "Ap29bx1442@");
//        EnviHandler.initEnv(EnviHandler.EnvType.TEST);
//        NetworkManager.getInstance().initService();
//        getLogin(loginRequest);
//    }

    @Test
    public void getLogin_Stage() {
        TestUtil.setStagingEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
        TestUtil.getLogin(loginRequest);
    }

    @Test
    public void getLogin_Prod() {
        TestUtil.setProdEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
        TestUtil.getLogin(loginRequest);
    }
}
