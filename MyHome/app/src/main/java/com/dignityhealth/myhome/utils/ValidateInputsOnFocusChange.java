package com.dignityhealth.myhome.utils;

import android.view.View;
import android.widget.TextView;

/**
 * Created by cmajji on 4/30/17.
 */

public class ValidateInputsOnFocusChange implements View.OnFocusChangeListener{

    private TextView view;
    private Constants.INPUT_TYPE type;

    public ValidateInputsOnFocusChange(TextView view, Constants.INPUT_TYPE type){
        this.view = view;
        this.type = type;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            return;

        if(type == Constants.INPUT_TYPE.TEXT){
            if(!CommonUtil.isValidTextInput(view))
                view.setError("Enter valid input");
        }else if (type == Constants.INPUT_TYPE.EMAIL){
            if (!CommonUtil.isValidEmail(view.getText().toString()))
                view.setError("Enter valid email");
        }else if (type== Constants.INPUT_TYPE.PASSWORD){
            if(!CommonUtil.isValidPassword(view.getText().toString()))
                view.setError("Enter valid password");
        }

    }
}
