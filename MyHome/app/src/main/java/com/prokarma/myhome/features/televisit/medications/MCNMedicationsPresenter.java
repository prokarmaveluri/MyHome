package com.prokarma.myhome.features.televisit.medications;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.americanwell.sdk.entity.health.Medication;
import com.prokarma.myhome.app.BaseFragment;

import java.util.List;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsPresenter implements MCNMedicationsContract.Presenter, MCNMedicationsContract.InteractorOutput {
    Activity activity;
    Context context;
    MCNMedicationsContract.View view;
    MCNMedicationsContract.Router router;
    MCNMedicationsContract.Interactor interactor;

    public MCNMedicationsPresenter(final Context context, final Activity activity, final BaseFragment fragment, final View view) {
        this.context = context;
        this.activity = activity;
        this.view = new MCNMedicationsView(view, this);
        this.router = new MCNMedicationsRouter(fragment);
        this.interactor = new MCNMedicationsInteractor(this);
    }

    @Override
    public void onCreate() {
        interactor.getMedications(null);
    }

    @Override
    public void onActivityCreated() {
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    public void updateMedications() {
        interactor.updateMedications(null);
    }

    public void searchMedications(String searchText) {
        interactor.searchMedications(searchText,null);
    }


    @Override
    public void receivedMedications(List<Medication> medications, String errorMessage) {
        view.showMedications(medications, errorMessage);
    }

    @Override
    public void receivedUpdateMedications(List<Medication> medications, String errorMessage) {
        view.showUpdateMedications(medications, errorMessage);
    }

    @Override
    public void receivedSearchMedications(List<Medication> medications, String errorMessage) {
        view.showSearchMedications(medications, errorMessage);
    }
}
