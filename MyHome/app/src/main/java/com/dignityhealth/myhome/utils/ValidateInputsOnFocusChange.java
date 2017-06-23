package com.dignityhealth.myhome.utils;

import android.view.View;
import android.widget.TextView;

import com.dignityhealth.myhome.features.enrollment.ValidateEmailResponse;
import com.dignityhealth.myhome.networking.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 4/30/17.
 */

public class ValidateInputsOnFocusChange implements View.OnFocusChangeListener {

    private TextView view;
    private Constants.INPUT_TYPE type;

    public ValidateInputsOnFocusChange(TextView view, Constants.INPUT_TYPE type) {
        this.view = view;
        this.type = type;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;

        if (type == Constants.INPUT_TYPE.TEXT) {
            if (!CommonUtil.isValidTextInput(view))
                view.setError("Enter valid input");
        } else if (type == Constants.INPUT_TYPE.EMAIL_ENROLL) {
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError("Enter valid email");
            else
                findEmail(view.getText().toString());
        }  else if (type == Constants.INPUT_TYPE.EMAIL_LOGIN) {
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError("Enter valid email");
        } else if (type == Constants.INPUT_TYPE.PASSWORD) {
            if (!CommonUtil.isValidPassword(view.getText().toString()))
                view.setError("Enter valid password");
        }
    }

    private void findEmail(final String email) {
        NetworkManager.getInstance().findEmail(email).enqueue(new Callback<ValidateEmailResponse>() {
            @Override
            public void onResponse(Call<ValidateEmailResponse> call, Response<ValidateEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult()) {
                        view.setError("Email already exists!");
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
