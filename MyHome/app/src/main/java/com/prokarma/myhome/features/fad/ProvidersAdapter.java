package com.prokarma.myhome.features.fad;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterProvidersListItemBinding;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.DeviceDisplayManager;
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
    private List<ProviderDetailsResponse> providerList;
    private IProviderClick listener;
    private Activity mContext;
    public static View coachMarkView;
    public static View coachItemLayout;
    private ArrayList<String> recentProviders;

    public ProvidersAdapter(List<ProviderDetailsResponse> providers,
                            Activity context, IProviderClick listener,
                            ArrayList<String> recentProviders, boolean recent) {
        providerList = providers;
        mContext = context;
        this.recent = recent;
        this.listener = listener;
        coachMarkView = null;
        coachItemLayout = null;
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

        if (position == 0) {
            coachMarkView = holder.binding.directions;
            coachItemLayout = holder.binding.docInfoLayout;
        }
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

        public void bind(ProviderDetailsResponse provider, int position) {
            binding.itemLayout.setTag(position);
            binding.directions.setTag(position);
            binding.docDisplayName.setText(provider.getDisplayFullName());
            binding.docSpeciality.setText(provider.getSpecialties() != null ? provider.getSpecialties().get(0) : "");
            binding.directions.setContentDescription(provider.getDisplayFullName() + ", " + mContext.getString(R.string.show_in_map));

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
            if (recentProviders.contains(provider.getNpi()))
                binding.recentlyViewed.setVisibility(View.VISIBLE);

            binding.bookOnline.setVisibility(View.GONE);
            if (provider.getHasAppointments() != null && provider.getHasAppointments()) {
                binding.bookOnline.setVisibility(View.VISIBLE);
            }

            binding.executePendingBindings();
        }
    }

    public class ProviderClick {
        public void onClickProvider(final View view) {
            int id = view.getId();
            switch (id) {
                case R.id.itemLayout:
                    Timber.i("Click " + view.getTag());
                    listener.providerClick((int) view.getTag());
                    break;
                case R.id.directions:
                    try {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                        alertDialog.setMessage(mContext.getString(R.string.map_alert));
                        alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Timber.i("Directions " + view.getTag());
                                ProviderDetailsResponse provider = providerList.get((int) view.getTag());
                                Address address = new Address(provider.getOffices().get(0).getAddress1(),
                                        provider.getOffices().get(0).getAddress2(),
                                        provider.getOffices().get(0).getCity(),
                                        provider.getOffices().get(0).getState(),
                                        provider.getOffices().get(0).getZipCode(), "");
                                CommonUtil.getDirections(mContext, address);
                            }
                        }).setNeutralButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
                        Timber.w(ex);
                    }
                    break;
            }
        }
    }

    public interface IProviderClick {
        void providerClick(int position);
    }
}
