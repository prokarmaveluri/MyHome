package com.prokarma.myhome.features.login.verify;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.databinding.ActivityVerifyBinding;
import com.prokarma.myhome.features.login.LoginActivity;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.features.tos.TosActivity;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/*
 * Activity to login.
 *
 * Created by cmajji on 4/26/17.
 */
public class EmailVerifyActivity extends AppCompatActivity {


    private ActivityVerifyBinding binding;
    private boolean isCreated = true;
    private AlertDialog alert;

    /*
     * Get an intent for Email Verify activity.
     */
    public static Intent getEmailVerifyIntent(Context context) {

        return new Intent(context, EmailVerifyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isCreated = true;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        binding.verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendEmail();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        if (null != ProfileManager.getProfile()) {
            binding.verifyEmailMessage.setText(String.format(getResources().getString(R.string.resend_email_message),
                    ProfileManager.getProfile().email));
        }

        binding.reEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.validate_email));
    }

    private void loginActivity() {
        Intent intent = LoginActivity.getLoginIntent(this);
        startActivity(intent);
        finish();
    }

    //If you do the back button via the manifest, you won't get the proper animation when you click back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                loginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.RESEND_EMAIL, null);
        if (!isCreated) {
            getProfileInfo();
        }
        isCreated = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != alert)
            alert.cancel();
    }

    private void resendEmail() {
        binding.verifyProgress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().resendEmail(AuthManager.getInstance().getBearerToken())
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        if (response.isSuccessful() && response.body().getIsValid()) {

                            // Notify the user.
                            buildAlert();
                        } else {
                            CommonUtil.showToast(getApplicationContext(),
                                    getApplicationContext().getString(R.string.something_went_wrong));
                        }
                        binding.verifyProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        CommonUtil.showToast(getApplicationContext(),
                                getApplicationContext().getString(R.string.something_went_wrong));
                        binding.verifyProgress.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * get the Profile of the currently logged in user and loads it into Singleton
     */
    public void getProfileInfo() {
        binding.verifyProgress.setVisibility(View.VISIBLE);
        String bearerToken = AuthManager.getInstance().getBearerToken();

        NetworkManager.getInstance().getProfile(bearerToken).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (response.isSuccessful()) {

                    binding.verifyProgress.setVisibility(View.GONE);

                    try {
                        ProfileManager.setProfile(response.body().getData().getUser());
                        NetworkManager.getInstance().getMyAppointments();
                        AuthManager.getInstance().setCount(0);

                        if (response.body().getData().getUser().isVerified
                                && response.body().getData().getUser().isTermsAccepted) {

                            Intent intentHome = new Intent(EmailVerifyActivity.this, NavigationActivity.class);
                            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(EmailVerifyActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                            ActivityCompat.startActivity(EmailVerifyActivity.this, intentHome, options.toBundle());
                            setResult(Activity.RESULT_OK);
                            finish();

                        } else if (response.body().getData().getUser().isVerified &&
                                !response.body().getData().getUser().isTermsAccepted) {

                            startTermsOfServiceActivity();
                        } else {
                            Timber.w("User not verified. Staying on same screen");
                        }
                    } catch (NullPointerException ex) {
                        Timber.w(ex);
                    }
                } else {
                    Timber.e("EmailVerify. getProfile. Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                Timber.e("EmailVerify. getProfile. Something failed! :/");
                Timber.e("Throwable = " + t);
                binding.verifyProgress.setVisibility(View.GONE);
            }
        });
    }

    private void startTermsOfServiceActivity() {
        Intent intent = new Intent(this, TosActivity.class);
        startActivity(intent);
    }

    private void buildAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.verify_email)
                .setMessage(R.string.resend_email_success)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
}
