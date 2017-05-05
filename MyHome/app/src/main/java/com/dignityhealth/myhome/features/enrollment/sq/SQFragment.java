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
import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.enrollment.tc.TermsOfServiceActivity;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/*
 * Fragment to select security question.
 *
 * Created by cmajji on 4/26/17.
 */
public class SQFragment extends Fragment {

    private FragmentSecqBinding binding;
    private EnrollmentRequest enrollmentRequest;
    private String selectedQuestionId;
    private String selectedQuestionTxt;

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
        if (null != getArguments()) {
            Bundle bundle = getArguments();
            enrollmentRequest = bundle.getParcelable(Constants.ENROLLMENT_REQUEST);
            selectedQuestionId = bundle.getString(Constants.ENROLLMENT_QUESTION_ID);
            selectedQuestionTxt = bundle.getString(Constants.ENROLLMENT_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secq,
                container, false);
        binding.setHandlers(new SQClickEvent());
        binding.answer.addTextChangedListener(new SQTextWatcher());

        binding.selectQuestion.setText(selectedQuestionTxt);
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
                    if(binding.answer.getText().toString().isEmpty()){
                        binding.answer.setError("Enter valid answer");
                        break;
                    }
                    if (null != selectedQuestionId &&
                            !binding.answer.getText().toString().isEmpty()) {
                        updateRequest();
                        startTermsOfServiceActivity();
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_QUESTION_ACTION) {
            if (resultCode == Activity.RESULT_OK) {

                if (null != data) {
                    selectedQuestionId = data.getStringExtra(Constants.ENROLLMENT_QUESTION_ID);
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
            } else {
                binding.submitQuestion.setEnabled(false);
            }
        }
    }

    private void updateRequest() {

        //Validate security question inputs
        EnrollmentRequest.RecoveryQuestion question =
                new EnrollmentRequest.RecoveryQuestion(selectedQuestionId,
                        binding.answer.getText().toString());

        List<EnrollmentRequest.RecoveryQuestion> questions = new ArrayList<>();
        questions.add(question);
        enrollmentRequest.setRecoveryQuestions(questions);
    }

    private void startTermsOfServiceActivity() {

        Intent intent = TermsOfServiceActivity.getTermsOfServiceActivityIntent(getActivity());
        intent.putExtra(Constants.ENROLLMENT_REQUEST, enrollmentRequest);
        startActivity(intent);
    }
}
