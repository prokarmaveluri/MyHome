package com.dignityhealth.myhome.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kwelsh on 4/25/17.
 * Master Activity to extend
 * Helps share common code across all activities (such as animations and the Navigation Bar)
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //navigationActivity.setActivityTag(setDrawerTag());
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}