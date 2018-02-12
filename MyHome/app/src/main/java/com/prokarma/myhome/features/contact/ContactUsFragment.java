package com.prokarma.myhome.features.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;


/**
 * Created by kwelsh on 4/26/17.
 */

public class ContactUsFragment extends BaseFragment {
    public static final String CONTACT_TAG = "contact_tag";

    ContactUsPresenter presenter;

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View contactUsView = inflater.inflate(R.layout.contact_us, container, false);
        getActivity().setTitle(getString(R.string.contact_support));
        presenter = new ContactUsPresenter(getContext(), this, contactUsView);
        presenter.onCreate();
        return contactUsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.CONTACT_US;
    }
}