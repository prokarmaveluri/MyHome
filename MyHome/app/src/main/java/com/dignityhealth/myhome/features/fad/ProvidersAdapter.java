package com.dignityhealth.myhome.features.fad;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.databinding.AdapterProvidersListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 */

public class ProvidersAdapter extends RecyclerView.Adapter<ProvidersAdapter.ProvidersVH> {

    private List<ProvidersResponse.Provider> providerList;
    private Context mContext;

    public ProvidersAdapter(List<ProvidersResponse.Provider> providers,
                            Context context) {
        providerList = providers;
        mContext = context;
    }

    @Override
    public ProvidersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterProvidersListItemBinding itemBinding =
                AdapterProvidersListItemBinding.inflate(layoutInflater, parent, false);
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

        private AdapterProvidersListItemBinding binding;

        public ProvidersVH(AdapterProvidersListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(ProvidersResponse.Provider provider, int position) {

            binding.itemLayout.setTag(position);
            binding.docDisplayName.setText(provider.getDisplayFullName());
            binding.docSpeciality.setText(provider.getSpecialties().get(0));
            binding.distance.setText(provider.getOffices().get(0).getDistanceMilesFromSearch() + " Miles");
            binding.docAddress.setText(provider.getOffices().get(0).getAddress());

            String url = provider.getImageUrl();
            url = url.replace("w60h80", "w120h160");
            Picasso.with(mContext)
                    .load(url)
                    .into(binding.docImage);

            binding.executePendingBindings();
        }
    }

    public class ProviderClick {
        public void onClickProvider(View view) {
            Timber.i("Click " + view.getTag());
        }
    }
}
