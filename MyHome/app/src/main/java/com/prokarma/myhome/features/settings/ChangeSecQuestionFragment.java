package com.prokarma.myhome.features.settings;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.databinding.FragmentSecPasswordBinding;
import com.prokarma.myhome.features.enrollment.sq.SQListActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ChangeSecQuestionFragment extends BaseFragment implements View.OnClickListener {
    public static final String CHANGE_SEC_PASSWORD_TAG = "change_sec_password_tag";
    private FragmentSecPasswordBinding binding;

    private static final int CHANGE_QUESTION = 100;

    public static ChangeSecQuestionFragment newInstance() {
        return new ChangeSecQuestionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sec_password, container, false);

        binding.nextButton.setOnClickListener(this);
        binding.password.addTextChangedListener(new PWDTextWatcher());

        getActivity().setTitle(getString(R.string.change_question));
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.CHANGE_SEC_QUESTION;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.CHANGE_SEC_QUESTION_PASSWORD_SCREEN, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                if (!CommonUtil.isValidPassword(binding.password.getText().toString())) {
                    binding.passwordLayout.setError(getString(R.string.valid_password));
                    break;
                }
                Intent sqIntent = SQListActivity.getSQListActivityIntent(getActivity());
                sqIntent.putExtra("IS_SEC_QUESTION_CHANGE", true);
                sqIntent.putExtra("SEC_QUESTION_PASSWORD", binding.password.getText().toString());
                startActivityForResult(sqIntent, CHANGE_QUESTION, null);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_QUESTION) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private class PWDTextWatcher implements TextWatcher {

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
            binding.passwordLayout.setError("");
        }
    }

    private boolean isAllInputsValid() {
        if (CommonUtil.isValidPassword(binding.password.getText().toString())) {
            return true;
        }
        return false;
    }

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binding.nextButton.setBackgroundResource(R.drawable.button_enabled);
            binding.nextButton.setTextColor(Color.WHITE);
        } else {
            binding.nextButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binding.nextButton.setTextColor(Color.GRAY);
        }
    }
}
