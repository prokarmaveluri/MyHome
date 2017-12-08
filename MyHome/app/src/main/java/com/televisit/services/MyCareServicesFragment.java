package com.televisit.services;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareServicesFragment extends BaseFragment implements ServicesListAdapter.IServiceClick {

    private RecyclerView servicesList;
    private ProgressBar progressBar;
    private List<Practice> practices;

    public static final String MY_CARE_SERVICES_TAG = "my_care_services_tag";

    public MyCareServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareServicesFragment newInstance() {
        return new MyCareServicesFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_care_services, container, false);

        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.choose_practice));
        servicesList = (RecyclerView) view.findViewById(R.id.servicesList);
        progressBar = (ProgressBar) view.findViewById(R.id.services_progress);
        getServices();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getServices() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK()
                .getPracticeProvidersManager().getPractices(AwsManager.getInstance().getConsumer(),
                new SDKCallback<List<Practice>, SDKError>() {
                    @Override
                    public void onResponse(List<Practice> practices, SDKError sdkError) {
                        if (sdkError == null) {
                            setListAdapter(practices);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setListAdapter(List<Practice> practice) {
        if (null != getActivity() && isAdded() && practice != null) {
            servicesList.setVisibility(View.VISIBLE);
            servicesList.setLayoutManager(new LinearLayoutManager(getActivity()));
            servicesList.setAdapter(new ServicesListAdapter(practice, this));
        } else {
            servicesList.setVisibility(View.GONE);
        }
        practices = practice;
    }

    @Override
    public void serviceClick(int position) {
        try {
            Practice practice = practices.get(position);

            ((NavigationActivity) getActivity()).loadFragment(
                    Constants.ActivityTag.MY_CARE_PROVIDERS, null);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_SERVICES;
    }
}
