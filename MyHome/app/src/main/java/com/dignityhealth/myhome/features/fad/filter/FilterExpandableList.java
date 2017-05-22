package com.dignityhealth.myhome.features.fad.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.CommonModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmajji on 5/16/17.
 */

public class FilterExpandableList extends BaseExpandableListAdapter {

    private enum GROUP {
        SPECIALTIES(0),
        GENDER(1),
        LANGUAGES(2),
        HOSPITALS(3),
        PRACTICE(4);

        private final int id;

        GROUP(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    private Context mContext;

    private List<CommonModel> specialties;
    private List<CommonModel> gender;
    private List<CommonModel> languages;
    private List<CommonModel> hospitals;
    private List<CommonModel> practices;

    public FilterExpandableList(Context context, List<CommonModel> specialties,
                                List<CommonModel> gender,
                                List<CommonModel> languages,
                                List<CommonModel> hospitals,
                                List<CommonModel> practices) {

        mContext = context;
        this.specialties = specialties;
        this.gender = gender;
        this.languages = languages;
        this.hospitals = hospitals;
        this.practices = practices;
    }

    @Override
    public int getGroupCount() {
        return getGroupTitles().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (GROUP.SPECIALTIES.getValue() == groupPosition) {
            return specialties.size();
        } else if (GROUP.GENDER.getValue() == groupPosition) {
            return gender.size();
        } else if (GROUP.LANGUAGES.getValue() == groupPosition) {
            return languages.size();
        } else if (GROUP.HOSPITALS.getValue() == groupPosition) {
            return hospitals.size();
        } else if (GROUP.PRACTICE.getValue() == groupPosition) {
            return practices.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getGroupTitles().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (GROUP.SPECIALTIES.getValue() == groupPosition) {
            return specialties.get(childPosition);
        } else if (GROUP.GENDER.getValue() == groupPosition) {
            return gender.get(childPosition);
        } else if (GROUP.LANGUAGES.getValue() == groupPosition) {
            return languages.get(childPosition);
        } else if (GROUP.HOSPITALS.getValue() == groupPosition) {
            return hospitals.get(childPosition);
        } else if (GROUP.PRACTICE.getValue() == groupPosition) {
            return practices.get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_group, parent, false);
        TextView view = (TextView) convertView.findViewById(R.id.filterGroup);
        view.setText(getGroupTitles().get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_child, parent, false);
        CheckBox view = (CheckBox) convertView.findViewById(R.id.expChild);
        if (groupPosition == GROUP.SPECIALTIES.getValue()) {
            view.setText(specialties.get(childPosition).getLabel());
            view.setChecked(specialties.get(childPosition).getSelected());
        } else if (groupPosition == GROUP.GENDER.getValue()) {
            view.setText(gender.get(childPosition).getLabel());
            view.setChecked(gender.get(childPosition).getSelected());
        } else if (groupPosition == GROUP.LANGUAGES.getValue()) {
            view.setText(languages.get(childPosition).getLabel());
            view.setChecked(languages.get(childPosition).getSelected());
        } else if (groupPosition == GROUP.HOSPITALS.getValue()) {
            view.setText(hospitals.get(childPosition).getLabel());
            view.setChecked(hospitals.get(childPosition).getSelected());
        } else if (groupPosition == GROUP.PRACTICE.getValue()) {
            view.setText(practices.get(childPosition).getLabel());
            view.setChecked(practices.get(childPosition).getSelected());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == GROUP.SPECIALTIES.getValue()) {
                    specialties.get(childPosition).setSelected(!specialties.get(childPosition).getSelected());
                } else if (groupPosition == GROUP.GENDER.getValue()) {
                    gender.get(childPosition).setSelected(!gender.get(childPosition).getSelected());
                } else if (groupPosition == GROUP.LANGUAGES.getValue()) {
                    languages.get(childPosition).setSelected(!languages.get(childPosition).getSelected());
                } else if (groupPosition == GROUP.HOSPITALS.getValue()) {
                    hospitals.get(childPosition).setSelected(!hospitals.get(childPosition).getSelected());
                } else if (groupPosition == GROUP.PRACTICE.getValue()) {
                    practices.get(childPosition).setSelected(!practices.get(childPosition).getSelected());
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
        groups.add("Specialty");
        groups.add("Gender");
        groups.add("Language");
        groups.add("Hospital");
        groups.add("Group Practice");
        return groups;
    }
}
