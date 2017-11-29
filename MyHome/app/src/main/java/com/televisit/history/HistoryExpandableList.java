package com.televisit.history;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.prokarma.myhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 5/16/17.
 */

public class HistoryExpandableList extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

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

    public HistoryExpandableList(Context context,
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (GROUP.CONDITIONS.getValue() == groupPosition.getValue()) {
            if (conditions != null) {
                return conditions.size();
            }
        } else if (GROUP.ALLERGIES.getValue() == groupPosition.getValue()) {
            if (allergies != null) {
                return allergies.size();
            }
        }
        return 0;
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
