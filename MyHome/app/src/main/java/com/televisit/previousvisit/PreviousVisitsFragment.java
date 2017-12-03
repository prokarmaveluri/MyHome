package com.televisit.previousvisit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static com.televisit.summary.SummaryFragment.VISIT_LIST_POSITION;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviousVisitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousVisitsFragment extends BaseFragment {

    private RecyclerView list;
    private ProgressBar progressBar;
    private PreviousVisitsAdapter adapter;
    private int reqCount = 0;

    public static final String PREVIOUS_VISITS_TAG = "previous_visits_tag";

    public PreviousVisitsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareFragment.
     */
    public static PreviousVisitsFragment newInstance() {
        PreviousVisitsFragment fragment = new PreviousVisitsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_previous_visits, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.previous_visit_summaries));

        list = (RecyclerView) view.findViewById(R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.req_progress);

        getPreviousVisits();

        //bindList();

        progressBar.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);

        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PREVIOUS_VISITS_SUMMARIES;
    }

    private void bindList() {

        adapter = new PreviousVisitsAdapter(getActivity(), AwsManager.getInstance().getVisitReports(), new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                if (position >= 0 && position < AwsManager.getInstance().getVisitReports().size()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(VISIT_LIST_POSITION, position);
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY, bundle);
                } else {
                    Timber.d("PreviousVisits: position out of bounds index. ");
                }
            }

            @Override
            public void onPinClick(Object model, int position) {
            }
        });

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (AwsManager.getInstance().getVisitReports() != null && AwsManager.getInstance().getVisitReports().size() > 0) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            list.addItemDecoration(itemDecoration);
        }
    }

    private void getPreviousVisits() {

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            Timber.d("visits isHasInitializedAwsdk: FALSE ");
            return;
        }

        reqCount++;
        progressBar.setVisibility(View.VISIBLE);

        final boolean scheduledOnly = false;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6);
        Date dateSince = calendar.getTime();

        if (AwsManager.getInstance().getVisitReports() == null || AwsManager.getInstance().getVisitReports().isEmpty()) {
            Timber.d("visits before: 0 ");
        } else {
            Timber.d("visits before: " + AwsManager.getInstance().getVisitReports().size());
        }

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReports(
                AwsManager.getInstance().getConsumer(),
                new SDKLocalDate(dateSince.getYear(), dateSince.getMonth(), dateSince.getDay()), scheduledOnly,
                new SDKCallback<List<VisitReport>, SDKError>() {
                    @Override
                    public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {

                        if (sdkError == null) {
                            AwsManager.getInstance().setVisitReports(visitReports);

                            /*CommonUtil.log(this.getClass().getSimpleName(), "visits after: " + AwsManager.getInstance().getVisitReports().size());

                            if (visitReports != null && visitReports.size() > 0) {
                                for (VisitReport visitReport : visitReports) {
                                    getVisitReportDetails(visitReport);
                                }
                            } else {
                                HashMap<VisitReport, VisitReportDetail> map = AwsManager.getInstance().getVisitReportDetailHashMap();
                                map.clear();
                                AwsManager.getInstance().setVisitReportDetailHashMap(map);
                            }

                            CommonUtil.log(this.getClass().getSimpleName(), "visits map after: " + AwsManager.getInstance().getVisitReportDetailHashMap().size());
                            */

                            bindList();
                            adapter.notifyDataSetChanged();
                        } else {
                            Timber.d("visits. sdkError not NULL. getMessage = " + sdkError.getMessage());
                            Timber.d("visits. sdkError not NULL. getSDKErrorReason = " + sdkError.getSDKErrorReason());
                        }

                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.d("visits. onFailure. getMessage = " + throwable.getMessage());
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }
}
