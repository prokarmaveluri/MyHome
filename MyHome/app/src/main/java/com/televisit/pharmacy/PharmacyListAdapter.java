package com.televisit.pharmacy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterPharmacyListItemBinding;

import java.util.List;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class PharmacyListAdapter extends RecyclerView.Adapter<PharmacyListAdapter.ProvidersVH> {

    private List<Pharmacy> pharmacyList;
    private Activity mContext;

    public PharmacyListAdapter(List<Pharmacy> pharmacies,
                               Activity context) {
        pharmacyList = pharmacies;
        mContext = context;
    }

    @Override
    public ProvidersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterPharmacyListItemBinding itemBinding =
                AdapterPharmacyListItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new PharmacyClick());
        return new ProvidersVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(ProvidersVH holder, int position) {
        holder.bind(pharmacyList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (null != pharmacyList) {
            return pharmacyList.size();
        }
        return 0;
    }

    public class ProvidersVH extends RecyclerView.ViewHolder {

        private AdapterPharmacyListItemBinding binding;

        public ProvidersVH(AdapterPharmacyListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(Pharmacy pharmacy, int position) {

            binding.itemLayout.setTag(position);
            binding.pharmacyDisplayName.setText(pharmacy.getName());
            binding.pharmacyAddress.setText(getPharmacyAddress(pharmacy));
            binding.distance.setText(String.valueOf(pharmacy.getDistance()));

            binding.executePendingBindings();
        }
    }

    public class PharmacyClick {
        public void onClickPharmacy(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.itemLayout:
                    break;
                case R.id.directions:
                    break;
            }
        }
    }

    public interface IPharmacyClick {
        void providerClick(int position);
    }

    private String getPharmacyAddress(Pharmacy pharmacy) {
        return pharmacy.getAddress().getAddress1() + "\n" + pharmacy.getAddress().getCity() + ", "
                + pharmacy.getAddress().getState().getCode() + " " + pharmacy.getAddress().getZipCode();
    }
}
