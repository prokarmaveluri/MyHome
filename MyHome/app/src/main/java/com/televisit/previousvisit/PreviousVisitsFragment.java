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
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;

import static com.prokarma.myhome.features.profile.ProfileManager.appointments;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviousVisitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousVisitsFragment extends BaseFragment {

    private RecyclerView list;
    private ProgressBar progressBar;
    private PreviousVisitsAdapter adapter;

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

        appointments = CommonUtil.getFutureAppointments(appointments);

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

        adapter = new PreviousVisitsAdapter(getActivity(), appointments, false, new RecyclerViewListener() {
            @Override
            public void onItemClick(Object model, int position) {
                /*
                Bundle bundle = new Bundle();
                bundle.putParcelable(VISIT_END_REASON_KEY, appointment);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.SUMMARY_TAG, bundle); */
            }

            @Override
            public void onPinClick(Object model, int position) {
                Appointment appointment = (Appointment) model;

                if (model == null || ((Appointment) model).facilityAddress == null) {
                    Toast.makeText(getContext(), getString(R.string.directions_not_found), Toast.LENGTH_LONG).show();
                } else {
                    CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
                }
            }
        });

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (appointments != null && appointments.size() > 0) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            list.addItemDecoration(itemDecoration);
        }
    }
}
