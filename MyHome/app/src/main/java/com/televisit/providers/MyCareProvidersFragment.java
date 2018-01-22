package com.televisit.providers;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.MatchmakerCallback;
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
    private OnDemandSpecialty specialty;
    private PracticeInfo practiceInfo;
    private List<ProviderInfo> providerInfos;
    private ProgressBar progressBar;
    private RecyclerView providerList;
    private View divider;
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
        divider = (View) view.findViewById(R.id.divider);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        providerList.addItemDecoration(itemDecoration);

        patient = AwsManager.getInstance().getPatient() != null ? AwsManager.getInstance().getPatient() : AwsManager.getInstance().getConsumer();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (patient != null) {
            chooseText.setText(patient.getFirstName() + ", " + getContext().getString(R.string.my_care_providers_desc));
            chooseText.setContentDescription(chooseText.getText());
        }

        nextAvailableProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAvailableProviderClick();
            }
        });

        getPractices();
    }


    private void refreshPeriodically() {

        refreshHandler = new Handler();
        refreshHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (refreshHandler != null) {

                            getProviders();

                            if (specialty == null) {
                                getSpecialties();
                            }

                            refreshHandler.postDelayed(this, REFRESH_INTERVAL_SECONDS * 1000);
                        }
                    }
                }, REFRESH_INTERVAL_SECONDS * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPeriodically();
        TealiumUtil.trackView(Constants.MCN_PROVIDERS_SCREEN, null);
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

    @Override
    public void providerClick(ProviderInfo provider) {
        if (provider != null && provider.getVisibility() == ProviderVisibility.OFFLINE) {
            CommonUtil.showToast(getActivity(), provider.getFullName() + " " + getActivity().getString(R.string.is_not_available));
        } else if (provider != null && provider.getVisibility() != ProviderVisibility.OFFLINE) {
            getVisitContextByProvider(provider);
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

    private void setListAdapter() {
        if (isAdded() && providerInfos != null) {
            providerList.setLayoutManager(new LinearLayoutManager(getActivity()));
            providerList.setAdapter(new ProvidersListAdapter(getContext(), providerInfos, this));

            if (providerInfos.size() > 0) {
                divider.setVisibility(View.VISIBLE);
            }
            else {
                divider.setVisibility(View.GONE);
            }
        }
    }

    private void setNextAvailableProviderButton() {

        /*if (specialty != null && specialty.areProvidersAvailable()) {
            nextAvailableProvider.setEnabled(true);
        } else {
            nextAvailableProvider.setEnabled(false);
        }*/

        // Rather than disabling, show appropriate message when tapped.
        // otherwise user maynot be aware as to why button is disabled and still some providers are ONLINE in the list below
        nextAvailableProvider.setEnabled(true);
    }

    private void nextAvailableProviderClick() {

        //as per AmwellSample app, we need to use specialty and matchmaking API to fetch the next available provider. iOS also implementing the same.
        if (specialty == null) {
            CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available_with_specialty_try_later));
        } else {
            getVisitContextBySpeciality();
        }

        //OLD LOGIC: from the list of doctors displayed in choose doctor, get the doctor who is Online and has no patients waiting for him. (basically No API call)
        /*ProviderInfo provider = CommonUtil.getNextAvailableProvider(providerInfos);
        if (provider != null) {
            getVisitContextByProvider(provider);
        } else {
            CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available));
        }*/
    }

    public void getPractices() {
        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().findPractices(
                patient, null,
                new SDKCallback<List<PracticeInfo>, SDKError>() {
                    @Override
                    public void onResponse(List<PracticeInfo> listPracticeInfo, SDKError sdkError) {

                        if (sdkError == null && listPracticeInfo != null && listPracticeInfo.size() > 0) {
                            practiceInfo = listPracticeInfo.get(0);

                            Timber.d("providers. practiceInfo Name = " + practiceInfo.getName());
                            if (practiceInfo.getPracticeType() != null) {
                                Timber.d("providers. practiceInfo PracticeType = " + practiceInfo.getPracticeType().toString());
                            }

                            getProviders();

                            getSpecialties();

                        } else {
                            Timber.e("providers. getPractices. Error = " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("providers. getPractices. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                    }
                });
    }

    public void getSpecialties() {

        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().getOnDemandSpecialties(
                patient,
                practiceInfo,
                null,
                new SDKCallback<List<OnDemandSpecialty>, SDKError>() {
                    @Override
                    public void onResponse(List<OnDemandSpecialty> listSpecialties, SDKError sdkError) {

                        if (sdkError == null && listSpecialties != null && listSpecialties.size() > 0) {
                            specialty = listSpecialties.get(0);
                            Timber.d("providers. getSpecialties. specialty areProvidersAvailable = " + specialty.areProvidersAvailable());

                        } else {
                            Timber.e("providers. getSpecialties. Error = " + sdkError);
                        }
                        setNextAvailableProviderButton();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("providers. getSpecialties. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        setNextAvailableProviderButton();
                    }
                });
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

            if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
                progressBar.setVisibility(View.GONE);
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
                        public void onResponse(List<ProviderInfo> providerInfoList, SDKError sdkError) {
                            if (sdkError == null) {
                                providerInfos = providerInfoList;
                                setListAdapter();
                            } else {
                                Timber.e("findProviders. Error = " + sdkError);
                            }
                            showList();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("findProviders. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            showList();
                        }
                    }
            );

        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }
    }

    private void getVisitContextBySpeciality() {

        AwsManager.getInstance().getAWSDK().getVisitManager().getVisitContext(
                patient,
                specialty,
                new SDKCallback<VisitContext, SDKError>() {
                    @Override
                    public void onResponse(VisitContext visitContext, SDKError sdkError) {
                        if (sdkError != null) {
                            Timber.e("providers. getVisitContextBySpeciality. Error = " + sdkError);
                            return;
                        }

                        setLegalTextsAccepted(true, visitContext);
                        setShareHealthSummary(visitContext);
                        AwsManager.getInstance().setVisitContext(visitContext);

                        getProviderByVisitContext(visitContext);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("providers. getVisitContextBySpeciality. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                    }
                });
    }

    private void getProviderByVisitContext(VisitContext visitContext) {

        AwsManager.getInstance().getAWSDK().getVisitManager().startMatchmaking(
                visitContext,
                new MatchmakerCallback() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.d("providers. startMatchmaking NO error ");
                        } else {
                            Timber.d("providers. startMatchmaking Error = " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("providers. startMatchmaking. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available_with_specialty_try_later));
                    }

                    @Override
                    public void onProviderFound(@NonNull Provider provider) {
                        if (provider != null) {
                            Timber.d("providers. startMatchmaking. onProviderFound = " + provider.getFullName());

                            CommonUtil.showToast(getContext(), getContext().getString(R.string.next_available_provider_is) + " " + provider.getFullName());

                            if (getActivity() != null) {
                                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_INTAKE, null);
                            }
                        }
                    }

                    @Override
                    public void onProviderListExhausted() {
                        Timber.d("providers. startMatchmaking. onProviderListExhausted ");
                        CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available_with_specialty_try_later));
                    }

                    @Override
                    public void onRequestGone() {
                        Timber.d("providers. startMatchmaking. onRequestGone ");
                        CommonUtil.showToast(getContext(), getContext().getString(R.string.no_provider_is_available_with_specialty_try_later));
                    }
                });
    }

    private void getVisitContextByProvider(ProviderInfo info) {
        AwsManager.getInstance().getAWSDK().getVisitManager().getVisitContext(
                patient,
                info, new SDKCallback<VisitContext, SDKError>() {
                    @Override
                    public void onResponse(VisitContext visitContext, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisitContext(visitContext);
                            if (isAdded() && getActivity() != null) {
                                ((NavigationActivity) getActivity()).loadFragment(
                                        Constants.ActivityTag.MY_CARE_INTAKE, null);
                            }
                            setLegalTextsAccepted(true, visitContext);
                            setShareHealthSummary(visitContext);
                        } else {
                            Timber.e("providers. getVisitContextByProvider. Error + " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("providers. getVisitContextByProvider. Something failed! :/");
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
