package com.prokarma.myhome.features.dev;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.OptionsActivity;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 2/1/18.
 */

public class DevRouter implements DevContract.Router {
    final Activity activity;

    public DevRouter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void goToApiScreen() {
        NavigationActivity.setActivityTag(Constants.ActivityTag.API);
        Intent intentDeveloper = new Intent(activity, OptionsActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.slide_in_right, R.anim.slide_out_left);
        ActivityCompat.startActivity(activity, intentDeveloper, options.toBundle());
    }
}
