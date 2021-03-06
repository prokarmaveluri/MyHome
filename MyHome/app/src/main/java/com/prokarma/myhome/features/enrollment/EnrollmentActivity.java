package com.prokarma.myhome.features.enrollment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.databinding.ActivityEnrollmentBinding;

/*
 * Activity to enroll.
 *
 * Created by cmajji on 4/26/17.
 */
public class EnrollmentActivity extends BaseActivity {

    private ActivityEnrollmentBinding binding;

    /*
     * Get an intent for enrollment activity.
     */
    public static Intent getEnrollmentIntent(Context context) {

        return new Intent(context, EnrollmentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment);

        EnrollmentFragment fragment = EnrollmentFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(binding.enrollFrame.getId(),
                fragment).commit();

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new EnrollmentPresenter(fragment, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
