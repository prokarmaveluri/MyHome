package com.prokarma.myhome.features.televisit.medications;

import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.app.BaseRouter;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsSearchMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateMedications;

import java.util.List;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsContract {

    public interface View {
        void showMedications(List<Medication> medications, String errorMessage);
        void showSearchMedications(List<Medication> medications, String errorMessage);
        void showUpdateMedications(List<Medication> medications, String errorMessage);
    }

    public interface Presenter extends BasePresenter {
        void onActivityCreated();
        void updateMedications();
        void searchMedications(String searchText);
    }

    public interface Interactor {
        void getMedications();
        void updateMedications(List<Medication> medicationsToSave);
        void searchMedications(String searchText);
    }

    public interface InteractorOutput {
        void receivedMedications(List<Medication> medications, String errorMessage);
        void receivedUpdateMedications(List<Medication> medications, String errorMessage);
        void receivedSearchMedications(List<Medication> medications, String errorMessage);
    }

    public interface Router extends BaseRouter {
    }
}
