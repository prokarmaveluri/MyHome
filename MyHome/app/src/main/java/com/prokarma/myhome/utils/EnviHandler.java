package com.prokarma.myhome.utils;

import com.prokarma.myhome.BuildConfig;

/**
 * Created by cmajji on 9/19/17.
 */

public class EnviHandler {

    public enum EnvType {
        DEV,
        STAGE,
        PROD,
        TEST,
        NONE
    }

    public static String VERSIONING_URL;
    public static String OKTA_BASE_URL;
    public static String CIAM_BASE_URL;
    public static String S2_BASE_URL;
    public static String SCHEDULING_BASE;

    public static String AUTH_CLIENT_ID;
    public static String CLIENT_ID;
    public static String AUTH_SCOPE;
    public static String AUTH_REDIRECT_URI;
    public static String GRANT_TYPE_AUTH;
    public static String GRANT_TYPE_REFRESH;

    public static void initEnv(EnvType type) {

        switch (type) {
            case DEV:
                initDev();
                break;
            case STAGE:
                initStage();
                break;
            case TEST:
                break;
            case PROD:
                initProd();
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

        AUTH_CLIENT_ID = BuildConfig.D_AUTH_CLIENT_ID;
        CLIENT_ID = BuildConfig.D_CLIENT_ID;
        AUTH_SCOPE = BuildConfig.D_AUTH_SCOPE;
        AUTH_REDIRECT_URI = BuildConfig.D_AUTH_REDIRECT_URI;
        GRANT_TYPE_AUTH = BuildConfig.D_GRANT_TYPE_AUTH;
        GRANT_TYPE_REFRESH = BuildConfig.D_GRANT_TYPE_REFRESH;
    }

    private static void initStage() {

        VERSIONING_URL = BuildConfig.S_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.S_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.S_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.S_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.S_SCHEDULING_BASE;

        AUTH_CLIENT_ID = BuildConfig.S_AUTH_CLIENT_ID;
        CLIENT_ID = BuildConfig.S_CLIENT_ID;
        AUTH_SCOPE = BuildConfig.S_AUTH_SCOPE;
        AUTH_REDIRECT_URI = BuildConfig.S_AUTH_REDIRECT_URI;
        GRANT_TYPE_AUTH = BuildConfig.S_GRANT_TYPE_AUTH;
        GRANT_TYPE_REFRESH = BuildConfig.S_GRANT_TYPE_REFRESH;
    }

    private void initTest() {

//        VERSIONING_URL = BuildConfig.D;
//        OKTA_BASE_URL = BuildConfig.OKTA_BASE_URL;
//        CIAM_BASE_URL = BuildConfig.CIAM_BASE_URL;
//        S2_BASE_URL = BuildConfig.S2_BASE_URL;
//        SCHEDULING_BASE = BuildConfig.SCHEDULING_BASE;
//
//
//        AUTH_CLIENT_ID = BuildConfig.D_AUTH_CLIENT_ID;
//        CLIENT_ID = BuildConfig.D_CLIENT_ID;
//        AUTH_SCOPE = BuildConfig.D_AUTH_SCOPE;
//        AUTH_REDIRECT_URI = BuildConfig.D_AUTH_REDIRECT_URI;
//        GRANT_TYPE_AUTH = BuildConfig.D_GRANT_TYPE_AUTH;
//        GRANT_TYPE_REFRESH = BuildConfig.D_GRANT_TYPE_REFRESH;
    }

    private static void initProd() {

        VERSIONING_URL = BuildConfig.P_VERSIONING_URL;
        OKTA_BASE_URL = BuildConfig.P_OKTA_BASE_URL;
        CIAM_BASE_URL = BuildConfig.P_CIAM_BASE_URL;
        S2_BASE_URL = BuildConfig.P_S2_BASE_URL;
        SCHEDULING_BASE = BuildConfig.P_SCHEDULING_BASE;

        AUTH_CLIENT_ID = BuildConfig.P_AUTH_CLIENT_ID;
        CLIENT_ID = BuildConfig.P_CLIENT_ID;
        AUTH_SCOPE = BuildConfig.P_AUTH_SCOPE;
        AUTH_REDIRECT_URI = BuildConfig.P_AUTH_REDIRECT_URI;
        GRANT_TYPE_AUTH = BuildConfig.P_GRANT_TYPE_AUTH;
        GRANT_TYPE_REFRESH = BuildConfig.P_GRANT_TYPE_REFRESH;
    }
}
