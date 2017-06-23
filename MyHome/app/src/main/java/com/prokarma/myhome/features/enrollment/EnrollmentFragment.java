package com.prokarma.myhome.features.enrollment;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentEnrollmentBinding;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.ValidateInputsOnFocusChange;

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
                Constants.INPUT_TYPE.EMAIL_ENROLL));
//        binding.password.setOnFocusChangeListener(new ValidateInputsOnFocusChange(binding.password,
//                Constants.INPUT_TYPE.PASSWORD));
        binding.reEnterPassword.setOnFocusChangeListener(
                new ValidateInputsOnFocusChange(binding.reEnterPassword,
                        Constants.INPUT_TYPE.PASSWORD));

        binding.firstName.addTextChangedListener(new EnrollTextWatcher());
        binding.lastName.addTextChangedListener(new EnrollTextWatcher());
        binding.email.addTextChangedListener(new EnrollTextWatcher());
        binding.password.addTextChangedListener(new EnrollTextWatcher());
        binding.reEnterPassword.addTextChangedListener(new EnrollTextWatcher());

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

        if (!CommonUtil.isValidPassword(binding.password.getText().toString(),
                binding.firstName.getText().toString(), binding.lastName.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_password),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        if (!binding.password.getText().toString().equals(binding.reEnterPassword.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_password_match),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        if (null != binding.email.getError() && binding.email.getError().length() > 0) {
            Toast.makeText(getActivity(), "Email already exists", Toast.LENGTH_LONG).show();
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
                    displayPopupWindow(getActivity(), binding.passwordCriteria);
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
                        final int DRAWABLE_RIGHT = 2;

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if ((int) event.getRawX() >= (binding.password.getRight() -
                                    binding.password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                                showPassword = !showPassword;

                                if (showPassword) {
                                    binding.password.setTransformationMethod(null);
                                    binding.reEnterPassword.setTransformationMethod(null);
                                    updateDrawable(R.mipmap.hide_password);
                                } else {
                                    binding.password.setTransformationMethod(new PasswordTransformationMethod());
                                    binding.reEnterPassword.setTransformationMethod(new PasswordTransformationMethod());
                                    updateDrawable(R.mipmap.show_password);
                                }
                                return true;
                            }
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

    private void displayPopupWindow(Activity activity, View anchorView) {
        PopupWindow popup = new PopupWindow(activity);
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_content, null);
        TextView textView = (TextView) layout.findViewById(R.id.criteria_text);
        textView.setText(CommonUtil.getBulletPoints(activity));
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

    }
}
