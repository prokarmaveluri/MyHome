package com.prokarma.myhome.features.fad.suggestions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.databinding.AdapterSuggestionListItemBinding;

import java.util.List;

import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 * <p>
 * Adapter to display the FAD list data.
 */

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionsVH> {

    private List<String> list;
    private ISuggestionClick listener;
    private Context mContext;

    public SuggestionsAdapter(List<String> list,
                              Context context, ISuggestionClick listener) {
        this.list = list;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public SuggestionsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        AdapterSuggestionListItemBinding itemBinding =
                AdapterSuggestionListItemBinding.inflate(layoutInflater, parent, false);
        itemBinding.setHandlers(new SuggestionsClick());
        return new SuggestionsVH(itemBinding);
    }

    @Override
    public void onBindViewHolder(SuggestionsVH holder, int position) {
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (null != list) {
            return list.size();
        }
        return 0;
    }

    public class SuggestionsVH extends RecyclerView.ViewHolder {

        private AdapterSuggestionListItemBinding binding;

        public SuggestionsVH(AdapterSuggestionListItemBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(String suggestion, int position) {

            try {
                binding.itemLayout.setTag(suggestion);
                binding.itemLayout.setId(position);
                binding.suggestionText.setText(suggestion);
            } catch (NullPointerException ex) {
                Timber.w(ex);
            }
            binding.executePendingBindings();
        }
    }

    public class SuggestionsClick {
        public void onClickSuggestion(View view) {
            Timber.i("Click " + view.getTag());
            if (listener != null)
                listener.suggestionClick((String) view.getTag(), (int) view.getId());
        }
    }

    public interface ISuggestionClick {
        void suggestionClick(String text, int position);
    }
}
