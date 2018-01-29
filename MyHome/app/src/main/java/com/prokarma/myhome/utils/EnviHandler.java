package com.prokarma.myhome.utils;

import com.prokarma.myhome.BuildConfig;

/**
 * Created by cmajji on 9/19/17.
 */

public class EnviHandler {

    public enum EnvType {
        DEV,
        SLOT1,
        STAGE,
        PROD,
        TEST,
        NONE;

        public static EnvType toEnvType(String enumString) {
            try {
                return valueOf(enumString);
            } catch (Exception ex) {
                // For error cases
                return NONE;
            }
        }
    }

    public enum AmWellEnvType {
        DEV,
        STAGE,
        IOT,
        PROD,
        NONE;

        public static AmWellEnvType toAmWellEnvType(String enumString) {
            try {
                return valueOf(enumString);
            } catch (Exception ex) {
                // For error cases
                return NONE;
            }
        }
    }

    public static String VERSIONING_URL;
    public static String OKTA_BASE_URL;
    public static String CIAM_BASE_URL;
    public static String S2_BASE_URL;
    public static String SCHEDULING_BASE;

    public static String AWSDK_KEY;
    public static String AWSDK_URL;

    public static boolean ATTEMPT_MUTUAL_AUTH;
    public static String AMWELL_USERNAME;
    public static String AMWELL_PASSWORD;

    public static void initEnv(EnvType type) {
        switch (type) {
            case DEV:
                initDev();
                break;
            case SLOT1:
                initSlot1();
                break;
            case STAGE:
                initStage();
                break;
            case TEST:
                initTest();
                break;
            case PROD:
                initProd();
                break;
            default:
                break;
        }
    }

    public static void initAmWellEnv(AmWellEnvType type) {
        switch (type) {
            case DEV:
                initAmWellDev();
                break;
            case STAGE:
                initAmWellStage();
                break;
            case IOT:
                initAmWellIot();
                break;
            case PROD:
                initAmWellProd();
                break;
            default:
                break;
        }
    }

    private static void initDev() {

        VERSIONING_URL = BuildConfig.D_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.D_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.D_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.D_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.D_SCHEDULING_BASE;
    }

    private static void initSlot1() {

        VERSIONING_URL = BuildConfig.S1_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.S1_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.S1_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.S1_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.S1_SCHEDULING_BASE;
    }

    private static void initStage() {

        VERSIONING_URL = BuildConfig.S_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.S_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.S_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.S_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.S_SCHEDULING_BASE;
    }

    private static void initTest() {

        VERSIONING_URL = BuildConfig.T_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.T_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.T_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.T_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.T_SCHEDULING_BASE;
    }

    private static void initProd() {

        VERSIONING_URL = BuildConfig.P_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.P_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.P_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.P_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.P_SCHEDULING_BASE;
    }

    private static void initAmWellDev() {
        AWSDK_KEY = BuildConfig.D_AWSDK_KEY;
        AWSDK_URL = BuildConfig.D_AWSDK_URL;
    }

    private static void initAmWellStage() {
        AWSDK_KEY = BuildConfig.S_AWSDK_KEY;
        AWSDK_URL = BuildConfig.S_AWSDK_URL;
    }

    private static void initAmWellIot() {
        AWSDK_KEY = BuildConfig.I_AWSDK_KEY;
        AWSDK_URL = BuildConfig.I_AWSDK_URL;
    }

    private static void initAmWellProd() {
        AWSDK_KEY = BuildConfig.P_AWSDK_KEY;
        AWSDK_URL = BuildConfig.P_AWSDK_URL;
    }

    public static boolean isAttemptMutualAuth() {
        return ATTEMPT_MUTUAL_AUTH;
    }

    public static void setAttemptMutualAuth(boolean attemptMutualAuth) {
        ATTEMPT_MUTUAL_AUTH = attemptMutualAuth;
    }

    public static String getAmwellUsername() {
        return AMWELL_USERNAME;
    }

    public static void setAmwellUsername(String amwellUsername) {
        AMWELL_USERNAME = amwellUsername;
    }

    public static String getAmwellPassword() {
        return AMWELL_PASSWORD;
    }

    public static void setAmwellPassword(String amwellPassword) {
        AMWELL_PASSWORD = amwellPassword;
    }
}
