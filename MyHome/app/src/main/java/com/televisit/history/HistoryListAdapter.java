package com.televisit.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;

import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.prokarma.myhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by veluri on 11/29/17.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.MyViewHolder> implements SectionIndexer {

    public enum GROUP {
        CONDITIONS(0),
        ALLERGIES(1);

        private final int id;

        GROUP(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    private Context mContext;
    private List<Condition> conditions;
    private List<Allergy> allergies;
    private GroupSelectionListener listener;
    private ArrayList<Integer> mSectionPositions;
    private GROUP groupPosition = GROUP.CONDITIONS;

    public HistoryListAdapter(Context context,
                              GROUP groupPosition,
                              List<Condition> conditions,
                              List<Allergy> allergies,
                              GroupSelectionListener listener) {

        mContext = context;
        this.groupPosition = groupPosition;
        this.conditions = conditions;
        this.allergies = allergies;
        this.listener = listener;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);

        if (GROUP.CONDITIONS.getValue() == groupPosition.getValue()) {
            if (conditions != null) {
                for (int i = 0, size = conditions.size(); i < size; i++) {
                    String section = String.valueOf(conditions.get(i).getName().charAt(0)).toUpperCase();
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                }
            }
        } else if (GROUP.ALLERGIES.getValue() == groupPosition.getValue()) {
            if (allergies != null) {
                for (int i = 0, size = allergies.size(); i < size; i++) {
                    String section = String.valueOf(allergies.get(i).getName().charAt(0)).toUpperCase();
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                }
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.med_history_child, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (groupPosition.getValue() == HistoryListAdapter.GROUP.CONDITIONS.getValue()) {
            if (position == 0) {
                holder.view.setText("I don't have any conditions");
                holder.view.setChecked(false);
            } else {
                holder.view.setText(conditions.get(position - 1).getName());
                holder.view.setChecked(conditions.get(position - 1).isCurrent());
            }
            holder.view.setTag(position);

        } else if (groupPosition.getValue() == HistoryListAdapter.GROUP.ALLERGIES.getValue()) {
            if (position == 0) {
                holder.view.setText("I don't have any allergies");
                holder.view.setTag(position);
            } else {
                holder.view.setText(allergies.get(position - 1).getName());
                holder.view.setChecked(allergies.get(position - 1).isCurrent());
            }
            holder.view.setTag(position);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (groupPosition.getValue() == HistoryListAdapter.GROUP.CONDITIONS.getValue()) {
                    listener.selectedGroup(groupPosition.getValue(), pos);
                } else if (groupPosition.getValue() == HistoryListAdapter.GROUP.ALLERGIES.getValue()) {
                    listener.selectedGroup(groupPosition.getValue(), pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //+1 added to account for the "I donot have any conditions/allergies"
        if (GROUP.CONDITIONS.getValue() == groupPosition.getValue()) {
            if (conditions != null) {
                return conditions.size() + 1;
            }
        } else if (GROUP.ALLERGIES.getValue() == groupPosition.getValue()) {
            if (allergies != null) {
                return allergies.size() + 1;
            }
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = (CheckBox) itemView.findViewById(R.id.expChild);
        }
    }

    private static List<String> getGroupTitles() {
        List<String> groups = new ArrayList<>();
        groups.add("Conditions");
        groups.add("Allergies");
        return groups;
    }

    interface GroupSelectionListener {
        void selectedGroup(int groupPosition, int childPosition);
    }
}
