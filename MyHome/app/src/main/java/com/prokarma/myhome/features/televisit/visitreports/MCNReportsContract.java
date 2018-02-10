package com.prokarma.myhome.features.televisit.visitreports;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.app.BaseRouter;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsContract {

    public interface View {
        void showVisitReports(List<VisitReport> visitReports, String errorMessage);
    }

    public interface Presenter extends BasePresenter {
        void onSummaryItemPressed(int position);
    }

    public interface Interactor {
        void getVisitReports();
    }

    public interface InteractorOutput {
        void receivedVisitReports(List<VisitReport> visitReports, String errorMessage);
    }

    public interface Router extends BaseRouter {
        void goToSummaryDetail(int position);
    }
}
