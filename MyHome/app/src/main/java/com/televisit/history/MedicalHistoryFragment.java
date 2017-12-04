package com.televisit.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;

import java.util.ArrayList;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicalHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalHistoryFragment extends BaseFragment implements
        HistoryListAdapter.GroupSelectionListener,
        TextWatcher {
    // TextView.OnEditorActionListener,

    private LinearLayout searchLayout;
    private EditText searchQuery;
    private IndexFastScrollRecyclerView expandableList;
    private ProgressBar progressBar;
    public HistoryListAdapter.GROUP selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
    private HistoryListAdapter adapter;
    private List<Condition> listConditions;
    private List<Allergy> listAllergies;
    private boolean isSearchResults = false;
    private Menu menu;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.med_history));

        searchLayout = (LinearLayout) view.findViewById(R.id.searchLayout);
        searchQuery = (EditText) view.findViewById(R.id.searchQuery);
        expandableList = (IndexFastScrollRecyclerView) view.findViewById(R.id.expandableList);
        progressBar = (ProgressBar) view.findViewById(R.id.req_progress);

        searchLayout.setVisibility(View.VISIBLE);
        searchQuery.addTextChangedListener(this);
        searchCancelClickEvent();

        selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
        setAdapter(false);

        if (AwsManager.getInstance().getConditions() != null && AwsManager.getInstance().getConditions().size() > 0) {
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
                    //if user is viewing conditions, then display Allergies
                    showAllergies();
                } else {
                    //if user is viewing allergies, then the button would be "DONE", so start saving the conditions and further save allergies too.
                    updateConditions();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showConditions() {

        searchQuery.setText("");
        searchQuery.clearFocus();

        selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
        setAdapter(false);

        if (menu != null && menu.getItem(0) != null) {
            menu.getItem(0).setTitle("Next");
        }
    }

    private void showAllergies() {

        searchQuery.setText("");
        searchQuery.clearFocus();

        selectedGroup = HistoryListAdapter.GROUP.ALLERGIES;
        setAdapter(false);

        if (menu != null && menu.getItem(0) != null) {
            menu.getItem(0).setTitle("Done");
        }
    }

    private void setAdapter(boolean isSearchResults) {

        this.isSearchResults = isSearchResults;

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        expandableList.setLayoutManager(llm);

        expandableList.setIndexBarTextColor("#" + Integer.toHexString(getResources().getColor(R.color.primary)));
        expandableList.setIndexBarColor("#" + Integer.toHexString(getResources().getColor(R.color.white)));

        if (listConditions == null || !isSearchResults) {
            listConditions = AwsManager.getInstance().getConditions();
        }
        if (listAllergies == null || !isSearchResults) {
            listAllergies = AwsManager.getInstance().getAllergies();
        }

        if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == selectedGroup.getValue()) {
            Timber.d("MH. adapter. conditions size = " + listConditions.size());
        } else {
            Timber.d("MH. adapter. allergies size = " + listAllergies.size());
        }

        adapter = new HistoryListAdapter(getActivity(), isSearchResults, selectedGroup,
                listConditions,
                listAllergies, this);

        expandableList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getConditions() {

        reqCount++;
        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConditions(
                AwsManager.getInstance().getConsumer(),
                new SDKCallback<List<Condition>, SDKError>() {
                    @Override
                    public void onResponse(List<Condition> conditions, SDKError sdkError) {
                        if (sdkError == null) {
                            listConditions = conditions;
                            AwsManager.getInstance().setConditions(conditions);

                            selectedGroup = HistoryListAdapter.GROUP.CONDITIONS;
                            setAdapter(false);
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
        AwsManager.getInstance().getAWSDK().getConsumerManager().getAllergies(
                AwsManager.getInstance().getConsumer(),
                new SDKCallback<List<Allergy>, SDKError>() {
                    @Override
                    public void onResponse(List<Allergy> allergies, SDKError sdkError) {
                        if (sdkError == null) {
                            listAllergies = allergies;
                            AwsManager.getInstance().setAllergies(allergies);
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
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateConditions(
                AwsManager.getInstance().getConsumer(),
                AwsManager.getInstance().getConditions(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        updateAllergies();
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
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateAllergies(
                AwsManager.getInstance().getConsumer(),
                AwsManager.getInstance().getAllergies(),
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void searchConditions(String searchText) {

        Timber.d("MH. searchConditions. searchText = " + searchText);

        progressBar.setVisibility(View.VISIBLE);

        List<Condition> conditions = AwsManager.getInstance().getConditions();

        List<Condition> searchConditions = new ArrayList<>();
        for (Condition c : conditions) {
            if (c.getName() != null && c.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchConditions.add(c);
            }
        }
        Timber.d("MH. searchConditions. all_size = " + conditions.size() + ". search_size = " + searchConditions.size());

        listConditions = searchConditions;
        setAdapter(true);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void searchAllergies(String searchText) {

        Timber.d("MH. searchAllergies. searchText = " + searchText);

        progressBar.setVisibility(View.VISIBLE);

        List<Allergy> allergies = AwsManager.getInstance().getAllergies();

        List<Allergy> searchAllergies = new ArrayList<>();
        for (Allergy c : allergies) {
            if (c.getName() != null && c.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchAllergies.add(c);
            }
        }
        Timber.d("MH. searchAllergies. all_size = " + allergies.size() + ". search_size = " + searchAllergies.size());

        listAllergies = searchAllergies;
        setAdapter(true);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void selectedItem(int groupSelected, int childPosition) {

        if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == groupSelected) {

            if (isSearchResults) {

                Condition conditionSelected = listConditions.get(childPosition);

                int i = 0;
                for (Condition condition : AwsManager.getInstance().getConditions()) {
                    if (condition.equals(conditionSelected)) {
                        AwsManager.getInstance().getConditions().get(i).setCurrent(
                                !AwsManager.getInstance().getConditions().get(i).isCurrent());
                        break;
                    }
                    i++;
                }
            } else {
                if (childPosition == 0) {
                    for (Condition condition : AwsManager.getInstance().getConditions()) {
                        condition.setCurrent(false);
                    }
                    AwsManager.getInstance().setHasConditionsFilledOut(AwsManager.State.FILLED_OUT_HAVE_NONE);
                } else {
                    AwsManager.getInstance().getConditions().get(childPosition - 1).setCurrent(
                            !AwsManager.getInstance().getConditions().get(childPosition - 1).isCurrent());
                }
            }
            AwsManager.getInstance().setConditions(AwsManager.getInstance().getConditions());
        } else {
            if (isSearchResults) {
                Allergy allergySelected = listAllergies.get(childPosition);

                int i = 0;
                for (Allergy allergy : AwsManager.getInstance().getAllergies()) {
                    if (allergy.equals(allergySelected)) {
                        AwsManager.getInstance().getAllergies().get(i).setCurrent(
                                !AwsManager.getInstance().getAllergies().get(i).isCurrent());
                        break;
                    }
                    i++;
                }
            } else {
                if (childPosition == 0) {
                    for (Allergy allergy : AwsManager.getInstance().getAllergies()) {
                        allergy.setCurrent(false);
                    }
                    AwsManager.getInstance().setHasAllergiesFilledOut(AwsManager.State.FILLED_OUT_HAVE_NONE);
                } else {
                    AwsManager.getInstance().getAllergies().get(childPosition - 1).setCurrent(
                            !AwsManager.getInstance().getAllergies().get(childPosition - 1).isCurrent());
                }
            }
            AwsManager.getInstance().setAllergies(AwsManager.getInstance().getAllergies());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() > 0) {
            if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == selectedGroup.getValue()) {
                searchConditions(s.toString().trim());
            } else {
                searchAllergies(s.toString().trim());
            }
        }
    }

    //work-in-progress
    /*@Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (searchQuery.getText().toString().trim().length() > 0) {
                if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == selectedGroup.getValue()) {
                    searchConditions(searchQuery.getText().toString().trim());
                } else {
                    searchAllergies(searchQuery.getText().toString().trim());
                }
            } else {
                if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == selectedGroup.getValue()) {
                    showConditions();
                } else {
                    showAllergies();
                }
            }
            return true;
        }
        return false;
    }*/

    private void searchCancelClickEvent() {
        searchQuery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if ((int) event.getRawX() >= (searchQuery.getRight() -
                            searchQuery.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        searchQuery.setText("");

                        if (HistoryListAdapter.GROUP.CONDITIONS.getValue() == selectedGroup.getValue()) {
                            showConditions();
                        } else {
                            showAllergies();
                        }
                        CommonUtil.hideSoftKeyboard(getActivity());
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
