package com.televisit.visitreports;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.televisit.common.BasePresenter;
import com.televisit.common.BaseRouter;

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
