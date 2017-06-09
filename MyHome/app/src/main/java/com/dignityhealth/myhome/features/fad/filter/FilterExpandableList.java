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
    private GroupSelectionListener listener;

    private List<CommonModel> specialties;
    private List<CommonModel> gender;
    private List<CommonModel> languages;
    private List<CommonModel> hospitals;
    private List<CommonModel> practices;

    private ArrayList<CommonModel> selectSpecialties = new ArrayList<>();
    private ArrayList<CommonModel> selectGender = new ArrayList<>();
    private ArrayList<CommonModel> selectLanguages = new ArrayList<>();
    private ArrayList<CommonModel> selectHospitals = new ArrayList<>();
    private ArrayList<CommonModel> selectPractices = new ArrayList<>();

    private boolean isSpecialtiesSelected;
    private boolean isGenderSelected;
    private boolean isLanguagesSelected;
    private boolean isHospitalsSelected;
    private boolean isPracticesSelected;

    public FilterExpandableList(Context context, List<CommonModel> specialties,
                                List<CommonModel> gender,
                                List<CommonModel> languages,
                                List<CommonModel> hospitals,
                                List<CommonModel> practices,
                                GroupSelectionListener list) {

        mContext = context;
        this.specialties = specialties;
        this.gender = gender;
        this.languages = languages;
        this.hospitals = hospitals;
        this.practices = practices;
        this.listener = list;

        clearSelection();
        updateSelection(this.languages, selectLanguages);
        updateSelection(this.gender, selectGender);
        updateSelection(this.practices, selectPractices);
        updateSelection(this.hospitals, selectHospitals);
        updateSelection(this.specialties, selectSpecialties);
    }

    @Override
    public int getGroupCount() {
        return getGroupTitles().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (GROUP.SPECIALTIES.getValue() == groupPosition) {
            if (isSpecialtiesSelected)
                return selectSpecialties.size();
            else
                return specialties.size();
        } else if (GROUP.GENDER.getValue() == groupPosition) {
            if (isGenderSelected)
                return selectGender.size();
            else
                return gender.size();
        } else if (GROUP.LANGUAGES.getValue() == groupPosition) {
            if (isLanguagesSelected)
                return selectLanguages.size();
            else
                return languages.size();
        } else if (GROUP.HOSPITALS.getValue() == groupPosition) {
            if (isHospitalsSelected)
                return selectHospitals.size();
            else
                return hospitals.size();
        } else if (GROUP.PRACTICE.getValue() == groupPosition) {
            if (isPracticesSelected)
                return selectPractices.size();
            else
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
            if (isSpecialtiesSelected)
                return selectSpecialties.get(childPosition);
            else
                return specialties.get(childPosition);
        } else if (GROUP.GENDER.getValue() == groupPosition) {
            if (isGenderSelected)
                return selectGender.get(childPosition);
            else
                return gender.get(childPosition);
        } else if (GROUP.LANGUAGES.getValue() == groupPosition) {
            if (isLanguagesSelected)
                return selectLanguages.get(childPosition);
            else
                return languages.get(childPosition);
        } else if (GROUP.HOSPITALS.getValue() == groupPosition) {
            if (isHospitalsSelected)
                return selectHospitals.get(childPosition);
            else
                return hospitals.get(childPosition);
        } else if (GROUP.PRACTICE.getValue() == groupPosition) {
            if (isPracticesSelected)
                return selectPractices.get(childPosition);
            else
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_group, parent, false);
        TextView view = (TextView) convertView.findViewById(R.id.filterGroup);
        view.setText(getGroupTitles().get(groupPosition));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == GROUP.SPECIALTIES.getValue()) {
                    isSpecialtiesSelected = !isSpecialtiesSelected;
                } else if (groupPosition == GROUP.GENDER.getValue()) {
                    isGenderSelected = !isGenderSelected;
                } else if (groupPosition == GROUP.LANGUAGES.getValue()) {
                    isLanguagesSelected = !isLanguagesSelected;
                } else if (groupPosition == GROUP.HOSPITALS.getValue()) {
                    isHospitalsSelected = !isHospitalsSelected;
                } else if (groupPosition == GROUP.PRACTICE.getValue()) {
                    isPracticesSelected = !isPracticesSelected;
                }
                listener.selectedGroup(groupPosition);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             final boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_child, parent, false);
        CheckBox view = (CheckBox) convertView.findViewById(R.id.expChild);
        if (groupPosition == GROUP.SPECIALTIES.getValue()) {
            if (!isSpecialtiesSelected) {
                view.setText(specialties.get(childPosition).getLabel());
                view.setChecked(specialties.get(childPosition).getSelected());
            } else {
                view.setText(selectSpecialties.get(childPosition).getLabel());
                view.setChecked(selectSpecialties.get(childPosition).getSelected());
            }
        } else if (groupPosition == GROUP.GENDER.getValue()) {
            if (!isGenderSelected) {
                view.setText(gender.get(childPosition).getLabel());
                view.setChecked(gender.get(childPosition).getSelected());
            } else {
                view.setText(selectGender.get(childPosition).getLabel());
                view.setChecked(selectGender.get(childPosition).getSelected());
            }
        } else if (groupPosition == GROUP.LANGUAGES.getValue()) {
            if (!isLanguagesSelected) {
                view.setText(languages.get(childPosition).getLabel());
                view.setChecked(languages.get(childPosition).getSelected());
            } else {
                view.setText(selectLanguages.get(childPosition).getLabel());
                view.setChecked(selectLanguages.get(childPosition).getSelected());
            }
        } else if (groupPosition == GROUP.HOSPITALS.getValue()) {
            if (!isHospitalsSelected) {
                view.setText(hospitals.get(childPosition).getLabel());
                view.setChecked(hospitals.get(childPosition).getSelected());
            } else {
                view.setText(selectHospitals.get(childPosition).getLabel());
                view.setChecked(selectHospitals.get(childPosition).getSelected());
            }
        } else if (groupPosition == GROUP.PRACTICE.getValue()) {
            if (!isPracticesSelected) {
                view.setText(practices.get(childPosition).getLabel());
                view.setChecked(practices.get(childPosition).getSelected());
            } else {
                view.setText(selectPractices.get(childPosition).getLabel());
                view.setChecked(selectPractices.get(childPosition).getSelected());
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == GROUP.SPECIALTIES.getValue()) {
                    if (isSpecialtiesSelected) {
                        selectSpecialties.get(childPosition).setSelected(!selectSpecialties.get(childPosition).getSelected());
                    } else {
                        specialties.get(childPosition).setSelected(!specialties.get(childPosition).getSelected());
                    }
                    updateSelection(specialties, selectSpecialties);
                } else if (groupPosition == GROUP.GENDER.getValue()) {
                    if (isGenderSelected) {
                        updateGender(selectGender);
                        selectGender.get(childPosition).setSelected(!selectGender.get(childPosition).getSelected());
                    } else {
                        updateGender(gender);
                        gender.get(childPosition).setSelected(!gender.get(childPosition).getSelected());
                    }
                    updateSelection(gender, selectGender);
                } else if (groupPosition == GROUP.LANGUAGES.getValue()) {
                    if (isLanguagesSelected) {
                        selectLanguages.get(childPosition).setSelected(!selectLanguages.get(childPosition).getSelected());
                    } else {
                        languages.get(childPosition).setSelected(!languages.get(childPosition).getSelected());
                    }
                    updateSelection(languages, selectLanguages);
                } else if (groupPosition == GROUP.HOSPITALS.getValue()) {
                    if (isHospitalsSelected) {
                        selectHospitals.get(childPosition).setSelected(!selectHospitals.get(childPosition).getSelected());
                    } else {
                        hospitals.get(childPosition).setSelected(!hospitals.get(childPosition).getSelected());
                    }
                    updateSelection(hospitals, selectHospitals);
                } else if (groupPosition == GROUP.PRACTICE.getValue()) {
                    if (isPracticesSelected) {
                        selectPractices.get(childPosition).setSelected(!selectPractices.get(childPosition).getSelected());
                    } else {
                        practices.get(childPosition).setSelected(!practices.get(childPosition).getSelected());
                    }
                    updateSelection(practices, selectPractices);
                }
                notifyDataSetChanged();
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

    private void updateGender(List<CommonModel> gender) {
        for (CommonModel gen : gender) {
            gen.setSelected(false);
        }
    }

    private void clearSelection() {
        isSpecialtiesSelected = true;
        isGenderSelected = true;
        isLanguagesSelected = true;
        isHospitalsSelected = true;
        isPracticesSelected = true;

        selectGender.clear();
        selectHospitals.clear();
        selectLanguages.clear();
        selectPractices.clear();
        selectSpecialties.clear();
    }

    private void updateSelection(List<CommonModel> filter, ArrayList<CommonModel> selects) {
        selects.clear();
        for (CommonModel model : filter) {
            if (model.getSelected())
                selects.add(model);
        }
    }

    interface GroupSelectionListener {
        public void selectedGroup(int position);
    }
}
