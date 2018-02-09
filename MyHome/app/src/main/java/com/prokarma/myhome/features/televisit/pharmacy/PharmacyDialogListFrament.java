package com.prokarma.myhome.features.televisit.pharmacy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by veluri on 12/19/17.
 */

public class PharmacyDialogListFrament extends Fragment implements PharmacyListAdapter.IPharmacyClick {

    public static final String PHARMACY_TAG = "pharmacy_list_tag";
    private EditText pharmacySearch;
    private RecyclerView pharmacyList;
    private ProgressBar progressBar;

    private String errorMsg;
    private PharmacyListAdapter adapter;
    private LinearLayoutManager manager;
    private List<Pharmacy> list = null;
    public PharmacyListAdapter.IPharmacyClick listenerPharmacy;

    private enum State {
        LIST,
        SUGGESTION
    }

    public void setListener(PharmacyListAdapter.IPharmacyClick list) {
        listenerPharmacy = list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = new ArrayList<>();
            ArrayList<Pharmacy> listPharmacies = getArguments().getParcelableArrayList("PHARMACY_LIST");
            list.addAll(listPharmacies);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy_list, container, false);
        pharmacyList = (RecyclerView) view.findViewById(R.id.pharmacyList);
        progressBar = (ProgressBar) view.findViewById(R.id.search_progress);
        pharmacySearch = (EditText) view.findViewById(R.id.pharmacySearch);
        pharmacySearch.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        NavigationActivity.eventBus.register(this);

        if (list != null && list.size() > 0) {
            if (listenerPharmacy == null) {
                adapter = new PharmacyListAdapter(getContext(), list, this);
            } else {
                adapter = new PharmacyListAdapter(getContext(), list, listenerPharmacy);
            }

            manager = new LinearLayoutManager(getActivity());
            pharmacyList.setLayoutManager(manager);
            pharmacyList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            pharmacyList.setAdapter(adapter);
            pharmacyList.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
        pharmacyList.addOnScrollListener(new ListScroll());
        showProgress(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        NavigationActivity.eventBus.unregister(this);
    }

    public void showProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void pharmacyClick(Pharmacy pharmacy) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PharmacyDetailsFragment.PHARMACY_KEY, pharmacy);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_PHARMACY_DETAILS, bundle);
    }

    private class ListScroll extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}
