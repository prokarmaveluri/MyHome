package com.dignityhealth.myhome.features.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 4/27/17.
 */

public class MoreFragment extends BaseFragment {
    public static final String MORE_TAG = "more_tag";
    View moreView;

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        moreView = inflater.inflate(R.layout.more, container, false);

        TextView help = (TextView) moreView.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView settings = (TextView) moreView.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.SETTINGS);
            }
        });

        TextView preferences = (TextView) moreView.findViewById(R.id.preferences);
        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView contactUs = (TextView) moreView.findViewById(R.id.contact_us);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.CONTACT_US);
            }
        });

        TextView termsOfService = (TextView) moreView.findViewById(R.id.terms_of_service);
        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return moreView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MORE;
    }
}
