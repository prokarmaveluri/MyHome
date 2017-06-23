package com.prokarma.myhome.app;

import android.support.v4.app.Fragment;

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
}