package com.dignityhealth.myhome.features.fad;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.FragmentProviderListBinding;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsFragment;
import com.dignityhealth.myhome.features.fad.recently.viewed.RecentlyViewedDataSourceDB;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class ProviderListFragment extends Fragment implements
        ProvidersAdapter.IProviderClick {

    private String errorMsg;
    private ProvidersAdapter adapter;
    private FragmentProviderListBinding binding;
    private ArrayList<String> recentlyViewed = new ArrayList<>();
    private List<Provider> providerList = new ArrayList<>();

    public static ProvidersAdapter.IProviderClick listener;

    private enum State {
        LIST,
        MESSAGE,
        SUGGESTION
    }

    public static final String FAD_TAG = "fad_list_tag";

    public static ProviderListFragment newInstance(ProvidersAdapter.IProviderClick listener) {
        ProviderListFragment.listener = listener;
        return new ProviderListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList = getArguments().getParcelableArrayList("PROVIDER_LIST");
            errorMsg = getArguments().getString("PROVIDER_MSG");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_list, container, false);
        recentlyViewed = RecentlyViewedDataSourceDB.getInstance().getAllEntry();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (providerList != null && providerList.size() > 0) {
            if (listener == null) {
                adapter = new ProvidersAdapter(providerList, getActivity(), this, recentlyViewed);
            } else {
                adapter = new ProvidersAdapter(providerList, getActivity(), listener, recentlyViewed);
            }
            binding.providersList.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.providersList.setAdapter(adapter);
            viewState(State.LIST);
            adapter.notifyDataSetChanged();
        } else if (errorMsg != null && !errorMsg.isEmpty()) {
            viewState(State.MESSAGE);
            binding.message.setText(errorMsg);
        } else {
            viewState(State.MESSAGE);
            binding.message.setText(getString(R.string.find_care));
        }

        showProgress(false);
    }

    public void showProgress(boolean inProgress) {
        if (inProgress) {
            binding.fadProgress.setVisibility(View.VISIBLE);
        } else {
            binding.fadProgress.setVisibility(View.GONE);
        }
    }

    private void showErrorMessage(boolean show, String message) {
        if (show) {
            viewState(State.MESSAGE);
            binding.message.setText(message);
        } else {
            viewState(State.LIST);
        }
    }

    @Override
    public void providerClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, providerList.get(position));
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }


    private void viewState(State current) {
        if (current == State.LIST) {
            binding.providersList.setVisibility(View.VISIBLE);
            binding.message.setVisibility(View.GONE);

        } else if (current == State.MESSAGE) {
            binding.message.setVisibility(View.VISIBLE);
            binding.providersList.setVisibility(View.GONE);
        }
    }

}
