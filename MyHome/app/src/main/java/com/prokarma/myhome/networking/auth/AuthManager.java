package com.prokarma.myhome.networking.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.features.dev.DeveloperFragment;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.login.endpoint.AmWellResponse;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.AppPreferences;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 5/1/17.
 */

@SuppressWarnings("unused")
public class AuthManager {

    private static String expiresAt;
    private static Integer expiresIn;
    private static String bearerToken;
    private static String refreshToken;
    private static String sessionToken;
    private static String sessionId;
    private static String sid;
    private static String amWellToken;
    private static boolean hasMyCare = false;

    private static long idleTime;
    private static int count = 0;
    private static Context context;

    private static long prevTimestamp = 0;
    private static long MINUTES_5 = 5 * 60 * 1000;
    public static long SESSION_EXPIRY_TIME = 10 * 24 * 60 * 60 * 1000;

    private static final int MAX_RETRIES_BEFORE_LOCKING_USER = 3;

    private static final AuthManager ourInstance = new AuthManager();

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        AuthManager.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        AuthManager.refreshToken = refreshToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        AuthManager.expiresAt = expiresAt;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        AuthManager.idleTime = idleTime;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(@Nullable String sid) {
        AuthManager.sid = sid;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        AuthManager.context = context;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        AuthManager.count = count;
    }

    public long getPrevTimestamp() {
        return prevTimestamp;
    }

    public void setPrevTimestamp(long prevTimestamp) {
        AuthManager.prevTimestamp = prevTimestamp;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(@Nullable String bearerToken) {
        AuthManager.bearerToken = bearerToken;
    }

    public static void setAmWellToken(String amWellToken) {
        AuthManager.amWellToken = amWellToken;
    }

    public static String getAmWellToken() {
        return amWellToken;
    }

    public static boolean hasMyCare() {
        return hasMyCare;
    }

    public static void setHasMyCare(boolean hasMyCare) {
        AuthManager.hasMyCare = hasMyCare;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(@Nullable String sessionToken) {
        AuthManager.sessionToken = sessionToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable String sessionId) {
        AuthManager.sessionId = sessionId;
    }

    public void setFailureAttempt() {
        if (System.currentTimeMillis() - prevTimestamp >= MINUTES_5) {
            prevTimestamp = System.currentTimeMillis();
            count = 1;
        } else {
            count++;
        }
        storeLockoutInfo();
    }

    public boolean isTimeStampGreaterThan5Mins() {
        return (System.currentTimeMillis() - prevTimestamp >= MINUTES_5);
    }

    /**
     * If the user tries to log in more than three times unsuccessfully, lock him out.
     * Developers are excluded from this.
     *
     * @return
     */
    public boolean isMaxFailureAttemptsReached() {
        return count >= MAX_RETRIES_BEFORE_LOCKING_USER && !BuildConfig.BUILD_TYPE.equalsIgnoreCase(DeveloperFragment.DEVELOPER);
    }

    public void storeLockoutInfo() {
        AppPreferences.getInstance().setPreference(LoginActivity.FAILURE_COUNT, getCount());
        AppPreferences.getInstance().setLongPreference(LoginActivity.FAILURE_TIME_STAMP,
                getPrevTimestamp());
    }

    public void fetchLockoutInfo() {
        count = AppPreferences.getInstance().getIntPreference(LoginActivity.FAILURE_COUNT);
        prevTimestamp = AppPreferences.getInstance().getLongPreference(LoginActivity.FAILURE_TIME_STAMP);
    }

    public boolean isExpired() {
        try {
            long fetchTime = AppPreferences.getInstance().getLongPreference("FETCH_TIME");
            long current = System.currentTimeMillis();

            // already expired
            if ((current - fetchTime) > (AuthManager.getInstance().getExpiresIn() * 1000)) {
                getHandler().sendEmptyMessage(0);
            }
            return false;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public void refreshToken() {
        NetworkManager.getInstance().signInRefresh(new RefreshRequest(
                AuthManager.getInstance().getRefreshToken())).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getValid()) {
                    try {
                        Timber.d("Successful Response\n" + response);
                        AppPreferences.getInstance().setLongPreference("FETCH_TIME", System.currentTimeMillis());
//                        AuthManager.getInstance().setExpiresIn(response.body().getExpiresIn());
                        AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                        AuthManager.getInstance().setRefreshToken(response.body().getResult().getRefreshToken());
                        CryptoManager.getInstance().saveToken();

                        getUsersAmWellToken();
                    } catch (NullPointerException ex) {
                        Timber.e(ex);
                        ex.printStackTrace();
                    }
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    public void getUsersAmWellToken() {
        if (bearerToken != null) {
            checkMyCareEligibility();
            NetworkManager.getInstance().getAmWellToken(bearerToken).enqueue(new Callback<AmWellResponse>() {
                @Override
                public void onResponse(Call<AmWellResponse> call, Response<AmWellResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getValid()) {
                        Timber.d("Successful Response\n" + response);
                        AuthManager.getInstance().setAmWellToken(response.body().result);
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        AuthManager.getInstance().setAmWellToken(null);
                    }
                }

                @Override
                public void onFailure(Call<AmWellResponse> call, Throwable t) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    AuthManager.getInstance().setAmWellToken(null);
                }
            });
        }
    }

    private void checkMyCareEligibility() {
        try {
            Log.d(this.getClass().getSimpleName(), "JWT bearerToken " + bearerToken);

            JWT jwt = new JWT(bearerToken);

            Log.d(this.getClass().getSimpleName(), "JWT subject = " + jwt.getSubject());

            Claim claim = jwt.getClaim("groups");
            List<String> groups = claim.asList(String.class);
            if (groups != null && groups.contains("Telehealth Users")) {
                setHasMyCare(true);
            }
            Log.d(this.getClass().getSimpleName(), "JWT groups = " + groups.toString());
            Log.d(this.getClass().getSimpleName(), "JWT hasMyCare = " + hasMyCare());

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    private static class AuthHandler extends Handler {
        private final WeakReference<AuthManager> mAuthManager;

        private AuthHandler(AuthManager authManager) {
            mAuthManager = new WeakReference<AuthManager>(authManager);
        }

        @Override
        public void handleMessage(Message msg) {
            AuthManager authManager = mAuthManager.get();
            if (authManager != null) {
                authManager.refreshToken();
            }
        }
    }

    private Handler getHandler() {
        return new AuthHandler(this);
    }
}
