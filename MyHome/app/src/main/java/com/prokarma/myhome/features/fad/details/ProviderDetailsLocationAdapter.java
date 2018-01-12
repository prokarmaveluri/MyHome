package com.prokarma.myhome.features.fad.details;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.utils.AddressUtil;
import com.prokarma.myhome.utils.CommonUtil;

import java.util.List;

/**
 * Created by kwelsh on 6/18/17.
 */

public class ProviderDetailsLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_NORMAL = 1;

    public final Context context;
    public List<ProviderDetailsAddress> locations;
    private final RecyclerViewListener onItemClickListener;

    public ProviderDetailsLocationAdapter(Context context, @Nullable List<ProviderDetailsAddress> locations, @Nullable RecyclerViewListener onItemClickListener) {
        this.context = context;
        this.locations = locations;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_details_location_item_empty, parent, false);
            return new ViewHolderEmpty(context, view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_details_location_item, parent, false);
            return new ViewHolder(context, view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (genericHolder.getItemViewType()) {
            case VIEW_TYPE_NORMAL:
                ViewHolder holder = (ViewHolder) genericHolder;
                ProviderDetailsAddress address = locations.get(position);
                holder.locationText.setText(address.getAddress() != null ? address.getName() + "\n" + CommonUtil.constructAddress(address.getAddress(), null, address.getCity(), address.getState(), address.getZip()) : context.getString(R.string.address_unknown));
                holder.locationText.setContentDescription(address.getAddress() != null ? address.getName() + "\n" + AddressUtil.getAddressForAccessibilityUser(address) : context.getString(R.string.address_unknown));
                holder.pinImage.setContentDescription(address.getName() + ", " + context.getString(R.string.show_in_map));
                holder.setOnPinClickListener(address, onItemClickListener);
                break;

            case VIEW_TYPE_EMPTY:
                ViewHolderEmpty holderEmpty = (ViewHolderEmpty) genericHolder;
                //Stuff for Empty View...
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (locations == null || locations.isEmpty()) {
            return 1;   //return 1 to show the empty view
        }

        return locations.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (locations == null || locations.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void setLocations(List<ProviderDetailsAddress> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public View layout;
        public TextView locationText;
        public ImageView pinImage;

        public ViewHolder(final Context context, final View view) {
            super(view);
            this.view = view;
            layout = view.findViewById(R.id.location_item_layout);
            locationText = (TextView) view.findViewById(R.id.location_text);
            pinImage = (ImageView) view.findViewById(R.id.pin_icon);
        }

        public void setOnPinClickListener(final ProviderDetailsAddress address, final RecyclerViewListener onItemClickListener) {
            pinImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onPinClick(address, getAdapterPosition());
                }
            });
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolderEmpty(final Context context, final View view) {
            super(view);
            this.view = view;
        }
    }
}
