package com.prokarma.myhome.features.fad.dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterFavListItemBinding;
import com.prokarma.myhome.features.preferences.MySavedDoctorsResponse;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class FavProvidersAdapter extends RecyclerView.Adapter<FavProvidersAdapter.ProvidersVH> {

    private List<MySavedDoctorsResponse.FavoriteProvider> providerList;
    private IProviderClick listener;
    private Context mContext;

    public FavProvidersAdapter(List<MySavedDoctorsResponse.FavoriteProvider> providers,
                               Context context, IProviderClick listener) {
        providerList = providers;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public ProvidersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterFavListItemBinding itemBinding =
                AdapterFavListItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new ProviderClick());
        return new ProvidersVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(ProvidersVH holder, int position) {
//        holder.bind(providerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (null != providerList) {
            return providerList.size();
        }
        return 2;
    }

    public class ProvidersVH extends RecyclerView.ViewHolder {

        private AdapterFavListItemBinding binding;

        public ProvidersVH(AdapterFavListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(MySavedDoctorsResponse.FavoriteProvider provider, int position) {

            try {
//                binding.itemLayout.setTag(position);
//                binding.docDisplayName.setText(provider.getDisplayName());
//                binding.docSpeciality.setText(provider.getPrimarySpecialities().get(0));
//
//                String url = provider.getProviderDetailsUrl();
//                url = url.replace(DeviceDisplayManager.W60H80, DeviceDisplayManager.W120H160);
//
//                Picasso.with(mContext)
//                        .load(url)
//                        .into(binding.docImage);
            } catch (NullPointerException ex) {

            }
            binding.executePendingBindings();
        }
    }

    public class ProviderClick {
        public void onClickProvider(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.itemLayout:
                    Timber.i("Click " + view.getTag());
//                    listener.providerClick((int) view.getTag());
                    break;
                case R.id.fadDashBoardFav:
                    try {

                    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
                    }
                    break;
            }
        }
    }

    public interface IProviderClick {
        void providerClick(int position);
    }
}
