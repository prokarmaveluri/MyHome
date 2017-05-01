package com.dignityhealth.myhome.features.enrollment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.FragmentEnrollmentBinding;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.ValidateInputsOnFocusChange;

import java.util.ArrayList;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/*
 * Fragment to enroll the user.
 *
 * Created by cmajji on 4/26/17.
 */
public class EnrollmentFragment extends Fragment implements EnrollmentInteractor.View {

    private FragmentEnrollmentBinding binding;
    private EnrollmentInteractor.Presenter presenter;

    public static EnrollmentFragment newInstance() {
        EnrollmentFragment fragment = new EnrollmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enrollment, container, false);

        binding.firstName.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.firstName,
                Constants.INPUT_TYPE.TEXT));
        binding.lastName.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.lastName,
                Constants.INPUT_TYPE.TEXT));

        binding.email.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.email,
                Constants.INPUT_TYPE.EMAIL));
        binding.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.password,
                Constants.INPUT_TYPE.PASSWORD));
        binding.reEnterPassword.setOnFocusChangeListener(
                new ValidateInputsOnFocusChange(binding.reEnterPassword,
                        Constants.INPUT_TYPE.PASSWORD));

        binding.setHandlers(new EnrollmentViewClickEvent());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
        showView(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }

    @Override
    public void setPresenter(EnrollmentInteractor.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            binding.enrollProgress.setVisibility(View.VISIBLE);
        } else {
            binding.enrollProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showView(boolean show) {
        if (show) {
            binding.loginSuggestion.setEnabled(true);
            binding.enrollButton.setEnabled(true);
        } else {
            binding.loginSuggestion.setEnabled(false);
            binding.enrollButton.setEnabled(false);
        }
    }

    @Override
    public void showEnrollmentStatus(String status) {

        checkNotNull(status);

        if (null != getActivity())
            Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
    }

    private EnrollmentRequest getRequest() {

        if (!CommonUtil.isValidTextInput(binding.firstName)) {
            binding.firstName.setError("Enter valid first name");
            return null;
        }

        if (!CommonUtil.isValidTextInput(binding.lastName)) {
            binding.lastName.setError("Enter valid last name");
            return null;
        }

        if (!CommonUtil.isValidEmail(binding.email.getText().toString())) {
            binding.email.setError("Enter valid email name");
            return null;
        }

        if (!CommonUtil.isValidPassword(binding.password.getText().toString())) {
            binding.password.setError("Enter valid password name");
            return null;
        }

        List<EnrollmentRequest.RecoveryQuestion> recoveryQuestions;
        recoveryQuestions = new ArrayList<>();

        EnrollmentRequest.RecoveryQuestion question =
                new EnrollmentRequest.RecoveryQuestion("name", "chandra");

        recoveryQuestions.add(question);

        EnrollmentRequest request = new EnrollmentRequest(binding.firstName.getText().toString(),
                binding.lastName.getText().toString(),
                binding.email.getText().toString(),
                binding.password.getText().toString(),
                true, // update according to user selection.
                true,
                recoveryQuestions
        );
        return request;
    }

    public class EnrollmentViewClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.login_suggestion:
                    presenter.openLoginPage();
                    break;
                case R.id.enroll_button:
                    EnrollmentRequest request = getRequest();
                    if (null != request)
                        presenter.enrollUser(request);
                    break;
            }
        }
    }
}
