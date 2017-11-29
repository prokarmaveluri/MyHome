package com.televisit.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SDKLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SDKLoginFragment extends BaseFragment {

    protected AWSDK awsdk;
    private ProgressBar progressBar;

    public SDKLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static SDKLoginFragment newInstance() {
        SDKLoginFragment fragment = new SDKLoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        awsdk = AwsManager.getInstance().getAWSDK();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.mycare_now));
        View view = inflater.inflate(R.layout.fragment_login2, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.login_progress);
        initSDK();
        return view;
    }

    private void initSDK() {

        String baseServiceUrl = getString(R.string.awsdkurl);
        String clientKey = getString(R.string.awsdkkey);
        String launchUri = null;

        final Map<AWSDK.InitParam, Object> initParams = new HashMap<>();
        initParams.put(AWSDK.InitParam.BaseServiceUrl, baseServiceUrl);
        initParams.put(AWSDK.InitParam.ApiKey, clientKey);
        initParams.put(AWSDK.InitParam.LaunchIntentData, launchUri);
        progressBar.setVisibility(View.VISIBLE);
        try {
            awsdk.initialize(
                    initParams,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null && isAdded()) {
                                consumerAuth("cmajji@mailinator.com", "Pass123*");

//                                if (AuthManager.getInstance().getAmWellToken() != null) {
//                                    consumerMutualAuth(AuthManager.getAmWellToken());
//                                } else {
//                                    Toast.makeText(getContext(), "Don't have AmWell Token yet.\nPlease try again later", Toast.LENGTH_LONG).show();
//                                    AuthManager.getInstance().getUsersAmWellToken();
//
//                                    if (isAdded())
//                                        progressBar.setVisibility(View.GONE);
//                                }
                            } else {
                                if (isAdded())
                                    progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (isAdded())
                                progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (AWSDKInitializationException e) {
            Timber.w(e);
        }
    }

    private void consumerAuth(String email, String password) {
        awsdk.authenticate(
                email,
                password,
                email,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null && isAdded()) {
                            AwsManager.getInstance().setAuthentication(authentication);
                            getConsumer(authentication);
                        } else {
                            if (isAdded())
                                progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded())
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void consumerMutualAuth(String amWellToken) {
        awsdk.authenticateMutual(
                amWellToken,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null && isAdded()) {
                            AwsManager.getInstance().setAuthentication(authentication);
                            getConsumer(authentication);
                        } else {
                            Toast.makeText(getContext(), "Error: " + sdkError.getHttpResponseCode() + "\n" + sdkError.getMessage(), Toast.LENGTH_LONG).show();
                            if (isAdded())
                                progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        if (isAdded())
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getConsumer(Authentication authentication) {
        awsdk.getConsumerManager().getConsumer(
                authentication,
                new SDKCallback<Consumer, SDKError>() {
                    @Override
                    public void onResponse(Consumer consumer, SDKError sdkError) {
                        if (sdkError == null && isAdded()) {
                            AwsManager.getInstance().setConsumer(consumer);
                            progressBar.setVisibility(View.GONE);
                            ((NavigationActivity) getActivity()).loadFragment(
                                    Constants.ActivityTag.MY_CARE_NOW, null);

                            getServices();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void getServices() {
        if (AwsManager.getInstance().getConsumer() == null)
            return;
        awsdk.getPracticeProvidersManager().getPractices(
                AwsManager.getInstance().getConsumer(),
                new SDKCallback<List<Practice>, SDKError>() {
                    @Override
                    public void onResponse(List<Practice> practices, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setPractices(practices);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_NOW_SDK_LOGIN;
    }
}
