package com.prokarma.myhome.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.prokarma.myhome.R;

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
                view.setError(context.getString(R.string.valid_email));
        } else if (type == Constants.INPUT_TYPE.EMAIL_LOGIN) {
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError(context.getString(R.string.valid_email));
        } else if (type == Constants.INPUT_TYPE.PASSWORD) {
            if (!CommonUtil.isValidPassword(view.getText().toString()))
                view.setError(context.getString(R.string.valid_password));
        }
    }
}
