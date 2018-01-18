package com.prokarma.myhome.features.settings;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
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
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class SettingsFragment extends BaseFragment implements SettingsAdapter.ISettingsClick {
    public static final String SETTINGS_TAG = "settings_tag";
    private View settingsView;
    private RecyclerView settingList;
    private SettingsAdapter listAdapter;


    public enum SettingsAction {
        TOUCH_ID("Enable Fingerprint Sign-In"),
        CHANGE_PASSWORD("Change Password"),
        CHANGE_SEC_QUESTION("Change Security Question");

        public final String name;
        SettingsAction(String s) {
            name = s;
        }
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
            Timber.w(ex);
        }
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();

        if (CommonUtil.isFingerPrintSupportedDevice(getContext())) {
            list.add(SettingsAction.TOUCH_ID.name);
        }
        list.add(SettingsAction.CHANGE_PASSWORD.name);
        list.add(SettingsAction.CHANGE_SEC_QUESTION.name);
        return list;
    }

    @Override
    public void settingsOptionClick(SettingsAction action) {

        if (action == SettingsAction.TOUCH_ID) {
            NavigationActivity.setActivityTag(Constants.ActivityTag.TOUCH_ID);
            Intent intentChangePassword = new Intent(getActivity(), OptionsActivity.class);
            ActivityCompat.startActivity(getActivity(), intentChangePassword, null);
        } else if (action == SettingsAction.CHANGE_PASSWORD) {
            NavigationActivity.setActivityTag(Constants.ActivityTag.CHANGE_PASSWORD);
            Intent intentChangePassword = new Intent(getActivity(), OptionsActivity.class);
            ActivityCompat.startActivity(getActivity(), intentChangePassword, null);

        } else if (action == SettingsAction.CHANGE_SEC_QUESTION) {
            NavigationActivity.setActivityTag(Constants.ActivityTag.ENTER_PASSWORD_SEC_QUESTION);
            Intent intentChangePassword = new Intent(getActivity(), OptionsActivity.class);
            ActivityCompat.startActivity(getActivity(), intentChangePassword, null);
        }
    }
}
