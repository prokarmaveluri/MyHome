package com.dignityhealth.myhome.features.enrollment.sq;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivitySecurityQuestionBinding;

public class SQActivity extends AppCompatActivity {

    private ActivitySecurityQuestionBinding binding;

    /*
     * Get an intent for SQActivity activity.
     */
    public static Intent getSQActivityIntent(Context context) {

        return new Intent(context, SQActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_security_question);
        transactSQFragment();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private SQFragment getFragment() {

        SQFragment fragment = SQFragment.newInstance();
        return fragment;
    }

    private void transactSQFragment() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(binding.securityQuestionFrame.getId(), getFragment())
                .addToBackStack("sec_question").commit();
    }
}
