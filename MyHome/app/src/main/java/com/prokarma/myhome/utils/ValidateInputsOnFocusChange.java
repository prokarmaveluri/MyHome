package com.prokarma.myhome.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.enrollment.ValidateEmailResponse;
import com.prokarma.myhome.networking.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 4/30/17.
 */

public class ValidateInputsOnFocusChange implements View.OnFocusChangeListener {

    private TextView view;
    private Context context;
    private Constants.INPUT_TYPE type;

    public ValidateInputsOnFocusChange(TextView view, Context context, Constants.INPUT_TYPE type) {
        this.view = view;
        this.type = type;
        this.context = context;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;

        if (type == Constants.INPUT_TYPE.TEXT) {
            if (!CommonUtil.isValidTextInput(view))
                view.setError(context.getString(R.string.enter_valid_input));
        } else if (type == Constants.INPUT_TYPE.FIRST_NAME) {
            if (!CommonUtil.isValidTextInput(view))
                view.setError(context.getString(R.string.valid_first_name));
        } else if (type == Constants.INPUT_TYPE.LAST_NAME) {
            if (!CommonUtil.isValidTextInput(view))
                view.setError(context.getString(R.string.valid_last_name));
        } else if (type == Constants.INPUT_TYPE.EMAIL_ENROLL) {
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError(context.getString(R.string.enter_valid_email));
            else
                findEmail(view.getText().toString());
        } else if (type == Constants.INPUT_TYPE.EMAIL_LOGIN) {
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError(context.getString(R.string.enter_valid_email));
        } else if (type == Constants.INPUT_TYPE.PASSWORD) {
            if (!CommonUtil.isValidPassword(view.getText().toString()))
                view.setError(context.getString(R.string.enter_valid_password));
        }
    }

    private void findEmail(final String email) {
        NetworkManager.getInstance().findEmail(email).enqueue(new Callback<ValidateEmailResponse>() {
            @Override
            public void onResponse(Call<ValidateEmailResponse> call, Response<ValidateEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult()) {
                        view.setError(context.getString(R.string.email_already_registered));
                        Timber.i("Email already exists!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidateEmailResponse> call, Throwable t) {
                Timber.i("validateEmail, failed");
            }
        });
    }
}
