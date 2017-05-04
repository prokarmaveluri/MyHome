package com.dignityhealth.myhome.features.login.forgot.password;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.ActivityForgotPasswordBinding;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.ConnectionUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    private static final String factorType = "EMAIL";
    private static final String relayState = "/myapp/some/deep/link/i/want/to/return/to";

    /*
     * Get an intent for forgot password activity.
     */
    public static Intent getForgotPasswordIntent(Context context) {

        return new Intent(context, ForgotPasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        binding.setHandlers(new ForgotPasswordClickEvent());
    }

    /*
     * action handler for forgot password.
     */
    public class ForgotPasswordClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.forgot_password_button:
                    if (!CommonUtil.isValidEmail(binding.email.getText().toString())) {
                        binding.email.setError(getResources().getString(R.string.valid_email));
                        break;
                    }
                    if (!ConnectionUtil.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), R.string.no_network_msg,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    ForgotPasswordRequest request = new ForgotPasswordRequest(binding.email.getText().toString(),
                            factorType, relayState);
                    forgotPassword(request);
                    break;
            }
        }
    }

    private void forgotPassword(final ForgotPasswordRequest request) {
        binding.forgotProgress.setVisibility(View.VISIBLE);

        NetworkManager.getInstance().forgotPassword(request).enqueue(
                new Callback<ForgotPasswordResponse>() {

                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call,
                                           Response<ForgotPasswordResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.forgot_password_success_msg), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                        binding.forgotProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        binding.forgotProgress.setVisibility(View.GONE);
                    }
                });
    }
}
