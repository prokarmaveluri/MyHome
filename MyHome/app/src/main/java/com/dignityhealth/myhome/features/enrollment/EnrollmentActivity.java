package com.dignityhealth.myhome.features.enrollment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityEnrollmentBinding;

/*
 * Activity to enroll.
 *
 * Created by cmajji on 4/26/17.
 */
public class EnrollmentActivity extends AppCompatActivity {

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
