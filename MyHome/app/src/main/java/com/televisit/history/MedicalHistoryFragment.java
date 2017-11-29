package com.televisit.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.SDKUtils;

import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicalHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalHistoryFragment extends BaseFragment implements HistoryListAdapter.GroupSelectionListener {

    private IndexFastScrollRecyclerView expandableList;
    private ProgressBar progressBar;
    private HistoryListAdapter adapter;
    private Menu menu;
    public HistoryListAdapter.GROUP selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
    private int reqCount = 0;

    public static final String MED_HISTORY_TAG = "history_view_tag";

    public MedicalHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareFragment.
     */
    public static MedicalHistoryFragment newInstance() {
        MedicalHistoryFragment fragment = new MedicalHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.med_history));

        expandableList = (IndexFastScrollRecyclerView) view.findViewById(R.id.expandableList);
        progressBar = (ProgressBar) view.findViewById(R.id.req_progress);

        selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
        bindList();

        if (SDKUtils.getInstance().getConditions() != null && SDKUtils.getInstance().getConditions().size() > 0) {
            progressBar.setVisibility(View.GONE);
            expandableList.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            expandableList.setVisibility(View.GONE);
            getConditions();
            getAllergies();
        }
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_MED_HISTORY;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.intake_menu, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:

                if (selectedGroup == HistoryListAdapter.GROUP.CONDITIONS) {
                    showAllergies();
                } else {
                    updateConditions();
                    updateAllergies();

                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showConditions() {
        selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
        bindList();
        adapter.notifyDataSetChanged();

        if (menu != null && menu.getItem(0) != null) {
            menu.getItem(0).setTitle("Next");
        }
    }

    private void showAllergies() {
        selectedGroup = HistoryListAdapter.GROUP.ALLERGIES;
        bindList();
        adapter.notifyDataSetChanged();

        if (menu != null && menu.getItem(0) != null) {
            menu.getItem(0).setTitle("Done");
        }
    }

    private void bindList() {

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        expandableList.setLayoutManager(llm);

        expandableList.setIndexBarTextColor("#" + Integer.toHexString(getResources().getColor(R.color.primary)));
        expandableList.setIndexBarColor("#" + Integer.toHexString(getResources().getColor(R.color.white)));

        adapter = new HistoryListAdapter(getActivity(), selectedGroup,
                SDKUtils.getInstance().getConditions(),
                SDKUtils.getInstance().getAllergies(), this);
        expandableList.setAdapter(adapter);
        CommonUtil.setExpandedListViewHeight(getContext(), expandableList);
    }

    private void getConditions() {

        reqCount++;
        progressBar.setVisibility(View.VISIBLE);
        SDKUtils.getInstance().getAWSDK().getConsumerManager().getConditions(
                SDKUtils.getInstance().getConsumer(),
                new SDKCallback<List<Condition>, SDKError>() {
                    @Override
                    public void onResponse(List<Condition> conditions, SDKError sdkError) {
                        if (sdkError == null) {
                            SDKUtils.getInstance().setConditions(conditions);

                            selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
                            bindList();
                            adapter.notifyDataSetChanged();
                        }
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            expandableList.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            expandableList.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    private void getAllergies() {

        reqCount++;
        progressBar.setVisibility(View.VISIBLE);
        SDKUtils.getInstance().getAWSDK().getConsumerManager().getAllergies(
                SDKUtils.getInstance().getConsumer(),
                new SDKCallback<List<Allergy>, SDKError>() {
                    @Override
                    public void onResponse(List<Allergy> allergies, SDKError sdkError) {
                        if (sdkError == null) {
                            SDKUtils.getInstance().setAllergies(allergies);
                        }
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            expandableList.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        reqCount--;
                        if (reqCount == 0) {
                            progressBar.setVisibility(View.GONE);
                            expandableList.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void updateConditions() {
        progressBar.setVisibility(View.VISIBLE);
        SDKUtils.getInstance().getAWSDK().getConsumerManager().updateConditions(
                SDKUtils.getInstance().getConsumer(),
                SDKUtils.getInstance().getConditions(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void updateAllergies() {
        progressBar.setVisibility(View.VISIBLE);
        SDKUtils.getInstance().getAWSDK().getConsumerManager().updateAllergies(
                SDKUtils.getInstance().getConsumer(),
                SDKUtils.getInstance().getAllergies(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    public void selectedItem(int groupSelected, int childPosition) {

        if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == groupSelected) {
            if (childPosition == 0) {
                for (Condition condition : SDKUtils.getInstance().getConditions()) {
                    condition.setCurrent(false);
                }
            } else {
                SDKUtils.getInstance().getConditions().get(childPosition - 1).setCurrent(
                        !SDKUtils.getInstance().getConditions().get(childPosition - 1).isCurrent());
            }
        } else {
            if (childPosition == 0) {
                for (Allergy allergy : SDKUtils.getInstance().getAllergies()) {
                    allergy.setCurrent(false);
                }
            } else {
                SDKUtils.getInstance().getAllergies().get(childPosition - 1).setCurrent(
                        !SDKUtils.getInstance().getAllergies().get(childPosition - 1).isCurrent());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
