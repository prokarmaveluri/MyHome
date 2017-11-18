package com.prokarma.myhome.networking.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.crypto.CryptoManager;
import com.prokarma.myhome.features.dev.DeveloperFragment;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.AppPreferences;

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

    private static long idleTime;
    private static int count = 0;
    private static Context context;

    private static long prevTimestamp = 0;
    private static long MINITUES_5 = 5 * 60 * 1000;
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
        if (System.currentTimeMillis() - prevTimestamp >= MINITUES_5) {
            prevTimestamp = System.currentTimeMillis();
            count = 1;
        } else {
            count++;
        }
        storeLockoutInfo();
    }

    public boolean isTimeStampGreaterThan5Mins() {
        return (System.currentTimeMillis() - prevTimestamp >= MINITUES_5);
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
                mHandler.sendEmptyMessage(0);
            }
            return false;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshToken();
        }
    };

    public void refreshToken() {
        NetworkManager.getInstance().signInRefresh(new RefreshRequest(
                AuthManager.getInstance().getRefreshToken())).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful() && response.body().getValid()) {
                    try {
                        AppPreferences.getInstance().setLongPreference("FETCH_TIME", System.currentTimeMillis());
//                        AuthManager.getInstance().setExpiresIn(response.body().getExpiresIn());
                        AuthManager.getInstance().setBearerToken(response.body().getResult().getAccessToken());
                        AuthManager.getInstance().setRefreshToken(response.body().getResult().getRefreshToken());
                        CryptoManager.getInstance().saveToken();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Timber.i("onFailure : ");
            }
        });
    }
}
