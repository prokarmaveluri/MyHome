package com.prokarma.myhome.features.televisit.medications.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.medications.MCNMedicationsContract;
import com.prokarma.myhome.features.televisit.medications.MCNMedicationsPresenter;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MedicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationsFragment extends BaseFragment {

    public static final String MEDICATIONS_TAG = "medications_tag";
    MCNMedicationsContract.Presenter presenter;


    public MedicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static MedicationsFragment newInstance() {
        return new MedicationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.medications));
            ((NavigationActivity) getActivity()).setActionBarLineVisibility(false);
        } else {
            getActivity().setTitle(getString(R.string.medications));
        }

        View view = inflater.inflate(R.layout.fragment_medications, container, false);

        setHasOptionsMenu(true);

        presenter = new MCNMedicationsPresenter(getContext(), this.getActivity(), this, view);
        presenter.onCreate();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.onActivityCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_MEDICATIONS_SCREEN, null);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.medications_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save_medications:
                presenter.updateMedications();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_MEDICATIONS;
    }

    /*
    private void saveMedications() {

        if (noMedicationsCheckbox.isChecked()) {
            medicationsListToSave.clear();
        }

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateMedications(
                AwsManager.getInstance().getPatient(),
                medicationsListToSave,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);

                        AwsManager.getInstance().setMedications(medicationsListToSave);
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("updateMedications. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void searchMedications(final String searchText) {

        //Amwell searchMedications() API required 3 characters or greater.
        //35013: to address this bug, trim() had to be removed on the search so we could identify " " as valid use-case for searching and so NoResults could be shown.
        if (searchText.length() < 3) {
            noResults.setVisibility(View.VISIBLE);
            searchSuggestions.setVisibility(View.GONE);
            showSearchView();
        } else {
            noResults.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().searchMedications(
                AwsManager.getInstance().getPatient(),
                searchText,
                new SDKValidatedCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        searchSuggestions.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        searchList = medications;
                        progressBar.setVisibility(View.GONE);

                        if (sdkError == null) {
                            setSuggestionAdapter(getSuggestions(medications));

                            if (medications == null || medications.size() == 0) {
                                noResults.setVisibility(View.VISIBLE);
                                searchSuggestions.setVisibility(View.GONE);
                                showSearchView();
                            } else {
                                noResults.setVisibility(View.GONE);
                                searchSuggestions.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Timber.e("searchMedications. Error + " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (isAdded()) {
                            Timber.e("searchMedications. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            searchSuggestions.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }*/


}
