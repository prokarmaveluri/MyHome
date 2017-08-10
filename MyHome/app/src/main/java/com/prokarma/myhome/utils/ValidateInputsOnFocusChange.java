package com.prokarma.myhome.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.networking.NetworkManager;

/**
 * Created by cmajji on 4/30/17.
 */

public class ValidateInputsOnFocusChange implements View.OnFocusChangeListener {

    private TextView textView;
    private TextInputLayout textLayout;
    private Context context;
    private Constants.INPUT_TYPE type;

    public ValidateInputsOnFocusChange(TextView textView, TextInputLayout textLayout, Context context, Constants.INPUT_TYPE type) {
        this.textView = textView;
        this.textLayout = textLayout;
        this.type = type;
        this.context = context;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;

        if (type == Constants.INPUT_TYPE.TEXT) {
            if (!CommonUtil.isValidTextInput(textView)) {
                textLayout.setError(context.getString(R.string.enter_valid_input));
            } else {
                textLayout.setError(null);
            }
        } else if (type == Constants.INPUT_TYPE.FIRST_NAME) {
            if (!CommonUtil.isValidTextInput(textView)) {
                textLayout.setError(context.getString(R.string.valid_first_name));
            } else {
                textLayout.setError(null);
            }
        } else if (type == Constants.INPUT_TYPE.LAST_NAME) {
            if (!CommonUtil.isValidTextInput(textView)) {
                textLayout.setError(context.getString(R.string.valid_last_name));
            } else {
                textLayout.setError(null);
            }
        } else if (type == Constants.INPUT_TYPE.EMAIL_ENROLL) {
            if (!CommonUtil.isValidEmail(textView.getText().toString())) {
                textLayout.setError(context.getString(R.string.valid_email));
            } else if (NetworkManager.isEmailTaken()){
                textLayout.setError(context.getString(R.string.email_already_registered));
            } else {
                textLayout.setError(null);
            }
        } else if (type == Constants.INPUT_TYPE.EMAIL_LOGIN) {
            if (!CommonUtil.isValidEmail(textView.getText().toString())) {
                textLayout.setError(context.getString(R.string.valid_email));
            } else {
                textLayout.setError(null);
            }
        } else if (type == Constants.INPUT_TYPE.PASSWORD) {
            if (!CommonUtil.isValidPassword(textView.getText().toString())) {
                textLayout.setError(context.getString(R.string.valid_password));
            } else {
                textLayout.setError(null);
            }
        }
    }
}
