package com.televisit.providers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
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
import com.televisit.AwsManager;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareProvidersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareProvidersFragment extends BaseFragment implements ProvidersListAdapter.IProviderClick {

    private PracticeInfo practiceInfo;
    private List<ProviderInfo> providerInfo;
    private ProgressBar progressBar;
    private RecyclerView providerList;
    private Button nextAvailableProvider;
    public static final String MY_CARE_PROVIDERS_TAG = "my_care_providers_tag";

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
        MyCareProvidersFragment fragment = new MyCareProvidersFragment();
        return fragment;
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

        View view = inflater.inflate(R.layout.fragment_my_care_providers, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.providers_progress);
        nextAvailableProvider = (Button) view.findViewById(R.id.btn_next_avail_provider);
        providerList = (RecyclerView) view.findViewById(R.id.providerList);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        providerList.addItemDecoration(itemDecoration);

        getProviders();
        return view;
    }

    private void getProviders() {
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().findProviders(
                AwsManager.getInstance().getConsumer(),
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
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void setListAdapter(List<ProviderInfo> providers) {
        if (null != getActivity() && isAdded() && providers != null) {
            providerList.setVisibility(View.VISIBLE);
            providerList.setLayoutManager(new LinearLayoutManager(getActivity()));
            providerList.setAdapter(new ProvidersListAdapter(getContext(), providers, this));
        } else {
            providerList.setVisibility(View.GONE);
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
                    Toast.makeText(getContext(), "No Provider is available.\nPlease Try Again Later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void providerClick(ProviderInfo provider) {
        if (provider != null && provider.getVisibility() != ProviderVisibility.OFFLINE) {
            progressBar.setVisibility(View.VISIBLE);
            getVisitContext(provider);
        } else if (providerInfo != null && provider.getVisibility() == ProviderVisibility.OFFLINE) {
            Toast.makeText(getActivity(), provider.getFullName() + " is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROVIDERS;
    }

    private void getVisitContext(ProviderInfo info) {
        AwsManager.getInstance().getAWSDK()
                .getVisitManager().getVisitContext(AwsManager.getInstance().getConsumer(),
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
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void setLegalTextsAccepted(boolean accepted, VisitContext visitContext) {
        for (LegalText legalText : visitContext.getLegalTexts()) {
            legalText.setAccepted(accepted);
        }
    }

    public void setShareHealthSummary(VisitContext visitContext) {
        visitContext.setShareHealthSummary(true);
    }
}
