package com.dignityhealth.myhome.features.tos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/9/17.
 */

public class TosFragment extends BaseFragment{
    public static final String TOS_TAG = "settings_tag";
    View tosView;

    public static TosFragment newInstance() {
        return new TosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        tosView = inflater.inflate(R.layout.tos, container, false);

        TextView accept = (TextView) tosView.findViewById(R.id.tc_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });

        TextView cancel = (TextView) tosView.findViewById(R.id.tc_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTerms();
            }
        });

        return tosView;
    }

    private void acceptTerms() {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(),
                    R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
        } else {
            //TODO send PATCH API call for ToS here...

        }
    }

    private void cancelTerms() {
        getActivity().finish();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.TERMS_OF_SERVICE;
    }

}
