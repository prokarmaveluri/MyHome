package com.prokarma.myhome.features.home;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 7/28/17.
 */

public class HomeDidYouKnowFragment extends BaseFragment {
    public static final String DID_YOU_KNOW_TAG = "did_you_know_tag";

    private WebView webView;
    private String sectionType;

    public static HomeDidYouKnowFragment newInstance() {
        return new HomeDidYouKnowFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TealiumUtil.trackView(Constants.HOME_DID_YOU_KNOW_SCREEN, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View faqView = inflater.inflate(R.layout.fragment_did_you_know, container, false);

        webView = (WebView) faqView.findViewById(R.id.webview);

        sectionType = getArguments().getString("SECTION_TYPE");
        TextView error = (TextView) faqView.findViewById(R.id.error);
        final ProgressBar webProgress = (ProgressBar) faqView.findViewById(R.id.progress);
        if (sectionType.contains("HOME_DID_YOU_KNOW_SEC_2")) {
            webView.loadUrl(Constants.DID_YOU_KNOW_SEC2);
        } else {
            webView.loadUrl(Constants.DID_YOU_KNOW_SEC1);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if (request.getUrl().toString().toLowerCase().endsWith(".pdf")) {

                    CommonUtil.loadPdf(getContext(), view, request.getUrl().toString());
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webProgress.setVisibility(View.GONE);
            }
        });

        error.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        getActivity().setTitle(getString(R.string.did_you_know));
        if (!ConnectionUtil.isConnected(getActivity())) {
            error.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
        return faqView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        if (sectionType != null && sectionType.contains("HOME_DID_YOU_KNOW_SEC_2"))
            return Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_2;
        else if (sectionType != null && sectionType.contains("HOME_DID_YOU_KNOW_SEC_1"))
            return Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_1;
        return Constants.ActivityTag.NONE;
    }
}
