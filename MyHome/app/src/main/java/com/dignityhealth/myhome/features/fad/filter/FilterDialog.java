package com.dignityhealth.myhome.features.fad.filter;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.features.fad.CommonModel;
import com.dignityhealth.myhome.utils.CommonUtil;

import java.util.ArrayList;

/*
 * Fragment dialog to display the password criteria.
 *
 * Created by cmajji on 1/03/17.
 */
public class FilterDialog extends DialogFragment {

    private ArrayList<CommonModel> specialties;
    private ArrayList<CommonModel> gender;
    private ArrayList<CommonModel> languages;
    private ArrayList<CommonModel> hospitals;
    private ArrayList<CommonModel> practices;

    private ExpandableListView filterList;
    private int selectedGroup = -1;

    public FilterDialog() {
        // Required empty public constructor
    }

    public static FilterDialog newInstance() {
        FilterDialog fragment = new FilterDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

        if (getArguments() != null) {
            specialties = getArguments().getParcelableArrayList("SPECIALITY");
            gender = getArguments().getParcelableArrayList("GENDER");
            languages = getArguments().getParcelableArrayList("LANGUAGE");
            hospitals = getArguments().getParcelableArrayList("HOSPITALS");
            practices = getArguments().getParcelableArrayList("PRACTICES");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        Toolbar appToolbar = (Toolbar) view.findViewById(R.id.toolbarWhite);
        filterList = (ExpandableListView) view.findViewById(R.id.expandableList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650,
                    getActivity().getTheme()));
        } else {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        ((NavigationActivity) getActivity()).setSupportActionBar(appToolbar);

        ((NavigationActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((NavigationActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.xblue);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.find_a_doctor));

        filterList.setAdapter(new FilterExpandableList(getActivity(), specialties,
                gender, languages, hospitals, practices));

        listListeners();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.filter_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_filter:
                dismiss();
                break;
            case android.R.id.home:
                dismiss();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listListeners() {
        filterList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (selectedGroup != -1 && groupPosition != selectedGroup) {
                    filterList.collapseGroup(selectedGroup);
                }
                selectedGroup = groupPosition;
            }
        });

        filterList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                CommonUtil.setListViewHeight(filterList, groupPosition, selectedGroup);
                return false;
            }
        });
    }

}
