package com.prokarma.myhome.features.login.forgot.password;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.databinding.ActivityForgotPasswordBinding;
import com.prokarma.myhome.features.login.LoginFragment;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {

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

        String email = getIntent().getStringExtra(LoginFragment.EMAIL_ID_KEY);

        binding.email.addTextChangedListener(new ForgotPasswordTextWatcher());
        if (null != email && !email.isEmpty()) {
            binding.email.setText(email);
        }

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.hideSoftKeyboard(this);
    }

    /*
     * action handler for forgot password.
     */
    public class ForgotPasswordClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.forgot_password_button:
                    if (!CommonUtil.isValidEmail(binding.email.getText().toString())) {
                        binding.emailLayout.setError(getResources().getString(R.string.valid_email));
                        break;
                    }
                    if (!ConnectionUtil.isConnected(getApplicationContext())) {
                        CommonUtil.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_network_msg));
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
                            buildForgotPasswordAlert(getString(R.string.forgot_password_success_msg));
                        } else {
                            ApiErrorUtil.getInstance().forgotPasswordError(getApplicationContext(), binding.getRoot(), response);
                        }
                        binding.forgotProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        ApiErrorUtil.getInstance().forgotPasswordFailed(getApplicationContext(), binding.getRoot(), t);
                        binding.forgotProgress.setVisibility(View.GONE);
                    }
                });
    }

    private class ForgotPasswordTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isAllInputsValid()) {
                updateButtonState(true);
            } else {
                updateButtonState(false);
            }
        }
    }

    private boolean isAllInputsValid() {
        if (CommonUtil.isValidEmail(binding.email.getText().toString())) {
            return true;
        }
        return false;
    }

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binding.forgotPasswordButton.setBackgroundResource(R.drawable.button_enabled);
            binding.forgotPasswordButton.setTextColor(Color.WHITE);
        } else {
            binding.forgotPasswordButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binding.forgotPasswordButton.setTextColor(Color.GRAY);
        }
    }


    private void buildForgotPasswordAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.action_reset_password)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
