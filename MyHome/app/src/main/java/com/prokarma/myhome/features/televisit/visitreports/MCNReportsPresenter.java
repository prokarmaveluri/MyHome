package com.prokarma.myhome.features.televisit.visitreports;

import android.content.Context;
import android.view.View;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.app.BaseFragment;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsPresenter implements MCNReportsContract.Presenter, MCNReportsContract.InteractorOutput {
    Context context;
    MCNReportsContract.View view;
    MCNReportsContract.Router router;
    MCNReportsContract.Interactor interactor;

    public MCNReportsPresenter(final Context context, final BaseFragment fragment, final View view) {
        this.context = context;
        this.view = new MCNReportsView(view, this);
        this.router = new MCNReportsRouter(fragment);
        this.interactor = new MCNReportsInteractor(this);
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
