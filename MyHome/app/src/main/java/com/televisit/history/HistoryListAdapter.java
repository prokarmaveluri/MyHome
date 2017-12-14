package com.televisit.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;

import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.prokarma.myhome.R;
import com.televisit.AwsManager;

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
    private GROUP groupSelected = GROUP.CONDITIONS;
    private String donotHaveConditionsState;
    private String donotHaveAllergiesState;
    private boolean isSearchResults = false;

    public HistoryListAdapter(Context context,
                              boolean isSearchResults,
                              GROUP groupSelected,
                              List<Condition> conditions,
                              List<Allergy> allergies,
                              GroupSelectionListener listener) {

        mContext = context;
        this.isSearchResults = isSearchResults;
        this.groupSelected = groupSelected;
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

        if (GROUP.CONDITIONS.getValue() == groupSelected.getValue()) {
            if (conditions != null) {
                for (int i = 0, size = conditions.size(); i < size; i++) {
                    String section = String.valueOf(conditions.get(i).getName().charAt(0)).toUpperCase();
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                }
            }
        } else if (GROUP.ALLERGIES.getValue() == groupSelected.getValue()) {
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

        if (groupSelected.getValue() == HistoryListAdapter.GROUP.CONDITIONS.getValue()) {

            if (isSearchResults) {
                holder.view.setText(conditions.get(position).getName());
                holder.view.setChecked(conditions.get(position).isCurrent());
            } else {
                if (position == 0) {
                    if (AwsManager.getInstance().isDependent()) {
                        holder.view.setText(mContext.getResources().getString(R.string.no_conditions_dependent));
                    } else {
                        holder.view.setText(mContext.getResources().getString(R.string.no_conditions));
                    }

                    if (donotHaveConditionsState != null) {
                        if (donotHaveConditionsState.equalsIgnoreCase("true")) {
                            holder.view.setChecked(true);
                        } else {
                            holder.view.setChecked(false);
                        }
                    } else {
                        if (AwsManager.getInstance().isHasConditionsFilledOut() == AwsManager.State.FILLED_OUT_HAVE_FEW
                                || AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.NOT_FILLED_OUT) {
                            holder.view.setChecked(false);
                        } else {
                            holder.view.setChecked(true);
                        }
                    }
                } else {
                    holder.view.setText(conditions.get(position - 1).getName());
                    holder.view.setChecked(conditions.get(position - 1).isCurrent());
                }
            }

        } else if (groupSelected.getValue() == HistoryListAdapter.GROUP.ALLERGIES.getValue()) {

            if (isSearchResults) {
                holder.view.setText(allergies.get(position).getName());
                holder.view.setChecked(allergies.get(position).isCurrent());
            } else {
                if (position == 0) {
                    if (AwsManager.getInstance().isDependent()) {
                        holder.view.setText(mContext.getResources().getString(R.string.no_allergies_dependent));
                    } else {
                        holder.view.setText(mContext.getResources().getString(R.string.no_allergies));
                    }

                    if (donotHaveAllergiesState != null) {
                        if (donotHaveAllergiesState.equalsIgnoreCase("true")) {
                            holder.view.setChecked(true);
                        } else {
                            holder.view.setChecked(false);
                        }
                    } else {
                        if (AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.FILLED_OUT_HAVE_FEW
                                || AwsManager.getInstance().isHasAllergiesFilledOut() == AwsManager.State.NOT_FILLED_OUT) {
                            holder.view.setChecked(false);
                        } else {
                            holder.view.setChecked(true);
                        }
                    }
                } else {
                    holder.view.setText(allergies.get(position - 1).getName());
                    holder.view.setChecked(allergies.get(position - 1).isCurrent());
                }
            }
        }
        holder.view.setTag(position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (groupSelected.getValue() == HistoryListAdapter.GROUP.CONDITIONS.getValue()) {
                    listener.selectedItem(groupSelected.getValue(), pos);

                    if (!isSearchResults && pos == 0) {
                        CheckBox checkBox = (CheckBox) v;
                        if (checkBox.isChecked()) {
                            donotHaveConditionsState = "true";
                        } else {
                            donotHaveConditionsState = "false";
                        }
                    }

                } else if (groupSelected.getValue() == HistoryListAdapter.GROUP.ALLERGIES.getValue()) {
                    listener.selectedItem(groupSelected.getValue(), pos);

                    if (!isSearchResults && pos == 0) {
                        CheckBox checkBox = (CheckBox) v;
                        if (checkBox.isChecked()) {
                            donotHaveAllergiesState = "true";
                        } else {
                            donotHaveAllergiesState = "false";
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (GROUP.CONDITIONS.getValue() == groupSelected.getValue()) {
            if (conditions != null) {
                if (isSearchResults) {
                    return conditions.size();
                } else {
                    //+1 added to account for the "I donot have any conditions/allergies"
                    return conditions.size() + 1;
                }
            }
        } else if (GROUP.ALLERGIES.getValue() == groupSelected.getValue()) {
            if (allergies != null) {
                if (isSearchResults) {
                    return allergies.size();
                } else {
                    //+1 added to account for the "I donot have any conditions/allergies"
                    return allergies.size() + 1;
                }
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
        void selectedItem(int groupSelected, int childPosition);
    }
}
