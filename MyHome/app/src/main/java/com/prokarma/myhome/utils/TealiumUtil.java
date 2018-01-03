package com.prokarma.myhome.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.tealium.internal.data.Dispatch;
import com.tealium.internal.listeners.WebViewCreatedListener;
import com.tealium.internal.listeners.WebViewLoadedListener;
import com.tealium.internal.tagbridge.RemoteCommand;
import com.tealium.library.DispatchValidator;
import com.tealium.library.Tealium;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by kwelsh on 6/1/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public final class TealiumUtil {
    private final static String ACCOUNT_NAME = "dignityhealth";
    private final static String PROFILE_NAME = "myhomeapp";
    private final static String KEY_TEALIUM_INIT_COUNT = "tealium_init_count";
    private final static String KEY_TEALIUM_INITIALIZED = "tealium_initialized";
    private final static String KEY_TEALIUM_APP_NAME = "appName";
    private final static String KEY_TEALIUM_APP_ID = "appId";
    private final static String KEY_TEALIUM_APP_VERSION = "appVersion";
    private final static String KEY_TEALIUM_SCREEN_NAME = "screenName";
    private final static String KEY_TEALIUM_DHOME_ID = "dhomeId";
    private final static String KEY_TEALIUM_USER_STATE = "userState";

    // Identifier for the main Tealium instance
    public static final String TEALIUM_MAIN = UUID.randomUUID().toString();

    static {
        Timber.i(" --- START TEALIUM --- ");
    }

    // Not instantiatable.
    private TealiumUtil() {
    }

    public static void initialize(Application application) {
        Timber.i("initialize(" + application.getClass().getSimpleName() + ")");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        final Tealium.Config config = Tealium.Config.create(
                application,
                ACCOUNT_NAME,
                PROFILE_NAME,
                BuildConfig.TEALIUM_ENVIRONMENT);

        // (OPTIONAL) Get the WebView with UTag loaded
        config.getEventListeners().add(createWebViewLoadedListener());

        // (OPTIONAL) Control how the library treats views/links
        config.getDispatchValidators().add(createDispatchValidator());

        final Tealium instance = Tealium.createInstance(TEALIUM_MAIN, config);

        // (OPTIONAL) Enhanced integrations
        instance.addRemoteCommand(createLoggerRemoteCommand());

        // (OPTIONAL) Use tealium.getDataSources().getPersistentDataSources() to set/modify lifetime values
        SharedPreferences sp = instance.getDataSources().getPersistentDataSources();
        sp.edit().putInt(KEY_TEALIUM_INIT_COUNT, sp.getInt(KEY_TEALIUM_INIT_COUNT, 0) + 1).apply();

        // (OPTIONAL) Use tealium.getDataSources().getVolatileDataSources() to set/modify runtime only values
        instance.getDataSources().getVolatileDataSources()
                .put(KEY_TEALIUM_INITIALIZED, System.currentTimeMillis());

        // (OPTIONAL) tracking initialization
        final Map<String, Object> data = new HashMap<>(2);
        data.put("logged_in", false);
        data.put("visitor_status", new String[]{"new_user", "unregistered"});
        TealiumUtil.trackEvent("initialization", data);
    }

    public static void trackView(String screenName, @Nullable Map<String, ?> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_MAIN);
        final Profile userProfile = ProfileManager.getProfile();

        // Instance can be remotely destroyed through publish settings
        if (instance != null) {
            Map<String, Object> tealiumData = new HashMap<>();
            tealiumData.put(KEY_TEALIUM_APP_NAME, BuildConfig.TEALIUM_APP_NAME);
            tealiumData.put(KEY_TEALIUM_APP_ID, "my-home-app-digital-home");
            tealiumData.put(KEY_TEALIUM_APP_VERSION, BuildConfig.VERSION_NAME);
            tealiumData.put(KEY_TEALIUM_SCREEN_NAME, screenName);
            tealiumData.put(KEY_TEALIUM_DHOME_ID, userProfile != null ? userProfile.userId : "Unknown");
            tealiumData.put(KEY_TEALIUM_USER_STATE, userProfile != null ? userProfile.idLevel : "Unknown");

            if (data != null) {
                tealiumData.putAll(data);
            }

            Timber.d("trackView " + screenName + "\n" + tealiumData);
            instance.trackView(screenName, tealiumData);
        }
    }

    public static void trackEvent(String eventName, @Nullable Map<String, ?> data) {
        final Tealium instance = Tealium.getInstance(TEALIUM_MAIN);
        final Profile userProfile = ProfileManager.getProfile();

        // Instance can be remotely destroyed through publish settings
        if (instance != null) {

            Map<String, Object> tealiumData = new HashMap<>();
            tealiumData.put(KEY_TEALIUM_APP_NAME, BuildConfig.TEALIUM_APP_NAME);
            tealiumData.put(KEY_TEALIUM_APP_ID, "my-home-app-digital-home");
            tealiumData.put(KEY_TEALIUM_APP_VERSION, BuildConfig.VERSION_NAME);
            tealiumData.put(KEY_TEALIUM_SCREEN_NAME, Constants.HOME_SCREEN);    //TODO KEVIN - This doesn't make sense, but the document has this value never changing...
            tealiumData.put(KEY_TEALIUM_DHOME_ID, userProfile != null ? userProfile.userId : "Unknown");
            tealiumData.put(KEY_TEALIUM_USER_STATE, userProfile != null ? userProfile.idLevel : "Unknown");

            if (data != null) {
                tealiumData.putAll(data);
            }

            instance.trackEvent(eventName, data);
        }
    }

    private static WebViewCreatedListener createCookieEnablerListener() {
        return new WebViewCreatedListener() {
            @Override
            public void onWebViewCreated(WebView webView) {
                final CookieManager mgr = CookieManager.getInstance();

                // Accept all cookies
                mgr.setAcceptCookie(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mgr.setAcceptThirdPartyCookies(webView, true);
                }

                CookieManager.setAcceptFileSchemeCookies(true);

                Timber.d("WebView " + webView + " created and cookies enabled.");
            }

            @Override
            public String toString() {
                return "EnableCookieWebViewCreatedListener";
            }
        };
    }

    private static WebViewLoadedListener createWebViewLoadedListener() {
        return new WebViewLoadedListener() {
            @Override
            public void onWebViewLoad(WebView webView, boolean success) {
                Timber.d("WebView " + webView + (success ? " loaded successfully" : "failed to load"));
            }

            @Override
            public String toString() {
                return "LoggingWebViewLoadListener";
            }
        };
    }

    private static DispatchValidator createDispatchValidator() {
        return new DispatchValidator() {
            @Override
            protected boolean shouldDrop(Dispatch dispatch) {
                // Drop any desired dispatches here by returning true. (Never queued nor sent)
                return super.shouldDrop(dispatch);
            }

            @Override
            protected boolean shouldQueue(Dispatch dispatch, boolean shouldQueue) {
                Timber.d(String.format(
                        Locale.ROOT,
                        "%s dispatch: %s",
                        (shouldQueue ? "Queueing" : "Sending"),
                        dispatch));

                return super.shouldQueue(dispatch, shouldQueue);
            }

            @Override
            public String toString() {
                return "CustomDispatchValidator";
            }
        };
    }

    private static RemoteCommand createLoggerRemoteCommand() {
        return new RemoteCommand("logger", "Logs dispatches") {
            @Override
            protected void onInvoke(Response response) throws Exception {
                final String message = response.getRequestPayload()
                        .optString("message", "no_message");
                Timber.i("RemoteCommand Message: " + message);
            }

            @Override
            public String toString() {
                return "LoggerRemoteCommand";
            }
        };
    }
}
