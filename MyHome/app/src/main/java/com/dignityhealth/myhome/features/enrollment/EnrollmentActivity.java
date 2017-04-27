package com.dignityhealth.myhome.features.enrollment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityEnrollmentBinding;

public class EnrollmentActivity extends AppCompatActivity {

    private ActivityEnrollmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment);

        EnrollmentFragment fragment = EnrollmentFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(binding.enrollFrame.getId(),
                fragment).commit();

        new EnrollmentPresenter(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
