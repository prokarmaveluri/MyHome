package com.dignityhealth.myhome.features.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityLoginBinding;


/*
 * Activity to login.
 *
 * Created by cmajji on 4/26/17.
 */
public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;

    /*
     * Get an intent for login activity.
     */
    public static Intent getLoginIntent(Context context) {

        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        LoginFragment fragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.loginFrame.getId(), fragment).commit();

        new LoginPresenter(fragment, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
