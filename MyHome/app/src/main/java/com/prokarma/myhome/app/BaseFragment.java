package com.prokarma.myhome.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/27/17.
 * Master Fragment to extend
 * Helps share common code across all fragments
 */
public abstract class BaseFragment extends Fragment implements BaseInterface {

    @Override
    public void onResume() {
        super.onResume();
        NavigationActivity.setActivityTag(setDrawerTag());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ApiErrorUtil.getInstance().clearErrorMessage();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        ApiErrorUtil.getInstance().clearErrorMessage();
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarLineVisibility(true);
        }
        try {
            CommonUtil.hideSoftKeyboard(getActivity());
        } catch (Exception ex) {
            Timber.w(ex);
        }
        super.onDestroyView();
    }
}