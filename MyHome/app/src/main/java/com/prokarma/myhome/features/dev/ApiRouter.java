package com.prokarma.myhome.features.dev;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.CommonUtil;

/**
 * Created by kwelsh on 2/1/18.
 */

public class ApiRouter implements ApiContract.Router {
    final BaseFragment fragment;

    public ApiRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void exitApp() {
        CommonUtil.exitApp(fragment.getContext(), fragment.getActivity());
    }
}
