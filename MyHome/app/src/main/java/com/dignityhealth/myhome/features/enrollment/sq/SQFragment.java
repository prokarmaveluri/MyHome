package com.dignityhealth.myhome.features.enrollment.sq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.FragmentSecqBinding;
import com.dignityhealth.myhome.utils.Constants;

public class SQFragment extends Fragment {

    private FragmentSecqBinding binding;

    private static final int SELECT_QUESTION_ACTION = 100;

    public SQFragment() {
        // Required empty public constructor
    }

    public static SQFragment newInstance() {
        SQFragment fragment = new SQFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secq,
                container, false);
        binding.setHandlers(new SQClickEvent());
        binding.answer.addTextChangedListener(new SQTextWatcher());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class SQClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.submit_question:
                    break;
                case R.id.select_question:
                    startQuestionsDialog();
                    break;
            }
        }
    }

    public interface ISelectSecurityQuestion {
        void selectQuestion();
    }

    private void startQuestionsDialog() {
        SQListDialog dialog = SQListDialog.newInstance();
        dialog.setTargetFragment(this, SELECT_QUESTION_ACTION);
        dialog.show(getFragmentManager(), "enrollment_questions");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_QUESTION_ACTION) {
            if (resultCode == Activity.RESULT_OK) {

                if (null != data) {
                    String qId = data.getStringExtra(Constants.ENROLLMENT_QUESTION_ID);
                    String question = data.getStringExtra(Constants.ENROLLMENT_QUESTION);
                    if (null != question) {
                        binding.selectQuestion.setText(question);
                        binding.answer.setEnabled(true);
                    }
                }
            }
        }
    }

    private class SQTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                binding.submitQuestion.setEnabled(true);
            }else {
                binding.submitQuestion.setEnabled(false);
            }
        }
    }
}
