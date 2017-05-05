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
            binding.enrollButton.setEnabled(true);
        } else {
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
            binding.firstName.setError(getResources().getString(R.string.valid_first_name));
            return null;
        }

        if (!CommonUtil.isValidTextInput(binding.lastName)) {
            binding.lastName.setError(getResources().getString(R.string.valid_last_name));
            return null;
        }

        if (!CommonUtil.isValidEmail(binding.email.getText().toString())) {
            binding.email.setError(getResources().getString(R.string.valid_email));
            return null;
        }

        if (!CommonUtil.isValidPassword(binding.password.getText().toString())) {
            binding.password.setError(getResources().getString(R.string.valid_password));
            return null;
        }

        EnrollmentRequest request = new EnrollmentRequest(binding.firstName.getText().toString(),
                binding.lastName.getText().toString(),
                binding.email.getText().toString(),
                binding.password.getText().toString(),
                true, // update according to user selection.
                true,
                null);
        return request;
    }

    public class EnrollmentViewClickEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.enroll_button:
                    EnrollmentRequest request = getRequest();
                    if (null != request)
                        presenter.enrollUser(request);
                    break;
//                case R.id.password_criteria:
//                    PasswordCriteriaDialog dialog = new PasswordCriteriaDialog();
//                    dialog.show(getFragmentManager(), "Password Criteria");
//                    break;
            }
        }
    }
}
