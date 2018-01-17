package com.televisit.previousvisit;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;
import static com.televisit.summary.VisitSummaryFragment.VISIT_LIST_POSITION;

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
        return new PreviousVisitsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_previous_visits, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.previous_visit_summaries));

        list = (RecyclerView) view.findViewById(R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.req_progress);

        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PREVIOUS_VISITS_SUMMARIES;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPreviousVisits();
    }

    private void setAdapter() {

        adapter = new PreviousVisitsAdapter(getActivity(), AwsManager.getInstance().getVisitReports(), new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                if (position >= 0 && position < AwsManager.getInstance().getVisitReports().size()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(VISIT_LIST_POSITION, position);
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY, bundle);
                }
            }

            @Override
            public void onPinClick(Object model, int position) {
            }
        });

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        try {
            if (AwsManager.getInstance().getVisitReports() != null && AwsManager.getInstance().getVisitReports().size() > 0) {
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
                list.addItemDecoration(itemDecoration);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPreviousVisits() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            progressBar.setVisibility(View.GONE);
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        reqCount++;

        progressBar.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);

        final boolean scheduledOnly = false;

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReports(
                AwsManager.getInstance().getPatient(),
                null, scheduledOnly,
                new SDKCallback<List<VisitReport>, SDKError>() {
                    @Override
                    public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {

                        if (sdkError == null) {
                            Collections.sort(visitReports, new VisitReportComparator());
                            AwsManager.getInstance().setVisitReports(visitReports);
                            setAdapter();
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
