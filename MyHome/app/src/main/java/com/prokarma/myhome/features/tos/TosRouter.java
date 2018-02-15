package com.prokarma.myhome.features.tos;

import com.prokarma.myhome.app.BaseFragment;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosRouter implements TosContract.Router {
    BaseFragment fragment;

    public TosRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void finishTos() {
        if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().finish();
        }
    }
}
