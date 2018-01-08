package com.televisit.providers;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.televisit.AwsManager;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareProvidersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareProvidersFragment extends BaseFragment implements ProvidersListAdapter.IProviderClick {
    public static final String MY_CARE_PROVIDERS_TAG = "my_care_providers_tag";

    private Consumer patient;

    private PracticeInfo practiceInfo;
    private List<ProviderInfo> providerInfo;
    private ProgressBar progressBar;
    private RecyclerView providerList;
    private Button nextAvailableProvider;
    private TextView chooseText;
    private final int REFRESH_INTERVAL_SECONDS = 5 * 60;
    private Handler refreshHandler;

    public MyCareProvidersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareProvidersFragment newInstance() {
        return new MyCareProvidersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            practiceInfo = getArguments().getParcelable("PracticeInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.choose_doctor));

        CommonUtil.setTitle(getActivity(), CommonUtil.isAccessibilityEnabled(getActivity()) ? getResources().getString(R.string.choose_doctor) : getResources().getString(R.string.choose_doctor), true);

        View view = inflater.inflate(R.layout.fragment_my_care_providers, container, false);

        chooseText = (TextView) view.findViewById(R.id.choose_text);
        progressBar = (ProgressBar) view.findViewById(R.id.providers_progress);
        nextAvailableProvider = (Button) view.findViewById(R.id.btn_next_avail_provider);
        providerList = (RecyclerView) view.findViewById(R.id.providerList);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        providerList.addItemDecoration(itemDecoration);

        patient = AwsManager.getInstance().getPatient() != null ? AwsManager.getInstance().getPatient() : AwsManager.getInstance().getConsumer();

        getProviders();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PROVIDERS_SCREEN, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AwsManager.getInstance().getPatient() != null) {
            chooseText.setText(AwsManager.getInstance().getPatient().getFirstName() + ", "
                    + getContext().getString(R.string.my_care_providers_desc));
            chooseText.setContentDescription(chooseText.getText());
        }
    }


    private void refreshPeriodically() {

        refreshHandler = new Handler();
        refreshHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (refreshHandler != null) {

                            getProviders();
                            refreshHandler.postDelayed(this, REFRESH_INTERVAL_SECONDS * 1000);
                        }
                    }
                }, REFRESH_INTERVAL_SECONDS * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPeriodically();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshHandler = null;
    }

    private void getProviders() {
        try {
            Timber.d("Refresh. Loading Providers");

            showLoading();

            //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
            //after that error, SDK throws following: IllegalArgumentException: sdk initialization is missing

            if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
                return;
            }

            if (!isAdded()) {
                Timber.d("Refresh. fragment not attached yet.");
                return;
            }

            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().findProviders(
                    patient,
                    practiceInfo,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new SDKCallback<List<ProviderInfo>, SDKError>() {
                        @Override
                        public void onResponse(List<ProviderInfo> providerInfos, SDKError sdkError) {
                            if (sdkError == null) {
                                providerInfo = providerInfos;
                                setNextAvailableProviderButton();
                                setListAdapter(providerInfo);
                            }
                            showList();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            showList();
                        }
                    }
            );

        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }
    }

    private void setListAdapter(List<ProviderInfo> providers) {
        if (isAdded() && providers != null) {
            providerList.setLayoutManager(new LinearLayoutManager(getActivity()));
            providerList.setAdapter(new ProvidersListAdapter(getContext(), providers, this));
        }
        this.providerInfo = providers;
    }

    private void setNextAvailableProviderButton() {
        nextAvailableProvider.setEnabled(providerInfo != null && !providerInfo.isEmpty());
        nextAvailableProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProviderInfo provider = CommonUtil.getNextAvailableProvider(providerInfo);
                if (provider != null) {
                    getVisitContext(provider);
                } else {
                    CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available));
                }
            }
        });
    }

    @Override
    public void providerClick(ProviderInfo provider) {
        if (providerInfo != null && provider.getVisibility() == ProviderVisibility.OFFLINE) {
            CommonUtil.showToast(getActivity(), provider.getFullName() + " " + getActivity().getString(R.string.is_not_available));
        } else if (provider != null && provider.getVisibility() != ProviderVisibility.OFFLINE) {
            getVisitContext(provider);
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROVIDERS;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        providerList.setVisibility(View.GONE);
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        providerList.setVisibility(View.VISIBLE);
    }

    private void getVisitContext(ProviderInfo info) {
        AwsManager.getInstance().getAWSDK()
                .getVisitManager().getVisitContext(
                patient,
                info, new SDKCallback<VisitContext, SDKError>() {
                    @Override
                    public void onResponse(VisitContext visitContext, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisitContext(visitContext);
                            if (isAdded() && getActivity() != null) {
                                ((NavigationActivity) getActivity()).loadFragment(
                                        Constants.ActivityTag.MY_CARE_COST, null);
                            }
                            setLegalTextsAccepted(true, visitContext);
                            setShareHealthSummary(visitContext);
                        } else {
                            Timber.e("getVisitContext. Error + " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("getVisitContext. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                        }
                    }
                });
    }

    private void setLegalTextsAccepted(boolean accepted, VisitContext visitContext) {
        for (LegalText legalText : visitContext.getLegalTexts()) {
            legalText.setAccepted(accepted);
        }
    }

    private void setShareHealthSummary(VisitContext visitContext) {
        visitContext.setShareHealthSummary(true);
    }
}
