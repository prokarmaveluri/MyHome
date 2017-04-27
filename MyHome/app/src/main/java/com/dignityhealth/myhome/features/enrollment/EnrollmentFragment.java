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

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


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

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
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

    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.login_suggestion:
                presenter.openLoginPage();
                break;
            case R.id.enroll_button:
                presenter.enrollUser();
                break;
        }
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
            binding.loginSuggestion.setClickable(true);
            binding.enrollButton.setClickable(true);
        } else {
            binding.loginSuggestion.setClickable(false);
            binding.enrollButton.setClickable(false);
        }
    }

    @Override
    public void showEnrollmentStatus(String status) {
        if (null != getActivity())
            Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
    }
}
