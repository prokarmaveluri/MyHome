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

import java.util.Date;
import java.util.List;

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

        bindList();

        progressBar.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);

        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PREVIOUS_VISITS_SUMMARY;
    }

    private void bindList() {

        adapter = new PreviousVisitsAdapter(getActivity(), AwsManager.getInstance().getVisitReports(), new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                /*
                Bundle bundle = new Bundle();
                bundle.putParcelable(VISIT_END_REASON_KEY, visitReport);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.SUMMARY_TAG, bundle); */
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

        if (!AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        reqCount++;
        progressBar.setVisibility(View.VISIBLE);

        Date currentDate = new Date();

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReports(
                AwsManager.getInstance().getConsumer(),
                new SDKLocalDate(currentDate.getYear(), currentDate.getMonth(), currentDate.getDay()), true,
                new SDKCallback<List<VisitReport>, SDKError>() {
                    @Override
                    public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisitReports(visitReports);

                            bindList();
                            adapter.notifyDataSetChanged();
                        }
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
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
