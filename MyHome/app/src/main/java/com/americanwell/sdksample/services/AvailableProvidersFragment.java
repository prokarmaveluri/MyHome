/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleUtils;
import com.americanwell.sdksample.widget.SwipeRefreshLayoutCheckChild;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Fragment to show currently available providers
 */
public class AvailableProvidersFragment extends Fragment {

    @Inject
    protected SampleUtils sampleUtils;

    @BindView(R.id.first_available_prompt)
    TextView firstAvailablePrompt;
    @BindView(R.id.button_first_available)
    Button buttonFirstAvailable;
    @BindView(R.id.list_header)
    TextView listHeader;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    View emptyView;

    protected ArrayAdapter<ProviderInfo> adapter;

    private OnAvailableProvidersFragmentListener mListener;

    private Unbinder unbinder;

    public AvailableProvidersFragment() {
        // Required empty public constructor
    }

    public static AvailableProvidersFragment newInstance() {
        AvailableProvidersFragment fragment = new AvailableProvidersFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProvidersAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_providers, container, false);
        unbinder = ButterKnife.bind(this, view);
        SampleApplication.getActivityComponent().inject(this);
        listView.setAdapter(adapter);
        setListItems(mListener.getPresenter().getProviders());
        setShowFirstAvailable(mListener.getPresenter().showFirstAvailable());
        mListener.getSwipeRefreshLayout().setChildView(listView);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAvailableProvidersFragmentListener) {
            mListener = (OnAvailableProvidersFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAvailableProvidersFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAvailableProvidersFragmentListener {
        ServicePresenter getPresenter();

        SwipeRefreshLayoutCheckChild getSwipeRefreshLayout();
    }

    public void setShowFirstAvailable(final boolean bShow) {
        if (firstAvailablePrompt != null) {
            firstAvailablePrompt.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
        if (buttonFirstAvailable != null) {
            buttonFirstAvailable.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.button_first_available)
    public void onFirstAvailableClick() {
        mListener.getPresenter().getFirstAvailableVisitContext();
    }

    public void setListHeader(@StringRes int res) {
        if (listHeader != null) listHeader.setText(res);
    }

    /**
     * the presenter will call this when there are new items to add to the list
     *
     * @param items
     */
    public void setListItems(@NonNull final List<ProviderInfo> items) {
        if (items != null) {
            if (adapter != null) {
                adapter.clear();
                adapter.addAll(items);
            }
            if (listView != null) {
                listView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }
            if (listHeader != null) {
                listHeader.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }

    public class ProvidersAdapter extends ArrayAdapter<ProviderInfo> {
        public ProvidersAdapter(final Context context) {
            super(context, 0);
        }

        public class ViewHolder {
            @BindView(R.id.image_view)
            ImageView imageView;
            @BindView(R.id.name_text_view)
            TextView nameTextView;
            @BindView(R.id.specialty_text_view)
            TextView specialtyTextView;
            @BindView(R.id.availability_text_view)
            TextView availabilityTextView;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_provider, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final ProviderInfo providerInfo = getItem(position);
            viewHolder.imageView.setImageBitmap(null);

            mListener.getPresenter().loadProviderImage(
                    providerInfo,
                    viewHolder.imageView,
                    getResources().getDrawable(R.drawable.img_provider_photo_placeholder));

            viewHolder.nameTextView.setText(providerInfo.getFullName());

            String availability = sampleUtils.getProviderAvailabilityString(
                    getResources(),
                    providerInfo.getVisibility(),
                    providerInfo.getWaitingRoomCount());
            viewHolder.availabilityTextView.setText(availability);

            if (providerInfo.getVisibility() == ProviderVisibility.WEB_AVAILABLE) {
                viewHolder.nameTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.availabilityTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.availabilityTextView.setTextColor(getResources().getColor(R.color.awsdk_color_black));
            }
            else {
                viewHolder.nameTextView.setTypeface(null, Typeface.NORMAL);
                viewHolder.availabilityTextView.setTypeface(null, Typeface.NORMAL);
                viewHolder.availabilityTextView.setTextColor(getResources().getColor(R.color.color_text_gray));
            }

            viewHolder.specialtyTextView.setText(providerInfo.getSpecialty().getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ProviderActivity.makeIntent(getContext(), providerInfo));
                }
            });

            return view;
        }
    }

}
