package com.prokarma.myhome.features.fad.suggestions;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.AdapterProviderSuggestionItemBinding;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class ProviderSuggestionsAdapter extends RecyclerView.Adapter<ProviderSuggestionsAdapter.SuggestionsVH> {

    private List<FadSuggesstions> list;
    private IProviderSuggestionClick listener;
    private Context mContext;

    public ProviderSuggestionsAdapter(List<FadSuggesstions> list,
                                      Context context, IProviderSuggestionClick listener) {
        this.list = list;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public SuggestionsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterProviderSuggestionItemBinding itemBinding =
                AdapterProviderSuggestionItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new SuggestionsClick());

        if (viewType == 0) {
            itemBinding.sugessionCarot.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.accent, mContext.getTheme()));
            } else {
                itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.accent));
            }
        } else {
            itemBinding.sugessionCarot.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_680, mContext.getTheme()));
            } else {
                itemBinding.suggestionText.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_680));
            }
        }
        return new SuggestionsVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(SuggestionsVH holder, int position) {
        FadSuggesstions sug = list.get(position);
        holder.bind(sug.getTitle(), position);
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
        if (list.get(position).getType().contains("SectionHeader")) {
            return 0;
        } else {
            return 1;
        }
    }

    public class SuggestionsVH extends RecyclerView.ViewHolder {

        private AdapterProviderSuggestionItemBinding binding;

        public SuggestionsVH(AdapterProviderSuggestionItemBinding itemView) {
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

    public class SuggestionsClick {
        public void onClickSuggestion(View view) {
            String type = list.get((int) view.getId()).getType();

            if (type.contains("SectionHeader")) {
                Timber.d("SuggestionsClick, SectionHeader");
                return;
            }
            listener.suggestionClick((String) view.getTag(),
                    list.get((int) view.getId()).getType(), list.get((int) view.getId()).getId());
        }
    }

    public interface IProviderSuggestionClick {
        void suggestionClick(String text, String type, String providerId);
    }
}
