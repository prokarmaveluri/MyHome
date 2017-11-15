package com.televisit.history;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.prokarma.myhome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 5/16/17.
 */

public class HistoryExpandableList extends BaseExpandableListAdapter {

    private enum GROUP {
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

    public HistoryExpandableList(Context context,
                                 List<Condition> conditions,
                                 List<Allergy> allergies,
                                 GroupSelectionListener listener) {

        mContext = context;
        this.conditions = conditions;
        this.allergies = allergies;
        this.listener = listener;
    }

    @Override
    public int getGroupCount() {
        return getGroupTitles().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (GROUP.CONDITIONS.getValue() == groupPosition) {
            if (conditions != null)
                return conditions.size();
        } else if (GROUP.ALLERGIES.getValue() == groupPosition) {
            if (allergies != null)
                return allergies.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getGroupTitles().get(groupPosition);
    }

    @Nullable
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (GROUP.CONDITIONS.getValue() == groupPosition) {
            return conditions.get(childPosition);
        } else if (GROUP.ALLERGIES.getValue() == groupPosition) {
            return allergies.get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, @Nullable View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_group, parent, false);
        TextView view = (TextView) convertView.findViewById(R.id.filterGroup);
        view.setText(getGroupTitles().get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, @Nullable View convertView,
                             final ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.med_history_child, parent, false);
        CheckBox view = (CheckBox) convertView.findViewById(R.id.expChild);
        if (groupPosition == GROUP.CONDITIONS.getValue()) {

            view.setText(conditions.get(childPosition).getName());
            view.setChecked(conditions.get(childPosition).isCurrent());

        } else if (groupPosition == GROUP.ALLERGIES.getValue()) {

            view.setText(allergies.get(childPosition).getName());
            view.setChecked(allergies.get(childPosition).isCurrent());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == GROUP.CONDITIONS.getValue()) {
                    listener.selectedGroup(groupPosition, childPosition);
                } else if (groupPosition == GROUP.ALLERGIES.getValue()) {
                    listener.selectedGroup(groupPosition, childPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
