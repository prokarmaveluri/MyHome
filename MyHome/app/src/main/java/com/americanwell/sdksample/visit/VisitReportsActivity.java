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
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdksample.BaseSwipeToRefreshActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to fetch and display visitReport summary reports
 */
@RequiresPresenter(VisitReportsPresenter.class)
public class VisitReportsActivity extends BaseSwipeToRefreshActivity<VisitReportsPresenter, VisitReport> {

    public static Intent makeIntent(final Context context) {
        return new Intent(context, VisitReportsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_visit_reports);
    }

    @Override
    protected ArrayAdapter createAdapter() {
        return new VisitReportsAdapter(this);
    }

    @Override
    @StringRes
    protected int getTitleRes() {
        return R.string.title_activity_visit_reports;
    }

    @Override
    public void onRefresh() {
        getPresenter().getVisitReports();
    }

    public class VisitReportsAdapter extends ArrayAdapter<VisitReport> {

        public VisitReportsAdapter(Context context) {
            super(context, 0);
        }

        public class ViewHolder {
            @BindView(R.id.visit_report_datetime)
            TextView datetime;
            @BindView(R.id.visit_report_provider)
            TextView provider;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_visit_report, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final VisitReport visitReport = getItem(position);
            try {
                viewHolder.datetime.setText(visitReport.getDate().toString(getString(R.string.dateFormat)));
            }
            catch (ParseException e) {
                throw new RuntimeException("invalid date format");
            }

            viewHolder.provider.setText(visitReport.getProviderName());

            // set item on-click to open details
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = VisitReportDetailActivity
                            .makeIntent(VisitReportsActivity.this, visitReport);
                    startActivity(intent);
                }
            });
            return view;
        }

    }

}
