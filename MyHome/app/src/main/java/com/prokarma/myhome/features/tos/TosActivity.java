package com.prokarma.myhome.features.tos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.app.SplashActivity;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Activity for Terms Of Service. This is used for Enrollment.
 *
 * Created by cmajji on 4/26/17.
 */
public class TosActivity extends BaseActivity {
    public static final String FILE_ANDROID_ASSET_TOS_HTML = "file:///android_asset/tos.html"; //Changes Fragment's ToS too
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
            //noinspection deprecation
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

        WebView tos = (WebView) findViewById(R.id.terms_of_service);
        tos.loadUrl(FILE_ANDROID_ASSET_TOS_HTML);

        enrollmentRequest = getIntent().getParcelableExtra(Constants.ENROLLMENT_REQUEST);
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

    private void registerUser(final EnrollmentRequest request) {
        if (!ConnectionUtil.isConnected(this)) {
            Toast.makeText(this,
                    R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showProgress(true);
        NetworkManager.getInstance().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.enrollment_success,
                            Toast.LENGTH_LONG).show();
                    startLoginPage(request.getEmail(), request.getPassword());
                } else {
                    ApiErrorUtil.getInstance().registerError(getApplicationContext(), findViewById(android.R.id.content), response);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ApiErrorUtil.getInstance().registerFailed(getApplicationContext(), findViewById(android.R.id.content), t);
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

    private void startLoginPage(String userName, String password) {

        Intent intent = SplashActivity.getSplashIntent(this);
        intent.putExtra("ENROLL_SUCCESS", true);
        intent.putExtra("USER_NAME", userName);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.TOS_SCREEN, null);
    }
}
