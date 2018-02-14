package com.prokarma.myhome.features.tos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/9/17.
 */

public class TosFragment extends BaseFragment {
    public static final String TOS_TAG = "tos_tag";
    private View tosView;
    private WebView webView;
    private ProgressBar progress;

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
                acceptTerms(AuthManager.getInstance().getBearerToken());
            }
        });

        TextView cancel = (TextView) tosView.findViewById(R.id.tc_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTerms();
            }
        });

        progress = (ProgressBar) tosView.findViewById(R.id.terms_progress);
        webView = (WebView) tosView.findViewById(R.id.terms_of_service);
        webView.loadUrl(TosActivity.FILE_ANDROID_ASSET_TOS_HTML);

        getTosInfo(AuthManager.getInstance().getBearerToken());
        tosView.findViewById(R.id.tc_button_bar).setVisibility(View.GONE);  //Hide buttons when you're already logged into the app
        return tosView;
    }

    private void getTosInfo(String bearer) {
        webView.setVisibility(View.GONE);
        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }
        progress.setVisibility(View.VISIBLE);
        Timber.i("getTosInfo. Session bearer " + bearer);
        NetworkManager.getInstance().getTos(bearer).enqueue(new Callback<Tos>() {
            @Override
            public void onResponse(Call<Tos> call, Response<Tos> response) {
                try {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body().isValid) {
                        Timber.d("getTosInfo. Successful Response\n" + response);
                        webView.setVisibility(View.VISIBLE);
                        if (isAdded() && response.body().result.isTermsAccepted) {
                            tosView.findViewById(R.id.tc_button_bar).setVisibility(View.GONE);
                        } else {
                            tosView.findViewById(R.id.tc_button_bar).setVisibility(View.VISIBLE);
                        }
                    } else {
                        Timber.e("getTosInfo. Response, but not successful?\n" + response);
                        ApiErrorUtil.getInstance().getTosError(getContext(), tosView, response);
                    }
                } catch (NullPointerException ex) {
                    Timber.w(ex);
                }
            }

            @Override
            public void onFailure(Call<Tos> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Timber.e("getTosInfo. Something failed! :/");
                Timber.e("Throwable = " + t);
                ApiErrorUtil.getInstance().getTosFailed(getContext(), tosView, t);
            }
        });
    }

    private void acceptTerms(String bearer) {
        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
        } else {
            progress.setVisibility(View.VISIBLE);
            NetworkManager.getInstance().acceptTos(bearer).enqueue(new Callback<Tos>() {
                @Override
                public void onResponse(Call<Tos> call, Response<Tos> response) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body().isValid) {
                        //TODO TOS accepted... notify the user.
                        tosView.findViewById(R.id.tc_button_bar).setVisibility(View.GONE);
                    } else {
                        Timber.e("acceptTerms. Response, but not successful?\n" + response);
                        ApiErrorUtil.getInstance().getTosError(getContext(), tosView, response);
                    }
                }

                @Override
                public void onFailure(Call<Tos> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Timber.e("acceptTerms. Something failed! :/");
                    Timber.e("Throwable = " + t);
                    ApiErrorUtil.getInstance().getTosFailed(getContext(), tosView, t);
                }
            });
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
