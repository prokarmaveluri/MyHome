package com.dignityhealth.myhome.features.fad;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.FragmentProviderListBinding;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsFragment;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class ProvidersMapFragment extends BaseFragment implements
        ProvidersAdapter.IProviderClick {

    private static String currentSearchQuery = "";

    private FragmentProviderListBinding binding;
    private ProvidersAdapter adapter;

    private LocationResponse location = null;
    private List<Provider> providerList = new ArrayList<>();

    private enum State {
        LIST,
        MESSAGE,
        SUGGESTION
    }

    public static final String FAD_TAG = "fad_tag";

    public static ProvidersMapFragment newInstance() {
        return new ProvidersMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentSearchQuery = getArguments().getString("QUERY");
            location = getArguments().getParcelable("LOCATION");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_provider_list, container, false);

//        if (null != currentSearchQuery && !currentSearchQuery.isEmpty() && location != null)
//            searchForQuery(currentSearchQuery);
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD_MAP;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ProvidersAdapter(providerList, getActivity(), this);
        binding.providersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.providersList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
