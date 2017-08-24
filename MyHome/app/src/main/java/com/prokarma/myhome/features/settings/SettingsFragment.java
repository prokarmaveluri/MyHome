package com.prokarma.myhome.features.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.OptionsActivity;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwelsh on 4/26/17.
 */

public class SettingsFragment extends BaseFragment implements SettingsAdapter.ISettingsClick {
    public static final String SETTINGS_TAG = "settings_tag";
    private View settingsView;
    private RecyclerView settingList;
    private SettingsAdapter listAdapter;


    public enum SettingsAction {
        TOUCH_ID,
        CHANGE_PASSWORD,
        CHANGE_SEC_QUESTION
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.settings, container, false);
        settingList = (RecyclerView) settingsView.findViewById(R.id.appSettings);
        getActivity().setTitle(getString(R.string.settings));
        updateList();
        return settingsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.SETTINGS_SCREEN, null);
    }

    private void updateList() {
        try {
            listAdapter = new SettingsAdapter(getList(), getActivity(), this);
            settingList.setLayoutManager(new LinearLayoutManager(getActivity()));
            settingList.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } catch (NullPointerException | IllegalStateException ex) {
        }
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("Touch ID");
        list.add("Change Password");
        list.add("Change Security Question");
        return list;
    }

    @Override
    public void settingsOptionClick(SettingsAction action) {

        if (action == SettingsAction.TOUCH_ID) {

        } else if (action == SettingsAction.CHANGE_PASSWORD) {
            NavigationActivity.setActivityTag(Constants.ActivityTag.CHANGE_PASSWORD);
            Intent intentChangePassword = new Intent(getActivity(), OptionsActivity.class);
            ActivityCompat.startActivity(getActivity(), intentChangePassword, null);
        } else if (action == SettingsAction.CHANGE_SEC_QUESTION) {

        }
    }
}
