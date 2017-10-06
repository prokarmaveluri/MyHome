package com.prokarma.myhome.features.settings;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.databinding.FragmentChangePassowrdBinding;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ChangePasswordFragment extends BaseFragment {
    public static final String CHANGE_PASSWORD_TAG = "change_password_tag";
    private FragmentChangePassowrdBinding binding;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_passowrd, container, false);

        getActivity().setTitle(getString(R.string.change_password));
        binding.newPassword.addTextChangedListener(new PWDTextWatcher(binding.newPasswordLayout));
        binding.existingPassword.addTextChangedListener(new PWDTextWatcher(binding.existingPasswordLayout));
        binding.confirmPassword.addTextChangedListener(new PWDTextWatcher(binding.confirmPasswordLayout));
        binding.setHandlers(new PWDHandler());
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.CHANGE_PASSWORD;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.CHANGE_PASSWORD_SCREEN, null);
    }

    public void changePassword() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        ChangePasswordRequest request = getRequest();
        if (request == null)
            return;
        binding.changePWDProgress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().changePassword(AuthManager.getInstance().getBearerToken(),
                request).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body().getIsValid()) {
                        TealiumUtil.trackEvent(Constants.CHANGED_PASSWORD_EVENT, null);
                        Toast.makeText(getActivity(), R.string.password_changed_successfully,
                                Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                        try {
                            //String message = response.body().getErrors().get(0).getMessage();
                            //Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            ApiErrorUtil.getInstance().changePasswordError(getContext(), getView(), response);
                        } catch (NullPointerException | IndexOutOfBoundsException ex) {
                            ApiErrorUtil.getInstance().changePasswordError(getContext(), getView(), response);
                        }
                    }
                } else {
                    Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    ApiErrorUtil.getInstance().changePasswordError(getContext(), getView(), response);
                }
                binding.changePWDProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                    ApiErrorUtil.getInstance().changePasswordFailed(getContext(), getView(), t);
                }
                binding.changePWDProgress.setVisibility(View.GONE);
            }
        });
    }

    public class PWDHandler {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.saveButton:
                    changePassword();
                    break;
                case R.id.password_criteria:
                    CommonUtil.displayPopupWindow(getActivity(), binding.passwordCriteria,
                            CommonUtil.getBulletPoints(CommonUtil.getCriteria(getActivity())));
                    break;
            }
        }
    }

    private ChangePasswordRequest getRequest() {
        if (!CommonUtil.isValidPassword(binding.existingPassword.getText().toString())) {
            binding.existingPasswordLayout.setError(getString(R.string.valid_password));
            return null;
        }
        if (!CommonUtil.isValidPassword(binding.newPassword.getText().toString())) {
            binding.newPasswordLayout.setError(getString(R.string.valid_password));
            return null;
        }
        if (!CommonUtil.isValidPassword(binding.confirmPassword.getText().toString())) {
            binding.confirmPasswordLayout.setError(getString(R.string.valid_password));
            return null;
        }
        if (!binding.confirmPassword.getText().toString().equals(binding.newPassword.getText().toString())) {
            binding.confirmPasswordLayout.setError(getString(R.string.valid_password_match));
            return null;
        }
        if (binding.existingPassword.getText().toString().equals(binding.newPassword.getText().toString())) {
            binding.confirmPasswordLayout.setError(getString(R.string.valid_password_same));
            return null;
        }
        ChangePasswordRequest request = new ChangePasswordRequest(binding.existingPassword.getText().toString(),
                binding.newPassword.getText().toString(), binding.confirmPassword.getText().toString());
        return request;
    }

    private class PWDTextWatcher implements TextWatcher {

        private TextInputLayout inputLayout;

        private PWDTextWatcher(TextInputLayout layout) {
            inputLayout = layout;
        }

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
            inputLayout.setError("");
        }
    }

    private boolean isAllInputsValid() {
        if (!CommonUtil.isValidPassword(binding.existingPassword.getText().toString())) {
            return false;
        }
        if (!CommonUtil.isValidPassword(binding.newPassword.getText().toString())) {
            return false;
        }
        if (!CommonUtil.isValidPassword(binding.confirmPassword.getText().toString())) {
            return false;
        }
        return true;
    }

    private void updateButtonState(boolean isEnabled) {
        if (isEnabled) {
            binding.saveButton.setBackgroundResource(R.drawable.button_enabled);
            binding.saveButton.setTextColor(Color.WHITE);
        } else {
            binding.saveButton.setBackgroundResource(R.drawable.button_boarder_grey);
            binding.saveButton.setTextColor(Color.GRAY);
        }
    }
}
