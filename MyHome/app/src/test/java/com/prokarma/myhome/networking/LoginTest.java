package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Created by kwelsh on 5/4/17.
 * Tests for DignityHealth APIs
 */

@RunWith(Parameterized.class)
public class LoginTest {
    @Parameterized.Parameter
    public EnviHandler.EnvType environment;

    @Parameterized.Parameters
    public static Object[] environments() {
        return new Object[]{
                EnviHandler.EnvType.DEV,
                EnviHandler.EnvType.STAGE,
                EnviHandler.EnvType.PROD,
        };
    }

    @Rule
    public Timeout glocalTimeout = Timeout.seconds(20);

    @Test
    public void getLogin() {
        SignInRequest loginRequest = null;

        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
                break;
        }

        TestUtil.getLogin(loginRequest);
    }
}
