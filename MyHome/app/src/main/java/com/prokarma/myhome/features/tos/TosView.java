package com.prokarma.myhome.features.tos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;

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

    public TosView(final Context context, final View masterView, final TosPresenter presenter, boolean showBar) {
        this.context = context;
        this.masterView = masterView;

        progress = (ProgressBar) masterView.findViewById(R.id.terms_progress);
        webView = (WebView) masterView.findViewById(R.id.terms_of_service);
        webView.loadUrl(TosActivity.FILE_ANDROID_ASSET_TOS_HTML);

        //Not sure if we need this anymore...
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (url.startsWith(String.valueOf(context.getString(R.string.url)))) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setMessage(context.getString(R.string.browser_alert));
                    alertDialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            loadURL(url);
                        }
                    }).setNeutralButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            webView.stopLoading();
                        }
                    }).show();
                    return true;
                }
                loadURL(url);
                return true;
            }

            private void loadURL(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

        tcBar = masterView.findViewById(R.id.tc_button_bar);
        tcBar.setVisibility(showBar ? View.VISIBLE : View.GONE);

        TextView accept = (TextView) masterView.findViewById(R.id.tc_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onRegisterUserSuccess(Response<Void> response) {
        progress.setVisibility(View.GONE);
        CommonUtil.showToast(context, context.getString(R.string.enrollment_success));
    }

    @Override
    public void onRegisterUserFailed(Response<Void> response) {
        progress.setVisibility(View.GONE);
        ApiErrorUtil.getInstance().registerError(context, masterView, response);
    }

    @Override
    public void onRegisterUserFailed(Throwable throwable) {
        progress.setVisibility(View.GONE);
        ApiErrorUtil.getInstance().registerFailed(context, masterView, throwable);
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