package com.prokarma.myhome.features.fad.dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterFavListItemBinding;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.utils.DeviceDisplayManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class FavProvidersAdapter extends RecyclerView.Adapter<FavProvidersAdapter.ProvidersVH> {

    private List<ProviderResponse> providerList = new ArrayList<>();
    private IProviderClick listener;
    private boolean isDashboard = false;
    private Context mContext;

    public FavProvidersAdapter(List<ProviderResponse> providers,
                               Context context, IProviderClick listener, boolean isDashboard) {
        providerList.clear();
        if (null != providers)
            providerList.addAll(providers);
        mContext = context;
        this.isDashboard = isDashboard;
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
        holder.bind(providerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (!isDashboard) {
            if (null != providerList) {
                return providerList.size();
            }
            return 0;
        } else {
            if (null != providerList && providerList.size() <= 5) {
                return providerList.size();
            } else if (null != providerList && providerList.size() > 5) {
                return 5; // display 5 fav providers in dashboard
            }
            return 0;
        }
    }

    public class ProvidersVH extends RecyclerView.ViewHolder {

        private AdapterFavListItemBinding binding;

        public ProvidersVH(AdapterFavListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(ProviderResponse provider, final int position) {

            try {
                binding.itemLayout.setTag(position);
                binding.fadDashBoardFav.setTag(position);
                binding.docDisplayName.setText(provider.getDisplayName());
                binding.docSpeciality.setText(provider.getPrimarySpecialities().get(0));

                String url = provider.getImages().get(2).getUrl();
                url = url.replace(DeviceDisplayManager.W60H80, DeviceDisplayManager.W120H160);

                Picasso.with(mContext)
                        .load(url)
                        .into(binding.docImage);
            } catch (NullPointerException | IndexOutOfBoundsException ex) {

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
                    listener.providerClick((int) view.getTag());
                    break;
                case R.id.fadDashBoardFav:
                    try {
                        int position = (int) view.getTag();
                        NetworkManager.getInstance().updateFavDoctor(false,
                                providerList.get(position).getNpi(), (ImageView) view,
                                providerList.get(position), true, mContext);
                        NetworkManager.getInstance().deleteSavedDocotor(providerList.get(position).getNpi());
                        providerList.remove(position);
                        listener.favProviderListUpdate(position);
                        notifyDataSetChanged();
                    } catch (NullPointerException | IndexOutOfBoundsException ex) {
                    }
                    break;
            }
        }
    }

    public interface IProviderClick {
        void providerClick(int position);

        void favProviderListUpdate(int position);
    }
}
