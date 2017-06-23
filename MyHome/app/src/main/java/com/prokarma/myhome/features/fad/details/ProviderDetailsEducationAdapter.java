package com.prokarma.myhome.features.fad.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.R;

import java.util.List;

/**
 * Created by kwelsh on 5/15/17.
 */

public class ProviderDetailsEducationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_NORMAL = 1;

    public final Context context;
    public List<String> curriculum;

    public ProviderDetailsEducationAdapter(Context context, List<String> curriculum) {
        this.context = context;
        this.curriculum = curriculum;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_details_education_item_empty, parent, false);
            return new ViewHolderEmpty(context, view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_details_education_item, parent, false);
            return new ViewHolder(context, view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (genericHolder.getItemViewType()) {
            case VIEW_TYPE_NORMAL:
                ViewHolder holder = (ViewHolder) genericHolder;
                holder.curriculumText.setText(curriculum.get(position));
                break;

            case VIEW_TYPE_EMPTY:
                ViewHolderEmpty holderEmpty = (ViewHolderEmpty) genericHolder;
                //Stuff for Empty View...
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (curriculum == null || curriculum.isEmpty()) {
            return 1;   //return 1 to show the empty view
        }

        return curriculum.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (curriculum == null || curriculum.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void setCurriculum(List<String> curriculum) {
        this.curriculum = curriculum;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public View layout;
        public TextView curriculumText;

        public ViewHolder(final Context context, final View view) {
            super(view);
            this.view = view;
            layout = view.findViewById(R.id.education_item_layout);
            curriculumText = (TextView) view.findViewById(R.id.curriculum_text);
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
