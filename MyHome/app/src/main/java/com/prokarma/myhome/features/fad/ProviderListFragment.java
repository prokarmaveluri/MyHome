package com.prokarma.myhome.features.fad;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.databinding.FragmentProviderListBinding;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.RESTConstants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class ProviderListFragment extends Fragment implements
        ProvidersAdapter.IProviderClick {

    private String errorMsg;
    private boolean loadingMore = false;
    private boolean pagination = true;
    private boolean recent = false;
    private ProvidersAdapter adapter;
    private LinearLayoutManager manager;
    private FragmentProviderListBinding binding;
    private ArrayList<String> recentlyViewed = new ArrayList<>();
    private List<ProviderDetailsResponse> providerList = null;

    public ProvidersAdapter.IProviderClick listener;

    private enum State {
        LIST,
        MESSAGE,
        SUGGESTION
    }

    public static final String FAD_TAG = "fad_list_tag";

    public void setListener(ProvidersAdapter.IProviderClick list) {
        listener = list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList = new ArrayList<>();
            ArrayList<ProviderDetailsResponse> list = getArguments().getParcelableArrayList("PROVIDER_LIST");
            providerList.addAll(list);
            errorMsg = getArguments().getString("PROVIDER_MSG");
            pagination = getArguments().getBoolean("PROVIDER_PAGINATION");
            recent = getArguments().getBoolean("PROVIDER_RECENT");
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

        NavigationActivity.eventBus.register(this);
        if (providerList != null && providerList.size() > 0) {
            if (listener == null) {
                adapter = new ProvidersAdapter(providerList, getActivity(), this, recentlyViewed, false);
            } else {
                adapter = new ProvidersAdapter(providerList, getActivity(), listener, recentlyViewed, false);
            }

            manager = new LinearLayoutManager(getActivity());
            manager.scrollToPosition(FadFragment.currentScroll);
            binding.providersList.setLayoutManager(manager);
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

        binding.providersList.addOnScrollListener(new ListScroll());
        showProgress(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        NavigationActivity.eventBus.unregister(this);
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

    private class ListScroll extends RecyclerView.OnScrollListener {
        /**
         * Callback method to be invoked when the RecyclerView has been scrolled. This will be
         * called after the scroll has completed.
         * <p>
         * This callback will also be called if visible item range changes after a layout
         * calculation. In that case, dx and dy will be 0.
         *
         * @param recyclerView The RecyclerView which scrolled.
         * @param dx           The amount of horizontal scroll.
         * @param dy           The amount of vertical scroll.
         */
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = manager.getItemCount();
            int lastVisibleItem = manager.findLastVisibleItemPosition();
            FadFragment.currentScroll = manager.findFirstVisibleItemPosition();

            if (totalItemCount != FadFragment.maxCount && ((totalItemCount - 10) <= (lastVisibleItem + 1))) {
                if (pagination)
                    onLoadMoreData();
            }
            if (totalItemCount == FadFragment.maxCount && (lastVisibleItem + 1) == FadFragment.maxCount) {
//                Toast.makeText(getActivity(), "end of the list", Toast.LENGTH_SHORT).show();
                Timber.i("list count maxCount " + FadFragment.maxCount);
            }
        }
    }

    private void onLoadMoreData() {
        if (loadingMore)
            return;

        loadingMore = true;
        if (FadFragment.mPageIndex * Integer.valueOf(RESTConstants.PROVIDER_PAGE_SIZE) < FadFragment.maxCount) {
            int page = FadFragment.mPageIndex + 1;
            PageData data = new PageData();
            data.setPageNo(page);
            NavigationActivity.eventBus.post(data);
        } else {
        }
    }

    public class PageData {
        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        int pageNo;
    }

    @Subscribe
    public void updateNewPageList(FadFragment.NewPageData pageData) {
        try {
            loadingMore = false;
            Timber.i("update new page list");
            this.providerList.addAll(pageData.getList());
            adapter.notifyDataSetChanged();
        } catch (NullPointerException ex) {
        }
    }
}
