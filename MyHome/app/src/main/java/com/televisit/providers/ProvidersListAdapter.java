package com.televisit.providers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterMyCareProviderItemBinding;
import com.televisit.AwsManager;

import java.util.List;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class ProvidersListAdapter extends RecyclerView.Adapter<ProvidersListAdapter.ProvidersVH> {
    private final Context context;
    private List<ProviderInfo> providerList;
    private IProviderClick clickListener;

    public ProvidersListAdapter(Context context, List<ProviderInfo> provider,
                                IProviderClick listenr) {
        this.context = context;
        providerList = provider;
        clickListener = listenr;
    }

    @Override
    public ProvidersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterMyCareProviderItemBinding itemBinding =
                AdapterMyCareProviderItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new ProviderClick());
        return new ProvidersVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(ProvidersVH holder, int position) {
        holder.bind(providerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (null != providerList) {
            return providerList.size();
        }
        return 0;
    }

    public class ProvidersVH extends RecyclerView.ViewHolder {

        private AdapterMyCareProviderItemBinding binding;

        public ProvidersVH(AdapterMyCareProviderItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(ProviderInfo providerInfo, int position) {
            binding.itemLayout.setTag(position);

            binding.displayName.setText(providerInfo.getFullName());
            binding.displayName.setContentDescription(binding.displayName.getText());

            binding.displaySpeciality.setText(providerInfo.getSpecialty().getName());
            binding.displaySpeciality.setContentDescription(binding.displaySpeciality.getText());

            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(providerInfo, binding.providerImage, ProviderImageSize.EXTRA_EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(context, R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();

            if (providerInfo.getVisibility().equals(ProviderVisibility.WEB_AVAILABLE)) {
                binding.visibility.setVisibility(View.GONE);
                binding.waitingCount.setVisibility(View.VISIBLE);

                if (providerInfo.getWaitingRoomCount() > 0) {
                    binding.waitingCount.setText(providerInfo.getWaitingRoomCount() + " patients ahead");
                } else {
                    binding.waitingCount.setText("You are the Next Patient");
                }
                binding.waitingCount.setContentDescription(binding.waitingCount.getText());
            } else {
                binding.visibility.setVisibility(View.VISIBLE);
                binding.waitingCount.setVisibility(View.GONE);

                if(providerInfo.getVisibility().equals(ProviderVisibility.WEB_BUSY)){
                    binding.visibility.setText(context.getString(R.string.busy));
                } else {
                    binding.visibility.setText(context.getString(R.string.offline));
                }
                binding.visibility.setContentDescription(binding.visibility.getText());
            }
            binding.executePendingBindings();
        }
    }

    public class ProviderClick {
        public void onClickProvider(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.itemLayout:
                    clickListener.providerClick(providerList.get((int) view.getTag()));
                    break;
                case R.id.directions:
                    break;
            }
        }
    }

    public interface IProviderClick {
        void providerClick(ProviderInfo provider);
    }
}
