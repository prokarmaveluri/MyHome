package com.televisit.visitreports;

import android.content.Context;
import android.view.View;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.app.BaseFragment;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class ReportsPresenter implements ReportsContract.Presenter, ReportsContract.InteractorOutput {
    Context context;
    ReportsContract.View view;
    ReportsContract.Router router;
    ReportsContract.Interactor interactor;

    public ReportsPresenter(final Context context, final BaseFragment fragment, final View view) {
        this.context = context;
        this.view = new ReportsView(view, this);
        this.router = new ReportsRouter(fragment);
        this.interactor = new ReportsInteractor(this);
    }

    @Override
    public void onCreate() {
        interactor.getVisitReports();
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onSummaryItemPressed(int position) {
        router.goToSummaryDetail(position);
    }

    @Override
    public void receivedVisitReports(List<VisitReport> visitReports, String errorMessage) {
        view.showVisitReports(visitReports, errorMessage);
    }
}
