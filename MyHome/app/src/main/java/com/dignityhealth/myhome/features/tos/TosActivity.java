package com.dignityhealth.myhome.features.tos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseActivity;
import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.login.LoginActivity;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Activity for Terms Of Service. This is used for Enrollment.
 *
 * Created by cmajji on 4/26/17.
 */
public class TosActivity extends BaseActivity {
    private EnrollmentRequest enrollmentRequest;
    private ProgressBar termsProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }

        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        termsProgress = (ProgressBar) findViewById(R.id.terms_progress);

        TextView accept = (TextView) findViewById(R.id.tc_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });

        TextView cancel = (TextView) findViewById(R.id.tc_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTerms();
            }
        });

        enrollmentRequest = (EnrollmentRequest) getIntent()
                .getParcelableExtra(Constants.ENROLLMENT_REQUEST);
    }

    private void acceptTerms() {
        if (!ConnectionUtil.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
        } else {
            enrollmentRequest.setHasAcceptedTerms(true);
            enrollmentRequest.setSkipVerification(true); // need to discuss about the skip.
            registerUser(enrollmentRequest);
        }
    }

    private void cancelTerms() {
        finish();
    }

    private void registerUser(EnrollmentRequest request) {
        showProgress(true);
        NetworkManager.getInstance().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.enrollment_success,
                            Toast.LENGTH_LONG).show();
                    startLoginPage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        if (show) {
            termsProgress.setVisibility(View.VISIBLE);
        } else {
            termsProgress.setVisibility(View.GONE);
        }
    }

    private void startLoginPage() {
        Intent intent = LoginActivity.getLoginIntent(this);
        intent.putExtra("ENROLL_SUCCESS", true);
        startActivity(intent);
        finishAffinity();
    }
}
