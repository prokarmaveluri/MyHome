package com.dignityhealth.myhome.features.tos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/9/17.
 */

public class TosFragment extends BaseFragment {
    public static final String TOS_TAG = "tos_tag";
    View tosView;

    public static TosFragment newInstance() {
        return new TosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        tosView = inflater.inflate(R.layout.tos, container, false);
        getActivity().setTitle(getString(R.string.terms_of_service));

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

        WebView tos = (WebView) tosView.findViewById(R.id.terms_of_service);
        tos.loadUrl("file:///android_asset/tos.html");

        //getTosInfo("Bearer " + AuthManager.getInstance().getBearerToken());
        tosView.findViewById(R.id.tc_button_bar).setVisibility(View.GONE);  //Hide buttons when you're already logged into the app
        return tosView;
    }

    private void getTosInfo(String bearer) {
        Timber.i("Session bearer " + bearer);
        NetworkManager.getInstance().getTos(bearer).enqueue(new Callback<Tos>() {
            @Override
            public void onResponse(Call<Tos> call, Response<Tos> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    Tos termsOfService = response.body();
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Tos> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
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
