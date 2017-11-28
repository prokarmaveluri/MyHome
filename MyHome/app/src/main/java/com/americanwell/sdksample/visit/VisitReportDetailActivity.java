/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.visit.adapters.VisitReportAdapter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to fetch and display the {@link VisitReportDetail} object
 */
@RequiresPresenter(VisitReportDetailPresenter.class)
public class VisitReportDetailActivity extends BaseSampleNucleusActivity<VisitReportDetailPresenter> {

    private static final String LOG_TAG = VisitReportDetailActivity.class.getName();

    private static final String EXTRA_VISIT_REPORT = "visitReport";

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    VisitReportAdapter adapter;

    public static Intent makeIntent(final Context context, final VisitReport visit) {
        final Intent intent = new Intent(context, VisitReportDetailActivity.class);
        intent.putExtra(EXTRA_VISIT_REPORT, visit);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_visit_report_detail);

        if (savedInstanceState == null) {
            getPresenter().setVisitReport((VisitReport) getIntent().getParcelableExtra(EXTRA_VISIT_REPORT));
        }
        DefaultLogger.d(LOG_TAG, "initialized lists and adapters");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        adapter = new VisitReportAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void setVisitReportDetail(VisitReportDetail report, Locale locale, String currencyCode) {
        adapter.setVisitReportDetail(report, locale, currencyCode);
    }

    @OnClick(R.id.fab)
    public void next() {
        getPresenter().viewPDF();
    }
}
