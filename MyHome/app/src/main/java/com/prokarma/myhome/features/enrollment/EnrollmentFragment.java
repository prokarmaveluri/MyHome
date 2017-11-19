package com.prokarma.myhome.features.enrollment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentEnrollmentBinding;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.ValidateInputsOnFocusChange;

import timber.log.Timber;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/*
 * Fragment to enroll the user.
 *
 * Created by cmajji on 4/26/17.
 */
public class EnrollmentFragment extends Fragment implements EnrollmentInteractor.View {

    private FragmentEnrollmentBinding binding;
    private EnrollmentInteractor.Presenter presenter;
    private static boolean showPassword = false;

    public static EnrollmentFragment newInstance() {
        return new EnrollmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enrollment, container, false);

        binding.firstName.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.firstName, binding.firstNameLayout,
                getActivity().getApplicationContext(), Constants.INPUT_TYPE.FIRST_NAME));
        binding.lastName.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.lastName, binding.lastNameLayout,
                getActivity().getApplicationContext(), Constants.INPUT_TYPE.LAST_NAME));

        binding.email.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.email, binding.emailLayout,
                getActivity().getApplicationContext(), Constants.INPUT_TYPE.EMAIL_ENROLL));
//        binding.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.password,
//                Constants.INPUT_TYPE.PASSWORD));
        binding.reEnterPassword.setOnFocusChangeListener(
                new ValidateInputsOnFocusChange(binding.reEnterPassword, binding.reEnterPasswordLayout,
                        getActivity().getApplicationContext(), Constants.INPUT_TYPE.PASSWORD));

        binding.firstName.addTextChangedListener(new EnrollTextWatcher(null, null));
        binding.lastName.addTextChangedListener(new EnrollTextWatcher(null, null));
        binding.email.addTextChangedListener(new EnrollTextWatcher(binding.emailLayout, Constants.INPUT_TYPE.EMAIL_ENROLL));
        binding.password.addTextChangedListener(new EnrollTextWatcher(null, null));
        binding.reEnterPassword.addTextChangedListener(new EnrollTextWatcher(null, null));

        drawableClickEvent();
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

    @Nullable
    private EnrollmentRequest getRequest() {

        if (!CommonUtil.isValidTextInput(binding.firstName)) {
            binding.firstNameLayout.setError(getString(R.string.valid_first_name));
            return null;
        }

        if (!CommonUtil.isValidTextInput(binding.lastName)) {
            binding.lastNameLayout.setError(getString(R.string.valid_last_name));
            return null;
        }

        if (!CommonUtil.isValidEmail(binding.email.getText().toString())) {
            binding.emailLayout.setError(getString(R.string.valid_email));
            return null;
        }

        if (!CommonUtil.isValidPassword(binding.password.getText().toString(),
                binding.firstName.getText().toString(), binding.lastName.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        if (!binding.password.getText().toString().equals(binding.reEnterPassword.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_password_match),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        if (null != binding.emailLayout.getError() && binding.emailLayout.getError().length() > 0) {
            Toast.makeText(getActivity(), getString(R.string.email_already_exists), Toast.LENGTH_LONG).show();
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
                case R.id.password_criteria:
                    CommonUtil.displayPopupWindow(getActivity(), binding.passwordCriteria,
                            CommonUtil.getBulletPoints(CommonUtil.getCriteria(getActivity())));
//                    toolTipPopup(binding.passwordCriteria);
                    break;
            }
        }
    }

    private boolean isAllInputsValid() {
        if (CommonUtil.isValidTextInput(binding.firstName) &&
                CommonUtil.isValidTextInput(binding.lastName) &&
                CommonUtil.isValidEmail(binding.email.getText().toString()) &&
                CommonUtil.isValidPassword(binding.password.getText().toString(),
                        binding.firstName.getText().toString(), binding.lastName.getText().toString()) &&
                CommonUtil.isValidPassword(binding.reEnterPassword.getText().toString(),
                        binding.firstName.getText().toString(), binding.lastName.getText().toString()) &&
                binding.password.getText().toString().equals(binding.reEnterPassword.getText().toString())) {
            return true;
        }
        return false;
    }

    private class EnrollTextWatcher implements TextWatcher {
        Constants.INPUT_TYPE textType;
        TextInputLayout view;

        EnrollTextWatcher(TextInputLayout view, Constants.INPUT_TYPE type) {
            textType = type;
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (textType == Constants.INPUT_TYPE.EMAIL_ENROLL && null != getActivity())
                NetworkManager.getInstance().findEmail(s.toString(), view, getActivity());

            if (isAllInputsValid()) {
                updateButtonState(true);
            } else {
                updateButtonState(false);
            }
        }
    }

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binding.enrollButton.setBackgroundResource(R.drawable.button_enabled);
            binding.enrollButton.setTextColor(Color.WHITE);
        } else {
            binding.enrollButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binding.enrollButton.setTextColor(Color.GRAY);
        }
    }

    private void drawableClickEvent() {
        binding.password.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        try {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int[] locationOnScreen = new int[2];
                                binding.password.getLocationOnScreen(locationOnScreen);
                                int touchXCoordinate = (int) event.getRawX() - locationOnScreen[0];
                                if (touchXCoordinate >= (binding.password.getRight() - binding.password.getTotalPaddingRight()) &&
                                        touchXCoordinate <= (binding.password.getRight() - binding.password.getPaddingRight())) {

                                    showPassword = !showPassword;

                                    if (showPassword) {
                                        binding.reEnterPassword.setTransformationMethod(null);
                                        binding.password.setTransformationMethod(null);
                                        updateDrawable(R.mipmap.hide_password);
                                    } else {
                                        binding.password.setTransformationMethod(new PasswordTransformationMethod());
                                        binding.reEnterPassword.setTransformationMethod(new PasswordTransformationMethod());
                                        updateDrawable(R.mipmap.show_password);
                                    }
                                    return true;
                                }
                            }
                        } catch (NullPointerException ex) {
                            Timber.i("NullPointerException in drawableClickEvent ");
                            return false;
                        }
                        return false;
                    }
                });
    }


    @SuppressWarnings("deprecation")
    private void updateDrawable(int res) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(res, getActivity().getTheme());
        } else {
            drawable = getResources().getDrawable(res);
        }
        if (null != drawable) {
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, w, h);
        }
        binding.password.setCompoundDrawables(null, null, drawable, null);
    }

}
