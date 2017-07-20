package com.prokarma.myhome.utils;

/**
 * Created by cmajji on 4/26/17.
 */

public class RESTConstants {


    //TEST
    public static final String auth2Url = "oauth2/v1/authorize?client_id=NtFXqaF8iMnVbfdtJfsF&redirect_uri=https%3A%2F%2Fdev.ciam.dignityhealth.org%2Fwidgets%2Fenrollment%2Fdist%2F&response_type=id_token&response_mode=fragment&state=%7B%22returnUrl%22%3A%22foobar%22%2C%22step%22%3A%22enrollment%22%2C%22stepProgress%22%3A%22confirmation%22%7D&nonce=o4VJePCJeaAE17PKtyHpXZK5xwIhvgNpOzaq80hucpd6DTYELlD8jt46D2Ex2WRu&scope=openid%20email%20profile%20phone%20groups&sessionToken=";

    public static final String FETCH_CODE = "oauth2/ausb2b0jbri7MsQGl0h7/v1/authorize?response_type=code&client_id=0oab54ai6q0zFI8fq0h7&redirect_uri=http://localhost:8080&response_mode=fragment&scope=email+profile+groups+offline_access+openid&state=foo&nonce=bar&code_challenge=%s&code_challenge_method=S256&sessionToken=%s";

    public static final String VERSIONING_DEV_URL = "http://web-usw1-versioningservices-dev.azurewebsites.net/";

    public static final String VERSIONING_URL = "http://web-usw1-versioningservices-tst.azurewebsites.net/";

    public static final String OKTA_BASE_URL = "https://dignityhealth.oktapreview.com/";
    public static final String CIAM_BASE_URL = "https://test.ciam.dignityhealth.org/";
    public static final String S2_BASE_URL = "https://web-usw1-fad-tst.azurewebsites.net/";
    public static final String SCHEDULING_BASE = "https://web-usw1-schedulingapi-tst.azurewebsites.net/";


    //PROD
//    public static final String VERSIONING_URL = "web-usw1-versioningservices-prd.azurewebsites.net/";
//    public static final String S2_BASE_URL = "https://e3findadocapp.azurewebsites.net/";
//    public static final String SCHEDULING_BASE = "https://web-usw1-schedulingapi-prd.azurewebsites.net/";
//    public static final String CIAM_BASE_URL = "https://prod.ciam.dignityhealth.org";


    public static final String SCHEDULING_VISIT = "api/v1/visit/";
    public static final String SCHEDULING_VALIDATION = "api/v1/visit/schedule/{scheduleID}/visit-settings";

    public static final String PROVIDER_PAGE_NO = "1";
    public static final String PROVIDER_PAGE_SIZE = "25";
    public static final String PROVIDER_DISTANCE = "100";


    public static final String CLIENT_ID = "0oab54ai6q0zFI8fq0h7";
    public static final String AUTH_SCOPE = "openid+email+profile+offline_access+groups";
    public static final String AUTH_REDIRECT_URI = "http://localhost:8080";
    public static final String GRANT_TYPE_AUTH = "authorization_code";
    public static final String GRANT_TYPE_REFRESH = "refresh_token";

}