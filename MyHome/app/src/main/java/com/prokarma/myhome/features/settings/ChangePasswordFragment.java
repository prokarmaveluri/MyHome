package com.prokarma.myhome.features.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.databinding.FragmentChangePassowrdBinding;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
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
                request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(getActivity(), R.string.password_changed_successfully,
                            Toast.LENGTH_LONG).show();
                } else {
                    Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG).show();
                }
                binding.changePWDProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (isAdded()) {
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG).show();
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
            }
        }
    }

    private ChangePasswordRequest getRequest() {
        if (!CommonUtil.isValidPassword(binding.existingPassword.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password), Toast.LENGTH_LONG).show();
            return null;
        }
        if (!CommonUtil.isValidPassword(binding.newPassword.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password), Toast.LENGTH_LONG).show();
            return null;
        }
        if (!CommonUtil.isValidPassword(binding.confirmPassword.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.valid_password), Toast.LENGTH_LONG).show();
            return null;
        }
        ChangePasswordRequest request = new ChangePasswordRequest(binding.existingPassword.getText().toString(),
                binding.newPassword.getText().toString(), binding.confirmPassword.getText().toString());
        return request;
    }
}
