package com.prokarma.myhome.features.televisit.visitreports;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.visitreports.ui.MCNReportsAdapter;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsView implements MCNReportsContract.View {

    private RecyclerView list;
    private ProgressBar progressBar;
    private MCNReportsPresenter presenter;

    public MCNReportsView(final View view, final MCNReportsPresenter presenter) {
        this.presenter = presenter;
        this.list = (RecyclerView) view.findViewById(R.id.list);
        this.progressBar = (ProgressBar) view.findViewById(R.id.req_progress);
    }

    @Override
    public void showVisitReports(List<VisitReport> visitReports, String errorMessage) {

        MCNReportsAdapter adapter = new MCNReportsAdapter(presenter.context, AwsManager.getInstance().getVisitReports(), new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                presenter.onSummaryItemPressed(position);
            }

            @Override
            public void onPinClick(Object model, int position) {
            }
        });

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(presenter.context, LinearLayoutManager.VERTICAL, false));

        if (AwsManager.getInstance().getVisitReports() != null && AwsManager.getInstance().getVisitReports().size() > 0) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(presenter.context, DividerItemDecoration.VERTICAL);
            list.addItemDecoration(itemDecoration);

            progressBar.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            //handle error message

            progressBar.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
