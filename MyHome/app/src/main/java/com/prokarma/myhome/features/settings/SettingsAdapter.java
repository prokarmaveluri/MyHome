package com.prokarma.myhome.features.settings;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterSettingsItemBinding;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsListVH> {

    private List<String> list;
    private Context mContext;

    public SettingsAdapter(List<String> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public SettingsListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterSettingsItemBinding itemBinding =
                AdapterSettingsItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new SettingListClick());

        itemBinding.sugessionCarot.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_680, mContext.getTheme()));
        } else {
            itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_680));
        }

        return new SettingsListVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(SettingsListVH holder, int position) {
        String title = list.get(position);
        holder.bind(title, position);
    }

    @Override
    public int getItemCount() {
        if (null != list) {
            return list.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class SettingsListVH extends RecyclerView.ViewHolder {

        private AdapterSettingsItemBinding binding;

        public SettingsListVH(AdapterSettingsItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(String suggestion, int position) {

            try {
                binding.itemLayout.setTag(suggestion);
                binding.itemLayout.setId(position);
                binding.suggestionText.setText(suggestion);
            } catch (NullPointerException ex) {

            }
            binding.executePendingBindings();
        }
    }

    public class SettingListClick {
        public void onClickSuggestion(View view) {
            Timber.i("POSITION " + (int) view.getId());
        }
    }

    public interface IProviderSuggestionClick {
        void suggestionClick(String text, String type, String providerId);
    }
}
