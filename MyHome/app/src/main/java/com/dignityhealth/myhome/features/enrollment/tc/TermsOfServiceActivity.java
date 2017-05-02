package com.dignityhealth.myhome.features.enrollment.tc;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityTermsOfServiceBinding;
import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Activity to Terms Of Service.
 *
 * Created by cmajji on 4/26/17.
 */
public class TermsOfServiceActivity extends AppCompatActivity {

    private EnrollmentRequest enrollmentRequest;
    private ActivityTermsOfServiceBinding binding;

    /*
    * Get an intent for TermsOfServiceActivity activity.
    */
    public static Intent getTermsOfServiceActivityIntent(Context context) {

        return new Intent(context, TermsOfServiceActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_of_service);

        binding.setHandlers(new TCViewClickEvent());
        enrollmentRequest = (EnrollmentRequest) getIntent()
                .getParcelableExtra(Constants.ENROLLMENT_REQUEST);
    }

    public class TCViewClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.tc_accept:
                    if (!ConnectionUtil.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(),
                                R.string.no_network_msg,
                                Toast.LENGTH_LONG).show();
                        break;
                    }

                    enrollmentRequest.setHasAcceptedTerms(true);
                    enrollmentRequest.setSkipVerification(true); // need to discuss about the skip.
                    registerUser(enrollmentRequest);

                    break;
                case R.id.tc_cancel:
                    finish();
                    break;
            }
        }
    }

    private void registerUser(EnrollmentRequest request) {
        showProgress(true);
        NetworkManager.getInstance().register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.enrollment_success,
                            Toast.LENGTH_LONG).show();
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
            binding.termsProgress.setVisibility(View.VISIBLE);
        } else {
            binding.termsProgress.setVisibility(View.GONE);
        }
    }
}
