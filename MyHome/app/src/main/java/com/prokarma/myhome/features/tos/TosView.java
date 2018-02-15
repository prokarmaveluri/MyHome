package com.prokarma.myhome.features.tos;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.utils.ApiErrorUtil;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 2/14/18.
 */

public class TosView implements TosContract.View {
    private final Context context;

    private View masterView;
    private WebView webView;
    private RelativeLayout tcBar;
    private ProgressBar progress;

    public TosView(final Context context, final View masterView, final TosPresenter presenter) {
        this.context = context;
        this.masterView = masterView;

        TextView accept = (TextView) masterView.findViewById(R.id.tc_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAcceptClicked();
            }
        });

        TextView cancel = (TextView) masterView.findViewById(R.id.tc_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCancelClicked();
            }
        });

        progress = (ProgressBar) masterView.findViewById(R.id.terms_progress);
        webView = (WebView) masterView.findViewById(R.id.terms_of_service);
        webView.setVisibility(View.GONE);
        webView.loadUrl(TosActivity.FILE_ANDROID_ASSET_TOS_HTML);

        tcBar = masterView.findViewById(R.id.tc_button_bar);
        tcBar.setVisibility(View.GONE);  //TODO This might not always be the case to hide the buttons...
    }

    @Override
    public void onTosInfoSuccess(Response<Tos> response) {
        progress.setVisibility(View.GONE);
        Timber.d("getTosInfo. Successful Response\n" + response);
        webView.setVisibility(View.VISIBLE);

        if (response.body().result.isTermsAccepted) {
            tcBar.setVisibility(View.GONE);
        } else {
            tcBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTosInfoFailed(Response<Tos> response) {
        progress.setVisibility(View.GONE);
        Timber.e("Response, but not successful?\n" + response);
        ApiErrorUtil.getInstance().getTosError(context, masterView, response);
    }

    @Override
    public void onTosInfoFailed(Throwable throwable) {
        progress.setVisibility(View.GONE);
        Timber.e("Throwable = " + throwable);
        ApiErrorUtil.getInstance().getTosFailed(context, masterView, throwable);
    }
}