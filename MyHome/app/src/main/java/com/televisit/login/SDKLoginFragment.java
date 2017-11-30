package com.televisit.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsUserAuthentication;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SDKLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SDKLoginFragment extends BaseFragment implements AwsUserAuthentication, AwsInitialization, AwsConsumer {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.mycare_now));
        View view = inflater.inflate(R.layout.fragment_login2, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.login_progress);
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().initializeAwsdk(BuildConfig.awsdkurl, BuildConfig.awsdkkey, null, this);
        return view;
    }

//    private void getServices() {
//        if (AwsManager.getInstance().getConsumer() == null)
//            return;
//        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().getPractices(
//                AwsManager.getInstance().getConsumer(),
//                new SDKCallback<List<Practice>, SDKError>() {
//                    @Override
//                    public void onResponse(List<Practice> practices, SDKError sdkError) {
//                        if (sdkError == null) {
//                            AwsManager.getInstance().setPractices(practices);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//
//                    }
//                });
//    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        //return Constants.ActivityTag.MY_CARE_NOW_SDK_LOGIN;
        return null;
    }

    @Override
    public void initializationComplete() {
        if (BuildConfig.awsdkurl.equals("https://sdk.myonlinecare.com")) {
            //Dev
            AwsManager.getInstance().getUsersAuthentication("cmajji@mailinator.com", "Pass123*", SDKLoginFragment.this);
        } else {
            //IoT
            AwsManager.getInstance().getUsersAuthentication("julie.testing@mailinator.com", "Password1", SDKLoginFragment.this);
        }
    }

    @Override
    public void initializationFailed(String errorMessage) {
        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void authenticationComplete(Authentication authentication) {
        AwsManager.getInstance().getConsumer(authentication, this);
    }

    @Override
    public void authentciationFailed(String errorMessage) {
        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void consumerComplete(Consumer consumer) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);

            ((NavigationActivity) getActivity()).loadFragment(
                    Constants.ActivityTag.MY_CARE_NOW, null);
        }
    }

    @Override
    public void consumerFailed(String errorMessage) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
