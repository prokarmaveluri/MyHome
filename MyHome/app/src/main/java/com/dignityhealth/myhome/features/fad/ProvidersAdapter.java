package com.dignityhealth.myhome.features.fad;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.AdapterProvidersListItemBinding;
import com.dignityhealth.myhome.features.profile.Address;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DeviceDisplayManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class ProvidersAdapter extends RecyclerView.Adapter<ProvidersAdapter.ProvidersVH> {

    private boolean recent;
    private List<Provider> providerList;
    private IProviderClick listener;
    private Context mContext;
    private ArrayList<String> recentProviders;

    public ProvidersAdapter(List<Provider> providers,
                            Context context, IProviderClick listener,
                            ArrayList<String> recentProviders, boolean recent) {
        providerList = providers;
        mContext = context;
        this.recent = recent;
        this.listener = listener;
        this.recentProviders = recentProviders;
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

        public void bind(Provider provider, int position) {

            try {
                binding.itemLayout.setTag(position);
                binding.directions.setTag(position);
                binding.docDisplayName.setText(provider.getDisplayFullName());
                binding.docSpeciality.setText(provider.getSpecialties().get(0));

                if (!recent) {
                    if (null != provider.getOffices().get(0).getDistanceMilesFromSearch() &&
                            !provider.getOffices().get(0).getDistanceMilesFromSearch().isEmpty()) {

                        //round distance to one decimal
                        Double distance = Double.valueOf(provider.getOffices().get(0).getDistanceMilesFromSearch());
                        String format = String.format(Locale.getDefault(), "%.1f", distance);
                        binding.distance.setText(format + " " + mContext.getString(R.string.miles_concat));
                    } else {
                        binding.distance.setText("");
                    }
                    binding.distance.setVisibility(View.VISIBLE);
                } else {
                    binding.distance.setVisibility(View.INVISIBLE);
                }
                binding.docAddress.setText(provider.getOffices().get(0).getAddress());

                String url = provider.getImageUrl();
                url = url.replace(DeviceDisplayManager.W60H80, DeviceDisplayManager.W120H160);

                Picasso.with(mContext)
                        .load(url)
                        .into(binding.docImage);
                binding.recentlyViewed.setVisibility(View.GONE);
                if (recentProviders.contains(provider.getProviderId()))
                    binding.recentlyViewed.setVisibility(View.VISIBLE);

                binding.bookOnline.setVisibility(View.GONE);
                if (provider.getHasAppointments()) {
                    binding.bookOnline.setVisibility(View.VISIBLE);
                }
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
                    listener.providerClick((int) view.getTag());
                    break;
                case R.id.directions:
                    try {
                        Timber.i("Directions " + view.getTag());
                        Provider provider = providerList.get((int) view.getTag());
                        Address address = new Address(provider.getOffices().get(0).getAddress1(),
                                provider.getOffices().get(0).getAddress2(),
                                provider.getOffices().get(0).getCity(),
                                provider.getOffices().get(0).getState(),
                                provider.getOffices().get(0).getZipCode(), "");
                        CommonUtil.getDirections(mContext, address);
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
