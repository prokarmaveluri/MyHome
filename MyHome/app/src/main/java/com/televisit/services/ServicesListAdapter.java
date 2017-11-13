package com.televisit.services;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdksample.SampleApplication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterServicesListItemBinding;

import java.util.List;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class ServicesListAdapter extends RecyclerView.Adapter<ServicesListAdapter.ProvidersVH> {

    private List<Practice> serviceList;
    private IServiceClick clickListener;

    public ServicesListAdapter(List<Practice> practice,
                               IServiceClick listenr) {
        serviceList = practice;
        clickListener = listenr;
    }

    @Override
    public ProvidersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterServicesListItemBinding itemBinding =
                AdapterServicesListItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new ServicesClick());
        return new ProvidersVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(ProvidersVH holder, int position) {
        holder.bind(serviceList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (null != serviceList) {
            return serviceList.size();
        }
        return 0;
    }

    public class ProvidersVH extends RecyclerView.ViewHolder {

        private AdapterServicesListItemBinding binding;

        public ProvidersVH(AdapterServicesListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(Practice practice, int position) {
            binding.itemLayout.setTag(position);
            binding.displayName.setText(practice.getName());

            if (practice.hasSmallLogo()) {
                // preferred method for loading image
                SampleApplication.getInstance().getAWSDK().getPracticeProvidersManager()
                        .newImageLoader(practice, binding.serviceImage, false)
                        .build()
                        .load();
            }
            binding.executePendingBindings();
        }
    }

    public class ServicesClick {
        public void onClickService(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.itemLayout:
                    clickListener.serviceClick((Integer) view.getTag());
                    break;
                case R.id.directions:
                    break;
            }
        }
    }

    public interface IServiceClick {
        void serviceClick(int position);
    }
}
