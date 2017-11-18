package com.prokarma.myhome.features.enrollment.sq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentSecqBinding;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.settings.ChangeSecurityQuestionRequest;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.features.tos.TosActivity;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    private boolean isChange = false;
    private String password;

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
            isChange = bundle.getBoolean("IS_SEC_QUESTION_CHANGE", false);
            password = bundle.getString("SEC_QUESTION_PASSWORD");
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

        if (isChange) {
            binding.submitQuestion.setText("Save");
        }
        drawableClickEvent();
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
                    if (!isAllInputsValid()) {
                        break;
                    }
                    if (isChange) {
                        //Submit the new question
                        Timber.i("Submit the new question");
                        changeSecurityQuestion();
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
            if (isAllInputsValid()) {
                updateButtonState(true);
            } else {
                updateButtonState(false);
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
        Intent intent = new Intent(getActivity(), TosActivity.class);
        intent.putExtra(Constants.ENROLLMENT_REQUEST, enrollmentRequest);
        startActivity(intent);
    }


    private boolean isAllInputsValid() {
        try {
            binding.answerLayout.setError("");
            String text = binding.answer.getText().toString().trim();
            Timber.i("Error " + text);
            if (binding.answer.getText().toString().trim().length() < 4) {
                binding.answerLayout.setError(getString(R.string.sq_criteria_4char));
                return false;
            }
            if (selectedQuestionTxt.toLowerCase().contains(binding.answer.getText().toString().trim().toLowerCase())) {
                binding.answerLayout.setError(getString(R.string.sq_criteria_question));
                return false;
            }
            if (isChange) {
                if (password.toLowerCase().contains(binding.answer.getText().toString().trim().toLowerCase())) {
                    binding.answerLayout.setError(getString(R.string.sq_criteria_password));
                    return false;
                }
                if (ProfileManager.getProfile() != null &&
                        ProfileManager.getProfile().email.contains(binding.answer.getText().toString().trim())) {
                    binding.answerLayout.setError(getString(R.string.sq_criteria_username));
                    return false;
                }
            } else if (null != enrollmentRequest) {
                if (enrollmentRequest.getPassword().toLowerCase().contains(binding.answer.getText().toString().trim().toLowerCase())) {
                    binding.answerLayout.setError(getString(R.string.sq_criteria_password));
                    return false;
                }
                if (enrollmentRequest.getEmail().contains(binding.answer.getText().toString().trim())) {
                    binding.answerLayout.setError(getString(R.string.sq_criteria_username));
                    return false;
                }
            }
        } catch (NullPointerException ex) {
        }
        return true;
    }

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binding.submitQuestion.setBackgroundResource(R.drawable.button_enabled);
            binding.submitQuestion.setTextColor(Color.WHITE);
        } else {
            binding.submitQuestion.setBackgroundResource(R.drawable.button_boarder_grey);
            binding.submitQuestion.setTextColor(Color.GRAY);
        }
    }

    private void changeSecurityQuestion() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!isAllInputsValid())
            return;
        ChangeSecurityQuestionRequest.Question question =
                new ChangeSecurityQuestionRequest.Question(selectedQuestionId,
                        binding.answer.getText().toString());

        binding.changeSecProgress.setVisibility(View.VISIBLE);
        ChangeSecurityQuestionRequest request = new ChangeSecurityQuestionRequest(password, question);
        NetworkManager.getInstance().changeSecurityQuestion(AuthManager.getInstance().getBearerToken(),
                request).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call,
                                   Response<CommonResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body().getIsValid()) {
                        Toast.makeText(getActivity(), R.string.sec_question_changed_successfully,
                                Toast.LENGTH_LONG).show();

                        TealiumUtil.trackEvent(Constants.CHANGED_SECURITY_QUESTION_EVENT, null);
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                        try {
//                            String message = response.body().getErrors().get(0).getMessage();
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            ApiErrorUtil.getInstance().changeSecurityQuestionError(getContext(), getView(), response);
                        } catch (NullPointerException | IndexOutOfBoundsException ex) {
                            ApiErrorUtil.getInstance().changeSecurityQuestionError(getContext(), getView(), response);
                        }
                    }
                } else {
                    Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    ApiErrorUtil.getInstance().changeSecurityQuestionError(getContext(), getView(), response);
                }
                binding.changeSecProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                    ApiErrorUtil.getInstance().changeSecurityQuestionFailed(getContext(), getView(), t);
                }
                binding.changeSecProgress.setVisibility(View.GONE);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommonUtil.hideSoftKeyboard(getActivity());
        }
    };

    private void drawableClickEvent() {
        binding.answer.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        try {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int[] locationOnScreen = new int[2];
                                binding.answer.getLocationOnScreen(locationOnScreen);
                                int touchXCoordinate = (int) event.getRawX() - locationOnScreen[0];
                                Timber.i("OnTouch " + (binding.answer.getRight() - binding.answer.getTotalPaddingRight()));
                                Timber.i("OnTouch  " + (binding.answer.getRight() - binding.answer.getPaddingRight()));
                                Timber.i("touchXCoordinate  " + touchXCoordinate);
                                if (touchXCoordinate >= (binding.answer.getRight() - binding.answer.getTotalPaddingRight()) &&
                                        touchXCoordinate <= (binding.answer.getRight() - binding.answer.getPaddingRight())) {
                                    CommonUtil.displayPopupWindow(getActivity(), binding.answer,
                                            CommonUtil.getBulletPoints(CommonUtil.getSecurityAnswerCriteria(getActivity())));
                                    mHandler.sendEmptyMessageDelayed(0, 500);
                                } else {
                                    CommonUtil.showSoftKeyboard(binding.answer, getActivity());
                                }
                            }
                        } catch (NullPointerException ex) {
                            return false;
                        }
                        return false;
                    }
                });
    }
}