package com.prokarma.myhome.features.tos;

import android.app.Activity;
import android.content.Intent;

import com.prokarma.myhome.app.SplashActivity;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosRouter implements TosContract.Router {
    Activity activity;

    public TosRouter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void finishTos() {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void startLoginPage(String userName, String password) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra("ENROLL_SUCCESS", true);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("PASSWORD", password);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

}
